package by.bsuir.exchange.manager;

import by.bsuir.exchange.bean.PageNavigationBean;
import by.bsuir.exchange.bean.UserBean;
import by.bsuir.exchange.chain.CommandHandler;
import by.bsuir.exchange.command.CommandEnum;
import by.bsuir.exchange.entity.RoleEnum;
import by.bsuir.exchange.manager.exception.ManagerInitializationException;
import by.bsuir.exchange.manager.exception.ManagerOperationException;
import by.bsuir.exchange.provider.ConfigurationProvider;
import by.bsuir.exchange.provider.RequestAttributesNameProvider;
import by.bsuir.exchange.provider.SessionAttributesNameProvider;
import by.bsuir.exchange.repository.exception.RepositoryInitializationException;
import by.bsuir.exchange.repository.exception.RepositoryOperationException;
import by.bsuir.exchange.repository.impl.UserSqlRepository;
import by.bsuir.exchange.specification.Specification;
import by.bsuir.exchange.specification.user.UserAllSpecification;
import by.bsuir.exchange.specification.user.UserByEmailSqlSpecification;
import by.bsuir.exchange.specification.user.UserByIdSpecification;
import by.bsuir.exchange.tagable.ManagerTagEnum;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * The class UserManager is used to manage state of persistence object holding
 * users' state in response to the commands.
 */
public class HttpSessionManager extends AbstractManager<UserBean> implements CommandHandler {
    private static final String DUPLICATE_EMAIL_ERROR = "User with the same email already exists.";
    private static final String INVALID_CREDENTIALS_ERROR = "Invalid email or password.";

    public HttpSessionManager() throws ManagerInitializationException {
        try{
            this.repository = new UserSqlRepository();
            this.tag = ManagerTagEnum.USER_MANAGER;
        } catch (RepositoryInitializationException e) {
            throw new ManagerInitializationException(e);
        }
    }

    @Override
    public boolean handle(HttpServletRequest request, CommandEnum command) throws ManagerOperationException {
        boolean status;
        try {
            switch (command) {
                case GET_USERS: {
                    status = getUsers(request);
                    break;
                }
                case DELETE_USER: {
                    status = deleteUser(request);
                    break;
                }
                case LOGIN: {
                    String attribute = RequestAttributesNameProvider.USER_ATTRIBUTE;
                    UserBean user = (UserBean) request.getAttribute(attribute);
                    status = login(request, user);
                    break;
                }
                case LOGOUT: {
                    HttpSession session = request.getSession();
                    session.invalidate();
                    status = true;
                    break;
                }
                case REGISTER: {
                    String attribute = RequestAttributesNameProvider.USER_ATTRIBUTE;
                    UserBean user = (UserBean) request.getAttribute(attribute);
                    status = register(request, user);
                    break;
                }
                case SET_LOCALE: {
                    status = changeLocale(request);
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

    private boolean deleteUser(HttpServletRequest request) throws RepositoryOperationException {
        UserBean user = (UserBean) request.getAttribute(RequestAttributesNameProvider.USER_ATTRIBUTE);
        long id = user.getId();
        Specification<UserBean, PreparedStatement, Connection> specification = new UserByIdSpecification(id);
        Optional < List< UserBean> > optionalUsers = repository.find(specification);
        boolean status;
        if (optionalUsers.isPresent()){
            UserBean foundUser = optionalUsers.get().get(0);
            foundUser.setArchival(true);
            repository.update(foundUser);
            status = true;
        }else{
            status = false;
        }
        return status;
    }

    private boolean getUsers(HttpServletRequest request) throws RepositoryOperationException {
        PageNavigationBean navigation = (PageNavigationBean) request.getAttribute(RequestAttributesNameProvider.NAVIGATION);
        long factor = ConfigurationProvider.PAGINATION_FACTOR;
        long repositorySize = repository.size() - 1;  /*Don't count admin*/
        long offset = navigation.getOffset() * factor;
        navigation.setHasNext(repositorySize - offset - factor > 0);
        navigation.setHasPrevious(offset > 0);
        Specification<UserBean, PreparedStatement, Connection> specification = new UserAllSpecification(offset);
        Optional< List<UserBean> > optionalUsers = repository.find(specification);
        List<UserBean> users = optionalUsers.orElse(Collections.emptyList());
        request.setAttribute(RequestAttributesNameProvider.USER_LIST, users);
        return true;
    }

    private boolean login(HttpServletRequest request, UserBean userRequest) throws RepositoryOperationException {
        Specification<UserBean, PreparedStatement, Connection> userEmailSpecification =
                new UserByEmailSqlSpecification(userRequest.getEmail());
        Optional<List<UserBean>> userOption = repository.find(userEmailSpecification);
        if (!userOption.isPresent()){
            request.setAttribute(RequestAttributesNameProvider.ERROR_STRING, INVALID_CREDENTIALS_ERROR);
            return false;
        }
        UserBean userFound = userOption.get().get(0);
        String actualPassword = userRequest.getPassword();
        String expectedPassword = userFound.getPassword();
        if (actualPassword.equals(expectedPassword)){
            HttpSession session = request.getSession();
            RoleEnum role = RoleEnum.valueOf(userFound.getRole().toUpperCase());
            session.setAttribute(SessionAttributesNameProvider.ROLE, role);
            request.setAttribute(RequestAttributesNameProvider.USER_ATTRIBUTE, userFound);
            return true;
        }else{
            request.setAttribute(RequestAttributesNameProvider.ERROR_STRING, INVALID_CREDENTIALS_ERROR);
            return false;
        }
    }

    private boolean register(HttpServletRequest request, UserBean user) throws RepositoryOperationException {
        Specification<UserBean, PreparedStatement, Connection> specification =
                new UserByEmailSqlSpecification(user.getEmail());
        Optional<List<UserBean>> optionalUsers = repository.find(specification);
        if (optionalUsers.isPresent()){
            request.setAttribute(RequestAttributesNameProvider.ERROR_STRING, DUPLICATE_EMAIL_ERROR);
            return false;
        }
        repository.add(user);
        HttpSession session = request.getSession();
        RoleEnum role = RoleEnum.valueOf(user.getRole().toUpperCase());
        session.setAttribute(SessionAttributesNameProvider.ROLE, role);
        return true;
    }

    private boolean changeLocale(HttpServletRequest request){
        String langAttribute = SessionAttributesNameProvider.LANG;
        String newLang = request.getParameter(langAttribute);
        HttpSession session = request.getSession();
        session.setAttribute(langAttribute, newLang);
        return true;
    }
}
