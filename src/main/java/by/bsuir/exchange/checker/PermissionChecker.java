package by.bsuir.exchange.checker;

import by.bsuir.exchange.command.CommandEnum;
import by.bsuir.exchange.entity.PermissionEnum;
import by.bsuir.exchange.entity.ResourceEnum;
import by.bsuir.exchange.entity.RoleEnum;

import java.util.EnumSet;
import java.util.Objects;

import static by.bsuir.exchange.entity.PermissionEnum.*;

/**
 * The class Permission is a wrapper for PermissionEnum collections, which was created to ease the implementation.
 */
class Permission{
    private EnumSet<PermissionEnum> permissions;

    Permission(EnumSet<PermissionEnum> permissions) {
        this.permissions = permissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return Objects.equals(permissions, that.permissions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(permissions);
    }

    boolean granted(Permission other){
        boolean status = true;
        for (PermissionEnum permission : permissions){
            if (!other.permissions.contains(permission) ){
               status = false;
               break;
            }
        }
        return status;
    }

}
/**
 * The class PermissionChecker is used to check rights for performing an operation.
 * It binds each command, role with resources' permissions of an exchange platform and compares them.
 */
public class PermissionChecker {
    private final static int N_COMMANDS = 25;
    private final static int N_RESOURCES = 9;
    private final static int N_ROLES = 4;

    private Permission[][] roleCompetencies;
    private Permission[][] commandCompetencies;
    private PermissionChecker(){}

    private static PermissionChecker instance;

    public static PermissionChecker getInstance(){
        if (instance == null){
            instance = new PermissionChecker();
            instance.commandCompetencies = new Permission[N_COMMANDS][N_RESOURCES];
            instance.roleCompetencies = new Permission[N_ROLES][N_RESOURCES];

            addLoginCommandCompetencies();
            addLogoutCommandCompetencies();
            addRegisterCommandCompetencies();
            addGetUsersCommandCompetencies();
            addDeleteUserCommandCompetencies();
            addGetOffersCommandCompetencies();
            addUpdateProfileClientCommandCompetencies();
            addUpdateProfileCourierCommandCompetencies();
            addUpdateOfferCommandCompetencies();
            addUpdateAvatarClientCommandCompetencies();
            addUpdateAvatarCourierCommandCompetencies();
            addLikeCourierCommandCompetencies();
            addFinishDeliveryCommandCompetencies();
            addRequestDeliveryCommandCompetencies();
            addGetDeliveriesCommandCompetencies();
            addGetProfileClientCommandCompetencies();
            addGetProfileCourierCommandCompetencies();
            addGetImageCommandCompetencies();

            addGuestCompetencies();
            addClientCompetencies();
            addCourierCompetencies();
            addAdminCompetencies();
        }

        return instance;
    }

    private static void addGetImageCommandCompetencies() {
        int i = CommandEnum.GET_IMAGE.ordinal();
        EnumSet<PermissionEnum> imagePermissions = EnumSet.of(READ);
        instance.commandCompetencies[i][ResourceEnum.IMAGE.ordinal()] = new Permission(imagePermissions);
    }

    private static void addGetProfileCourierCommandCompetencies() {
        int i = CommandEnum.GET_PROFILE_COURIER.ordinal();
        EnumSet<PermissionEnum> courierPermissions = EnumSet.of(READ);
        instance.commandCompetencies[i][ResourceEnum.COURIER.ordinal()] = new Permission(courierPermissions);
    }

    private static void addGetProfileClientCommandCompetencies() {
        int i = CommandEnum.GET_PROFILE_CLIENT.ordinal();
        EnumSet<PermissionEnum> clientPermissions = EnumSet.of(READ);
        instance.commandCompetencies[i][ResourceEnum.CLIENT.ordinal()] = new Permission(clientPermissions);
    }

    private static void addGetDeliveriesCommandCompetencies() {
        int i = CommandEnum.GET_DELIVERIES.ordinal();
        EnumSet<PermissionEnum> deliveryPermissions = EnumSet.of(READ);
        instance.commandCompetencies[i][ResourceEnum.DELIVERY.ordinal()] = new Permission(deliveryPermissions);
    }

    private static void addFinishDeliveryCommandCompetencies() {
        int i = CommandEnum.FINISH_DELIVERY.ordinal();
        EnumSet<PermissionEnum> deliveryPermissions = EnumSet.of(UPDATE);
        instance.commandCompetencies[i][ResourceEnum.DELIVERY.ordinal()] = new Permission(deliveryPermissions);
    }

    private static void addRequestDeliveryCommandCompetencies() {
        int i = CommandEnum.REQUEST_DELIVERY.ordinal();
        EnumSet<PermissionEnum> deliveryPermissions = EnumSet.of(CREATE);
        instance.commandCompetencies[i][ResourceEnum.DELIVERY.ordinal()] = new Permission(deliveryPermissions);
    }

