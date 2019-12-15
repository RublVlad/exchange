package by.bsuir.exchange.validator;

import by.bsuir.exchange.bean.WalletBean;

public class WalletValidator {

    public static boolean validate(WalletBean bean) {
        double balance = bean.getBalance();
        return balance >= 0 && balance <= 1_000_000;
    }
}
