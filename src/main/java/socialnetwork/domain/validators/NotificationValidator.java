package socialnetwork.domain.validators;

import socialnetwork.domain.entities.Notification;

public class NotificationValidator implements Validator<Notification> {
    @Override
    public void validate(Notification notification) throws ValidationException {
        if (notification == null) {
            throw new ValidationException("Entity not instance of Notification");
        }
        // validate ID
        if (notification.getID() == null) {
            throw new ValidationException("Notification send error!");
        }
        // validate from
        if (notification.getFrom() == null) {
            throw new ValidationException("Notification send error!");
        }
        // validate notification text
        if (notification.getNotificationText().equals("")) {
            throw new ValidationException("Notification text can't be null!");
        }
        // validate entity text
        if (notification.getEntityText().equals("")) {
            throw new ValidationException("Entity text can't be null!");
        }
    }
}
