package socialnetwork.repository.database;

import socialnetwork.domain.entities.Chat;
import socialnetwork.domain.entities.User;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Set;

public class ChatDBRepository extends AbstractDBRepository<Long, Chat> implements Repository<Long, Chat> {
    private final UserDBRepository userDBRepository;

    public ChatDBRepository(String url, String username, String password, Validator<Chat> validator) {
        super(validator, username, password, url);
        this.userDBRepository = new UserDBRepository(url, username, password, new UserValidator());
    }

    @Override
    public Chat extractEntity(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("ID_CHAT");
        long notificationID = resultSet.getLong("ID_NOTIFICATION");
        String title = resultSet.getString("TITLE");
        LocalDateTime dateTime = resultSet.getTimestamp("DATE").toLocalDateTime();

        Chat chat = new Chat(title, notificationID);
        chat.setID(id);
        chat.setCount(id);
        chat.setDate(dateTime);

        Set<Long> usersID = super.getUsersID(String.format(
                "select \"ID_USER\" from \"CHATS_USERS\" where \"ID_CHAT\" = %d", id));
        for (long userID : usersID) {
            chat.addUser(userDBRepository.findOne(userID));
        }

        return chat;
    }

    @Override
    public Chat findOne(Long id) throws RepositoryException {
        if (id == null) {
            throw new RepositoryException("ID must be not null");
        }
        return super.findOne(String.format("select * from \"CHATS\" where \"ID_CHAT\" = %d", id));
    }

    @Override
    public Iterable<Chat> findAll() {
        return super.findAll("select * from \"CHATS\"");
    }

    @Override
    public Chat save(Chat chat) throws ValidationException {
        if (findOne(chat.getID()) != null) {
            throw new RepositoryException("Chat already exists");
        }
        super.save(chat, String.format("insert into \"CHATS\" values(%d, %d, '%s', '%s'::timestamp)",
                chat.getID(), chat.getNotificationID(), chat.getTitle(), chat.getDate()));
        for (User user : chat.getUsers()) {
            super.save(chat, String.format("insert into \"CHATS_USERS\" values(%d, %d)",
                    chat.getID(), user.getID()));
        }
        return null;
    }

    @Override
    public Chat delete(Long id) throws RepositoryException {
        Chat chat = findOne(id);
        if (chat == null) {
            throw new RepositoryException("ID does not exist");
        }
        super.delete(id, String.format("delete from \"CHATS\" where \"ID_CHAT\" = %d", id));
        return chat;
    }

    @Override
    public Chat update(Chat entity) throws ValidationException {
        return null;
    }
}
