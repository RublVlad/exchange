package by.bsuir.exchange.validator;

import by.bsuir.exchange.bean.OfferBean;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class OfferValidatorTest {

    @DataProvider
    public Object[][] validateOfferData() {
        return new Object[][]{
                {new OfferBean(1, "BMW", 100, 2, false), true},
                {new OfferBean(1, "Plane", 100, 2, false), true},
                {new OfferBean(1, "1_X", 100, 2, false), true},
                {new OfferBean(1, "lotus", 100, 2, false), true},
                {new OfferBean(1, "", 100, 2, false), false},
                {new OfferBean(1, "Car", 1_000_000_000, 5, false), false},
        };
    }

    @Test(dataProvider = "validateOfferData")
    public void validateOfferTest(OfferBean offer, boolean expected) {
        boolean actual = OfferValidator.validate(offer);
        Assert.assertEquals(actual, expected);
    }
}
