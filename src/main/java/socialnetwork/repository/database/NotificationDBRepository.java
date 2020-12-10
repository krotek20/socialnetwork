package socialnetwork.repository.database;

import socialnetwork.domain.entities.Friendship;
import socialnetwork.domain.entities.Notification;
import socialnetwork.domain.entities.User;
import socialnetwork.domain.enums.NotificationStatus;
import socialnetwork.domain.enums.NotificationType;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryException;
import socialnetwork.ui.gui.MainGUI;
import socialnetwork.ui.gui.controllers.LoginController;
import socialnetwork.ui.tui.BaseTUI;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class NotificationDBRepository
        extends AbstractDBRepository<Long, Notification>
        implements Repository<Long, Notification> {
    private final UserDBRepository userDBRepository;

    public NotificationDBRepository(String url, String username, String password, Validator<Notification> validator) {
        super(validator, username, password, url);
        this.userDBRepository = new UserDBRepository(url, username, password, new UserValidator());
        this.findAll();
    }

    @Override
    public Notification extractEntity(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("ID_NOTIFICATION");
        long fromID = resultSet.getLong("FROM_ID_USER");
        short typeShort = resultSet.getShort("NOTIFICATION_TYPE");
        String entityText = resultSet.getString("ENTITY_TEXT");
        String notificationText = resultSet.getString("NOTIFICATION_TEXT");
        LocalDateTime timestamp = resultSet.getTimestamp("NOTIFICATION_TIMESTAMP").toLocalDateTime();

        Map<User, NotificationStatus> notifiedUsers = new HashMap<>();
        User from = userDBRepository.findOne(fromID);
        NotificationType type = NotificationType.fromValue(typeShort);

        Map<Long, NotificationStatus> usersID = super.getNotifiedUsersID(String.format(
                "select \"ID_USER\", \"NOTIFICATION_STATUS\" " +
                        "from \"USERS_NOTIFICATIONS\" " +
                        "where \"ID_NOTIFICATION\" = %d and \"NOTIFICATION_STATUS\" = %d",
                id, NotificationStatus.toValue(NotificationStatus.UNSEEN)));
        for (Map.Entry<Long, NotificationStatus> userID : usersID.entrySet()) {
            notifiedUsers.put(userDBRepository.findOne(userID.getKey()), userID.getValue());
        }

        Notification notification = new Notification(from, entityText, type, notificationText, timestamp);
        notification.setID(id);
        notification.setCount(id);
        notification.setNotifiedUsers(notifiedUsers);

        return notification;
    }

    @Override
    public Notification findOne(Long id) throws RepositoryException {
        if (id == null) {
            throw new RepositoryException("ID must be not null");
        }
        return super.findOne(String.format("select * from \"NOTIFICATIONS\" where \"ID_NOTIFICATION\" = %d", id));
    }

    @Override
    public Iterable<Notification> findAll() {
        return super.findAll("select * from \"NOTIFICATIONS\"");
    }

    @Override
    public Notification save(Notification notification) throws ValidationException, RepositoryException {
        if (findOne(notification.getID()) != null) {
            throw new RepositoryException("Notification already exists");
        }
        super.save(notification, String.format("insert into \"NOTIFICATIONS\" " +
                        "values(%d, %d, %d, '%s', '%s'::timestamp, '%s')",
                notification.getID(), notification.getFrom().getID(),
                NotificationType.toValue(notification.getType()), notification.getNotificationText(),
                notification.getTimestamp(), notification.getEntityText()));
        for (Map.Entry<User, NotificationStatus> user : notification.getNotifiedUsers().entrySet()) {
            super.save(notification, String.format("insert into \"USERS_NOTIFICATIONS\" values(%d, %d, %d)",
                    user.getKey().getID(), notification.getID(), NotificationStatus.toValue(user.getValue())));
        }
        return null;
    }

    @Override
    public Notification delete(Long id) throws RepositoryException {
        Notification notification = findOne(id);
        if (notification == null) {
            throw new RepositoryException("ID does not exist");
        }
        super.delete(id, String.format("delete from \"NOTIFICATIONS\" where \"ID_NOTIFICATION\" = %d", id));
        return notification;
    }

    @Override
    public Notification update(Notification notification) throws ValidationException, RepositoryException {
        if (findOne(notification.getID()) == null) {
            throw new RepositoryException("ID is invalid");
        }
        return super.update(notification, String.format("update \"USERS_NOTIFICATIONS\" " +
                        "set \"NOTIFICATION_STATUS\" = %d " +
                        "where \"ID_NOTIFICATION\" = %d and \"ID_USER\" = %d",
                NotificationStatus.toValue(NotificationStatus.SEEN), notification.getID(),
                notification.getNotifiedUsers()
                        .keySet()
                        .stream()
                        .filter(x -> x.getID().equals(LoginController.loggedUser.getID()))
                        .collect(Collectors.toList())
                        .get(0)
                        .getID()));
    }
}
