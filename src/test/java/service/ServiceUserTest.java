package service;

import org.junit.Before;
import org.junit.Test;
import socialnetwork.domain.entities.User;
import socialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.Repository;
import socialnetwork.repository.file.FriendshipFileRepository;
import socialnetwork.repository.memory.InMemoryRepository;
import socialnetwork.service.UserService;

import java.util.HashMap;
import java.util.Map;

public class ServiceUserTest {
    private UserService userServiceTest;
    private Map<String, String> userMapInvalid;
    private Map<String, String> userMapValid;

    @Before
    public void setUp() {
        Repository<Long, User> userRepositoryTest = new InMemoryRepository<>(new UserValidator());
        FriendshipFileRepository friendshipRepositoryTest = new FriendshipFileRepository(new FriendshipValidator());
        userServiceTest = new UserService(userRepositoryTest, friendshipRepositoryTest);

        userMapInvalid = new HashMap<String, String>() {{
            put("firstname", null);
            put("lastname", null);
            put("email", null);
            put("password", null);
            put("birthdate", "2000-02-02");
            put("gender", "FEMALE");
        }};

        userMapValid = new HashMap<String, String>() {{
            put("firstname", "Name");
            put("lastname", "Name");
            put("email", "email@yahoo.com");
            put("password", "Password123");
            put("birthdate", "2000-01-01");
            put("gender", "MALE");
        }};
    }

    @Test(expected = ValidationException.class)
    public void createUserFail() {
        userServiceTest.createUser(userMapInvalid);
    }

    @Test
    public void createUser() {
        userServiceTest.createUser(userMapValid);
    }
}
