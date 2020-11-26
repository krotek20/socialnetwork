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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class MessageDBRepository extends AbstractDBRepository<Long, Message> implements Repository<Long, Message> {
    private final UserDBRepository userDBRepository;
    private final ChatDBRepository chatDBRepository;

    public MessageDBRepository(String url, String username, String password, Validator<Message> validator) {
        super(validator, username, password, url);
        this.userDBRepository = new UserDBRepository(url, username, password, new UserValidator());
        this.chatDBRepository = new ChatDBRepository(url, username, password, new ChatValidator());
    }

    @Override
    public Message extractEntity(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("ID_MESSAGE");
        long userID = resultSet.getLong("ID_USER");
        long chatID = resultSet.getLong("ID_CHAT");
        long notificationID = resultSet.getLong("ID_NOTIFICATION");
        String messageText = resultSet.getString("MESSAGE_TEXT");
        String replyText = resultSet.getString("REPLY_TEXT");
        LocalDateTime timestamp = resultSet.getTimestamp("MESSAGE_TIMESTAMP").toLocalDateTime();

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
