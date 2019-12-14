package by.bsuir.exchange.validator;

import by.bsuir.exchange.bean.UserBean;
import by.bsuir.exchange.entity.RoleEnum;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserValidator {
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}" +
                                                "\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";

    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=\\S+$).{8,16}$";

    public static boolean validate(UserBean userBean){
        if (userBean.getEmail() == null){
            return false;
        }
        if (userBean.getPassword() == null){
            return false;
        }
        if (userBean.getRole() == null){
            return false;
        }
        String email = userBean.getEmail();
        String password = userBean.getPassword();
        String role = userBean.getRole();
        return validateEmail(email) && validatePassword(password) && validateRole(role);
    }

    private static boolean validateEmail(String email){
        Pattern p = Pattern.compile(EMAIL_PATTERN);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    private static boolean validatePassword(String password){
        Pattern p = Pattern.compile(PASSWORD_PATTERN);
        Matcher m = p.matcher(password);
        return m.matches();
    }

    private static boolean validateRole(String roleString){
        boolean status;
        try {
            RoleEnum role = RoleEnum.valueOf(roleString.toUpperCase());
            status = role != RoleEnum.ADMIN;
        }catch (IllegalArgumentException e){
            status = false;
        }
        return status;
    }
}
