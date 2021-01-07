package socialnetwork.domain.validators;

import socialnetwork.domain.entities.Event;

public class EventValidator implements Validator<Event> {
    @Override
    public void validate(Event event) throws ValidationException {
        if (event == null) {
            throw new ValidationException("Entity not instance of event");
        }
        // validate event title
        if (event.getTitle() == null
                || event.getTitle().equals("")
                || event.getTitle().length() > 100) {
            throw new ValidationException("Invalid title");
        }
        // validate notification ID
        if (event.getNotificationID() < 1) {
            throw new ValidationException("Invalid notification ID");
        }
    }
}
