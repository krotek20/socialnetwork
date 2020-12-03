package service;

import org.junit.Before;
import org.junit.Test;
import socialnetwork.domain.entities.Chat;
import socialnetwork.domain.entities.Friendship;
import socialnetwork.domain.entities.Notification;
import socialnetwork.domain.enums.Gender;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.entities.User;
import socialnetwork.domain.validators.*;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryException;
import socialnetwork.repository.memory.InMemoryRepository;
import socialnetwork.service.FriendshipService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ServiceFriendshipTest {
    private FriendshipService friendshipServiceTest;
    private Map<String, String> friendshipMapInvalid;
    private Map<String, String> friendshipMapValid;

    @Before
    public void setUp() {
        Validator<Friendship> friendshipValidatorTest = new FriendshipValidator();
        Validator<User> userValidatorTest = new UserValidator();
        Validator<Chat> chatValidatorTest = new ChatValidator();
        Validator<Notification> notificationValidatorTest = new NotificationValidator();

        Repository<Tuple<Long, Long>, Friendship> friendshipRepositoryTest = new InMemoryRepository<>(friendshipValidatorTest);
        Repository<Long, User> userRepositoryTest = new InMemoryRepository<>(userValidatorTest);
        Repository<Long, Chat> chatRepositoryTest = new InMemoryRepository<>(chatValidatorTest);

        User ghostUser = new User("", "", "", "", null, null);
        ghostUser.resetCount();

        User user = new User("Test", "Test", "test@yahoo.com",
                "TestTest999", LocalDate.of(2000, 1, 1), Gender.UNKNOWN);
        user.setCount(user.getID());
        userRepositoryTest.save(user);
        userRepositoryTest.save(new User("Test", "Test", "test@yahoo.com",
                "TestTest999", LocalDate.of(2000, 1, 1), Gender.UNKNOWN));

        friendshipServiceTest = new FriendshipService(friendshipRepositoryTest, userRepositoryTest, chatRepositoryTest);

        friendshipMapInvalid = new HashMap<String, String>() {{
            put("id1", "");
            put("id2", null);
        }};

        friendshipMapValid = new HashMap<String, String>() {{
            put("id1", "1");
            put("id2", "2");
        }};
    }

    @Test(expected = RepositoryException.class)
    public void createFriendshipFail() {
        friendshipServiceTest.addFriendship(friendshipMapInvalid);
    }

    @Test
    public void createFriendship() {
        friendshipServiceTest.addFriendship(friendshipMapValid);
    }
}
