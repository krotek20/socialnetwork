package socialnetwork.repository.file;

import socialnetwork.Utils.Parse;
import socialnetwork.domain.enums.Gender;
import socialnetwork.domain.enums.Role;
import socialnetwork.domain.entities.User;
import socialnetwork.domain.validators.Validator;

import java.time.LocalDate;
import java.util.List;

/**
 * File Repository for Users
 */
public class UserFileRepository extends AbstractFileRepository<Long, User> {
    /**
     * Constructor calling its superclass constructor {@code AbstractFileRepository}
     * using users.csv relative path
     *
     * @param validator friendship validator
     */
    public UserFileRepository(Validator<User> validator) {
        super("data/users.csv", validator);
    }

    public static User getUser(List<String> attributes) {
        String id = attributes.get(0);
        String firstName = attributes.get(1);
        String lastName = attributes.get(2);
        String email = attributes.get(3);
        String password = attributes.get(4);
        String birthDate = attributes.get(5);
        String gender = attributes.get(6);
        String role = attributes.get(7);

        User user = new User(firstName, lastName, email, password,
                Parse.safeParseLocalDate(birthDate), Gender.valueOf(gender));
        user.setRole(Role.valueOf(role));
        user.setID(Parse.safeParseLong(id));
        user.setCount(Parse.safeParseLong(id));

        return user;
    }

    @Override
    public User extractEntity(List<String> attributes) {
        return getUser(attributes);
    }

    @Override
    protected String createEntityAsString(User user) {
        return user.toString();
    }
}
