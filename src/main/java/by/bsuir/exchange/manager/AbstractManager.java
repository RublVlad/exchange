package by.bsuir.exchange.manager;

import by.bsuir.exchange.bean.Markable;
import by.bsuir.exchange.chain.CommandHandler;
import by.bsuir.exchange.command.CommandEnum;
import by.bsuir.exchange.manager.exception.ManagerOperationException;
import by.bsuir.exchange.provider.SessionAttributesNameProvider;
import by.bsuir.exchange.repository.exception.RepositoryInitializationException;
import by.bsuir.exchange.repository.exception.RepositoryOperationException;
import by.bsuir.exchange.repository.impl.SqlRepository;
import by.bsuir.exchange.tag.ManagerTagEnum;
import by.bsuir.exchange.tag.Tagable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.LinkedList;

public abstract class AbstractManager<T extends Markable> implements CommandHandler, Tagable {
    ManagerTagEnum tag;
    SqlRepository<T> repository;
    LinkedList<AbstractManager> abstractManagers;


    public abstract boolean handle(HttpServletRequest request, CommandEnum command) throws ManagerOperationException;

    public static <T extends Markable> AbstractManager<T> createTransactionalManager(AbstractManager<T> manager){
        AbstractManager<T> transactional = new AbstractManager<>() {
            private static final String START_TRANSACTION_LOG_TEMPLATE =
                    "Start to perform transaction for %s with id - %d. Command : %s, Repository : %s";
            private static final String SUCCESS_TRANSACTION_LOG_TEMPLATE =
                    "Transaction completed for %s with id - %d. Command : %s, Repository : %s";
            private static final String ABORT_TRANSACTION_LOG_TEMPLATE =
                    "Transaction aborted for %s with id - %d. Command : %s, Repository : %s";
            private Logger logger = LogManager.getRootLogger();


            @Override
            public boolean handle(HttpServletRequest request, CommandEnum command) throws ManagerOperationException {
                boolean status;
                try{
                    HttpSession session = request.getSession();
                    String role = (String) session.getAttribute(SessionAttributesNameProvider.ROLE);
                    long id = (long) session.getAttribute(SessionAttributesNameProvider.ID);
                    String commandString = command.toString();
                    String repositoryTag = getTag();
                    String startLog = String.format(START_TRANSACTION_LOG_TEMPLATE, role, id, commandString, repositoryTag);
                    logger.info(startLog);
                    repository.startTransaction();
                    status = true;
                    for (AbstractManager m : abstractManagers){
                        status = m.handle(request, command);
                        if (!status){
                            break;
                        }
                    }
                    if (status){
                        String successLog = String.format(SUCCESS_TRANSACTION_LOG_TEMPLATE, role, id, commandString, repositoryTag);
                        logger.info(successLog);
                        repository.finishTransaction();
                    }else{
                        String failureLog = String.format(ABORT_TRANSACTION_LOG_TEMPLATE, role, id, commandString, repositoryTag);
                        logger.warn(failureLog);
                        repository.abortTransaction();
                    }
                } catch (RepositoryOperationException e) {
                    throw new ManagerOperationException(e);
                }
                return status;
            }

            @Deprecated
            public void closeManager() throws ManagerOperationException {
                for (Iterator<AbstractManager> it = abstractManagers.descendingIterator(); it.hasNext();){
                    AbstractManager m = it.next();
                    m.closeManager();
                }
            }
        };
        transactional.repository = manager.repository;
        transactional.tag = manager.tag;
        transactional.abstractManagers = new LinkedList<>();
        transactional.abstractManagers.add(manager);
        return transactional;
    }

    @Override
    public String getTag(){
        return tag.toString();
    }

    public <T2 extends Markable> AbstractManager<T> combine(AbstractManager<T2> other) throws ManagerOperationException {
        try {
            repository.pack(other.repository);
            if (abstractManagers == null){
                abstractManagers = new LinkedList<>();
            }
            abstractManagers.add(other);
        } catch (RepositoryInitializationException e) {
            throw new ManagerOperationException(e);
        }
        return this;
    }

    public void closeManager() throws ManagerOperationException {
        try {
            repository.closeRepository();
        } catch (RepositoryOperationException e) {
            throw new ManagerOperationException(e);
        }
    }
}
