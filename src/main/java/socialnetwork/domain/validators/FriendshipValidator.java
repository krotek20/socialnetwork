package socialnetwork.domain.validators;

import socialnetwork.domain.entities.Friendship;

public class FriendshipValidator implements Validator<Friendship> {
    @Override
    public void validate(Friendship friendship) throws ValidationException {
        if (friendship == null) {
            throw new ValidationException("Entity not instance of Friendship");
        }
        if (friendship.getID().getLeft().equals(friendship.getID().getRight())) {
            throw new ValidationException("A user cannot be friend with himself");
        }
        // validate left entity
        if (friendship.getID().getLeft() == null) {
            throw new ValidationException("Left entity not instance of Entity");
        }
        // validate left entity
        if (friendship.getID().getRight() == null) {
            throw new ValidationException("Right entity not instance of Entity");
        }
        // validate notification ID
        if (friendship.getNotificationID() < 1) {
            throw new ValidationException("Invalid notification ID");
        }
    }
}
