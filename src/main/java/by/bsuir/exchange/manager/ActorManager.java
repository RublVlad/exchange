package by.bsuir.exchange.manager;

import by.bsuir.exchange.bean.*;
import by.bsuir.exchange.command.CommandEnum;
import by.bsuir.exchange.entity.RoleEnum;
import by.bsuir.exchange.manager.exception.ManagerInitializationException;
import by.bsuir.exchange.manager.exception.ManagerOperationException;
import by.bsuir.exchange.provider.RequestAttributesNameProvider;
import by.bsuir.exchange.provider.SessionAttributesNameProvider;
import by.bsuir.exchange.repository.exception.RepositoryInitializationException;
import by.bsuir.exchange.repository.exception.RepositoryOperationException;
import by.bsuir.exchange.repository.factory.ActorSqlRepositoryFactory;
import by.bsuir.exchange.specification.Specification;
import by.bsuir.exchange.specification.actor.factory.ActorIdSqlSpecificationFactory;
import by.bsuir.exchange.specification.actor.factory.ActorUserIdSqlSpecificationFactory;
import by.bsuir.exchange.tagable.ManagerTagEnum;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;

/**
 * The class ActorManager is used to manage state of persistence object holding
 * actors' state in response to the commands.
 */
public class ActorManager extends AbstractManager<ActorBean>{

    private RoleEnum role;

    public ActorManager(RoleEnum role) throws ManagerInitializationException {
        try {
            this.repository = ActorSqlRepositoryFactory.getRepository(role);
            this.role = role;
            this.tag = ManagerTagEnum.ACTOR_MANAGER;
        } catch (RepositoryInitializationException e) {
            throw new ManagerInitializationException(e);
        }
    }

