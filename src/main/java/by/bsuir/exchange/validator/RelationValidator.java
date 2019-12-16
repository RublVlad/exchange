package by.bsuir.exchange.validator;

import by.bsuir.exchange.bean.DeliveryBean;
import by.bsuir.exchange.bean.RelationBean;
import by.bsuir.exchange.entity.RelationEnum;
import by.bsuir.exchange.entity.RoleEnum;

public class RelationValidator {

    public static boolean validate(RelationBean relation){
        long courierId = relation.getCourierId();
        String relationString = relation.getRelation();
        return validateId(courierId) && validateRelation(relationString);
    }

    private static boolean validateId(long id) {
        return id > 0;
    }

    private static boolean validateRelation(String relationString){
        boolean status;
        try {
            RelationEnum.valueOf(relationString.toUpperCase());
            status = true;
        }catch (IllegalArgumentException e){
            status = false;
        }
        return status;
    }
}
