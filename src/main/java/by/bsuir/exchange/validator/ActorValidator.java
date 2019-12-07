package by.bsuir.exchange.validator;

import by.bsuir.exchange.bean.ActorBean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActorValidator {
    private static final String NAME_PATTERN = "^\\p{Lu}\\p{Ll}+$";

    public static boolean validate(ActorBean actorBean){
        boolean isValid = true;
         if (actorBean.getName() != null && !actorBean.getName().isEmpty()){
            isValid &= validateName(actorBean);
        }
        if (actorBean.getSurname() != null && !actorBean.getSurname().isEmpty()){
            isValid &= validateSurname(actorBean);
        }
        return isValid;
    }

    private static boolean validateName(ActorBean actorBean){
        String name = actorBean.getName();
        Pattern p = Pattern.compile(NAME_PATTERN);
        Matcher m = p.matcher(name);
        return m.matches();
    }

    private static boolean validateSurname(ActorBean actorBean){
        String surname = actorBean.getSurname();
        Pattern p = Pattern.compile(NAME_PATTERN);
        Matcher m = p.matcher(surname);
        return m.matches();
    }

}