    @Override
    public boolean handle(HttpServletRequest request, CommandEnum command) throws ManagerOperationException {
        boolean status;
        try{
            switch (command){
                case REGISTER:{
                    status = register(request);
                    break;
                }
                case LOGIN: {
                    status = login(request);
                    break;
                }
                case GET_USERS: {
                    status = getUsers(request);
                    break;
                }
                case DELETE_USER: {
                    status = deleteUser(request);
                    break;
                }
                case GET_PROFILE: {
                    status = getProfile(request);
                    break;
                }
                case UPDATE_PROFILE: {
                    status = updateProfile(request);
                    break;
                }
                case GET_OFFERS: {
                    status = getOffers(request);
                    break;
                }
                case GET_DELIVERIES: {
                    status = getDeliveries(request);
                    break;
                }
                case LIKE_COURIER: {
                    status = likeCourier(request);
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

    private boolean likeCourier(HttpServletRequest request) throws RepositoryOperationException {
        RelationBean relation = (RelationBean) request.getAttribute(RequestAttributesNameProvider.RELATION_ATTRIBUTE);
        long courierId = relation.getCourierId();
        Specification<ActorBean, PreparedStatement, Connection> specification =
                ActorIdSqlSpecificationFactory.getSpecification(RoleEnum.COURIER, courierId);
        Optional< List< ActorBean>> optionalActors = repository.find(specification);
        boolean status;
        if (optionalActors.isPresent()){
            ActorBean courier = optionalActors.get().get(0);
            long nLikes = courier.getLikes() + 1;
            courier.setLikes(nLikes);
            repository.update(courier);
            status = true;
        }else{
            status = false;
        }
        return status;
    }

    private boolean deleteUser(HttpServletRequest request) throws RepositoryOperationException {
        UserBean user = (UserBean) request.getAttribute(RequestAttributesNameProvider.USER_ATTRIBUTE);
        Specification<ActorBean, PreparedStatement, Connection> specification =
                ActorUserIdSqlSpecificationFactory.getSpecification(role, user.getId());
        Optional< List<ActorBean> > optionalActors = repository.find(specification);
        boolean status;
        if (optionalActors.isPresent()){
            ActorBean foundActor = optionalActors.get().get(0);
            foundActor.setArchival(true);
            repository.update(foundActor);
            request.setAttribute(RequestAttributesNameProvider.ACTOR_ATTRIBUTE, foundActor);
            status = true;
        }else{
            status = false;
        }
        return  status;
    }

    private boolean getUsers(HttpServletRequest request) throws RepositoryOperationException {
        List<UserBean> users = (List<UserBean>) request.getAttribute(RequestAttributesNameProvider.USER_LIST);
        Map<Long, ActorBean> actors = (Map<Long, ActorBean>) request.getAttribute(RequestAttributesNameProvider.ACTOR_MAP_ATTRIBUTE);
        if (actors == null){
            actors = new HashMap<>();
        }
        for (Iterator<UserBean> iterator = users.iterator(); iterator.hasNext();){
            UserBean user = iterator.next();
            if (user.getRole().equals( role.toString() )){
                long userId = user.getId();
                Specification<ActorBean, PreparedStatement, Connection> specification =
                        ActorUserIdSqlSpecificationFactory.getSpecification(role, userId);
                Optional< List<ActorBean> > optionalActors = repository.find(specification);
                if (optionalActors.isPresent()){
                    ActorBean actor = optionalActors.get().get(0);
                    actors.put(userId, actor);
                }else{
                    iterator.remove();
                }
            }
        }
        request.setAttribute(RequestAttributesNameProvider.ACTOR_MAP_ATTRIBUTE, actors);
        return true;
    }

    private boolean getProfile(HttpServletRequest request) throws RepositoryOperationException {
        HttpSession session = request.getSession();
        long id = (long) session.getAttribute(SessionAttributesNameProvider.ID);
        Specification<ActorBean, PreparedStatement, Connection> specification =
                ActorIdSqlSpecificationFactory.getSpecification(role, id);
        Optional<List<ActorBean>> optionalActors = repository.find(specification);
        boolean status;
        if (optionalActors.isPresent()){
            ActorBean actor = optionalActors.get().get(0);
            request.setAttribute(RequestAttributesNameProvider.ACTOR_ATTRIBUTE, actor);
            status = true;
        }else{
            status = false;
        }
        return status;
    }

    private boolean getDeliveries(HttpServletRequest request) throws RepositoryOperationException {
        List<DeliveryBean> deliveries = (List<DeliveryBean>) request.getAttribute(RequestAttributesNameProvider.DELIVERY_LIST_ATTRIBUTE);
        List<ActorBean> actors = new LinkedList<>();
        for (DeliveryBean delivery : deliveries){
            long id = role == RoleEnum.CLIENT? delivery.getClientId() : delivery.getCourierId();
            Specification<ActorBean, PreparedStatement, Connection> spec =
                    ActorIdSqlSpecificationFactory.getSpecification(role, id);
            Optional<List<ActorBean>> optionalActors = repository.find(spec);
            if (optionalActors.isPresent()){
                ActorBean courier = optionalActors.get().get(0);
                actors.add(courier);
            }else{
                deliveries.remove(delivery);
            }
        }
        request.setAttribute(RequestAttributesNameProvider.DELIVERY_LIST_ATTRIBUTE, deliveries);
        String actorAttributeList = role == RoleEnum.CLIENT? RequestAttributesNameProvider.CLIENT_LIST
                                                        : RequestAttributesNameProvider.COURIER_LIST;
        request.setAttribute(actorAttributeList, actors);
        return true;
    }

    private boolean login(HttpServletRequest request) throws RepositoryOperationException {
        UserBean user = (UserBean) request.getAttribute(RequestAttributesNameProvider.USER_ATTRIBUTE);
        RoleEnum role = RoleEnum.valueOf(user.getRole());
        if (role == RoleEnum.ADMIN){
            return true;
        }
        boolean status = false;
        long userId = user.getId();
        Specification<ActorBean, PreparedStatement, Connection> specification = ActorUserIdSqlSpecificationFactory.getSpecification(role, userId);
        Optional<List<ActorBean> > clientsOptional = repository.find(specification);
        if (clientsOptional.isPresent()){
            ActorBean client = clientsOptional.get().get(0);
            request.setAttribute(RequestAttributesNameProvider.ACTOR_ATTRIBUTE, client);
            HttpSession session = request.getSession();
            session.setAttribute(SessionAttributesNameProvider.ID, client.getId());
            status = true;
        }
        return status;
    }


    private boolean register(HttpServletRequest request) throws RepositoryOperationException {
        String userAttribute = RequestAttributesNameProvider.USER_ATTRIBUTE;
        UserBean user = (UserBean) request.getAttribute(userAttribute);
        long userId = user.getId();

        ActorBean actor = (ActorBean) request.getAttribute(RequestAttributesNameProvider.ACTOR_ATTRIBUTE);
        actor.setUserId(userId);
        repository.add(actor);
        request.setAttribute(RequestAttributesNameProvider.ACTOR_ATTRIBUTE, actor);
        HttpSession session = request.getSession();
        session.setAttribute(SessionAttributesNameProvider.ID,  actor.getId());
        return true;
    }

    private boolean updateProfile(HttpServletRequest request) throws RepositoryOperationException {
        String attribute = RequestAttributesNameProvider.ACTOR_ATTRIBUTE;
        ActorBean actor = (ActorBean) request.getAttribute(attribute);
        HttpSession session = request.getSession();
        long id = (long) session.getAttribute(SessionAttributesNameProvider.ID);
        RoleEnum role = (RoleEnum) session.getAttribute(SessionAttributesNameProvider.ROLE);
        actor.setId(id);
        boolean status;
        Specification<ActorBean, PreparedStatement, Connection> actorByIdSpecification = ActorIdSqlSpecificationFactory.getSpecification(role, id);
        Optional< List<ActorBean> >  optionalCouriers = repository.find(actorByIdSpecification);
        if (optionalCouriers.isPresent()){
            ActorBean clientFound = optionalCouriers.get().get(0);
            if (actor.getName().isEmpty()){
                actor.setName(clientFound.getName());
            }
            if (actor.getSurname().isEmpty()){
                actor.setSurname(clientFound.getSurname());
            }
            repository.update(actor);
            request.setAttribute(RequestAttributesNameProvider.ACTOR_ATTRIBUTE, actor);

            status = true;
        }else{
            status = false;
        }
        return status;
    }

    private boolean getOffers(HttpServletRequest request) throws RepositoryOperationException {
        List<OfferBean> offers = (List<OfferBean>) request.getAttribute(RequestAttributesNameProvider.OFFER_LIST_ATTRIBUTE);
        List<ActorBean> actors = new LinkedList<>();
        for (OfferBean offer : offers){
            Specification<ActorBean, PreparedStatement, Connection> spec =
                    ActorIdSqlSpecificationFactory.getSpecification(RoleEnum.COURIER, offer.getCourierId());
            Optional<List<ActorBean>> optionalActors = repository.find(spec);
            if (optionalActors.isPresent()){
                ActorBean courier = optionalActors.get().get(0);
                actors.add(courier);
            }else{
                offers.remove(offer);
            }
        }
        request.setAttribute(RequestAttributesNameProvider.OFFER_LIST_ATTRIBUTE, offers);
        request.setAttribute(RequestAttributesNameProvider.ACTOR_LIST_ATTRIBUTE, actors);
        return true;
    }
}


