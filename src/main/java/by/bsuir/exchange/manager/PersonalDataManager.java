package by.bsuir.exchange.manager;

import by.bsuir.exchange.bean.PersonalDataBean;
import by.bsuir.exchange.chain.CommandHandler;
import by.bsuir.exchange.command.CommandEnum;
import by.bsuir.exchange.entity.RoleEnum;
import by.bsuir.exchange.manager.exception.ManagerInitializationException;
import by.bsuir.exchange.manager.exception.ManagerOperationException;
import by.bsuir.exchange.provider.RequestAttributesNameProvider;
import by.bsuir.exchange.provider.SessionAttributesNameProvider;
import by.bsuir.exchange.repository.exception.RepositoryInitializationException;
import by.bsuir.exchange.repository.exception.RepositoryOperationException;
import by.bsuir.exchange.repository.factory.PersonalDataSqlRepositoryFactory;
import by.bsuir.exchange.specification.Specification;
import by.bsuir.exchange.specification.personaldata.PersonalDataIdSqlSpecificationFactory;
import by.bsuir.exchange.tagable.ManagerTagEnum;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

public class PersonalDataManager extends AbstractManager<PersonalDataBean> implements CommandHandler {
    private RoleEnum role;

    public PersonalDataManager(RoleEnum role) throws ManagerInitializationException {
        try {
            this.repository = PersonalDataSqlRepositoryFactory.getRepository(role);
            this.role = role;
            this.tag = ManagerTagEnum.WALLET_MANAGER;
        } catch (RepositoryInitializationException e) {
            throw new ManagerInitializationException(e);
        }
    }

    @Override
    public boolean handle(HttpServletRequest request, CommandEnum command) throws ManagerOperationException {
        boolean status;
        try{
            switch (command){
                case UPDATE_PROFILE: {
                    status = updateProfile(request);
                    break;
                }
                case GET_PROFILE: {
                    status = getProfile(request);
                    break;
                }
                default:throw new ManagerOperationException("Unexpected command");
            }
        }catch (RepositoryOperationException e){
            throw new ManagerOperationException(e);
        }
        return status;
    }

    private boolean updateProfile(HttpServletRequest request) throws RepositoryOperationException {
        PersonalDataBean newData =
                (PersonalDataBean) request.getAttribute(RequestAttributesNameProvider.PERSONAL_DATA_ATTRIBUTE);
        HttpSession session = request.getSession();
        long id = (long) session.getAttribute(SessionAttributesNameProvider.ID);
        Specification<PersonalDataBean, PreparedStatement, Connection> specification =
                PersonalDataIdSqlSpecificationFactory.getSpecification(role, id);
        Optional<List<PersonalDataBean>> optionalWallets = repository.find(specification);
        boolean status = false;
        if (optionalWallets.isPresent()){
            PersonalDataBean foundWallet = optionalWallets.get().get(0);
            String newCity = newData.getCity();
            long newAge = newData.getAge();
            foundWallet.setCity(newCity);
            foundWallet.setAge(newAge);
            repository.update(foundWallet);
            status = true;
        }
        return status;
    }

    private boolean getProfile(HttpServletRequest request) throws RepositoryOperationException {
        HttpSession session = request.getSession();
        long id = (long) session.getAttribute(SessionAttributesNameProvider.ID);
        Specification<PersonalDataBean, PreparedStatement, Connection> specification =
                PersonalDataIdSqlSpecificationFactory.getSpecification(role, id);
        Optional<List<PersonalDataBean>> optionalPersonalData = repository.find(specification);
        boolean status = false;
        if (optionalPersonalData.isPresent()){
            PersonalDataBean personalData = optionalPersonalData.get().get(0);
            if (!personalData.getCity().equals( PersonalDataBean.DEFAULT)){
                request.setAttribute(RequestAttributesNameProvider.PERSONAL_DATA_ATTRIBUTE, personalData);
            }
            status = true;
        }
        return status;
    }
}
