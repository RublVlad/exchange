package by.bsuir.exchange.validator;

import by.bsuir.exchange.bean.OfferBean;

public class OfferValidator {
    public static boolean validate(OfferBean offerBean){
        String transport = offerBean.getTransport();
        double price = offerBean.getPrice();
        long courierId = offerBean.getCourierId();
        return validateTransport(transport) && validatePrice(price) && validateId(courierId);
    }

    private static boolean validateTransport(String transport){
        return transport != null && !transport.isEmpty();
    }

    private static boolean validatePrice(double price){
        return  price > 0 && price < 1_000_000;
    }

    private static boolean validateId(long id){
        return id > 0;
    }
}
