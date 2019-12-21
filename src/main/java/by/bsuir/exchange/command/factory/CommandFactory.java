package by.bsuir.exchange.command.factory;

import by.bsuir.exchange.chain.ChainFactory;
import by.bsuir.exchange.chain.CommandHandler;
import by.bsuir.exchange.command.Command;
import by.bsuir.exchange.command.CommandEnum;
import by.bsuir.exchange.provider.ConfigurationProvider;
import by.bsuir.exchange.provider.PageAttributesNameProvider;
import by.bsuir.exchange.provider.RequestAttributesNameProvider;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

import static by.bsuir.exchange.provider.ConfigurationProvider.*;
import static by.bsuir.exchange.provider.PageAttributesNameProvider.COMMAND;

public class CommandFactory {
    private static final int N_COMMANDS = 25;
    private static final String FEED_BACK_TEMPLATE = "/controller?command=%s";

    private static String[] successPages;
    private static String[] failurePages;
    private static Set<CommandEnum> redirectCommand;
    private static CommandEnum[] feedBacks;

    static {
        initSuccessPages();
        initFeedBacks();
        initRedirects();
    }

    private static void initRedirects() {
        redirectCommand = Set.of(CommandEnum.FINISH_DELIVERY, CommandEnum.LOGOUT, CommandEnum.SET_LOCALE,
                            CommandEnum.LIKE_COURIER, CommandEnum.REQUEST_DELIVERY, CommandEnum.DELETE_USER,
                            CommandEnum.REGISTER, CommandEnum.UPDATE_OFFER, CommandEnum.UPDATE_PROFILE,
                            CommandEnum.UPDATE_WALLET, CommandEnum.LOGIN);
    }

    private static void initFeedBacks(){
        feedBacks = new CommandEnum[N_COMMANDS];
        feedBacks[CommandEnum.LOGIN.ordinal()] = CommandEnum.GET_PROFILE;
        feedBacks[CommandEnum.FINISH_DELIVERY.ordinal()] = CommandEnum.GET_DELIVERIES;
        feedBacks[CommandEnum.DELETE_USER.ordinal()] = CommandEnum.GET_USERS;
        feedBacks[CommandEnum.LIKE_COURIER.ordinal()] = CommandEnum.GET_OFFERS;
    }

