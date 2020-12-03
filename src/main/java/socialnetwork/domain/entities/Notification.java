package socialnetwork.domain.entities;

import socialnetwork.domain.Entity;
import socialnetwork.domain.enums.NotificationStatus;
import socialnetwork.domain.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.*;

public class Notification implements Entity<Long> {
    private static long count = 1;
    private long id;
    private User from;
    private String entityText;
    private String notificationText;
    private LocalDateTime timestamp;
    private NotificationType type;
    private Map<User, NotificationStatus> notifiedUsers;

    public Notification(Set<User> notifiedUsers, User from, String entityText,
                        NotificationType type, String notificationText,
                        LocalDateTime timestamp) {
        this.id = count;
        this.from = from;
        this.type = type;
        this.timestamp = timestamp;
        this.entityText = entityText;
        this.notifiedUsers = initializeNotifiedUsers(notifiedUsers);
        this.notificationText = notificationText;
    }

    public Notification(User from, String entityText,
                        NotificationType type, String notificationText,
                        LocalDateTime timestamp) {
        this.id = count;
        this.from = from;
        this.type = type;
        this.entityText = entityText;
        this.timestamp = timestamp;
        this.notifiedUsers = new HashMap<>();
        this.notificationText = notificationText;
    }

    @Override
    public Long getID() {
        return id;
    }

    @Override
    public void setID(Long id) {
        this.id = id;
    }

    public Map<User, NotificationStatus> getNotifiedUsers() {
        return notifiedUsers;
    }

    public void setNotifiedUsers(Map<User, NotificationStatus> notifiedUsers) {
        this.notifiedUsers = notifiedUsers;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getEntityText() {
        return entityText;
    }

    public void setEntityText(String entityText) {
        this.entityText = entityText;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    /**
     * Setting the current count on the next maximum id.
     * This should be called specifically after every setID call and User instantiation.
     *
     * @param id id of the current entity instance
     */
    public void setCount(long id) {
        if (id >= count) count = id + 1;
    }

    /**
     * Resetting count to 1.
     * This should be use only for tests.
     */
    public void resetCount() {
        count = 1;
    }

    /**
     * Initializes notified users of this notification
     * with the notification status as SEEN.
     *
     * @param notifiedUsers users to be notified.
     * @return the map of required users (keys)
     * with their notification status (values).
     */
    private Map<User, NotificationStatus> initializeNotifiedUsers(Set<User> notifiedUsers) {
        Map<User, NotificationStatus> users = new HashMap<>();
        for (User user : notifiedUsers) {
            users.put(user, NotificationStatus.UNSEEN);
        }
        return users;
    }

    /**
     * Sets the status of a notification as seen for a single User.
     *
     * @param user user to be notified.
     */
    public void setUserNotificationStatus(User user) {
        for (Map.Entry<User, NotificationStatus> entry : notifiedUsers.entrySet()) {
            if (entry.getKey().equals(user)) {
                entry.setValue(NotificationStatus.SEEN);
            }
        }
    }

    @Override
    public String toString() {
        return notificationText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
