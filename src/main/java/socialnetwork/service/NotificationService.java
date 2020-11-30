package socialnetwork.service;

import socialnetwork.domain.entities.Notification;
import socialnetwork.domain.entities.User;
import socialnetwork.domain.enums.NotificationType;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Notification service class
 * Main notification functionalities are implemented here
 */
public class NotificationService {
    private final Repository<Long, Notification> notificationRepository;

    public NotificationService(Repository<Long, Notification> notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * Creates a new notification either for
     * a message or a friendship request.
     *
     * @param notifiedUsers    Users to be notified.
     * @param from             User who sent a message or a friendship request.
     * @param entityText       informational text to be displayed when a notification is read.
     * @param type             the type of the notification to be saved.
     * @param notificationText text to be displayed to notifiedUser.
     * @param timestamp        the exact time when the notification is saved.
     * @return the currently saved notification.
     * @throws RepositoryException if notification ID is null.
     * @throws ValidationException if notification is not valid.
     */
    public Notification createNotification(Set<User> notifiedUsers, User from, String entityText, NotificationType type,
                                           String notificationText, LocalDateTime timestamp)
            throws RepositoryException, ValidationException {
        Notification notification = new Notification(
                notifiedUsers, from, entityText, type, notificationText, timestamp);
        notification.setCount(notification.getID());
        notificationRepository.save(notification);
        return notification;
    }

    /**
     * Reads all notifications of the inserted user.
     *
     * @param user instance of {@code User}.
     * @return list of all inserted user's notifications.
     */
    public List<Notification> readAllNotifications(User user) {
        return StreamSupport.stream(notificationRepository.findAll().spliterator(), false)
                .filter(x -> x.getNotifiedUsers().keySet()
                        .stream()
                        .anyMatch(y -> y.getID().equals(user.getID())))
                .collect(Collectors.toList());
    }

    /**
     * Sets a notification status as SEEN.
     *
     * @param notification instance of notification.
     * @throws RepositoryException if the notification was not found.
     */
    public void updateSeenNotification(Notification notification) throws RepositoryException {
        notificationRepository.update(notification);
    }
}
