package by.bsuir.exchange.chain;

import by.bsuir.exchange.bean.UserBean;
import by.bsuir.exchange.checker.PermissionChecker;
import by.bsuir.exchange.command.CommandEnum;
import by.bsuir.exchange.entity.RoleEnum;
import by.bsuir.exchange.provider.RequestAttributesNameProvider;
import by.bsuir.exchange.provider.SessionAttributesNameProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class ChainFactory { //Loads on servlet initialization

    /*Chains*/
    static CommandHandler emptyChain;

    /*Checkers*/
    private static CommandHandler permissionChecker;
    static CommandHandler isCourierSession;
    static CommandHandler isCourierRequest;

    /*Loggers*/
    private static CommandHandler permissionLogger;
    private static CommandHandler validatorLogger;
    private static CommandHandler managerLogger;

    static {
        initLoggers();
        initCheckers();
        createEmptyChain();
    }

    public static CommandHandler getChain(CommandEnum command){
        CommandHandler beanCreator = BeanCreatorFactory.getBeanCreator(command);
        CommandHandler validator = ValidatorFactory.getValidator(command);
        CommandHandler manager = ManagerFactory.getManager(command);
        return permissionLogger.chain(permissionChecker)
                .chain(beanCreator).chain(validatorLogger).chain(validator)
                .chain(managerLogger).chain(manager);
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
        Logger logger = LogManager.getRootLogger();
        emptyChain = (request, command) ->{
            String log = String.format("Using an empty chain as handler for command - %s", command);
            logger.info(log);
            return true;
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
