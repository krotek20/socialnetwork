package socialnetwork.domain.entities;

import socialnetwork.domain.Entity;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.enums.FriendshipStatus;

import java.time.LocalDateTime;
import java.util.Objects;

public class Friendship implements Entity<Tuple<Long, Long>> {
    private long notificationID;
    private LocalDateTime date;
    private FriendshipStatus status;
    private Tuple<Long, Long> id;

    public Friendship(long id1, long id2, long notificationID) {
        this.notificationID = notificationID;
        this.id = new Tuple<>(id1, id2);
        this.date = LocalDateTime.now();
        this.status = FriendshipStatus.PENDING;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setStatus(FriendshipStatus status) {
        this.status = status;
    }

    public FriendshipStatus getStatus() {
        return status;
    }

    public void setNotificationID(long notificationID) {
        this.notificationID = notificationID;
    }

    public long getNotificationID() {
        return notificationID;
    }

    @Override
    public void setID(Tuple<Long, Long> id) {
        this.id = id;
    }

    @Override
    public Tuple<Long, Long> getID() {
        return id;
    }

    @Override
    public String toString() {
        return id.getLeft() + ";" + id.getRight() + ";" + date.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friendship that = (Friendship) o;
        return (Objects.equals(that.id.getLeft(), id.getLeft()) &&
                Objects.equals(that.id.getRight(), id.getRight())) ||
                (Objects.equals(that.id.getLeft(), id.getRight()) &&
                        Objects.equals(that.id.getRight(), id.getLeft()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}