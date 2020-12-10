package socialnetwork.service;

import socialnetwork.Utils.design.Observable;
import socialnetwork.domain.entities.Notification;
import socialnetwork.domain.entities.User;
import socialnetwork.domain.enums.NotificationStatus;
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
public class NotificationService extends Observable {
    private static Repository<Long, Notification> notificationRepository = null;

    public NotificationService(Repository<Long, Notification> notificationRepository) {
        NotificationService.notificationRepository = notificationRepository;
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
    public static Notification createNotification(Set<User> notifiedUsers, User from, String entityText,
                                                  NotificationType type, String notificationText,
                                                  LocalDateTime timestamp)
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
                .filter(x -> x.getNotifiedUsers().entrySet()
                        .stream()
                        .filter(value -> value.getValue().equals(NotificationStatus.UNSEEN))
                        .anyMatch(y -> y.getKey().getID().equals(user.getID())))
                .collect(Collectors.toList());
    }

    /**
     * Sets a notification status as SEEN.
     *
     * @param notification instance of notification.
     * @throws RepositoryException if the notification was not found.
     */
    public static void updateSeenNotification(Notification notification) throws RepositoryException {
        notificationRepository.update(notification);
    }

    /**
     * Deletes a notification by its ID.
     *
     * @param notificationID the ID of the current notification.
     * @throws RepositoryException if there is no notification with the specified ID.
     */
    public static void deleteNotification(long notificationID) throws RepositoryException {
        notificationRepository.delete(notificationID);
    }
}
