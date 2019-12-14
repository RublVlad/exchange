package by.bsuir.exchange.validator;

import by.bsuir.exchange.bean.UserBean;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class UserValidatorTest {
    @DataProvider
    public Object[][] validateUserData() {
        return new Object[][]{
                {new UserBean(1, "john@gmail.com", "john1234", "courier", false), true},
                {new UserBean(1, "john@gmail.com", "star1234", "CliEnt", false), true},
                {new UserBean(1, "john@gmail.com", "Capital1234", "CLIENT", false), true},
                {new UserBean(1, "john@gmail.com", "star1234", "CliEnt", false), true},
                {new UserBean(1, "john@gmail.com", "Capital1234", "CLIENT", false), true},
                {new UserBean(1, "john@gmail.com", "john", "client", false), false},
                {new UserBean(1, "никита@gmail.com", "Capital1234", "CLIENT", false), false},
                {new UserBean(1, "john.com", "john1234", "courier", false), false},
                {new UserBean(1, "john@gmail", "john1234", "courier", false), false},
                {new UserBean(1, "john@gmail.com", "john123", "courier", false), false},
                {new UserBean(1, "john@gmail.com", "john", "", false), false},
                {new UserBean(1, "john@gmail.com", "john", "client1", false), false},
        };
    }

    @Test(dataProvider = "validateUserData")
    public void validateActorTest(UserBean user, boolean expected) {
        boolean actual = UserValidator.validate(user);
        Assert.assertEquals(actual, expected);
    }
}
