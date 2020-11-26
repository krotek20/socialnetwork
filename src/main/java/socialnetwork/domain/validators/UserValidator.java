package socialnetwork.domain.validators;

import socialnetwork.domain.enums.Gender;
import socialnetwork.domain.entities.User;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User user) throws ValidationException {
        if (user == null) {
            throw new ValidationException("Entity not instance of User");
        }
        // validate first name
        if (user.getFirstName() == null
                || !user.getFirstName().matches("^[A-Z][a-z]+([ -][A-Z][a-z]+)*$")) {
            throw new ValidationException("First name is invalid (null or malformed)");
        }
        // validate last name
        if (user.getLastName() == null
                || !user.getLastName().matches("^[A-Z][a-z]+([ -][A-Z][a-z]+)*$")) {
            throw new ValidationException("Last name is invalid (null or malformed)");
        }
        // validate email
        if (user.getEmail() == null
                || !user.getEmail().matches("^\\w{1,32}([.-]\\w+)*@\\w+([-]\\w+)*([.]\\w+)+$")) {
            throw new ValidationException("Email is invalid (null or malformed)");
        }
        // validate password
        if (user.getPassword() == null
                || user.getPassword().length() < 8
                || !user.getPassword().matches("^.*[A-Z]+.*$")
                || !user.getPassword().matches("^.*[0-9]+.*$")) {
            throw new ValidationException("Password is invalid (null or malformed)");
        }
        // validate birthdate
        List<String> userBirthdate = Arrays.asList(user.getBirthDate().toString().split("-"));
        if (user.getBirthDate() == null
                || userBirthdate.size() != 3
                || Integer.parseInt(userBirthdate.get(0)) < 1920
                || Integer.parseInt(userBirthdate.get(0)) >= LocalDate.now().getYear()
                || Integer.parseInt(userBirthdate.get(1)) < 1
                || Integer.parseInt(userBirthdate.get(1)) > 12
                || Integer.parseInt(userBirthdate.get(2)) < 1
                || Integer.parseInt(userBirthdate.get(2)) > 31) {
            throw new ValidationException("Birthdate is invalid (null or malformed)");
        }
        // validate gender
        HashSet<String> genderEnum = Gender.getEnum();
        if (user.getGender() == null
                || !genderEnum.contains(user.getGender().toString())) {
            throw new ValidationException("Gender is invalid (null or not allowed). Please choose from MALE / FEMALE / UNKNOWN");
        }
    }
}
