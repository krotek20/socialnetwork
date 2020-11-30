package socialnetwork.repository.file;

import socialnetwork.Utils.Parse;
import socialnetwork.domain.entities.Chat;
import socialnetwork.domain.entities.User;
import socialnetwork.domain.validators.Validator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatFileRepository extends AbstractFileRepository<Long, Chat> {
    /**
     * Constructor
     * Creates the repository file if it doesn't exist, or reuses it
     * Calls loadData to read the file contents
     * Observes itself to writeToFile after every successful CRUD operation from base class
     *
     * @param validator entity validator
     */
    public ChatFileRepository(Validator<Chat> validator) {
        super("data/chats.csv", validator);
    }

    @Override
    public Chat extractEntity(List<String> attributes) {
        String id = attributes.get(0);
        String name = attributes.get(1);

        List<User> users = super.loadUsers();
        Set<User> chatUsers = new HashSet<>();
        String[] chatUserIDs = attributes.get(2)
                .replace("[", "")
                .replace("]", "")
                .split(", ");
        for (String cid : chatUserIDs) {
            for (User user : users) {
                if (user.getID().toString().equals(cid)) {
                    chatUsers.add(user);
                }
            }
        }
        Chat chat = new Chat(name, 0);
        chat.setID(Parse.safeParseLong(id));
        chat.setCount(Parse.safeParseLong(id));
        chat.setUsers(chatUsers);

        return chat;
    }

    @Override
    protected String createEntityAsString(Chat chat) {
        return chat.toString();
    }
}
