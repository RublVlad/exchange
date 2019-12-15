package by.bsuir.exchange.validator;

import by.bsuir.exchange.bean.ActorBean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActorValidator {
    private static final String NAME_PATTERN = "^\\p{Lu}\\p{Ll}+$";

    public static boolean validate(ActorBean actorBean){
        if (actorBean.getName() == null || actorBean.getName().isEmpty()){
            return false;
        }
        if (actorBean.getSurname() == null || actorBean.getSurname().isEmpty()){
            return false;
        }
        String name = actorBean.getName();
        String surname = actorBean.getSurname();
        long likes = actorBean.getLikes();
        return validateName(name) && validateSurname(surname) && validateLikes(likes);
    }

    private static boolean validateName(String name){
        Pattern p = Pattern.compile(NAME_PATTERN);
        Matcher m = p.matcher(name);
        return m.matches();
    }

    private static boolean validateSurname(String surname){
        Pattern p = Pattern.compile(NAME_PATTERN);
        Matcher m = p.matcher(surname);
        return m.matches();
    }

    private static boolean validateLikes(long likes){
        return likes >= 0;
    }

    private static boolean validateBalance(double balance){
        return balance >= 0 && balance <= 1_000_000;
    }
}
