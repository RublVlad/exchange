package by.bsuir.exchange.repository;

import by.bsuir.exchange.bean.Markable;
import by.bsuir.exchange.repository.exception.RepositoryOperationException;
import by.bsuir.exchange.specification.Specification;

import java.util.List;
import java.util.Optional;

public interface Repository<T extends Markable, R, H> {
    void add(T entity) throws RepositoryOperationException;
    Optional< List<T> > find(Specification<T, R, H> specification) throws RepositoryOperationException;
    void update(T entity) throws RepositoryOperationException;
}
