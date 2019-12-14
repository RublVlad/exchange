package by.bsuir.exchange.validator;

import by.bsuir.exchange.bean.ImageBean;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ImageValidatorTest {
    @DataProvider
    public Object[][] validateImageData() {
        return new Object[][]{
                {new ImageBean(1, "courier", 1, "logo.jpg", false), true},
                {new ImageBean(1, "client", 1, "logo.jpg", false), true},
                {new ImageBean(1, "courier", 1, "summer.jpeg", false), true},
                {new ImageBean(1, "courier", 1, "AVATAR.jpg", false), true},
                {new ImageBean(1, "courier", 1, "logo.png", false), true},
                {new ImageBean(1, "courier", -5, "logo.jpg", false), false},
                {new ImageBean(1, "", 1, "logo.jpg", false), false},
                {new ImageBean(1, "courier", 1, "", false), false},
                {new ImageBean(1, "courier", 1, "logo.", false), false},
                {new ImageBean(1, "courier", 1, ".jpg", false), false},
                {new ImageBean(1, "courier", 1, "logo", false), false},
                {new ImageBean(1, "courier", 1, "/logo.jpg", false), false},
                {new ImageBean(1, "courier", 1, "..png", false), false},
                {new ImageBean(1, "courier", 1, "logo.jpg.jpg", false), false},

        };
    }

    @Test(dataProvider = "validateImageData")
    public void validateActorTest(ImageBean image, boolean expected) {
        boolean actual = ImageValidator.validate(image);
        Assert.assertEquals(actual, expected);
    }
}
