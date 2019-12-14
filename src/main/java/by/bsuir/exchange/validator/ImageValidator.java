package by.bsuir.exchange.validator;

import by.bsuir.exchange.bean.ImageBean;
import by.bsuir.exchange.entity.RoleEnum;

import java.nio.file.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageValidator {
    private static final String FILE_NAME_PATTERN = "^[a-zA-Z0-9]+\\.(png|jpg|bmp|jpeg)$";

    public static boolean validate(ImageBean image){
        long roleId = image.getRoleId();
        String roleString = image.getRole();
        String fileName = image.getFileName();
        return validateId(roleId) && validateRole(roleString) && validateFilePath(fileName);
    }

    private static boolean validateId(long id) {
        return id > 0;
    }

    private static boolean validateRole(String roleString){
        boolean status;
        try{
            RoleEnum.valueOf(roleString.toUpperCase());
            status = true;
        }catch (IllegalArgumentException e){
            status = false;
        }
        return status;
    }

    private static boolean validateFilePath(String fileName){
        Path path;
        try{
            path = Paths.get(fileName);
        }catch (IllegalArgumentException e){
            return false;
        }
        boolean absolute = path.isAbsolute();
        boolean singleName = path.getNameCount() == 1;
        return !path.isAbsolute() && path.getNameCount() == 1 && validateFileName(path.getName(0).toString());
    }

    private static boolean validateFileName(String name) {
        Pattern p = Pattern.compile(FILE_NAME_PATTERN);
        Matcher m = p.matcher(name);
        return m.matches();
    }
}
