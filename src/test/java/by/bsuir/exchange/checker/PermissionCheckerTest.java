package by.bsuir.exchange.checker;

import by.bsuir.exchange.command.CommandEnum;
import by.bsuir.exchange.entity.RoleEnum;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class PermissionCheckerTest {
    private PermissionChecker permissionChecker;

    @BeforeClass
    public void setUp(){
        permissionChecker = PermissionChecker.getInstance();
    }

    @DataProvider
    public Object[][] checkPermissionCourierData() {
        return new Object[][]{
                {RoleEnum.COURIER, CommandEnum.UPDATE_PROFILE_COURIER, true},
                {RoleEnum.COURIER, CommandEnum.UPDATE_AVATAR, true},
                {RoleEnum.COURIER, CommandEnum.UPDATE_OFFER, true},
                {RoleEnum.COURIER, CommandEnum.GET_OFFERS, true},
                {RoleEnum.COURIER, CommandEnum.GET_DELIVERIES, true},
                {RoleEnum.COURIER, CommandEnum.GET_PROFILE, true},
                {RoleEnum.COURIER, CommandEnum.GET_IMAGE, true},
                {RoleEnum.COURIER, CommandEnum.FINISH_DELIVERY, true},
                {RoleEnum.COURIER, CommandEnum.SET_LOCALE, true},
                {RoleEnum.COURIER, CommandEnum.FINISH_DELIVERY, true},
                {RoleEnum.COURIER, CommandEnum.LOGOUT, true},

                {RoleEnum.COURIER, CommandEnum.REGISTER, false},
                {RoleEnum.COURIER, CommandEnum.GET_USERS, false},
                {RoleEnum.COURIER, CommandEnum.DELETE_USER, false},
                {RoleEnum.COURIER, CommandEnum.LIKE_COURIER, false},
                {RoleEnum.COURIER, CommandEnum.REQUEST_DELIVERY, false},
        };
    }

    @DataProvider
    public Object[][] checkPermissionClientData() {
        return new Object[][]{
                {RoleEnum.CLIENT, CommandEnum.UPDATE_PROFILE_CLIENT, true},
                {RoleEnum.CLIENT, CommandEnum.UPDATE_AVATAR, true},
                {RoleEnum.CLIENT, CommandEnum.GET_OFFERS, true},
                {RoleEnum.CLIENT, CommandEnum.GET_DELIVERIES, true},
                {RoleEnum.CLIENT, CommandEnum.GET_PROFILE, true},
                {RoleEnum.CLIENT, CommandEnum.GET_IMAGE, true},
                {RoleEnum.CLIENT, CommandEnum.FINISH_DELIVERY, true},
                {RoleEnum.CLIENT, CommandEnum.SET_LOCALE, true},
                {RoleEnum.CLIENT, CommandEnum.FINISH_DELIVERY, true},
                {RoleEnum.CLIENT, CommandEnum.LOGOUT, true},
                {RoleEnum.CLIENT, CommandEnum.LIKE_COURIER, true},
                {RoleEnum.CLIENT, CommandEnum.REQUEST_DELIVERY, true},

                {RoleEnum.CLIENT, CommandEnum.REGISTER, false},
                {RoleEnum.CLIENT, CommandEnum.UPDATE_OFFER, false},
                {RoleEnum.CLIENT, CommandEnum.GET_USERS, false},
                {RoleEnum.CLIENT, CommandEnum.DELETE_USER, false},
        };
    }


    @DataProvider
    public Object[][] checkPermissionAdminData() {
        return new Object[][]{
                {RoleEnum.ADMIN, CommandEnum.GET_USERS, true},
                {RoleEnum.ADMIN, CommandEnum.DELETE_USER, true},
                {RoleEnum.ADMIN, CommandEnum.LOGOUT, true},
                {RoleEnum.ADMIN, CommandEnum.SET_LOCALE, true},
                {RoleEnum.ADMIN, CommandEnum.GET_IMAGE, true},

                {RoleEnum.ADMIN, CommandEnum.UPDATE_PROFILE_CLIENT, false},
                {RoleEnum.ADMIN, CommandEnum.UPDATE_AVATAR, false},
                {RoleEnum.ADMIN, CommandEnum.GET_OFFERS, false},
                {RoleEnum.ADMIN, CommandEnum.GET_DELIVERIES, false},
                {RoleEnum.ADMIN, CommandEnum.GET_PROFILE, false},
                {RoleEnum.ADMIN, CommandEnum.FINISH_DELIVERY, false},
                {RoleEnum.ADMIN, CommandEnum.LIKE_COURIER, false},
                {RoleEnum.ADMIN, CommandEnum.REQUEST_DELIVERY, false},
        };
    }


    @DataProvider
    public Object[][] checkPermissionGuestData() {
        return new Object[][]{
                {RoleEnum.GUEST, CommandEnum.REGISTER, true},
                {RoleEnum.GUEST, CommandEnum.LOGIN, true},
                {RoleEnum.GUEST, CommandEnum.SET_LOCALE, true},

                {RoleEnum.GUEST, CommandEnum.UPDATE_PROFILE_CLIENT, false},
                {RoleEnum.GUEST, CommandEnum.UPDATE_AVATAR, false},
                {RoleEnum.GUEST, CommandEnum.GET_OFFERS, false},
                {RoleEnum.GUEST, CommandEnum.GET_DELIVERIES, false},
                {RoleEnum.GUEST, CommandEnum.GET_PROFILE, false},
                {RoleEnum.GUEST, CommandEnum.GET_IMAGE, false},
                {RoleEnum.GUEST, CommandEnum.FINISH_DELIVERY, false},
                {RoleEnum.GUEST, CommandEnum.FINISH_DELIVERY, false},
                {RoleEnum.GUEST, CommandEnum.LIKE_COURIER, false},
                {RoleEnum.GUEST, CommandEnum.LOGOUT, false},
                {RoleEnum.GUEST, CommandEnum.REQUEST_DELIVERY, false},
        };
    }


    @Test(dataProvider = "checkPermissionCourierData")
    public void checkPermissionCourierTest(RoleEnum actor, CommandEnum command, boolean expected) {
        boolean actual = permissionChecker.checkPermission(actor, command);
        Assert.assertEquals(actual, expected);
    }


    @Test(dataProvider = "checkPermissionClientData")
    public void checkPermissionClientTest(RoleEnum actor, CommandEnum command, boolean expected) {
        boolean actual = permissionChecker.checkPermission(actor, command);
        Assert.assertEquals(actual, expected);
    }


    @Test(dataProvider = "checkPermissionAdminData")
    public void checkPermissionAdminTest(RoleEnum actor, CommandEnum command, boolean expected) {
        boolean actual = permissionChecker.checkPermission(actor, command);
        Assert.assertEquals(actual, expected);
    }


    @Test(dataProvider = "checkPermissionGuestData")
    public void checkPermissionGuestTest(RoleEnum actor, CommandEnum command, boolean expected) {
        boolean actual = permissionChecker.checkPermission(actor, command);
        Assert.assertEquals(actual, expected);
    }
}
