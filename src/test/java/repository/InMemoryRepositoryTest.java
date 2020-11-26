package repository;

import org.junit.Before;
import org.junit.Test;
import socialnetwork.domain.enums.Gender;
import socialnetwork.domain.entities.User;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.RepositoryException;
import socialnetwork.repository.memory.InMemoryRepository;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class InMemoryRepositoryTest {
    private InMemoryRepository<Long, User> testInMemoryRepositoryUser;

    @Before
    public void setUp() {
        testInMemoryRepositoryUser = new InMemoryRepository<>(new UserValidator());
    }

    @Test(expected = RepositoryException.class)
    public void findOneFail() {
        testInMemoryRepositoryUser.findOne(null);
        testInMemoryRepositoryUser.findOne(1L);
    }

    @Test
    public void findOne() {
        User user1 = new User("User", "Name", "user1@yahoo.com",
                "UserName1", LocalDate.of(2001, 1, 1), Gender.FEMALE);
        user1.setCount(user1.getID());
        // id=1
        testInMemoryRepositoryUser.save(user1);
        assertNotNull(testInMemoryRepositoryUser.findOne(user1.getID()));
    }

    @Test
    public void findAll() {
        assertFalse(testInMemoryRepositoryUser.findAll().iterator().hasNext());
        User user2 = new User("User", "Name", "user2@yahoo.com",
                "UserName2", LocalDate.of(2002, 2, 2), Gender.MALE);
        user2.setCount(user2.getID());
        // id=2
        testInMemoryRepositoryUser.save(user2);
        assertTrue(testInMemoryRepositoryUser.findAll().iterator().hasNext());
    }

    @Test(expected = ValidationException.class)
    public void saveValidate() {
        testInMemoryRepositoryUser.save(null);
        // More validation tests in ValidatorUserTest/ValidatorFriendshipTest classes
    }

    @Test
    public void save() {
        User user3 = new User("User", "Name", "user3@yahoo.com",
                "UserName3", LocalDate.of(2003, 3, 3), Gender.UNKNOWN);
        user3.setCount(user3.getID());
        // id=3
        assertNull(testInMemoryRepositoryUser.save(user3));
        assertEquals(testInMemoryRepositoryUser.save(user3), user3);
    }

    @Test(expected = RepositoryException.class)
    public void deleteFail() {
        testInMemoryRepositoryUser.delete(null);
        testInMemoryRepositoryUser.delete(5L);
    }

    @Test
    public void delete() {
        User user4 = new User("User", "Name", "user4@yahoo.com",
                "UserName4", LocalDate.of(2004, 4, 4), Gender.FEMALE);
        user4.setCount(user4.getID());
        testInMemoryRepositoryUser.save(user4);
        assertNotNull(testInMemoryRepositoryUser.delete(user4.getID()));
    }
}
