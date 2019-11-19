package by.bsuir.exchange.manager;

import by.bsuir.exchange.bean.CourierBean;
import by.bsuir.exchange.bean.UserBean;
import by.bsuir.exchange.chain.CommandHandler;
import by.bsuir.exchange.command.CommandEnum;
import by.bsuir.exchange.manager.exception.ManagerInitializationException;
import by.bsuir.exchange.manager.exception.ManagerOperationException;
import by.bsuir.exchange.provider.PageAttributesNameProvider;
import by.bsuir.exchange.provider.SessionAttributesNameProvider;
import by.bsuir.exchange.repository.exception.RepositoryInitializationException;
import by.bsuir.exchange.repository.exception.RepositoryOperationException;
import by.bsuir.exchange.repository.impl.CourierSqlRepository;
import by.bsuir.exchange.repository.impl.SqlRepository;
import by.bsuir.exchange.repository.specification.CourierAllSpecification;
import by.bsuir.exchange.repository.specification.Specification;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CourierManager implements CommandHandler {
    private static CourierManager instance;

    public static CourierManager getInstance() throws ManagerInitializationException {
        if (instance == null){
            SqlRepository<CourierBean> courierRepository;
            try {
                courierRepository = new CourierSqlRepository();
            } catch (RepositoryInitializationException e) {
                throw new ManagerInitializationException(e);
            }
            instance = new CourierManager(courierRepository);
        }
        return instance;
    }

    private SqlRepository<CourierBean> courierRepository;

    private CourierManager(SqlRepository<CourierBean> courierRepository){
        this.courierRepository = courierRepository;
    }


    @Override
    public boolean handle(HttpServletRequest request, CommandEnum command) throws ManagerOperationException {
        boolean status;
        switch (command){
            case REGISTER:{
                status = register(request);
                break;
            }
            case GET_COURIERS:{
                status = getCouriers(request);
                break;
            }
            default: {
                throw new ManagerOperationException("Unexpected command");
            }
        }
        return status;
    }

    private boolean register(HttpServletRequest request) throws ManagerOperationException {
        String userAttribute = PageAttributesNameProvider.USER_ATTRIBUTE;
        UserBean user = (UserBean) request.getAttribute(userAttribute);

        HttpSession session = request.getSession();
        long userId = user.getId();
        CourierBean courier = (CourierBean) request.getAttribute(PageAttributesNameProvider.COURIER_ATTRIBUTE);
        courier.setUserId(userId);
        try {
            courierRepository.add(courier);
            session.setAttribute(SessionAttributesNameProvider.ID,  courier.getId());
        } catch (RepositoryOperationException e) {
            throw new ManagerOperationException(e);
        }
        return true;
    }


    private boolean getCouriers(HttpServletRequest request) throws ManagerOperationException {
        Specification<CourierBean, PreparedStatement, Connection> specification = new CourierAllSpecification();
        List<CourierBean > couriers = Collections.emptyList();
        try {
            Optional< List< CourierBean > > optionalCouriers = courierRepository.find(specification);
            if (optionalCouriers.isPresent()){
                couriers = optionalCouriers.get();
            }
        } catch (RepositoryOperationException e) {
            throw new ManagerOperationException(e);
        }
        request.setAttribute("lst", couriers);
        return true;
    }

}