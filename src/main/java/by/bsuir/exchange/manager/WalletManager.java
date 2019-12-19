package by.bsuir.exchange.manager;

import by.bsuir.exchange.bean.*;
import by.bsuir.exchange.chain.CommandHandler;
import by.bsuir.exchange.command.CommandEnum;
import by.bsuir.exchange.entity.RoleEnum;
import by.bsuir.exchange.manager.exception.ManagerInitializationException;
import by.bsuir.exchange.manager.exception.ManagerOperationException;
import by.bsuir.exchange.provider.RequestAttributesNameProvider;
import by.bsuir.exchange.provider.SessionAttributesNameProvider;
import by.bsuir.exchange.repository.exception.RepositoryInitializationException;
import by.bsuir.exchange.repository.exception.RepositoryOperationException;
import by.bsuir.exchange.repository.factory.WalletSqlRepositoryFactory;
import by.bsuir.exchange.specification.Specification;
import by.bsuir.exchange.specification.wallet.WalletIdSqlSpecificationFactory;
import by.bsuir.exchange.tagable.ManagerTagEnum;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The class PersonalManager is used to manage state of persistence object holding
 * data about the wallet of actors in response to the commands.
 */
public class WalletManager extends AbstractManager<WalletBean> implements CommandHandler {
    private RoleEnum role;

    public WalletManager(RoleEnum role) throws ManagerInitializationException {
        try {
            this.repository = WalletSqlRepositoryFactory.getRepository(role);
            this.role = role;
            this.tag = ManagerTagEnum.WALLET_MANAGER;
        } catch (RepositoryInitializationException e) {
            throw new ManagerInitializationException(e);
        }
    }

    @Override
    public boolean handle(HttpServletRequest request, CommandEnum command) throws ManagerOperationException {
        boolean status;
        try {
            switch (command) {
                case REQUEST_DELIVERY: {
                    status = requestDelivery(request);
                    break;
                }
                case FINISH_DELIVERY: {
                    status = finishDelivery(request);
                    break;
                }
                case GET_PROFILE: {
                    status = getProfile(request);
                    break;
                }
                case GET_USERS: {
                    status = getUsers(request);
                    break;
                }
                case UPDATE_WALLET: {
                    status = updateWallet(request);
                    break;
                }
                default:
                    throw new ManagerOperationException("Unexpected command");
            }
        }catch (RepositoryOperationException e){
            throw new ManagerOperationException(e);
        }
        return status;
    }

    private boolean getUsers(HttpServletRequest request) throws RepositoryOperationException {
        List<UserBean> users = (List<UserBean>) request.getAttribute(RequestAttributesNameProvider.USER_LIST);
        Map<Long, ActorBean> actors = (Map<Long, ActorBean>) request.getAttribute(RequestAttributesNameProvider.ACTOR_MAP_ATTRIBUTE);
        Map<Long, WalletBean> wallets = (Map<Long, WalletBean>) request.getAttribute(RequestAttributesNameProvider.WALLET_MAP_ATTRIBUTE);
        if (wallets == null){
            wallets = new HashMap<>();
        }
        for (UserBean user : users) {
            if (user.getRole().equals(role.toString())) {
                long userId = user.getId();
                ActorBean actor = actors.get(userId);
                long actorId = actor.getId();
                Specification<WalletBean, PreparedStatement, Connection> specification =
                        WalletIdSqlSpecificationFactory.getSpecification(role, actorId);
                Optional<List<WalletBean>> optionalWallets = repository.find(specification);
                if (optionalWallets.isPresent()) {
                    WalletBean wallet = optionalWallets.get().get(0);
                    wallets.put(userId, wallet);
                }
            }
        }
        request.setAttribute(RequestAttributesNameProvider.WALLET_MAP_ATTRIBUTE, wallets);
        return true;
    }

    private boolean updateWallet(HttpServletRequest request) throws RepositoryOperationException {
        WalletBean newWallet = (WalletBean) request.getAttribute(RequestAttributesNameProvider.WALLET_ATTRIBUTE);
        HttpSession session = request.getSession();
        long id = (long) session.getAttribute(SessionAttributesNameProvider.ID);
        Specification<WalletBean, PreparedStatement, Connection> specification =
                WalletIdSqlSpecificationFactory.getSpecification(role, id);
        Optional<List<WalletBean>> optionalWallets = repository.find(specification);
        boolean status = false;
        if (optionalWallets.isPresent()){
            WalletBean foundWallet = optionalWallets.get().get(0);
            double newBalance = newWallet.getBalance();
            foundWallet.setBalance(newBalance);
            repository.update(foundWallet);
            status = true;
        }
        return status;
    }

    private boolean getProfile(HttpServletRequest request) throws RepositoryOperationException {
        HttpSession session = request.getSession();
        long id = (long) session.getAttribute(SessionAttributesNameProvider.ID);
        Specification<WalletBean, PreparedStatement, Connection> specification =
                WalletIdSqlSpecificationFactory.getSpecification(role, id);
        Optional<List<WalletBean>> optionalWallets = repository.find(specification);
        boolean status = false;
        if (optionalWallets.isPresent()){
            WalletBean actor = optionalWallets.get().get(0);
            request.setAttribute(RequestAttributesNameProvider.WALLET_ATTRIBUTE, actor);
            status = true;
        }
        return status;
    }

    private boolean finishDelivery(HttpServletRequest request) throws RepositoryOperationException {
        DeliveryBean delivery = (DeliveryBean) request.getAttribute(RequestAttributesNameProvider.DELIVERY_ATTRIBUTE);
        if (delivery.isFinished()){
            long id = role == RoleEnum.COURIER? delivery.getCourierId() : delivery.getClientId();
            Specification<WalletBean, PreparedStatement, Connection> specification =
                    WalletIdSqlSpecificationFactory.getSpecification(role, id);
            Optional<List<WalletBean>> optionalWallets = repository.find(specification);
            if (optionalWallets.isPresent()){
                OfferBean offer = (OfferBean)request.getAttribute(RequestAttributesNameProvider.OFFER_ATTRIBUTE);
                double price = offer.getPrice();
                WalletBean wallet = optionalWallets.get().get(0);
                double balance = wallet.getBalance();
                if (role == RoleEnum.CLIENT){
                    balance -= price;
                }else {
                    balance += price;
                }
                wallet.setBalance(balance);
                repository.update(wallet);
            }
        }
        return true;
    }

    private boolean requestDelivery(HttpServletRequest request) throws RepositoryOperationException {
        DeliveryBean delivery = (DeliveryBean) request.getAttribute(RequestAttributesNameProvider.DELIVERY_ATTRIBUTE);
        long id = role == RoleEnum.COURIER? delivery.getCourierId() : delivery.getClientId();
        Specification<WalletBean, PreparedStatement, Connection> specification = WalletIdSqlSpecificationFactory.getSpecification(role, id);
        boolean status = false;
        Optional<List<WalletBean>> optionalWallets = repository.find(specification);
        if (optionalWallets.isPresent()){
            WalletBean wallet = optionalWallets.get().get(0);
            request.setAttribute(RequestAttributesNameProvider.WALLET_ATTRIBUTE, wallet);
            status = true;
        }
        return status;
    }


}
