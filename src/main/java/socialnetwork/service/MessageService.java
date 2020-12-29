package socialnetwork.service;

import socialnetwork.Utils.Constants;
import socialnetwork.Utils.Parse;
import socialnetwork.Utils.design.NotifyStatus;
import socialnetwork.Utils.design.Observable;
import socialnetwork.domain.entities.*;
import socialnetwork.domain.enums.NotificationType;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryException;
import socialnetwork.ui.gui.controllers.LoginController;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Message service class
 * Main message functionalities are implemented here
 */
public class MessageService extends Observable {
    private final Repository<Long, Message> messageRepository;
    private final Repository<Long, Chat> chatRepository;

    public MessageService(Repository<Long, Message> messageRepository,
                          Repository<Long, Chat> chatRepository) {
        this.messageRepository = messageRepository;
        this.chatRepository = chatRepository;
    }

    /**
     * Sending a message to a chat.
     * It will automatically send a notification
     * of a new message to the users in that chat.
     *
     * @param from    the user who sends the message.
     * @param chatMap pair key-value map that holds the chat ID,
     *                the message to be sent and the message reply.
     * @return the new message sent.
     * @throws ServiceException    if inserted data can't be parsed.
     * @throws RepositoryException if the chat is not found.
     * @throws ValidationException if {@code Message} data are not valid.
     */
    public Message sendMessage(User from, Map<String, String> chatMap)
            throws ServiceException, RepositoryException, ValidationException {
        String chatID = chatMap.get("to");
        String messageText = chatMap.get("message");
        String replyText = chatMap.get("reply");

        Chat to = chatRepository.findOne(Parse.safeParseLong(chatID));
        if (to == null) {
            throw new RepositoryException("Chat not found!");
        }
        Set<User> users = to.getUsers()
                .stream()
                .filter(x -> !x.getID().equals(from.getID()))
                .collect(Collectors.toSet());

        LocalDateTime currentTime = LocalDateTime.now();
        Notification notification = NotificationService.createNotification(users, from, String.format(
                "[%s] %s %s: %s", currentTime.format(Constants.DATE_TIME_FORMATTER),
                from.getFirstName(), from.getLastName(), messageText),
                NotificationType.MESSAGE, "New message!", LocalDateTime.now());

        Message message = new Message(from, to, messageText.trim(), replyText, notification.getID());
        message.setCount(message.getID());
        if (messageRepository.save(message) == null) {
            setChanged();
            notifyObservers(NotifyStatus.MESSAGE);
            return message;
        }
        return null;
    }

    /**
     * Read all messages sent in a chat sorted by the timestamp.
     *
     * @param from   the user who wants to read the messages.
     * @param chatID string representing the ID of the chat
     *               user {@code from} wants to read messages from.
     * @return a list of all messages in the specified chat.
     * @throws RepositoryException if the chat was not found.
     * @throws ServiceException    if the user is not in the requested chat.
     */
    public List<Message> readAllMessagesFromChat(User from, String chatID) throws ServiceException, RepositoryException {
        Chat to = chatRepository.findOne(Parse.safeParseLong(chatID));
        if (to == null) {
            throw new RepositoryException("Chat not found!");
        }
        if (to.getUsers().stream().noneMatch(y -> y.getID().equals(from.getID()))) {
            throw new ServiceException("Access denied!");
        }
        return StreamSupport
                .stream(messageRepository.findAll().spliterator(), false)
                .filter(x -> x.getChat().equals(to))
                .sorted(Comparator.comparing(Message::getTimestamp))
                .collect(Collectors.toList());
    }

    /**
     * Reads all messages of a user.
     *
     * @param from the user who wants to read the messages.
     * @return a list of all messages of this user.
     */
    public List<Message> readAllMessages(User from) {
        List<Chat> chats = StreamSupport.stream(chatRepository.findAll().spliterator(), false)
                .filter(x -> x.getUsers()
                        .stream()
                        .anyMatch(y -> y.getID().equals(from.getID())))
                .collect(Collectors.toList());
        List<Message> allMessages = new ArrayList<>();
        for (Chat chat : chats) {
            List<Message> currentChatMessages =
                    readAllMessagesFromChat(LoginController.loggedUser, chat.getID().toString());
            allMessages = Stream.of(allMessages, currentChatMessages)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        }
        return allMessages;
    }

    /**
     * Reads the message with the given notification ID.
     *
     * @param notificationID ID of notification.
     * @return instance of message found or null if there is
     * no message with the specified notification ID.
     */
    public Message readMessageByNotification(long notificationID) {
        Iterable<Message> messages = messageRepository.findAll();
        for (Message message : messages) {
            if (message.getNotificationID() == notificationID) {
                return message;
            }
        }
        return null;
    }
}
