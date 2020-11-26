package validator;

import org.junit.Before;
import org.junit.Test;
import socialnetwork.domain.entities.Friendship;
import socialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;

public class ValidatorFriendshipTest {
    private Validator<Friendship> validatorTest;

    @Before
    public void setUp() {
        validatorTest = new FriendshipValidator();
    }

    @Test(expected = ValidationException.class)
    public void validateFail() {
        validatorTest.validate(null);
        validatorTest.validate(new Friendship(null, 1L));
        validatorTest.validate(new Friendship(1L, null));
        validatorTest.validate(new Friendship(1L, 1L));
    }

    @Test
    public void validate() {
        validatorTest.validate(new Friendship(1L, 2L));
    }
}
