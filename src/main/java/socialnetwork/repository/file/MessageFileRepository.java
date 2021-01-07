package socialnetwork.repository.file;

import socialnetwork.domain.entities.Chat;
import socialnetwork.domain.entities.Message;
import socialnetwork.domain.entities.User;
import socialnetwork.domain.validators.Validator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MessageFileRepository extends AbstractFileRepository<Long, Message> {
    /**
     * Constructor
     * Creates the repository file if it doesn't exist, or reuses it
     * Calls loadData to read the file contents
     * Observes itself to writeToFile after every successful CRUD operation from base class
     *
     * @param validator entity validator
     */
    public MessageFileRepository(Validator<Message> validator) {
        super("data/messages.csv", validator);
    }

    @Override
    public Message extractEntity(List<String> attributes) {
        String fromText = attributes.get(0);
        String toName = attributes.get(1);
        String toUsers = attributes.get(2);
        String timestamp = attributes.get(3);
        String messageText = attributes.get(4);
        String reply = attributes.get(5);

        List<User> users = super.loadUsers();
        User from = null;
        for (User user : users) {
            if (user.getID().toString().equals(fromText)) {
                from = user;
            }
        }
        Set<User> chatUsers = new HashSet<>();
        String[] chatUserIDs = toUsers
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
        Chat chat = new Chat(toName, 0);
        chat.setUsers(chatUsers);

        Message message = new Message(from, chat, messageText, reply, 0);
        message.setTimestamp(LocalDateTime.parse(timestamp));
        return message;
    }

    @Override
    protected String createEntityAsString(Message message) {
        return message.toString();
    }

    @Override
    public Iterable<Message> findPage(int limit, int offset) {
        return null;
    }
}
