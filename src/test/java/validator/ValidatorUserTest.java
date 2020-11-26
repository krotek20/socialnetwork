package validator;

import org.junit.Before;
import org.junit.Test;
import socialnetwork.domain.enums.Gender;
import socialnetwork.domain.entities.User;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ValidatorUserTest {
    private static final int CURRENT_YEAR = LocalDate.now().getYear();
    private Validator<User> validatorTest;
    private List<User> dummyDataInvalid;
    private List<User> dummyDataValid;


    @Before
    public void setUp() {
        validatorTest = new UserValidator();
        dummyDataInvalid = new ArrayList<>();
        dummyDataValid = new ArrayList<>();

        // BEGIN DUMMY DATA

        // invalid (malformed) users
        // -- null User
        dummyDataInvalid.add(null);
        dummyDataInvalid.add(new User("", "", "", "", null, null));
        // -- invalid firstname users
        dummyDataInvalid.add(new User("name", "",
                "", "", null, null));
        dummyDataInvalid.add(new User("Name-user", "",
                "", "", null, null));
        dummyDataInvalid.add(new User("Name1", "",
                "", "", null, null));
        // -- invalid lastname users
        dummyDataInvalid.add(new User("Valid", "name",
                "", "", null, null));
        dummyDataInvalid.add(new User("Valid", "Name user",
                "", "", null, null));
        dummyDataInvalid.add(new User("Valid", "Name1",
                "", "", null, null));
        // -- invalid email users
        dummyDataInvalid.add(new User("Valid", "Valid", "cast.c",
                "", null, null));
        dummyDataInvalid.add(new User("Valid", "Valid", ".ceva@yahoo.com",
                "", null, null));
        dummyDataInvalid.add(new User("Valid", "Valid", "ceva.@yahoo.com",
                "", null, null));
        dummyDataInvalid.add(new User("Valid", "Valid", "ceva.ceva@yahoo-.com",
                "", null, null));
        dummyDataInvalid.add(new User("Valid", "Valid", "ceva.ceva@yahoo-.com",
                "", null, null));
        dummyDataInvalid.add(new User("Valid", "Valid", "ceva.ceva@yahoo.co-uk",
                "", null, null));
        // -- invalid password users
        dummyDataInvalid.add(new User("Valid", "Valid", "valid.valid@yahoo.co.uk",
                "s", null, null));
        dummyDataInvalid.add(new User("Valid", "Valid", "valid.valid@yahoo.co.uk",
                "S2", null, null));
        dummyDataInvalid.add(new User("Valid", "Valid", "valid.valid@yahoo.co.uk",
                "Servicies", null, null));
        dummyDataInvalid.add(new User("Valid", "Valid", "valid.valid@yahoo.co.uk",
                "servicies222", null, null));
        // -- invalid birthdate users
        dummyDataInvalid.add(new User("Valid", "Valid", "valid.valid@yahoo.co.uk",
                "Valid222", LocalDate.of(1919, 1, 1), null));
        dummyDataInvalid.add(new User("Valid", "Valid", "valid.valid@yahoo.co.uk",
                "Valid222", LocalDate.of(CURRENT_YEAR, 1, 1), null));
        // valid users
        dummyDataValid.add(new User("Valid", "Valid", "valid.valid@yahoo.co.uk",
                "Valid222", LocalDate.of(2000, 10, 15), Gender.FEMALE));
        dummyDataValid.add(new User("Valid-User", "Valid User", "valid-valid-valid@yahoo.co.uk",
                "VaLiDdDdD2@2!2$", LocalDate.of(1995, 5, 10), Gender.MALE));

        // END DUMMY DATA
    }

    @Test(expected = ValidationException.class)
    public void validateFail() {
        for (User user : dummyDataInvalid) {
            validatorTest.validate(user);
        }
    }

    @Test
    public void validate() {
        for (User user : dummyDataValid) {
            validatorTest.validate(user);
        }
    }
}
