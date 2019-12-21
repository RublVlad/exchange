package by.bsuir.exchange.chain;

import by.bsuir.exchange.bean.ActorBean;
import by.bsuir.exchange.bean.DeliveryBean;
import by.bsuir.exchange.bean.UserBean;
import by.bsuir.exchange.command.CommandEnum;
import by.bsuir.exchange.entity.RoleEnum;
import by.bsuir.exchange.manager.*;
import by.bsuir.exchange.manager.exception.ManagerInitializationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class ManagerFactory {
    private static final int N_COMMANDS = 25;
    private static Logger logger = LogManager.getRootLogger();

    private static CommandHandler registerTransaction;
    private static CommandHandler finishDeliveryTransaction;
    private static CommandHandler deleteUserTransactional;
    private static CommandHandler likeTransaction;

    private static CommandHandler actorBranch;
    private static CommandHandler dataBranch;
    private static CommandHandler walletBranch;
    private static CommandHandler clientManager;

    private static CommandHandler courierManager;
    private static CommandHandler walletManagerClient;
    private static CommandHandler walletManagerCourier;
    private static CommandHandler sessionManager;
    private static CommandHandler offerManager;
    private static CommandHandler relationManager;
    private static CommandHandler deliveryManager;

    private static CommandHandler[] handlers;

    static {
        try {
            initManagers();
            initTransactions();
            initHandlers();
        }catch (ManagerInitializationException e){
            logger.fatal("Unable to initialize ManagerFactory");
            throw new RuntimeException(e);
        }
    }

    static CommandHandler getManager(CommandEnum command){
        int i = command.ordinal();
        return handlers[i];
    }

    private static CommandHandler initManager(CommandEnum command) {
        CommandHandler manager;
        switch (command){
            case LOGIN: {
                manager = sessionManager.chain(actorBranch);
                break;
            }
            case LOGOUT:
            case SET_LOCALE: {
               manager = sessionManager;
               break;
            }
            case UPDATE_PROFILE: {
                manager = dataBranch;
                break;
            }
            case UPDATE_WALLET: {
                manager = walletBranch;
                break;
            }
            case UPDATE_OFFER: {
                manager = offerManager;
                break;
            }
            case GET_USERS: {
                manager = sessionManager.chain(clientManager).chain(courierManager)
                        .chain(walletManagerClient).chain(walletManagerCourier);
                break;
            }
            case GET_PROFILE: {
                manager = actorBranch.chain(walletBranch).chain(dataBranch);
                break;
            }
            case GET_OFFERS: {
                manager = offerManager.chain(courierManager).chain(relationManager);
                break;
            }
            case GET_DELIVERIES: {
                CommandHandler branch = courierManager.branch(ChainFactory.isCourierSession, clientManager);
                manager = deliveryManager.chain(branch);
                break;
            }
            case REQUEST_DELIVERY: {
                manager = walletManagerClient.chain(offerManager).chain(deliveryManager);
                break;
            }
            case REGISTER: {
                manager = registerTransaction;
                break;
            }
            case DELETE_USER: {
                manager = deleteUserTransactional;
                break;
            }
            case FINISH_DELIVERY: {
                manager = finishDeliveryTransaction;
                break;
            }
            case LIKE_COURIER: {
                manager = likeTransaction;
                break;
            }
            default: manager = ChainFactory.emptyChain;
        }
        return manager;
    }

    private static void initManagers() throws ManagerInitializationException {
        clientManager = new ActorManager(RoleEnum.CLIENT);
        courierManager = new ActorManager(RoleEnum.COURIER);
        walletManagerClient = new WalletManager(RoleEnum.CLIENT);
        walletManagerCourier = new WalletManager(RoleEnum.COURIER);
        sessionManager = new HttpSessionManager();
        offerManager = new OfferManager();
        relationManager = new RelationManager();
        deliveryManager = new DeliveryManager();
        actorBranch = clientManager.branch(ChainFactory.isCourierSession, courierManager);

        PersonalDataManager clientDataManager = new PersonalDataManager(RoleEnum.CLIENT);
        PersonalDataManager courierDataManager = new PersonalDataManager(RoleEnum.COURIER);
        dataBranch = clientDataManager.branch(ChainFactory.isCourierSession, courierDataManager);

        walletBranch = walletManagerClient.branch(ChainFactory.isCourierSession, walletManagerCourier);
    }

    private static void initHandlers(){
        handlers = new CommandHandler[N_COMMANDS];
        for (CommandEnum command : CommandEnum.values()){
            handlers[command.ordinal()] = initManager(command);
        }
    }

    private static void initTransactions(){
        initDeleteUserTransaction();
        initRegisterTransaction();
        initFinishDeliveryTransaction();
        initLikeTransaction();
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
        registerTransaction = clientRegisterTransactional
                .branch(ChainFactory.isCourierRequest, courierRegisterTransactional);
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
        finishDeliveryTransaction = offerManager.chain(deliveryTransaction);
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

        deleteUserTransactional = clientDeleteTransactional.branch(ChainFactory.isCourierRequest, courierDeleteTransactional);
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
}
