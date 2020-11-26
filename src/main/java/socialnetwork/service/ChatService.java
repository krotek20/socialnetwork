package socialnetwork.service;

import socialnetwork.Utils.Parse;
import socialnetwork.domain.entities.Chat;
import socialnetwork.domain.entities.User;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryException;

import java.util.Map;

public class ChatService {
    private final Repository<Long, Chat> chatRepository;
    private final Repository<Long, User> userRepository;

    public ChatService(Repository<Long, Chat> chatRepository, Repository<Long, User> userRepository) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new chat from the input data.
     *
     * @param name string name of the chat.
     * @param ids  key-value pairs corresponding to the user ids.
     * @return true if saved; false otherwise.
     * @throws RepositoryException when there is no user with the input id in user repository.
     * @throws ValidationException when at least one value is invalid.
     * @throws ServiceException    when input user ids are not numbers.
     */
    public boolean createChat(String name, Map<String, String> ids)
            throws ValidationException, RepositoryException, ServiceException {
        // TODO: notify users of the new chat
        Chat chat = new Chat(name);
        chat.setCount(chat.getID());
        for (Map.Entry<String, String> userID : ids.entrySet()) {
            User user = userRepository.findOne(Parse.safeParseLong(userID.getValue()));
            chat.addUser(user);
        }
        return chatRepository.save(chat) == null;
    }
}
