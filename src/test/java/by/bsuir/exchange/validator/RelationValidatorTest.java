package by.bsuir.exchange.validator;

import by.bsuir.exchange.bean.RelationBean;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class RelationValidatorTest {
    @DataProvider
    public Object[][] validateRelationData() {
        return new Object[][]{
                {new RelationBean(1, 1, 4, "LIKE"), true},
                {new RelationBean(1, 1, 4, "NONE"), true},
                {new RelationBean(1, 1, 4, "like"), true},
                {new RelationBean(1, 1, 4, ""), false},
                {new RelationBean(1, 1, -5, "LIKE"), false},
                {new RelationBean(1, 1, 4, "LIKE1"), false},
        };
    }

    @Test(dataProvider = "validateRelationData")
    public void validateRelationTest(RelationBean relation, boolean expected) {
        boolean actual = RelationValidator.validate(relation);
        Assert.assertEquals(actual, expected);
    }
}
