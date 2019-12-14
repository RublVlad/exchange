package by.bsuir.exchange.validator;

import by.bsuir.exchange.bean.DeliveryBean;

public class DeliveryValidator {
    public static boolean validate(DeliveryBean delivery){
        long clientId = delivery.getClientId();
        long courierId = delivery.getCourierId();
        return validateId(clientId) && validateId(courierId);
    }

    private static boolean validateId(long id) {
        return id > 0;
    }
}
