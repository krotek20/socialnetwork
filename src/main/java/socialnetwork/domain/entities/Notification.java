package socialnetwork.domain.entities;

import socialnetwork.domain.Entity;
import socialnetwork.domain.enums.NotificationStatus;
import socialnetwork.domain.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Notification implements Entity<Long> {
    private static long count = 1;
    private long id;
    private User from;
    private long entityID;
    private String notificationText;
    private Map<User, NotificationStatus> notifiedUsers;
    private LocalDateTime timestamp;
    private NotificationType type;

    public Notification(Set<User> notifiedUsers, User from, long entityID,
                        NotificationType type, String notificationText,
                        LocalDateTime timestamp) {
        this.id = count;
        this.from = from;
        this.type = type;
        this.entityID = entityID;
        this.timestamp = timestamp;
        this.notifiedUsers = new HashMap<User, NotificationStatus>();
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

    public Set<User> getNotifiedUsers() {
        return notifiedUsers;
    }

    public void setNotifiedUsers(Set<User> notifiedUsers) {
        this.notifiedUsers = notifiedUsers;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public long getEntityID() {
        return entityID;
    }

    public void setEntityID(long entityID) {
        this.entityID = entityID;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
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

    public void addNotifiedUser(User user) {
        this.notifiedUsers.add(user);
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(";");
        joiner.add(String.valueOf(id))
                .add(from.getID().toString())
                .add(notifiedUsers.stream()
                        .map(User::getID)
                        .collect(Collectors.toList())
                        .toString())
                .add(notificationText)
                .add(type.toString())
                .add(String.valueOf(entityID))
                .add(status.toString());
        return joiner.toString();
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
