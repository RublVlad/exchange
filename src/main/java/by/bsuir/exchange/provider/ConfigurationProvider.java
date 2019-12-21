package by.bsuir.exchange.provider;


import java.util.ResourceBundle;

public class ConfigurationProvider{
    private static final String BUNDLE_NAME = "config";
    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);
    public static final String LOGIN_PAGE_PATH = "LOGIN_PAGE_PATH";
    public static final String REGISTER_PAGE_PATH = "REGISTER_PAGE_PATH";
    public static final String DELIVERIES_PAGE_PATH = "DELIVERIES_PAGE_PATH";
    public static final String PROFILE_PAGE_PATH = "PROFILE_PAGE_PATH";
    public static final String ERROR_PAGE_PATH = "ERROR_PAGE_PATH";
    public static final String EDIT_PROFILE_PAGE_PATH = "EDIT_PROFILE_PAGE_PATH";
    public static final String OFFERS_PAGE_PATH = "OFFERS_PAGE_PATH";
    public static final String ADMIN_PAGE_PATH = "ADMIN_PAGE_PATH";

    public static final String IMAGE_PATH = "IMAGE_PATH";
    public static final String IMAGE_SERVLET = "IMAGE_SERVLET";
    public static final String GET_IMAGE_PATH = "GET_IMAGE_PATH";

    public static final int PAGINATION_FACTOR = 3;

    public static String getProperty(String name){
        return resourceBundle.getString(name);
    }
}
