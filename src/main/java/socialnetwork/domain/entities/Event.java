package socialnetwork.domain.entities;

import socialnetwork.domain.Entity;
import socialnetwork.domain.enums.EventSubscription;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Event implements Entity<Long> {
    private static long count = 1;
    private long notificationID;
    private long id;
    private short targetTime;
    private String title;
    private LocalDateTime startAt;
    private LocalDateTime createdAt;
    private Map<User, EventSubscription> users;

    public Event(String title, LocalDateTime startAt, long notificationID) {
        this.id = count;
        this.title = title;
        this.startAt = startAt;
        this.users = new HashMap<>();
        this.createdAt = LocalDateTime.now();
        this.notificationID = notificationID;
    }

    @Override
    public void setID(Long id) {
        this.id = id;
    }

    @Override
    public Long getID() {
        return id;
    }

    public long getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(long notificationID) {
        this.notificationID = notificationID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public short getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(short targetTime) {
        this.targetTime = targetTime;
    }

    public int getEventSize() {
        return this.users.size();
    }

    public Map<User, EventSubscription> getUsers() {
        return users;
    }

    public void setUsers(Map<User, EventSubscription> users) {
        this.users = users;
    }

    /**
     * Adds a new User to the Event.
     *
     * @param user the user to be added.
     */
    public void addUser(User user) {
        this.users.put(user, EventSubscription.SUBSCRIBE);
    }

    /**
     * Change a user subscription for notifications on this event.
     *
     * @param user         the user that wants to change his subscription.
     * @param subscription new subscription.
     */
    public void changeUserSubscription(User user, EventSubscription subscription) {
        for (Map.Entry<User, EventSubscription> entry : users.entrySet()) {
            if (entry.getKey().getID().equals(user.getID())) {
                entry.setValue(subscription);
            }
        }
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

    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
