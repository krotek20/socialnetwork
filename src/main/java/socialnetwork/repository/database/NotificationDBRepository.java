package socialnetwork.repository.database;

import socialnetwork.domain.entities.Notification;
import socialnetwork.domain.entities.User;
import socialnetwork.domain.enums.NotificationStatus;
import socialnetwork.domain.enums.NotificationType;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class NotificationDBRepository
        extends AbstractDBRepository<Long, Notification>
        implements Repository<Long, Notification> {
    private final UserDBRepository userDBRepository;

    public NotificationDBRepository(String url, String username, String password, Validator<Notification> validator) {
        super(validator, username, password, url);
        this.userDBRepository = new UserDBRepository(url, username, password, new UserValidator());
    }

    @Override
    public Notification extractEntity(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("ID_NOTIFICATION");
        long fromID = resultSet.getLong("FROM_ID_USER");
        long entityID = resultSet.getLong("ID_ENTITY");
        short typeShort = resultSet.getShort("NOTIFICATION_TYPE");
        short shortStatus = resultSet.getShort("NOTIFICATION_STATUS");
        String notificationText = resultSet.getString("NOTIFICATION_TEXT");
        LocalDateTime timestamp = resultSet.getTimestamp("NOTIFICATION_TIMESTAMP").toLocalDateTime();

        Set<User> notifiedUsers = new HashSet<>();
        User from = userDBRepository.findOne(fromID);
        NotificationType type = NotificationType.fromValue(typeShort);
        NotificationStatus status = NotificationStatus.fromValue(shortStatus);

        Set<Long> usersID = super.getUsersID(String.format(
                "select \"ID_USER\" from \"USERS_NOTIFICATIONS\" where \"ID_NOTIFICATION\" = %d", id));
        for (long userID : usersID) {
            notifiedUsers.add(userDBRepository.findOne(userID));
        }

        Notification notification = new Notification(
                notifiedUsers, from, entityID, type, notificationText, timestamp);
        notification.setID(id);
        notification.setCount(id);
        notification.setStatus(status);

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
                        "values(%d, %d, %d, '%s', '%s'::timestamp, %d, %d)",
                notification.getID(), notification.getFrom().getID(),
                NotificationType.toValue(notification.getType()),
                notification.getNotificationText(), notification.getTimestamp(),
                NotificationStatus.toValue(notification.getStatus()), notification.getEntityID()));
        for (User user : notification.getNotifiedUsers()) {
            super.save(notification, String.format("insert into \"USERS_NOTIFICATIONS\" values(%d, %d)",
                    user.getID(), notification.getID()));
        }
        return null;
    }

    @Override
    public Notification delete(Long aLong) throws RepositoryException {
        return null;
    }

    @Override
    public Notification update(Notification notification) throws ValidationException, RepositoryException {
        if (findOne(notification.getID()) == null) {
            throw new RepositoryException("ID is invalid");
        }
        return super.update(notification, String.format("update \"NOTIFICATIONS\"" +
                        "set \"NOTIFICATION_STATUS\" = %d where \"ID_NOTIFICATION\" = %d",
                NotificationStatus.toValue(notification.getStatus()), notification.getID()));
    }
}
