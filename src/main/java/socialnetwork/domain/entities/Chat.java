package socialnetwork.domain.entities;

import socialnetwork.domain.Entity;
import socialnetwork.ui.gui.controllers.LoginController;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Chat class is an Entity type data
 * with an unique Long type ID.
 * It holds a List of Users that can be used
 * for Chat-messaging functionalities.
 * <p>
 * Any Chat should be formed of at least two Users.
 * If it has less than two Users it should be deleted or
 * exactly two Users then it is a private-Chat or
 * more than two Users and so it becomes a group-Chat.
 */
public class Chat implements Entity<Long> {
    private static long count = 1;
    private long notificationID;
    private long id;
    private String title;
    private Set<User> users;
    private LocalDateTime date;

    public Chat(String title, long notificationID) {
        this.id = count;
        this.date = LocalDateTime.now();
        this.title = title;
        this.users = new HashSet<>();
        this.notificationID = notificationID;
    }

    @Override
    public Long getID() {
        return id;
    }

    @Override
    public void setID(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public long getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(long notificationID) {
        this.notificationID = notificationID;
    }

    /**
     * Adds a new User to the Chat.
     *
     * @param user the user to be added.
     */
    public void addUser(User user) {
        this.users.add(user);
    }

    public int getChatSize() {
        return this.users.size();
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
        if (users.size() > 2) {
            return title;
        } else {
            List<User> localUsers = new ArrayList<>(users);
            User user = (LoginController.loggedUser.getID().equals(localUsers.get(0).getID())
                    ? localUsers.get(1) : localUsers.get(0));
            return user.getFirstName() + " " + user.getLastName();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chat chat = (Chat) o;
        return Objects.equals(id, chat.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
