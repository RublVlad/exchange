package by.bsuir.exchange.repository.impl;

import by.bsuir.exchange.bean.Markable;
import by.bsuir.exchange.pool.ConnectionPool;
import by.bsuir.exchange.pool.GlobalConnectionPool;
import by.bsuir.exchange.pool.exception.PoolDestructionException;
import by.bsuir.exchange.pool.exception.PoolInitializationException;
import by.bsuir.exchange.pool.exception.PoolOperationException;
import by.bsuir.exchange.pool.exception.PoolTimeoutException;
import by.bsuir.exchange.repository.Repository;
import by.bsuir.exchange.repository.exception.RepositoryInitializationException;
import by.bsuir.exchange.repository.exception.RepositoryOperationException;
import by.bsuir.exchange.specification.Specification;

import java.sql.*;
import java.util.List;
import java.util.Optional;


public abstract class SqlRepository<T extends Markable> implements Repository<T, PreparedStatement, Connection> {
    private ConnectionPool pool;
    private Connection transactionConnection;

    SqlRepository() throws RepositoryInitializationException {
        try {
            pool = GlobalConnectionPool.getInstance();
        } catch (PoolInitializationException e) {
            throw new RepositoryInitializationException(e);
        }
    }

    SqlRepository(ConnectionPool pool){
        this.pool = pool;
    }

    @Override
    public Optional<List<T>> find(Specification<T, PreparedStatement, Connection> specification) throws RepositoryOperationException {
        Optional< List<T> > list;
        PreparedStatement preparedStatement = null;
        try{
            Connection connection = pool.getConnection();
            specification.setHelperObject(connection);
            preparedStatement = specification.specify();
            ResultSet resultSet = preparedStatement.executeQuery();
            list = process(resultSet);
            pool.releaseConnection(connection);
        } catch (PoolTimeoutException | SQLException | PoolOperationException e) {
            throw new RepositoryOperationException(e);
        } catch (Exception e) {
            throw new RepositoryOperationException(e);
        } finally {
            if (preparedStatement != null){
                try{
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    public abstract Optional< List<T> > process(ResultSet resultSet) throws SQLException;
    public abstract String getAddQuery();
    public abstract void populateAddStatement(T entity, PreparedStatement statement) throws SQLException;
    public abstract String getUpdateQuery();
    public abstract void populateUpdateStatement(T entity, PreparedStatement statement) throws SQLException;

    @Override
    public void add(T entity) throws RepositoryOperationException {
        String query = getAddQuery();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try{
            connection = pool.getConnection();
            preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            populateAddStatement(entity, preparedStatement);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0){
                throw new RepositoryOperationException("Unable to perform operation");
            }
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()){
                entity.setId(generatedKeys.getLong(1));
            }
        } catch (PoolTimeoutException | SQLException | PoolOperationException e) {
            throw new RepositoryOperationException(e);
        }
        finally {
            if (connection != null){
                try {
                    pool.releaseConnection(connection);
                } catch (PoolTimeoutException e) {
                    e.printStackTrace();    //FIXME log
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void update(T entity) throws RepositoryOperationException {
        Connection connection = null;
        PreparedStatement statement = null;
        try{
            String template = getUpdateQuery();
            connection = pool.getConnection();
            statement = connection.prepareStatement(template);
            populateUpdateStatement(entity, statement);
            statement.executeUpdate();
        } catch (PoolTimeoutException | SQLException | PoolOperationException e) {
            throw new RepositoryOperationException(e);
        }finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null){
                try {
                    pool.releaseConnection(connection);
                } catch (PoolTimeoutException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    public void startTransaction() throws RepositoryOperationException {
        try {
            transactionConnection = pool.getConnection();
            transactionConnection.setAutoCommit(false);
        } catch (PoolTimeoutException | PoolOperationException | SQLException e) {
            throw new RepositoryOperationException(e);
        }
    }

    public void abortTransaction() throws RepositoryOperationException {
        try {
            transactionConnection.rollback();
            transactionConnection.setAutoCommit(true);
            pool.releaseConnection(transactionConnection);
        } catch (PoolTimeoutException | SQLException e) {
            throw new RepositoryOperationException(e);
        }
    }

    public void finishTransaction() throws RepositoryOperationException {
        try {
            transactionConnection.commit();
            transactionConnection.setAutoCommit(true);
            pool.releaseConnection(transactionConnection);
        } catch (PoolTimeoutException | SQLException e) {
            throw new RepositoryOperationException(e);
        }
    }

    public void closeRepository() throws RepositoryOperationException {
        try {
            pool.closePool();
        } catch (PoolDestructionException e) {
            throw new RepositoryOperationException(e);
        }
    }

    public <T2 extends Markable> SqlRepository<T> pack(SqlRepository<T2> other) throws RepositoryInitializationException { //Two only
        try {
            if (this.pool == GlobalConnectionPool.getInstance()){
                this.pool = ConnectionPool.getLocalPool();
            }
            if (other.pool == GlobalConnectionPool.getInstance()){
                other.pool = ConnectionPool.getLocalPool();
            }
            this.pool.combine(other.pool);
        } catch (PoolInitializationException e) {
            throw new RepositoryInitializationException(e);
        }
        return this;
    }

}
