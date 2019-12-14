package by.bsuir.exchange.validator;

import by.bsuir.exchange.bean.ActorBean;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ActorValidatorTest {

    @DataProvider
    public Object[][] validateActorData() {
        return new Object[][]{
                {new ActorBean(1, "John", "Oslo", 100, 2, false), true},
                {new ActorBean(1, "Рома", "Пантеев", 0, 2, false), true},
                {new ActorBean(1, "Han", "Solo", 100, 2, true), true},
                {new ActorBean(1, "Hans", "Zimmer", 100, 2, false), true},
                {new ActorBean(1, "Michael", "Caine", 5222, 4, false), true},
                {new ActorBean(1, "Hans", "Zimmer", -100, 2, false), false},
                {new ActorBean(1, "Miccs1", "Ivleev", 100, 2, false), false},
                {new ActorBean(1, "john", "Oslo", 100, 2, false), false},
                {new ActorBean(1, "", "", 0, 2, false), false},
        };
    }

    @Test(dataProvider = "validateActorData")
    public void validateActorTest(ActorBean actor, boolean expected) {
        boolean actual = ActorValidator.validate(actor);
        Assert.assertEquals(actual, expected);
    }
}
