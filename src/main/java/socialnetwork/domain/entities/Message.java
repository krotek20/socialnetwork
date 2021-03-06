package socialnetwork.domain.entities;

import socialnetwork.Utils.Constants;
import socialnetwork.domain.Entity;
import socialnetwork.ui.gui.controllers.LoginController;

import java.time.LocalDateTime;
import java.util.Objects;

public class Message implements Entity<Long> {
    private static long count = 1;
    private long notificationID;
    private long id;
    private Chat to;
    private User from;
    private String reply;
    private String messageText;
    private LocalDateTime timestamp;

    public Message(User from, Chat to, String message, String reply, long notificationID) {
        this.id = count;
        this.to = to;
        this.from = from;
        this.reply = reply;
        this.timestamp = LocalDateTime.now();
        this.messageText = message;
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

    public long getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(long notificationID) {
        this.notificationID = notificationID;
    }

    public Chat getChat() {
        return to;
    }

    public void setChat(Chat chat) {
        this.to = chat;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
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
        StringBuilder builder = new StringBuilder();
        if (LoginController.loggedUser.getID().equals(from.getID())) {
            builder.append("You: ");
        } else {
            builder.append(from.getFirstName()).append(" ").append(from.getLastName()).append(": ");
        }
        builder.append(messageText);
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(id, message.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
