package by.bsuir.exchange.validator;

import by.bsuir.exchange.bean.PersonalDataBean;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class PersonalDataValidatorTest {

    @DataProvider
    public Object[][] validateTestData() {
        return new Object[][]{
                {new PersonalDataBean(1,  100, "Витебск"), true},
                {new PersonalDataBean(1,  54, "Los-Angeles"), true},
                {new PersonalDataBean(1,  54, "Brooklyn"), true},
                {new PersonalDataBean(1,  500, "Brooklyn"), false},
                {new PersonalDataBean(1,  0, "Минск"), false},
                {new PersonalDataBean(1,  -17, "Витебск"), false},
        };
    }

    @Test(dataProvider = "validateTestData")
    public void validateWalletTest(PersonalDataBean data, boolean expected) {
        boolean actual = PersonalDataValidator.validate(data);
        Assert.assertEquals(actual, expected);
    }
}
