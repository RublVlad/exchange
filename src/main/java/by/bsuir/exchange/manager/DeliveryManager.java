package by.bsuir.exchange.manager;

import by.bsuir.exchange.bean.ActorBean;
import by.bsuir.exchange.bean.DeliveryBean;
import by.bsuir.exchange.bean.OfferBean;
import by.bsuir.exchange.chain.CommandHandler;
import by.bsuir.exchange.command.CommandEnum;
import by.bsuir.exchange.entity.RoleEnum;
import by.bsuir.exchange.manager.exception.ManagerInitializationException;
import by.bsuir.exchange.manager.exception.ManagerOperationException;
import by.bsuir.exchange.provider.RequestAttributesNameProvider;
import by.bsuir.exchange.provider.SessionAttributesNameProvider;
import by.bsuir.exchange.repository.exception.RepositoryInitializationException;
import by.bsuir.exchange.repository.exception.RepositoryOperationException;
import by.bsuir.exchange.repository.impl.DeliverySqlRepository;
import by.bsuir.exchange.specification.Specification;
import by.bsuir.exchange.specification.delivery.DeliveryByActorIdSpecification;
import by.bsuir.exchange.specification.delivery.DeliveryByClientIdSpecification;
import by.bsuir.exchange.specification.delivery.DeliveryByCourierIdSpecification;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DeliveryManager extends AbstractManager<DeliveryBean> implements CommandHandler {

    public DeliveryManager() throws ManagerInitializationException {
        try {
            this.repository = new DeliverySqlRepository();
        } catch (RepositoryInitializationException e) {
            throw new ManagerInitializationException(e);
        }

    }

    @Override
    public boolean handle(HttpServletRequest request, CommandEnum command) throws ManagerOperationException {
        boolean status;
        try{
            switch (command){
                case REQUEST_DELIVERY: {
                    status = requestDelivery(request);
                    break;
                }
                case GET_DELIVERIES: {
                    status = getDeliveries(request);
                    break;
                }
                case FINISH_DELIVERY: {
                    status = finishDelivery(request);
                    break;
                }
                default: {
                    throw new ManagerOperationException("Unexpected command");
                }
            }
        }catch (RepositoryOperationException e){
            throw new ManagerOperationException(e);
        }

        return status;
    }

    private boolean getDeliveries(HttpServletRequest request) throws RepositoryOperationException {
        HttpSession session = request.getSession();
        RoleEnum role = (RoleEnum) session.getAttribute(SessionAttributesNameProvider.ROLE);
        long id = (long) session.getAttribute(SessionAttributesNameProvider.ID);
        Specification<DeliveryBean, PreparedStatement, Connection> specification = role == RoleEnum.CLIENT ?
                                                new DeliveryByClientIdSpecification(id) :
                                                new DeliveryByCourierIdSpecification(id);
        List<DeliveryBean > deliveries = Collections.emptyList();
        Optional< List< DeliveryBean > > optionalDeliveries = repository.find(specification);
        if (optionalDeliveries.isPresent()){
            deliveries = optionalDeliveries.get();
        }
        request.setAttribute(RequestAttributesNameProvider.DELIVERY_LIST_ATTRIBUTE, deliveries);
        return true;
    }

    private boolean requestDelivery(HttpServletRequest request) throws RepositoryOperationException {
        DeliveryBean delivery = (DeliveryBean) request.getAttribute(RequestAttributesNameProvider.DELIVERY_ATTRIBUTE);
        delivery.setCourierFinished(false);
        delivery.setClientFinished(false);

        OfferBean offer = (OfferBean) request.getAttribute(RequestAttributesNameProvider.OFFER_ATTRIBUTE);
        double price = offer.getPrice();

        ActorBean client = (ActorBean) request.getAttribute(RequestAttributesNameProvider.ACTOR_ATTRIBUTE);
        double clientBalance = client.getBalance();

        boolean status = false;
        if (clientBalance > price){
            repository.add(delivery);
            status = true;
        }
        return status;
    }

    private boolean finishDelivery(HttpServletRequest request) throws RepositoryOperationException {
        HttpSession session = request.getSession();
        RoleEnum role = (RoleEnum) session.getAttribute(SessionAttributesNameProvider.ROLE);
        long firstId = (long) session.getAttribute(SessionAttributesNameProvider.ID);
        String secondIdString = request.getParameter("delivery");
        long secondId = Long.parseLong(secondIdString);
        if (role == RoleEnum.COURIER){
            long tmp = firstId + secondId;
            firstId = tmp - firstId;
            secondId = tmp - firstId;
        }
        DeliveryByActorIdSpecification specification = new DeliveryByActorIdSpecification(firstId, secondId);
        DeliveryBean delivery;
        boolean status;
        Optional< List< DeliveryBean > > optionalDeliveries = repository.find(specification);
        if (optionalDeliveries.isPresent()){
            delivery = optionalDeliveries.get().get(0);
            if (role == RoleEnum.CLIENT) {
                delivery.setClientFinished(true);
            } else {
                delivery.setCourierFinished(true);
            }
            repository.update(delivery);
            status = true;
        }else{
            status = false;
        }
        return status;
    }
}
