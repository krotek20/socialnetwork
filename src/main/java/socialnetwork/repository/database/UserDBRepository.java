package socialnetwork.repository.database;

import socialnetwork.domain.enums.Gender;
import socialnetwork.domain.enums.Role;
import socialnetwork.domain.entities.User;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryException;

import java.time.LocalDate;
import java.sql.Date;
import java.util.Map;

public class UserDBRepository extends AbstractDBRepository<Long, User> implements Repository<Long, User> {

    public UserDBRepository(Validator<User> validator) {
        super(validator);
        findAll();
    }

    @Override
    public User extractEntity(Map<String, Object> resultSet) {
        long id = (long) resultSet.get("ID_USER");
        String firstName = (String) resultSet.get("FIRST_NAME");
        String lastName = (String) resultSet.get("LAST_NAME");
        String email = (String) resultSet.get("EMAIL");
        String password = (String) resultSet.get("PASSWORD");
        LocalDate birthDate = ((Date) resultSet.get("BIRTH_DATE")).toLocalDate();
        Gender gender = Gender.valueOf((String) resultSet.get("GENDER"));
        Role role = Role.valueOf((String) resultSet.get("ROLE"));

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
    public Iterable<User> findPage(int limit, int offset) {
        return super.findAll(String.format(
                "select * from \"USERS\" order by \"ID_USER\" limit %d offset %d",
                limit, offset));
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