    private static void initSuccessPages(){
        successPages = new String[N_COMMANDS];
        failurePages = new String[N_COMMANDS];

        /*Feed back command*/
        failurePages[CommandEnum.LOGIN.ordinal()] = ConfigurationProvider.getProperty(LOGIN_PAGE_PATH);

        successPages[CommandEnum.LOGOUT.ordinal()] = ConfigurationProvider.getProperty(LOGIN_PAGE_PATH);
        failurePages[CommandEnum.LOGOUT.ordinal()] = ConfigurationProvider.getProperty(ERROR_PAGE_PATH);

        successPages[CommandEnum.GET_PROFILE.ordinal()] = ConfigurationProvider.getProperty(PROFILE_PAGE_PATH);
        failurePages[CommandEnum.GET_PROFILE.ordinal()] = ConfigurationProvider.getProperty(ERROR_PAGE_PATH);

        successPages[CommandEnum.REGISTER.ordinal()] = ConfigurationProvider.getProperty(EDIT_PROFILE_PAGE_PATH);
        failurePages[CommandEnum.REGISTER.ordinal()] = ConfigurationProvider.getProperty(REGISTER_PAGE_PATH);

        successPages[CommandEnum.UPDATE_PROFILE.ordinal()] = ConfigurationProvider.getProperty(PROFILE_PAGE_PATH);
        failurePages[CommandEnum.UPDATE_PROFILE.ordinal()] = ConfigurationProvider.getProperty(EDIT_PROFILE_PAGE_PATH);

        successPages[CommandEnum.UPDATE_WALLET.ordinal()] = ConfigurationProvider.getProperty(PROFILE_PAGE_PATH);
        failurePages[CommandEnum.UPDATE_WALLET.ordinal()] = ConfigurationProvider.getProperty(EDIT_PROFILE_PAGE_PATH);

        successPages[CommandEnum.UPDATE_AVATAR.ordinal()] = ConfigurationProvider.getProperty(PROFILE_PAGE_PATH);
        failurePages[CommandEnum.UPDATE_AVATAR.ordinal()] = ConfigurationProvider.getProperty(EDIT_PROFILE_PAGE_PATH);

        successPages[CommandEnum.UPDATE_OFFER.ordinal()] = ConfigurationProvider.getProperty(PROFILE_PAGE_PATH);
        failurePages[CommandEnum.UPDATE_OFFER.ordinal()] = ConfigurationProvider.getProperty(EDIT_PROFILE_PAGE_PATH);

        successPages[CommandEnum.REQUEST_DELIVERY.ordinal()] = ConfigurationProvider.getProperty(PROFILE_PAGE_PATH);
        failurePages[CommandEnum.REQUEST_DELIVERY.ordinal()] = "/controller?command=get_offers";

        /*Feed back command*/
        failurePages[CommandEnum.FINISH_DELIVERY.ordinal()] = ConfigurationProvider.getProperty(DELIVERIES_PAGE_PATH);

        successPages[CommandEnum.GET_DELIVERIES.ordinal()] = ConfigurationProvider.getProperty(DELIVERIES_PAGE_PATH);
        failurePages[CommandEnum.GET_DELIVERIES.ordinal()] = ConfigurationProvider.getProperty(ERROR_PAGE_PATH);

        successPages[CommandEnum.GET_IMAGE.ordinal()] = ConfigurationProvider.getProperty(GET_IMAGE_PATH);
        failurePages[CommandEnum.GET_IMAGE.ordinal()] = ConfigurationProvider.getProperty(ERROR_PAGE_PATH);

        successPages[CommandEnum.GET_OFFERS.ordinal()] = ConfigurationProvider.getProperty(OFFERS_PAGE_PATH);
        failurePages[CommandEnum.GET_OFFERS.ordinal()] = ConfigurationProvider.getProperty(ERROR_PAGE_PATH);

        successPages[CommandEnum.GET_USERS.ordinal()] = ConfigurationProvider.getProperty(ADMIN_PAGE_PATH);
        failurePages[CommandEnum.GET_USERS.ordinal()] = ConfigurationProvider.getProperty(ERROR_PAGE_PATH);

        /*Feed back command*/
        failurePages[CommandEnum.DELETE_USER.ordinal()] = ConfigurationProvider.getProperty(ERROR_PAGE_PATH);

        failurePages[CommandEnum.LIKE_COURIER.ordinal()] = ConfigurationProvider.getProperty(OFFERS_PAGE_PATH);

    }

    public static Command getCommand(HttpServletRequest request) {
        String action = request.getParameter(COMMAND);
        CommandEnum commandEnum;
        if (action == null || action.isEmpty()){
            commandEnum = CommandEnum.EMPTY;
        }else{
            commandEnum = CommandEnum.valueOf(action.toUpperCase());
        }
        CommandHandler handler;
        handler = ChainFactory.getChain(commandEnum);
        String successPage;
        String failurePage;
        if (isSamePage(commandEnum)){
            String pageParameter = request.getParameter(PageAttributesNameProvider.PAGE);
            successPage = pageParameter;
            failurePage = pageParameter;
        }else if(isContentRelated(commandEnum)){
            successPage = ConfigurationProvider.getProperty(IMAGE_SERVLET);
            request.setAttribute(RequestAttributesNameProvider.PAGE, successPages[commandEnum.ordinal()]);
            failurePage = failurePages[commandEnum.ordinal()];
        }else if(isFeedBackCommand(commandEnum)){
            CommandEnum nextCommand = feedBacks[commandEnum.ordinal()];
            successPage = String.format(FEED_BACK_TEMPLATE, nextCommand.toString());
            failurePage = failurePages[commandEnum.ordinal()];
        }else{
            successPage = successPages[commandEnum.ordinal()];
            failurePage = failurePages[commandEnum.ordinal()];
        }
        boolean redirect = isRedirect(commandEnum);
        return new Command(handler, commandEnum, successPage, failurePage, redirect);
    }

    private static boolean isRedirect(CommandEnum command) {
        return redirectCommand.contains(command);
    }

    private static boolean isSamePage(CommandEnum command){
        return command == CommandEnum.SET_LOCALE;
    }

    private static boolean isContentRelated(CommandEnum command){
        return command == CommandEnum.GET_IMAGE || command == CommandEnum.UPDATE_AVATAR;
    }

    private static boolean isFeedBackCommand(CommandEnum command){
        return command == CommandEnum.LOGIN || command == CommandEnum.DELETE_USER
                || command == CommandEnum.FINISH_DELIVERY || command == CommandEnum.LIKE_COURIER;
    }
}
