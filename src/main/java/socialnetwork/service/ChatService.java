package socialnetwork.service;

import socialnetwork.Utils.Parse;
import socialnetwork.domain.entities.Chat;
import socialnetwork.domain.entities.Notification;
import socialnetwork.domain.entities.User;
import socialnetwork.domain.enums.NotificationType;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Chat service class
 * Main chat functionalities are implemented here.
 */
public class ChatService extends NotificationService {
    private final Repository<Long, Chat> chatRepository;
    private final Repository<Long, User> userRepository;

    public ChatService(Repository<Long, Chat> chatRepository,
                       Repository<Long, User> userRepository,
                       Repository<Long, Notification> notificationRepository) {
        super(notificationRepository);
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new chat from the input data.
     *
     * @param ids        key-value pairs corresponding to the user ids.
     * @param title      string title of the chat.
     * @param loggedUser user instance of the currently logged user.
     * @return true if saved; false otherwise.
     * @throws RepositoryException when there is no user with the input id in user repository.
     * @throws ValidationException when at least one value is invalid.
     * @throws ServiceException    when input user ids are not numbers.
     */
    public boolean createChat(String title, Map<String, String> ids, User loggedUser)
            throws ValidationException, RepositoryException, ServiceException {
        Set<User> users = new HashSet<>();
        for (Map.Entry<String, String> userID : ids.entrySet()) {
            User user = userRepository.findOne(Parse.safeParseLong(userID.getValue()));
            if (user == null) {
                throw new RepositoryException("User not found!");
            }
            users.add(user);
        }

        Notification notification = super.createNotification(
                users, loggedUser, String.format("%s %s added you in a new chat",
                        loggedUser.getFirstName(), loggedUser.getLastName()),
                NotificationType.CHAT, "New chat!", LocalDateTime.now());

        Chat chat = new Chat(title, notification.getID());
        chat.setCount(chat.getID());
        users.add(loggedUser);
        chat.setUsers(users);
        return chatRepository.save(chat) == null;
    }

    /**
     * Reads all chats of the {@code loggedUser};
     *
     * @param loggedUser user instance currently logged user.
     * @return list of required chats informational data
     * or a list with a single string "The list of chats is empty"
     * if the logged user doesn't have any chat.
     */
    public List<String> readAllChats(User loggedUser) {
        List<String> list = StreamSupport.stream(chatRepository.findAll().spliterator(), false)
                .filter(x -> x.getUsers()
                        .stream()
                        .anyMatch(y -> y.getID().equals(loggedUser.getID())))
                .map(x -> String.format("%d. %s", x.getID(), x.getTitle()))
                .collect(Collectors.toList());
        return list.size() != 0 ? list : new ArrayList<>(Collections.singletonList("The list of chats is empty"));
    }
}
