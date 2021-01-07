package socialnetwork.repository.database;

import socialnetwork.domain.entities.Event;
import socialnetwork.domain.entities.User;
import socialnetwork.domain.enums.EventSubscription;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class EventDBRepository extends AbstractDBRepository<Long, Event> implements Repository<Long, Event> {
    private final UserDBRepository userDBRepository;

    public EventDBRepository(Validator<Event> validator) {
        super(validator);
        this.userDBRepository = new UserDBRepository(new UserValidator());
        for (Event event : this.findAll()) {
            if (event.getStartAt().compareTo(LocalDateTime.now()) < 0) {
                this.delete(event.getID());
            }
        }
    }

    @Override
    public Event extractEntity(Map<String, Object> resultSet) {
        long id = (long) resultSet.get("ID_EVENT");
        long notificationID = (long) resultSet.get("ID_NOTIFICATION");
        String title = (String) resultSet.get("TITLE");
        LocalDateTime createdAt = ((Timestamp) resultSet.get("CREATED_AT")).toLocalDateTime();
        LocalDateTime startAt = ((Timestamp) resultSet.get("START_AT")).toLocalDateTime();

        Map<User, EventSubscription> users = new HashMap<>();
        Map<Long, EventSubscription> usersID = super.getUsersSubscriptions(String.format(
                "select \"ID_USER\", \"SUBSCRIPTION\" from \"EVENTS_USERS\" where \"ID_EVENT\" = %d", id));
        for (Map.Entry<Long, EventSubscription> userID : usersID.entrySet()) {
            users.put(userDBRepository.findOne(userID.getKey()), userID.getValue());
        }

        Event event = new Event(title, startAt, notificationID);
        event.setID(id);
        event.setCount(id);
        event.setUsers(users);
        event.setCreatedAt(createdAt);

        return event;
    }

    @Override
    public Event findOne(Long id) throws RepositoryException {
        if (id == null) {
            throw new RepositoryException("ID must be not null");
        }
        return super.findOne(String.format("select * from \"EVENTS\" where \"ID_EVENT\" = %d", id));
    }

    @Override
    public Iterable<Event> findAll() {
        return super.findAll("select * from \"EVENTS\"");
    }

    @Override
    public Iterable<Event> findPage(int limit, int offset) {
        return null;
    }

    @Override
    public Event save(Event event) throws ValidationException, RepositoryException {
        if (findOne(event.getID()) != null) {
            throw new RepositoryException("Event already exists");
        }
        super.save(event, String.format("insert into \"EVENTS\" values(%d, %d, '%s', '%s'::timestamp, '%s'::timestamp)",
                event.getID(), event.getNotificationID(), event.getTitle(), event.getCreatedAt(), event.getStartAt()));
        for (Map.Entry<User, EventSubscription> entry : event.getUsers().entrySet()) {
            super.save(event, String.format("insert into \"EVENTS_USERS\" values(%d, %d, %d)",
                    event.getID(), entry.getKey().getID(), EventSubscription.toValue(entry.getValue())));
        }
        return null;
    }

    public Event saveUser(Event event, User user) throws ValidationException {
        return super.save(event, String.format("insert into \"EVENTS_USERS\" values(%d, %d, %d)",
                event.getID(), user.getID(), EventSubscription.toValue(EventSubscription.SUBSCRIBE)));
    }

    @Override
    public Event delete(Long id) throws RepositoryException {
        Event event = findOne(id);
        if (event == null) {
            throw new RepositoryException("ID does not exist");
        }
        super.delete(id, String.format("delete from \"EVENTS\" where \"ID_EVENT\" = %d", id));
        return event;
    }

    @Override
    public Event update(Event event) throws ValidationException, RepositoryException {
        if (findOne(event.getID()) == null) {
            throw new RepositoryException("ID is invalid");
        }
        return super.update(event, String.format("update \"EVENTS\" set \"ID_NOTIFICATION\" = %d" +
                " where \"ID_EVENT\" = %d", event.getNotificationID(), event.getID()));
    }

    public void updateSubscription(Event event, User user, EventSubscription subscription)
            throws ValidationException, RepositoryException {
        if (findOne(event.getID()) == null) {
            throw new RepositoryException("ID is invalid");
        }
        super.update(event, String.format("update \"EVENTS_USERS\" set \"SUBSCRIPTION\" = %d" +
                        " where \"ID_EVENT\" = %d and \"ID_USER\" = %d",
                EventSubscription.toValue(subscription), event.getID(), user.getID()));
    }
}
