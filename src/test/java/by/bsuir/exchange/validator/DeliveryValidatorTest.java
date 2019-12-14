package by.bsuir.exchange.validator;

import by.bsuir.exchange.bean.DeliveryBean;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class DeliveryValidatorTest {
    @DataProvider
    public Object[][] validateDeliveryData() {
        return new Object[][]{
                {new DeliveryBean(1, 1, true, 2, false, false), true},
                {new DeliveryBean(1, 0, true, 2, false, false), false},
                {new DeliveryBean(1, -5, true, 2, false, false), false},
                {new DeliveryBean(1, 1, true, 0, false, false), false},
                {new DeliveryBean(1, 1, true, -7, false, false), false},
        };
    }

    @Test(dataProvider = "validateDeliveryData")
    public void validateActorTest(DeliveryBean delivery, boolean expected) {
        boolean actual = DeliveryValidator.validate(delivery);
        Assert.assertEquals(actual, expected);
    }
}
