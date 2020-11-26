package socialnetwork.service;

import socialnetwork.Utils.Parse;
import socialnetwork.domain.entities.Chat;
import socialnetwork.domain.entities.Message;
import socialnetwork.domain.entities.Notification;
import socialnetwork.domain.entities.User;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Message service class
 * Main message functionalities are implemented here
 */
public class MessageService extends NotificationService {
    private final Repository<Chat, Message> messageRepository;
    private final Repository<Long, Chat> chatRepository;
    private final Repository<Long, User> userRepository;

    public MessageService(Repository<Chat, Message> messageRepository,
                          Repository<Long, User> userRepository,
                          Repository<Long, Chat> chatRepository,
                          Repository<User, Notification> notificationRepository) {
        super(notificationRepository);
        this.messageRepository = messageRepository;
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    /**
     * Sending a message to a user.
     * It will automatically send a
     * notification of a new message to that user.
     *
     * @param from    the user who sends the message.
     * @param chatMap pair key-value map that holds the chat ID,
     *                the message to be sent and the message reply.
     * @return a new {@code Message} if the message was saved; otherwise null.
     * @throws ServiceException    if inserted data can't be parsed.
     * @throws RepositoryException if the user is not founded.
     * @throws ValidationException if {@code Message} data are not valid.
     */
    public Message sendMessage(User from, Map<String, String> chatMap)
            throws ServiceException, RepositoryException, ValidationException {
        String chatID = chatMap.get("to");
        String messageText = chatMap.get("message");
        String replyText = chatMap.get("reply");

        Chat to = chatRepository.findOne(Parse.safeParseLong(chatID));

        Message message = new Message(from, to, messageText, replyText);
        Message result = messageRepository.save(message);
        for (User toUser : to.getUsers()) {
            if (!toUser.equals(from)) {
                super.createNotification(toUser, from, message, null, "New message!");
            }
        }
        return result;
    }

    /**
     * Read all messages sent by a user.
     *
     * @param from   the user who wants to reads his messages.
     * @param chatID string representing the ID of the chat
     *               user {@code from} wants to read messages from.
     * @return a list of strings representing the {@code from} user messages.
     */
    public List<String> readAllMessages(User from, String chatID) {
        Chat to = chatRepository.findOne(Parse.safeParseLong(chatID));
        if (!to.getUsers().contains(from)) {
            throw new ServiceException("Access denied!");
        }
        List<String> list = StreamSupport.stream(messageRepository.findAll().spliterator(), false)
                .filter(x -> x.getID().equals(to))
                .map(Message::toString)
                .collect(Collectors.toList());
        return list.size() != 0 ? list : new ArrayList<>(Collections.singletonList("The list of messages is empty"));
    }
}
