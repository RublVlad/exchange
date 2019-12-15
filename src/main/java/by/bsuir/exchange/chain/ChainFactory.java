package by.bsuir.exchange.chain;

import by.bsuir.exchange.bean.*;
import by.bsuir.exchange.checker.PermissionChecker;
import by.bsuir.exchange.command.CommandEnum;
import by.bsuir.exchange.entity.RoleEnum;
import by.bsuir.exchange.manager.*;
import by.bsuir.exchange.manager.exception.ManagerInitializationException;
import by.bsuir.exchange.manager.exception.ManagerOperationException;
import by.bsuir.exchange.provider.PageAttributesNameProvider;
import by.bsuir.exchange.provider.RequestAttributesNameProvider;
import by.bsuir.exchange.provider.SessionAttributesNameProvider;
import by.bsuir.exchange.validator.ActorValidator;
import by.bsuir.exchange.validator.OfferValidator;
import by.bsuir.exchange.validator.UserValidator;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;

public class ChainFactory { //Loads on servlet initialization

    /*Chains*/
    private static CommandHandler emptyChain;

    /*Bean creators*/
    private static CommandHandler userBeanCreator;
    private static CommandHandler actorBeanCreator;
    private static CommandHandler offerBeanCreator;
    private static CommandHandler deliveryBeanCreator;
    private static CommandHandler relationBeanCreator;
    private static CommandHandler walletBeanCreator;
    private static CommandHandler personalDataBeanCreator;

    /*Branches*/
    private static CommandHandler sessionBranch;
    private static CommandHandler actorBranch;
    private static CommandHandler walletBranch;
    private static CommandHandler personalDataBranch;

    /*Managers*/
    private static CommandHandler sessionManager;
    private static CommandHandler clientManager;
    private static CommandHandler walletManagerClient;
    private static CommandHandler dataManagerClient;
    private static CommandHandler courierManager;
    private static CommandHandler walletManagerCourier;
    private static CommandHandler dataManagerCourier;
    private static CommandHandler deliveryManager;
    private static CommandHandler offerManager;
    private static CommandHandler relationManager;

    /*Transactional*/
    private static CommandHandler registerTransaction;
    private static CommandHandler finishDeliveryTransaction;
    private static CommandHandler deleteUserTransactional;
    private static CommandHandler likeTransaction;

    /*Validators*/
    private static CommandHandler userBeanValidator;
    private static CommandHandler actorBeanValidator;
    private static CommandHandler offerBeanValidator;

    /*Checkers*/
    private static CommandHandler permissionChecker;
    private static CommandHandler isCourierSession;
    private static CommandHandler isCourierRequest;

    /*Loggers*/
    private static CommandHandler permissionLogger;
    private static CommandHandler validatorLogger;
    private static CommandHandler managerLogger;

    static {
        initLoggers();
        initCheckers();
        initValidators();
        initBeanCreators();
        createEmptyChain();
        try {
            initManagers();
            initBranches();     //FIXME
            initTransactional();
        } catch (ManagerInitializationException e) {
            e.printStackTrace();
        }
    }

    public static CommandHandler getChain(CommandEnum command) {
        CommandHandler chain;
        switch (command){
            case LOGIN:{
                CommandHandler actorBranch = clientManager.branch(isCourierSession, courierManager);
                chain = permissionChecker.chain(sessionBranch).chain(actorBranch);
                break;
            }
            case LOGOUT: {
                chain = permissionChecker.chain(sessionManager);
                break;
            }
            case REGISTER: {
                chain = permissionChecker.chain(registerTransaction);
                break;
            }
            case UPDATE_PROFILE:{
                CommandHandler personalDataBranch = dataManagerClient.branch(isCourierSession, dataManagerCourier);
                chain = permissionChecker.chain(personalDataBeanCreator).chain(personalDataBranch);
                break;
            }
            case UPDATE_WALLET: {
                chain = walletBranch;
                break;
            }
            case UPDATE_OFFER: {
                chain = permissionChecker.chain(offerBeanCreator).chain(offerBeanValidator).chain(offerManager);
                break;
            }
            case UPDATE_AVATAR: {
                chain = emptyChain;
                break;
            }
            case SET_LOCALE:{
                chain = sessionManager;
                break;
            }
            case GET_USERS: {
                chain = permissionChecker.chain(sessionManager).chain(clientManager).chain(courierManager)
                        .chain(walletManagerClient).chain(walletManagerCourier);
                break;
            }
            case DELETE_USER: {
                chain = deleteUserTransactional;
                break;
            }
            case GET_PROFILE: {  //FIXME check for permissions
                CommandHandler actorBranch = clientManager.branch(isCourierSession, courierManager);
                CommandHandler walletBranch = walletManagerClient.branch(isCourierSession, walletManagerCourier);
                CommandHandler personalDataBranch = dataManagerClient.branch(isCourierSession, dataManagerCourier);
                chain = actorBranch.chain(walletBranch).chain(personalDataBranch);
                break;
            }
            case GET_COURIERS: {
                chain = permissionChecker.chain(courierManager);
                break;
            }
            case GET_OFFERS: {
                chain = permissionChecker.chain(offerManager).chain(courierManager).chain(relationManager);
                break;
            }
            case GET_DELIVERIES: {  //FIXME check for permissions
                CommandHandler branch = courierManager.branch(isCourierSession, clientManager);
                chain = permissionChecker.chain(deliveryManager).chain(branch);
                break;
            }
            case GET_IMAGE: {
                chain = permissionChecker;
                break;
            }
            case REQUEST_DELIVERY: {
                chain = permissionChecker.chain(deliveryBeanCreator).chain(walletManagerClient)
                        .chain(offerManager).chain(deliveryManager);
                break;
            }
            case FINISH_DELIVERY: {
                chain = finishDeliveryTransaction;
                break;
            }
            case LIKE_COURIER: {
                chain = permissionChecker.chain(relationBeanCreator).chain(likeTransaction);
                break;
            }
            default:{
                chain = emptyChain;
                break;
            }
        }
        return chain;
    }

