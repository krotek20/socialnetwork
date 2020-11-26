package socialnetwork.service;

import socialnetwork.domain.entities.Friendship;
import socialnetwork.domain.entities.Message;
import socialnetwork.domain.entities.Notification;
import socialnetwork.domain.entities.User;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Notification service class
 * Main notification functionalities are implemented here
 */
public class NotificationService {
    private final Repository<User, Notification> notificationRepository;

    public NotificationService(Repository<User, Notification> notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * Creates a new notification either for
     * a message or a friendship request.
     *
     * @param notifiedUser     User to be notified.
     * @param from             User who sent a message or a friendship request.
     * @param message          the {@code Message} object if the desired notification
     *                         is created for a Message; otherwise message is null.
     * @param friendship       the {@code Friendship} object if the desired notification
     *                         is created for a Friendship request; otherwise friendship is null.
     * @param notificationText text to be displayed to notifiedUser.
     * @throws RepositoryException if notification ID is null.
     * @throws ValidationException if notification is not valid.
     */
    protected void createNotification(User notifiedUser, User from,
                                      Message message, Friendship friendship,
                                      String notificationText) throws RepositoryException, ValidationException {
        Notification notification = (message != null ?
                new Notification(notifiedUser, from, message, notificationText) :
                new Notification(notifiedUser, from, friendship, notificationText));
        notificationRepository.save(notification);
    }

    /**
     * Reads all notifications of the inserted user.
     *
     * @param user instance of {@code User}.
     * @return list of all inserted user's notifications.
     */
    public List<Notification> readAllNotifications(User user) {
        return StreamSupport.stream(notificationRepository.findAll().spliterator(), false)
                .filter(x -> x.getID().getID().equals(user.getID()))
                .collect(Collectors.toList());
    }

    /**
     * Deletes all notifications of the inserted user.
     *
     * @param user instance of {@code User}.
     */
    public void deleteAllNotifications(User user) {
        while (true) {
            try {
                notificationRepository.delete(user);
            } catch (RepositoryException e) {
                break;
            }
        }
    }
}
