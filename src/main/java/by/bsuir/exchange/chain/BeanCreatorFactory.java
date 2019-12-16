package by.bsuir.exchange.chain;

import by.bsuir.exchange.bean.*;
import by.bsuir.exchange.command.CommandEnum;
import by.bsuir.exchange.provider.RequestAttributesNameProvider;
import by.bsuir.exchange.provider.SessionAttributesNameProvider;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;


class BeanCreatorFactory {

    static CommandHandler getBeanCreator(CommandEnum command){
        CommandHandler creator;
        switch (command){
            case REGISTER:{
                CommandHandler userCreator = (request, command1) -> {
                    UserBean user = new UserBean();
                    CommandHandler handler = getCreator(user, RequestAttributesNameProvider.USER_ATTRIBUTE);
                    return handler.handle(request, command1);
                };
                CommandHandler actorCreator = (request, command1) -> {
                    ActorBean actor = new ActorBean();
                    CommandHandler handler = getCreator(actor, RequestAttributesNameProvider.ACTOR_ATTRIBUTE);
                    return handler.handle(request, command1);
                };
                creator = userCreator.chain(actorCreator);
                break;
            }
            case LOGIN:
            case DELETE_USER: {
                creator = (request, command1) -> {
                    UserBean user = new UserBean();
                    CommandHandler handler = getCreator(user, RequestAttributesNameProvider.USER_ATTRIBUTE);
                    return handler.handle(request, command1);
                };
                break;
            }
            case REQUEST_DELIVERY:
            case FINISH_DELIVERY: {
                creator = (request, command1) -> {
                    DeliveryBean delivery = new DeliveryBean();
                    CommandHandler handler = getCreator(delivery, RequestAttributesNameProvider.DELIVERY_ATTRIBUTE);
                    return handler.handle(request, command1);
                };
                break;
            }
            case UPDATE_PROFILE: {
                creator = (request, command1) -> {
                    PersonalDataBean personalData = new PersonalDataBean();
                    CommandHandler handler = getCreator(personalData, RequestAttributesNameProvider.PERSONAL_DATA_ATTRIBUTE);
                    return handler.handle(request, command1);
                };
                break;
            }
            case LIKE_COURIER: {
                creator = (request, command1) -> {
                    RelationBean relation = new RelationBean();
                    CommandHandler handler = getCreator(relation, RequestAttributesNameProvider.RELATION_ATTRIBUTE);
                    return handler.handle(request, command1);
                };
                break;
            }
            case UPDATE_OFFER: {
                creator = (request, command1) -> {
                    OfferBean offer = new OfferBean();
                    CommandHandler handler = getCreator(offer, RequestAttributesNameProvider.OFFER_ATTRIBUTE);
                    return handler.handle(request, command1);
                };
                break;
            }
            case UPDATE_WALLET: {
                creator = (request, command1) -> {
                    WalletBean wallet = new WalletBean();
                    CommandHandler handler = getCreator(wallet, RequestAttributesNameProvider.WALLET_ATTRIBUTE);
                    return handler.handle(request, command1);
                };
                break;
            }
            default: creator = ChainFactory.emptyChain;
        }
        return creator;
    }

    private static <T> CommandHandler getCreator(T bean, String attribute){
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
}
