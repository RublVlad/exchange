package by.bsuir.exchange.chain;

import by.bsuir.exchange.bean.*;
import by.bsuir.exchange.command.CommandEnum;
import by.bsuir.exchange.provider.PageAttributesNameProvider;
import by.bsuir.exchange.provider.RequestAttributesNameProvider;
import by.bsuir.exchange.validator.*;


class ValidatorFactory {
    private static final int N_COMMANDS = 25;
    private static CommandHandler[] validators;

    static {
        initValidators();
    }

    static CommandHandler getValidator(CommandEnum command){
        return validators[command.ordinal()];
    }

    private static CommandHandler initValidator(CommandEnum command){
        CommandHandler validator;
        switch (command){
            case REGISTER:{
                CommandHandler userValidator = (request, command1) -> {
                    String attribute = RequestAttributesNameProvider.USER_ATTRIBUTE;
                    UserBean bean = (UserBean) request.getAttribute(attribute);
                    return UserValidator.validate(bean);
                };
                CommandHandler actorValidator = (request, command1) -> {
                    String attribute = RequestAttributesNameProvider.ACTOR_ATTRIBUTE;
                    ActorBean bean = (ActorBean) request.getAttribute(attribute);
                    return ActorValidator.validate(bean);
                };
                validator = userValidator.chain(actorValidator);
                break;
            }
            case LOGIN: {
                validator = (request, command1) -> {
                    String attribute = PageAttributesNameProvider.USER_ATTRIBUTE;
                    UserBean bean = (UserBean) request.getAttribute(attribute);
                    return UserValidator.validate(bean);
                };
                break;
            }
            case REQUEST_DELIVERY:
            case FINISH_DELIVERY: {
                validator = (request, command1) -> {
                    String attribute = RequestAttributesNameProvider.DELIVERY_ATTRIBUTE;
                    DeliveryBean bean = (DeliveryBean) request.getAttribute(attribute);
                    return DeliveryValidator.validate(bean);
                };
                break;
            }
            case UPDATE_PROFILE: {
                validator = (request, command1) -> {
                    String attribute = RequestAttributesNameProvider.PERSONAL_DATA_ATTRIBUTE;
                    PersonalDataBean bean = (PersonalDataBean) request.getAttribute(attribute);
                    return PersonalDataValidator.validate(bean);
                };
                break;
            }
            case LIKE_COURIER: {
                validator = (request, command1) -> {
                    String attribute = RequestAttributesNameProvider.RELATION_ATTRIBUTE;
                    RelationBean bean = (RelationBean) request.getAttribute(attribute);
                    return RelationValidator.validate(bean);
                };
                break;
            }
            case UPDATE_OFFER: {
                validator = (request, command1) -> {
                    String attribute = RequestAttributesNameProvider.OFFER_ATTRIBUTE;
                    OfferBean bean = (OfferBean) request.getAttribute(attribute);
                    return OfferValidator.validate(bean);
                };
                break;
            }
            case UPDATE_WALLET: {
                validator = (request, command1) -> {
                    String attribute = RequestAttributesNameProvider.WALLET_ATTRIBUTE;
                    WalletBean bean = (WalletBean) request.getAttribute(attribute);
                    return WalletValidator.validate(bean);
                };
                break;
            }
            default: validator = ChainFactory.emptyChain;
        }
        return validator;
    }

    private static void initValidators(){
        validators = new CommandHandler[N_COMMANDS];
        for (CommandEnum command: CommandEnum.values()){
            int i = command.ordinal();
            validators[i] = initValidator(command);
        }
    }
}
