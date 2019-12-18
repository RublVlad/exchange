package by.bsuir.exchange.manager;

import by.bsuir.exchange.bean.ActorBean;
import by.bsuir.exchange.bean.RelationBean;
import by.bsuir.exchange.command.CommandEnum;
import by.bsuir.exchange.entity.RelationEnum;
import by.bsuir.exchange.manager.exception.ManagerInitializationException;
import by.bsuir.exchange.manager.exception.ManagerOperationException;
import by.bsuir.exchange.provider.RequestAttributesNameProvider;
import by.bsuir.exchange.provider.SessionAttributesNameProvider;
import by.bsuir.exchange.repository.exception.RepositoryInitializationException;
import by.bsuir.exchange.repository.exception.RepositoryOperationException;
import by.bsuir.exchange.repository.impl.RelationSqlRepository;
import by.bsuir.exchange.specification.Specification;
import by.bsuir.exchange.specification.relation.RelationByActorIdSpecification;
import by.bsuir.exchange.tagable.ManagerTagEnum;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class RelationManager extends AbstractManager<RelationBean> {

    public RelationManager() throws ManagerInitializationException {
        try {
            this.repository = new RelationSqlRepository();
            this.tag = ManagerTagEnum.RELATION_MANAGER;
        } catch (RepositoryInitializationException e) {
            throw new ManagerInitializationException(e);
        }
    }

    @Override
    public boolean handle(HttpServletRequest request, CommandEnum command) throws ManagerOperationException {
        try {
            boolean status;
            switch (command){
                case LIKE_COURIER: {
                    status = likeCourier(request);
                    break;
                }
                case GET_OFFERS: {
                    status= getOffers(request);
                    break;
                }
                default: throw new IllegalArgumentException("Unexpected command");
            }
            return status;
        }catch (RepositoryOperationException e) {
            throw new ManagerOperationException(e);
        }
    }

    private boolean getOffers(HttpServletRequest request) throws RepositoryOperationException {
        List<ActorBean> couriers = (List<ActorBean>) request.getAttribute(RequestAttributesNameProvider.ACTOR_LIST_ATTRIBUTE);
        HttpSession session = request.getSession();
        long clientId = (long) session.getAttribute(SessionAttributesNameProvider.ID);
        List<RelationBean> relations = new LinkedList<>();
        for (ActorBean courier : couriers){
            Specification<RelationBean, PreparedStatement, Connection> specification =
                    new RelationByActorIdSpecification(clientId, courier.getId());
            Optional<List<RelationBean> > optionalRelations = repository.find(specification);
            RelationBean relation;
            if (optionalRelations.isPresent()){
                relation = optionalRelations.get().get(0);
            }else{
                relation = new RelationBean();
                relation.setRelation(RelationEnum.NONE.toString());
            }
            relations.add(relation);
        }
        request.setAttribute(RequestAttributesNameProvider.RELATION_LIST_ATTRIBUTE, relations);
        return true;
    }

    private boolean likeCourier(HttpServletRequest request) throws RepositoryOperationException {
        RelationBean relationBean = (RelationBean) request.getAttribute(RequestAttributesNameProvider.RELATION_ATTRIBUTE);
        HttpSession session = request.getSession();
        long clientId = (long) session.getAttribute(SessionAttributesNameProvider.ID);
        relationBean.setClientId(clientId);
        String relation = relationBean.getRelation();
        relationBean.setRelation(relation.toUpperCase());
        repository.add(relationBean);
        return true;
    }


}
