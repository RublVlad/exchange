package by.bsuir.exchange.manager;

import by.bsuir.exchange.bean.Markable;
import by.bsuir.exchange.chain.CommandHandler;
import by.bsuir.exchange.command.CommandEnum;
import by.bsuir.exchange.manager.exception.ManagerOperationException;
import by.bsuir.exchange.repository.exception.RepositoryInitializationException;
import by.bsuir.exchange.repository.exception.RepositoryOperationException;
import by.bsuir.exchange.repository.impl.SqlRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.LinkedList;

public abstract class AbstractManager<T extends Markable> implements CommandHandler {
    SqlRepository<T> repository;
    LinkedList<AbstractManager> abstractManagers;

    public abstract boolean handle(HttpServletRequest request, CommandEnum command) throws ManagerOperationException;

    public static <T extends Markable> AbstractManager<T> createTransactionalManager(AbstractManager<T> manager){
        AbstractManager<T> transactional = new AbstractManager<>() {

            @Override
            public boolean handle(HttpServletRequest request, CommandEnum command) throws ManagerOperationException {
                boolean status;
                try{
                    repository.startTransaction();
                    status = true;
                    for (AbstractManager m : abstractManagers){
                        status = m.handle(request, command);
                        if (!status){
                            break;
                        }
                    }
                    if (status){
                        repository.finishTransaction();
                    }else{
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
        transactional.abstractManagers = new LinkedList<>();
        transactional.abstractManagers.add(manager);
        return transactional;
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
