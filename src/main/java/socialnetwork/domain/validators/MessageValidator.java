package socialnetwork.domain.validators;

import socialnetwork.domain.entities.Message;

public class MessageValidator implements Validator<Message> {

    @Override
    public void validate(Message message) throws ValidationException {
        if (message == null) {
            throw new ValidationException("Entity not instance of Message");
        }
        // validate from
        if (message.getFrom() == null) {
            throw new ValidationException("From not instance of User");
        }
        // validate chat
        if (message.getChat() == null) {
            throw new ValidationException("To not instance of chat");
        }
        // validate timestamp
        if (message.getTimestamp() == null) {
            throw new ValidationException("Timestamp not instance of LocalDateTime");
        }
        // validate message
        if (message.getMessageText().equals("")) {
            throw new ValidationException("Invalid message!");
        }
        // validate notification ID
        if (message.getNotificationID() < 1) {
            throw new ValidationException("Invalid notification ID");
        }
    }
}
