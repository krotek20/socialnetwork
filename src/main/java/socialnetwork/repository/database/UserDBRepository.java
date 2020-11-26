package socialnetwork.repository.database;

import socialnetwork.domain.enums.Gender;
import socialnetwork.domain.enums.Role;
import socialnetwork.domain.entities.User;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryException;

import java.sql.*;
import java.time.LocalDate;

public class UserDBRepository extends AbstractDBRepository<Long, User> implements Repository<Long, User> {

    public UserDBRepository(String url, String username, String password, Validator<User> validator) {
        super(validator, username, password, url);
    }

    @Override
    public User extractEntity(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("ID_USER");
        String firstName = resultSet.getString("FIRST_NAME");
        String lastName = resultSet.getString("LAST_NAME");
        String email = resultSet.getString("EMAIL");
        String password = resultSet.getString("PASSWORD");
        LocalDate birthDate = resultSet.getDate("BIRTH_DATE").toLocalDate();
        Gender gender = Gender.valueOf(resultSet.getString("GENDER"));
        Role role = Role.valueOf(resultSet.getString("ROLE"));

        User user = new User(firstName, lastName, email, password, birthDate, gender);
        user.setID(id);
        user.setCount(id);
        user.setRole(role);

        return user;
    }

    @Override
    public User findOne(Long id) throws RepositoryException {
        if (id == null) {
            throw new RepositoryException("ID must be not null");
        }
        return super.findOne(String.format("select * from \"USERS\" where \"ID_USER\" = %d", id));
    }

    @Override
    public Iterable<User> findAll() {
        return super.findAll("select * from \"USERS\"");
    }

    @Override
    public User save(User user) throws ValidationException {
        if (findOne(user.getID()) != null) {
            throw new RepositoryException("User already exists");
        }
        return super.save(user, String.format("insert into \"USERS\" " +
                        "values(%d, '%s', '%s', '%s', '%s', DATE('%s'), '%s', '%s')",
                user.getID(), user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getPassword(), user.getBirthDate(), user.getGender(), user.getRole()));
    }

    @Override
    public User delete(Long id) throws RepositoryException {
        User user = findOne(id);
        if (user == null) {
            throw new RepositoryException("ID does not exist");
        }
        super.delete(id, String.format("delete from \"USERS\" where \"ID_USER\" = %d", id));
        return user;
    }

    @Override
    public User update(User user) throws ValidationException {
        return null;
    }
}