    private static <T> CommandHandler getBeanCreator(T bean, String attribute){
        return (request, command) -> {
            try {
                BeanUtils.populate(bean, request.getParameterMap());
            } catch (IllegalAccessException | InvocationTargetException e) {
                Logger logger = LogManager.getRootLogger();
                HttpSession session = request.getSession();
                String role = (String) session.getAttribute(SessionAttributesNameProvider.ROLE);
                long id = (long) session.getAttribute(SessionAttributesNameProvider.ID);
                String log = String.format("Failed to populate bean for %s with id - %d", role, id);
                logger.warn(log, e);
            }
            request.setAttribute(attribute, bean);
            return true;
        };
    }

    private static void initValidators(){
        userBeanValidator = (request, command) -> {
            String attribute = PageAttributesNameProvider.USER_ATTRIBUTE;
            UserBean bean = (UserBean) request.getAttribute(attribute);
            return UserValidator.validate(bean);
        };
        userBeanValidator = validatorLogger.chain(userBeanValidator);

        actorBeanValidator = (request, command) -> {
            String attribute = RequestAttributesNameProvider.ACTOR_ATTRIBUTE;
            ActorBean bean = (ActorBean) request.getAttribute(attribute);
            return ActorValidator.validate(bean);
        };
        actorBeanValidator = validatorLogger.chain(actorBeanValidator);

        offerBeanValidator = (request, command) -> {
            String attribute = RequestAttributesNameProvider.OFFER_ATTRIBUTE;
            OfferBean  bean = (OfferBean) request.getAttribute(attribute);
            return OfferValidator.validate(bean);
        };
        offerBeanValidator = validatorLogger.chain(offerBeanValidator);
    }

    private static void initCheckers(){
        permissionChecker = (request, command) -> {
            String attribute = SessionAttributesNameProvider.ROLE;
            HttpSession session = request.getSession();
            RoleEnum role = (RoleEnum) session.getAttribute(attribute);
            return PermissionChecker.getInstance().checkPermission(role, command);
        };
        permissionChecker = permissionLogger.chain(permissionChecker);

        isCourierSession = (request, command1) -> {
            HttpSession session = request.getSession();
            RoleEnum role = (RoleEnum) session.getAttribute(SessionAttributesNameProvider.ROLE);
            return role == RoleEnum.COURIER;
        };

        isCourierRequest = (request, command) -> {
            UserBean user = (UserBean) request.getAttribute(RequestAttributesNameProvider.USER_ATTRIBUTE);
            String courierRole = RoleEnum.COURIER.toString();
            String actualRole = user.getRole().toUpperCase();
            return courierRole.equals(actualRole);
        };
    }


    private static void createEmptyChain() {
        emptyChain = (request, command) -> true;
    }


    private static void initBranches() throws ManagerInitializationException {
        initSessionBranch();
        initActorBranch();
        initWalletBranch();
        initPersonalDataBranch();
    }

    private static void initPersonalDataBranch() throws ManagerInitializationException {  //FIXME without permission check
            PersonalDataManager clientDataManager = new PersonalDataManager(RoleEnum.CLIENT);
            PersonalDataManager courierDataManager = new PersonalDataManager(RoleEnum.COURIER);
            personalDataBranch = clientDataManager.branch(isCourierSession, courierDataManager);
    }

    private static void initSessionBranch() {
        sessionBranch = userBeanCreator.chain(userBeanValidator).chain(sessionManager);
    }

