package by.bsuir.exchange.validator;

import by.bsuir.exchange.bean.WalletBean;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class WalletValidatorTest {

    @DataProvider
    public Object[][] validateWalletData() {
        return new Object[][]{
                {new WalletBean(1,  100.17), true},
                {new WalletBean(1,  0), true},
                {new WalletBean(1,  -7), false},
        };
    }

    @Test(dataProvider = "validateWalletData")
    public void validateWalletTest(WalletBean wallet, boolean expected) {
        boolean actual = WalletValidator.validate(wallet);
        Assert.assertEquals(actual, expected);
    }
}
