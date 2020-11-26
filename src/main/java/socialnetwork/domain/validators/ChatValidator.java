package socialnetwork.domain.validators;

import socialnetwork.domain.entities.Chat;

public class ChatValidator implements Validator<Chat> {
    @Override
    public void validate(Chat chat) throws ValidationException {
        if (chat == null) {
            throw new ValidationException("Entity not instance of Friendship");
        }
        // validate chat name (sets default value)
        if (chat.getTitle() == null || chat.getTitle().equals("")) {
            chat.setTitle("Default");
        }
        // validate chat name length
        if (chat.getTitle().length() > 100) {
            throw new ValidationException("Invalid chat name");
        }
        // validate user list
        if (chat.getUsers() == null || chat.getChatSize() < 2) {
            throw new ValidationException("Invalid size of user list");
        }
        // validate notification ID
        if (chat.getNotificationID() < 1) {
            throw new ValidationException("Invalid notification ID");
        }
    }
}
