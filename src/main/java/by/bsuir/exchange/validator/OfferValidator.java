package by.bsuir.exchange.validator;

import by.bsuir.exchange.bean.OfferBean;

public class OfferValidator {
    public static boolean validate(OfferBean offerBean){
        String transport = offerBean.getTransport();
        double price = offerBean.getPrice();
        return validateTransport(transport) && validatePrice(price);
    }

    private static boolean validateTransport(String transport){
        return transport != null && !transport.isEmpty();
    }

    private static boolean validatePrice(double price){
        return  price > 0 && price < 1_000_000;
    }
}
