package socialnetwork.repository.file;

import socialnetwork.domain.Tuple;
import socialnetwork.domain.entities.*;
import socialnetwork.domain.validators.Validator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NotificationFileRepository extends AbstractFileRepository<User, Notification> {
    /**
     * Constructor
     * Creates the repository file if it doesn't exist, or reuses it
     * Calls loadData to read the file contents
     * Observes itself to writeToFile after every successful CRUD operation from base class
     *
     * @param validator entity validator
     */
    public NotificationFileRepository(Validator<Notification> validator) {
        super("data/notifications.csv", validator);
    }

    private Tuple<User, User> findFromTo(List<User> users, String toID, String fromID) {
        User from = null, to = null;
        for (User user : users) {
            if (user.getID().toString().equals(toID)) {
                to = user;
            }
            if (user.getID().toString().equals(fromID)) {
                from = user;
            }
        }
        return new Tuple<>(from, to);
    }

    @Override
    public Notification extractEntity(List<String> attributes) {
        Notification notification;
        List<User> users = super.loadUsers();

        Tuple<User, User> fromTo = findFromTo(users, attributes.get(0), attributes.get(1));
        User from = fromTo.getLeft();
        User to = fromTo.getRight();

        if (attributes.size() < 4) {
            // friendship notification
            assert from != null;
            assert to != null;
            Friendship friendship = new Friendship(from.getID(), to.getID());
            notification = new Notification(to, from, friendship, attributes.get(2));
        } else {
            // message notification
            Set<User> chatUsers = new HashSet<>();
            String[] chatUserIDs = attributes.get(3)
                    .replace("[", "")
                    .replace("]", "")
                    .split(", ");
            for (String id : chatUserIDs) {
                for (User user : users) {
                    if (user.getID().toString().equals(id)) {
                        chatUsers.add(user);
                    }
                }
            }
            Chat chat = new Chat(attributes.get(2));
            chat.setUsers(chatUsers);
            Message message = new Message(from, chat, attributes.get(5), attributes.get(6));
            message.setTimestamp(LocalDateTime.parse(attributes.get(4)));
            notification = new Notification(to, from, message, attributes.get(7));
        }
        return notification;
    }

    @Override
    protected String createEntityAsString(Notification notification) {
        return notification.toString();
    }
}