    private static void addUpdateAvatarClientCommandCompetencies() {
        int i = CommandEnum.UPDATE_AVATAR_CLIENT.ordinal();
        EnumSet<PermissionEnum> sessionPermissions = EnumSet.of(UPDATE);
        instance.commandCompetencies[i][ResourceEnum.CLIENT.ordinal()] = new Permission(sessionPermissions);
    }

    private static void addUpdateAvatarCourierCommandCompetencies() {
        int i = CommandEnum.UPDATE_AVATAR_COURIER.ordinal();
        EnumSet<PermissionEnum> sessionPermissions = EnumSet.of(UPDATE);
        instance.commandCompetencies[i][ResourceEnum.COURIER.ordinal()] = new Permission(sessionPermissions);
    }

    private static void addUpdateOfferCommandCompetencies() {
        int i = CommandEnum.UPDATE_OFFER.ordinal();
        EnumSet<PermissionEnum> sessionPermissions = EnumSet.of(UPDATE);
        instance.commandCompetencies[i][ResourceEnum.OFFER.ordinal()] = new Permission(sessionPermissions);
    }

    private static void addDeleteUserCommandCompetencies() {
        int i = CommandEnum.DELETE_USER.ordinal();
        EnumSet<PermissionEnum> sessionPermissions = EnumSet.of(UPDATE);
        instance.commandCompetencies[i][ResourceEnum.USER.ordinal()] = new Permission(sessionPermissions);
    }

    private static void addGetUsersCommandCompetencies() {
        int i = CommandEnum.GET_USERS.ordinal();
        EnumSet<PermissionEnum> sessionPermissions = EnumSet.of(READ);
        instance.commandCompetencies[i][ResourceEnum.USER.ordinal()] = new Permission(sessionPermissions);
    }

    private static void addLoginCommandCompetencies(){
        int i = CommandEnum.LOGIN.ordinal();
        EnumSet<PermissionEnum> sessionPermissions = EnumSet.of(CREATE, UPDATE);
        instance.commandCompetencies[i][ResourceEnum.HTTP_SESSION.ordinal()] = new Permission(sessionPermissions);
    }

    private static void addLogoutCommandCompetencies() {
        int i = CommandEnum.LOGOUT.ordinal();
        EnumSet<PermissionEnum> sessionPermissions = EnumSet.of(DELETE);
        instance.commandCompetencies[i][ResourceEnum.HTTP_SESSION.ordinal()] = new Permission(sessionPermissions);
    }

    private static void addRegisterCommandCompetencies() {
        int i = CommandEnum.REGISTER.ordinal();
        EnumSet<PermissionEnum> userPermissions = EnumSet.of(CREATE, UPDATE);
        instance.commandCompetencies[i][ResourceEnum.USER.ordinal()] = new Permission(userPermissions);
    }

    private static void addGetOffersCommandCompetencies(){
        int i = CommandEnum.GET_OFFERS.ordinal();
        EnumSet<PermissionEnum> offerPermissions = EnumSet.of(READ);
        instance.commandCompetencies[i][ResourceEnum.OFFER.ordinal()] = new Permission(offerPermissions);
    }

    private static void addUpdateProfileClientCommandCompetencies(){
        int i = CommandEnum.UPDATE_PROFILE_CLIENT.ordinal();
        EnumSet<PermissionEnum> clientPermissions = EnumSet.of(READ, UPDATE);
        instance.commandCompetencies[i][ResourceEnum.CLIENT.ordinal()] = new Permission(clientPermissions);
    }

    private static void addUpdateProfileCourierCommandCompetencies(){
        int i = CommandEnum.UPDATE_PROFILE_COURIER.ordinal();
        EnumSet<PermissionEnum> courierPermissions = EnumSet.of(READ, UPDATE);
        instance.commandCompetencies[i][ResourceEnum.COURIER.ordinal()] = new Permission(courierPermissions);
    }

    private static void addLikeCourierCommandCompetencies(){
        int i = CommandEnum.LIKE_COURIER.ordinal();
        EnumSet<PermissionEnum> relationPermissions = EnumSet.of(CREATE);
        instance.commandCompetencies[i][ResourceEnum.RELATION.ordinal()] = new Permission(relationPermissions);
    }

    private static void addGuestCompetencies(){
        int i = RoleEnum.GUEST.ordinal();
        EnumSet<PermissionEnum> sessionPermissions = EnumSet.of(CREATE, UPDATE);
        EnumSet<PermissionEnum> userPermissions = EnumSet.of(CREATE, UPDATE);
        instance.roleCompetencies[i][ResourceEnum.HTTP_SESSION.ordinal()] = new Permission(sessionPermissions);
        instance.roleCompetencies[i][ResourceEnum.USER.ordinal()] = new Permission(userPermissions);
    }