    private static void initActorBranch() {
        CommandHandler branch = clientManager.branch(isCourierSession, courierManager);
        actorBranch = actorBeanCreator.chain(actorBeanValidator).chain(branch);
    }


    private static void initWalletBranch() {
        CommandHandler branch = walletManagerClient.branch(isCourierSession, walletManagerCourier);
        walletBranch = walletBeanCreator.chain(branch);
    }

    private static void initManagers() throws ManagerInitializationException {
        sessionManager = new HttpSessionManager();
        clientManager = new ActorManager(RoleEnum.CLIENT);
        walletManagerClient = new WalletManager(RoleEnum.CLIENT);
        dataManagerClient = new PersonalDataManager(RoleEnum.CLIENT);
        courierManager = new ActorManager(RoleEnum.COURIER);
        walletManagerCourier = new WalletManager(RoleEnum.COURIER);
        dataManagerCourier = new PersonalDataManager(RoleEnum.COURIER);
        deliveryManager = new DeliveryManager();
        offerManager = new OfferManager();
        relationManager = new RelationManager();
    }


    private static void initTransactional() {
        initFinishDeliveryTransaction();
        initRegisterTransaction();
        initDeleteUserTransaction();
        initLikeTransaction();
    }

    private static void initLikeTransaction() {
        likeTransaction = (request, command) -> {
            ActorManager courierManager = new ActorManager(RoleEnum.COURIER);
            RelationManager relationManager = new RelationManager();
            AbstractManager<ActorBean> transactionalManager =
                    AbstractManager.createTransactionalManager(courierManager);
            AbstractManager<ActorBean> combination = transactionalManager.combine(relationManager);
            boolean status = combination.handle(request, command);
            combination.closeManager();
            return status;
        };
    }

    private static void initRegisterTransaction(){
        CommandHandler clientRegisterTransactional = (request, command1) -> {
            HttpSessionManager sessionManager = new HttpSessionManager();
            ActorManager actorManager = new ActorManager(RoleEnum.CLIENT);
            AbstractManager<UserBean> transactionalManager = AbstractManager.createTransactionalManager(sessionManager);
            AbstractManager fullCombination = transactionalManager.combine(actorManager);
            boolean status = fullCombination.handle(request, command1);
            fullCombination.closeManager();
            return status;
        };
        CommandHandler courierRegisterTransactional = (request, command1) -> {
            HttpSessionManager sessionManager = new HttpSessionManager();
            ActorManager actorManager = new ActorManager(RoleEnum.COURIER);
            AbstractManager<UserBean> transactionalManager = AbstractManager.createTransactionalManager(sessionManager);
            AbstractManager fullCombination = transactionalManager.combine(actorManager);
            boolean status = fullCombination.handle(request, command1);
            fullCombination.closeManager();
            return status;
        };
        CommandHandler registerBranch = clientRegisterTransactional.branch(isCourierRequest, courierRegisterTransactional);
        registerTransaction = userBeanCreator
                .chain(userBeanValidator)
                .chain(actorBeanCreator)
                .chain(actorBeanValidator)
                .chain(registerBranch);
    }

    private static void initFinishDeliveryTransaction(){
        CommandHandler deliveryTransaction = (request, command) -> {
            DeliveryManager deliveryManager = new DeliveryManager();
            WalletManager walletManagerClient = new WalletManager(RoleEnum.CLIENT);
            WalletManager walletManagerCourier = new WalletManager(RoleEnum.COURIER);
            AbstractManager<DeliveryBean> transactionalManager =
                    AbstractManager.createTransactionalManager(deliveryManager);
            AbstractManager<DeliveryBean> deliveryClientCombination = transactionalManager.combine(walletManagerClient);
            AbstractManager<DeliveryBean> deliveryActorCombination = deliveryClientCombination.combine(walletManagerCourier);
            boolean status = deliveryActorCombination.handle(request, command);
            deliveryActorCombination.closeManager();
            return status;
        };
        finishDeliveryTransaction = deliveryBeanCreator.chain(offerManager).chain(deliveryTransaction);
    }

