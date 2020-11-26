import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import repository.InMemoryRepositoryTest;
import service.ServiceFriendshipTest;
import service.ServiceUserTest;
import utils.GraphTest;
import validator.ValidatorFriendshipTest;
import validator.ValidatorUserTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ValidatorUserTest.class,
        ValidatorFriendshipTest.class,
        InMemoryRepositoryTest.class,
        ServiceFriendshipTest.class,
        ServiceUserTest.class,
        GraphTest.class
})
public class AllTests {

}