    private static void addClientCompetencies(){
        int i = RoleEnum.CLIENT.ordinal();
        EnumSet<PermissionEnum> deliveryPermissions = EnumSet.of(CREATE, READ, UPDATE);
        instance.roleCompetencies[i][ResourceEnum.DELIVERY.ordinal()] = new Permission(deliveryPermissions);
        EnumSet<PermissionEnum> courierPermissions = EnumSet.of(READ);
        instance.roleCompetencies[i][ResourceEnum.COURIER.ordinal()] = new Permission(courierPermissions);
        EnumSet<PermissionEnum> clientPermissions = EnumSet.of(READ, UPDATE);
        instance.roleCompetencies[i][ResourceEnum.CLIENT.ordinal()] = new Permission(clientPermissions);
        EnumSet<PermissionEnum> offersPermissions = EnumSet.of(READ);
        instance.roleCompetencies[i][ResourceEnum.OFFER.ordinal()] = new Permission(offersPermissions);
        EnumSet<PermissionEnum> sessionPermissions = EnumSet.of(DELETE);
        instance.roleCompetencies[i][ResourceEnum.HTTP_SESSION.ordinal()] = new Permission(sessionPermissions);
        EnumSet<PermissionEnum> relationPermissions = EnumSet.of(CREATE);
        instance.roleCompetencies[i][ResourceEnum.RELATION.ordinal()] = new Permission(relationPermissions);
        EnumSet<PermissionEnum> imagePermissions = EnumSet.of(READ);
        instance.roleCompetencies[i][ResourceEnum.IMAGE.ordinal()] = new Permission(imagePermissions);
    }

    private static void addCourierCompetencies() {
        int i = RoleEnum.COURIER.ordinal();
        EnumSet<PermissionEnum> deliveryPermissions = EnumSet.of(READ, UPDATE);
        instance.roleCompetencies[i][ResourceEnum.DELIVERY.ordinal()] = new Permission(deliveryPermissions);
        EnumSet<PermissionEnum> courierPermissions = EnumSet.of(READ, UPDATE);
        instance.roleCompetencies[i][ResourceEnum.COURIER.ordinal()] = new Permission(courierPermissions);
        EnumSet<PermissionEnum> sessionPermissions = EnumSet.of(DELETE);
        instance.roleCompetencies[i][ResourceEnum.HTTP_SESSION.ordinal()] = new Permission(sessionPermissions);
        EnumSet<PermissionEnum> offerPermissions = EnumSet.of(CREATE, READ, UPDATE);
        instance.roleCompetencies[i][ResourceEnum.OFFER.ordinal()] = new Permission(offerPermissions);
        EnumSet<PermissionEnum> imagePermissions = EnumSet.of(READ);
        instance.roleCompetencies[i][ResourceEnum.IMAGE.ordinal()] = new Permission(imagePermissions);
    }

    private static void addAdminCompetencies(){
        int i = RoleEnum.ADMIN.ordinal();
        EnumSet<PermissionEnum> userPermissions = EnumSet.of(READ, UPDATE, DELETE);
        instance.roleCompetencies[i][ResourceEnum.USER.ordinal()] = new Permission(userPermissions);
        EnumSet<PermissionEnum> sessionPermissions = EnumSet.of(DELETE);
        instance.roleCompetencies[i][ResourceEnum.HTTP_SESSION.ordinal()] = new Permission(sessionPermissions);
        EnumSet<PermissionEnum> imagePermissions = EnumSet.of(READ);
        instance.roleCompetencies[i][ResourceEnum.IMAGE.ordinal()] = new Permission(imagePermissions);
    }



    public boolean checkPermission(RoleEnum role, CommandEnum command){
        int iRole = role.ordinal();
        if (command == CommandEnum.UPDATE_PROFILE){
            command = role == RoleEnum.CLIENT? CommandEnum.UPDATE_PROFILE_CLIENT : CommandEnum.UPDATE_PROFILE_COURIER;
        }
        if (command == CommandEnum.UPDATE_AVATAR){
            command = role == RoleEnum.CLIENT? CommandEnum.UPDATE_AVATAR_CLIENT : CommandEnum.UPDATE_AVATAR_COURIER;
        }

        if (command == CommandEnum.GET_PROFILE){
            command = role == RoleEnum.CLIENT? CommandEnum.GET_PROFILE_CLIENT : CommandEnum.GET_PROFILE_COURIER;
        }

        int iCommand = command.ordinal();
        for (int j = 0; j < N_RESOURCES; j++){
            if (commandCompetencies[iCommand][j] == null){
                continue;
            }

            if (roleCompetencies[iRole][j] == null){
                return false;
            }

            if (!commandCompetencies[iCommand][j].granted(roleCompetencies[iRole][j])){
                return false;
            }
        }
        return true;
    }
}