    private static void initDeleteUserTransaction(){
        CommandHandler clientDeleteTransactional = (request, command) -> {
            HttpSessionManager userManager = new HttpSessionManager();
            ActorManager clientManager = new ActorManager(RoleEnum.CLIENT);
            DeliveryManager deliveryManager = new DeliveryManager();
            ImageManager imageManager = new ImageManager();
            AbstractManager<UserBean> transactionalManager =
                    AbstractManager.createTransactionalManager(userManager);
            AbstractManager<UserBean> userClientCombination = transactionalManager.combine(clientManager);
            AbstractManager<UserBean> deliveryCombination = userClientCombination.combine(deliveryManager);
            AbstractManager<UserBean> fullCombination = deliveryCombination.combine(imageManager);
            boolean status = fullCombination.handle(request, command);
            fullCombination.closeManager();
            return status;
        };

        CommandHandler courierDeleteTransactional = (request, command) -> {
            HttpSessionManager userManager = new HttpSessionManager();
            ActorManager courierManager = new ActorManager(RoleEnum.COURIER);
            DeliveryManager deliveryManager = new DeliveryManager();
            OfferManager offerManager = new OfferManager();
            ImageManager imageManager = new ImageManager();
            AbstractManager<UserBean> transactionalManager =
                    AbstractManager.createTransactionalManager(userManager);
            AbstractManager<UserBean> userClientCombination = transactionalManager.combine(courierManager);
            AbstractManager<UserBean> deliveryCombination = userClientCombination.combine(deliveryManager);
            AbstractManager<UserBean> offerCombination = deliveryCombination.combine(offerManager);
            AbstractManager<UserBean> fullCombination = offerCombination.combine(imageManager);
            boolean status = fullCombination.handle(request, command);
            fullCombination.closeManager();
            return status;
        };

        CommandHandler deleteUserBranch = clientDeleteTransactional.branch(isCourierRequest, courierDeleteTransactional);

        deleteUserTransactional = permissionChecker.chain(userBeanCreator).chain(deleteUserBranch);
    }



    private static void initBeanCreators() {
        userBeanCreator = (request, command1) -> {
            UserBean user = new UserBean();
            return getBeanCreator(user, RequestAttributesNameProvider.USER_ATTRIBUTE).handle(request, command1);
        };

        actorBeanCreator = (request, command1) -> {
            ActorBean actor = new ActorBean();
            return getBeanCreator(actor, RequestAttributesNameProvider.ACTOR_ATTRIBUTE).handle(request, command1);
        };

        offerBeanCreator = (request, command) -> {
            OfferBean offer = new OfferBean();
            return getBeanCreator(offer, RequestAttributesNameProvider.OFFER_ATTRIBUTE).handle(request, command);
        };


        deliveryBeanCreator = (request, command) -> {
            DeliveryBean delivery = new DeliveryBean();
            return getBeanCreator(delivery, RequestAttributesNameProvider.DELIVERY_ATTRIBUTE).handle(request, command);
        };

        relationBeanCreator = (request, command) -> {
            RelationBean delivery = new RelationBean();
            return getBeanCreator(delivery, RequestAttributesNameProvider.RELATION_ATTRIBUTE).handle(request, command);
        };

        walletBeanCreator = (request, command) ->{
            WalletBean wallet = new WalletBean();
            return getBeanCreator(wallet, RequestAttributesNameProvider.WALLET_ATTRIBUTE).handle(request, command);
        };

        personalDataBeanCreator = (request, command) ->{
            PersonalDataBean personalData = new PersonalDataBean();
            return getBeanCreator(personalData, RequestAttributesNameProvider.PERSONAL_DATA_ATTRIBUTE).handle(request, command);
        };
    }

    private static void initLoggers() {
        String permissionBeforeLog = "Checking for permissions: %s with id - %d";
        String permissionFailureLog = "Actor failed to pass permission check: %s with id - %d";
        permissionLogger = getLoggerCommandHandler(permissionBeforeLog, permissionFailureLog);

        String managerBeforeLog = "Start to perform command for %s with id - %d";
        String managerFailureLog = "Failed to perform command for: %s with id - %d";
        managerLogger = getLoggerCommandHandler(managerBeforeLog, managerFailureLog);

        String validatorBeforeLog = "Start to validate data for %s with id - %d";
        String validatorFailureLog = "Failed to validate data for: %s with id - %d";
        validatorLogger = getLoggerCommandHandler(validatorBeforeLog, validatorFailureLog);
    }

    /*Expects log arguments to accept role string and role id*/
    private static CommandHandler getLoggerCommandHandler(String logBeforeCommand, String logOnFailure){
        return new CommandHandler() {
            private Logger logger = LogManager.getRootLogger();

            @Override
            public CommandHandler chain(CommandHandler other){
                return (request, command) -> {
                    HttpSession session = request.getSession();
                    RoleEnum role = (RoleEnum) session.getAttribute(SessionAttributesNameProvider.ROLE);
                    long id = (long) session.getAttribute(SessionAttributesNameProvider.ID);
                    String successLog = String.format(logBeforeCommand, role, id);
                    logger.info(successLog);
                    boolean status = other.handle(request, command);
                    if (!status){
                        String failureLog = String.format(logOnFailure, role, id);
                        logger.warn(failureLog);
                    }
                    return status;
                };
            }

            @Override
            public boolean handle(HttpServletRequest request, CommandEnum command) {
                return true;
            }
        };
    }

}
