package socialnetwork.domain.dto;

import socialnetwork.domain.entities.User;
import socialnetwork.domain.enums.NotificationType;

import java.time.LocalDateTime;

public class NotificationDTO {
    private User from;
    private String messageText;
    private LocalDateTime timestamp;
    private NotificationType type;

    public NotificationDTO(NotificationType type) {
        this.type = type;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(" [").append(timestamp).append("] ")
                .append(from.getFirstName()).append(" ").append(from.getLastName());
        switch (type) {
            case MESSAGE:
                builder.append(": ").append(messageText);
                break;
            case FRIENDSHIP:
                builder.append(" sent you a friend request!");
                break;
            default:
                builder.append(" added you in a new chat!");
        }
        return builder.toString();
    }
}
