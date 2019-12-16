package by.bsuir.exchange.validator;

import by.bsuir.exchange.bean.PersonalDataBean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PersonalDataValidator {
    private static final String CITY_PATTERN = "^[\\p{Lu}\\p{Ll}]+(?:[\\s-][\\p{Lu}\\p{Ll}]+)*$";

    public static boolean validate(PersonalDataBean personalData){
        String city = personalData.getCity();
        long age = personalData.getAge();
        return validateCity(city) && validateAge(age);
    }

    private static boolean validateCity(String city){
        Pattern p = Pattern.compile(CITY_PATTERN);
        Matcher m = p.matcher(city);
        return m.matches();
    }

    private static boolean validateAge(long age){
        return  age > 0 && age < 200;
    }
}
