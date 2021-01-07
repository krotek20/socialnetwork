package socialnetwork.repository.database;

import socialnetwork.domain.entities.Chat;
import socialnetwork.domain.entities.Message;
import socialnetwork.domain.entities.User;
import socialnetwork.domain.validators.ChatValidator;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

public class MessageDBRepository extends AbstractDBRepository<Long, Message> implements Repository<Long, Message> {
    private final UserDBRepository userDBRepository;
    private final ChatDBRepository chatDBRepository;

    public MessageDBRepository(Validator<Message> validator) {
        super(validator);
        this.userDBRepository = new UserDBRepository(new UserValidator());
        this.chatDBRepository = new ChatDBRepository(new ChatValidator());
    }

    @Override
    public Message extractEntity(Map<String, Object> resultSet) {
        long id = (long) resultSet.get("ID_MESSAGE");
        long userID = (long) resultSet.get("ID_USER");
        long chatID = (long) resultSet.get("ID_CHAT");
        long notificationID = (long) resultSet.get("ID_NOTIFICATION");
        String messageText = (String) resultSet.get("MESSAGE_TEXT");
        String replyText = (String) resultSet.get("REPLY_TEXT");
        LocalDateTime timestamp = ((Timestamp) resultSet.get("MESSAGE_TIMESTAMP")).toLocalDateTime();

        User from = userDBRepository.findOne(userID);
        Chat to = chatDBRepository.findOne(chatID);

        Message message = new Message(from, to, messageText, replyText, notificationID);
        message.setID(id);
        message.setCount(id);
        message.setTimestamp(timestamp);

        return message;
    }

    @Override
    public Message findOne(Long id) throws RepositoryException {
        if (id == null) {
            throw new RepositoryException("ID must be not null");
        }
        return super.findOne(String.format("select * from \"MESSAGES\" where \"ID_MESSAGE\" = %d", id));
    }

    @Override
    public Iterable<Message> findAll() {
        return super.findAll("select * from \"MESSAGES\"");
    }

    @Override
    public Iterable<Message> findPage(int limit, int offset) {
        return null;
    }

    @Override
    public Message save(Message message) throws ValidationException, RepositoryException {
        if (findOne(message.getID()) != null) {
            throw new RepositoryException("Message already exists");
        }
        return super.save(message, String.format("insert into \"MESSAGES\" " +
                        "values(%d, %d, %d, %d, '%s', '%s', '%s'::timestamp)",
                message.getID(), message.getFrom().getID(), message.getChat().getID(),
                message.getNotificationID(), message.getMessageText(),
                message.getReply(), message.getTimestamp()));
    }

    @Override
    public Message delete(Long id) throws RepositoryException {
        return null;
    }

    @Override
    public Message update(Message message) throws ValidationException {
        return null;
    }
}
