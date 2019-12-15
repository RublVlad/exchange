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
import by.bsuir.exchange.tag.RepositoryTagEnum;
import by.bsuir.exchange.tag.Tagable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.List;
import java.util.Optional;


public abstract class SqlRepository<T extends Markable> implements Repository<T, PreparedStatement, Connection>, Tagable {
    private static final String SPECIFICATION_EXCEPTION = "Specification threw an exception, repository - %s";
    private static final String ERROR_RELEASING_CONNECTION = "Failed to release a connection, repository - %s";
    private static final String ERROR_CLOSING_STATEMENT = "Failed to close a statement, repository - %s";
    private static final String ERROR_GETTING_CONNECTION = "Unable to get a connection, repository - %s";
    private static final String ERROR_DURING_RESULT_PROCESSING = "An error occurred during result processing, " +
                                                                  "repository - %s";
    private static final String ERROR_DURING_STATEMENT_POPULATION = "An error occurred during statemnt population, " +
                                                                   "repository - %s";
    private static final String ERROR_CHANGING_AUTO_COMMIT_MODE = "Unable to change auto commit mode, repository - %s";
    private static final String ERROR_CLOSING_POOL = "Unable to close pool, repository - %s";
    private static final String ERROR_INITIALIZING_POOL = "Unable to get instance of a pool, repository - %s";

    RepositoryTagEnum tag;
    private ConnectionPool pool;
    private Connection transactionConnection;
    private Logger logger;

    SqlRepository() throws RepositoryInitializationException {
        try {
            pool = GlobalConnectionPool.getInstance();
            logger = LogManager.getRootLogger();
        } catch (PoolInitializationException e) {
            throw new RepositoryInitializationException(e);
        }
    }

    SqlRepository(ConnectionPool pool){
        this.pool = pool;
        this.logger = LogManager.getRootLogger();
    }

    @Override
    public String getTag(){
        return tag.toString();
    }

    @Override
    public Optional<List<T>> find(Specification<T, PreparedStatement, Connection> specification) throws RepositoryOperationException {
        Optional< List<T> > list;
        PreparedStatement preparedStatement = null;
        Connection connection = null;
        try{
            connection = pool.getConnection();
            specification.setHelperObject(connection);
            preparedStatement = specification.specify();
            ResultSet resultSet = preparedStatement.executeQuery();
            list = process(resultSet);
        } catch (PoolTimeoutException | PoolOperationException e) {
            String log = String.format(ERROR_GETTING_CONNECTION, getTag());
            logger.fatal(log, e);
            throw new RepositoryOperationException(e);
        } catch(SQLException e){
            String log = String.format(ERROR_DURING_RESULT_PROCESSING, getTag());
            logger.error(log, e);
            throw new RepositoryOperationException(e);
        }catch (Exception e) {
            String log = String.format(SPECIFICATION_EXCEPTION, getTag());
            logger.error(log, e);
            throw new RepositoryOperationException(e);
        } finally {
            if (preparedStatement != null){
                try{
                    preparedStatement.close();
                } catch (SQLException e) {
                    String log = String.format(ERROR_CLOSING_STATEMENT, getTag());
                    logger.error(log, e);
                }
            }
            if (connection != null){
                try {
                    pool.releaseConnection(connection);
                } catch (PoolTimeoutException e) {
                    String log = String.format(ERROR_RELEASING_CONNECTION, getTag());
                    logger.error(log, e);
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
        } catch (PoolTimeoutException | PoolOperationException e) {
            String log = String.format(ERROR_GETTING_CONNECTION, getTag());
            logger.fatal(log, e);
            throw new RepositoryOperationException(e);
        }catch (SQLException e){
            String log = String.format(ERROR_DURING_STATEMENT_POPULATION, getTag());
            logger.error(log, e);
            throw new RepositoryOperationException(e);
        }
        finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    String log = String.format(ERROR_CLOSING_STATEMENT, getTag());
                    logger.error(log, e);
                }
            }
            if (connection != null){
                try {
                    pool.releaseConnection(connection);
                } catch (PoolTimeoutException e) {
                    String log = String.format(ERROR_RELEASING_CONNECTION, getTag());
                    logger.error(log, e);
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
        } catch (PoolTimeoutException | PoolOperationException e) {
            String log = String.format(ERROR_GETTING_CONNECTION, getTag());
            logger.fatal(log, e);
            throw new RepositoryOperationException(e);
        } catch (SQLException e) {
            String log = String.format(ERROR_DURING_STATEMENT_POPULATION, getTag());
            logger.error(log, e);
            throw new RepositoryOperationException(e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    String log = String.format(ERROR_CLOSING_STATEMENT, getTag());
                    logger.error(log, e);
                }
            }
            if (connection != null){
                try {
                    pool.releaseConnection(connection);
                } catch (PoolTimeoutException e) {
                    String log = String.format(ERROR_RELEASING_CONNECTION, getTag());
                    logger.error(log, e);
                }
            }
        }
    }



    public void startTransaction() throws RepositoryOperationException {
        try {
            transactionConnection = pool.getConnection();
            transactionConnection.setAutoCommit(false);
        } catch (PoolTimeoutException | PoolOperationException e) {
            String log = String.format(ERROR_GETTING_CONNECTION, getTag());
            logger.fatal(log, e);
            throw new RepositoryOperationException(e);
        } catch (SQLException e) {
            String log = String.format(ERROR_CHANGING_AUTO_COMMIT_MODE, getTag());
            logger.fatal(log, e);
            throw new RepositoryOperationException(e);
        }
    }

    public void abortTransaction() throws RepositoryOperationException {
        try {
            transactionConnection.rollback();
            transactionConnection.setAutoCommit(true);
            pool.releaseConnection(transactionConnection);
        } catch (PoolTimeoutException e) {
            String log = String.format(ERROR_RELEASING_CONNECTION, getTag());
            logger.error(log, e);
            throw new RepositoryOperationException(e);
        } catch (SQLException e) {
            String log = String.format(ERROR_CHANGING_AUTO_COMMIT_MODE, getTag());
            logger.fatal(log, e);
            throw new RepositoryOperationException(e);
        }
    }

    public void finishTransaction() throws RepositoryOperationException {
        try {
            transactionConnection.commit();
            transactionConnection.setAutoCommit(true);
            pool.releaseConnection(transactionConnection);
        } catch (PoolTimeoutException e) {
            String log = String.format(ERROR_RELEASING_CONNECTION, getTag());
            logger.error(log, e);
            throw new RepositoryOperationException(e);
        } catch (SQLException e) {
            throw new RepositoryOperationException(e);
        }
    }

    public void closeRepository() throws RepositoryOperationException {
        try {
            pool.closePool();
        } catch (PoolDestructionException e) {
            String log = String.format(ERROR_CLOSING_POOL, getTag());
            logger.error(log, e);
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
            String log = String.format(ERROR_INITIALIZING_POOL, getTag());
            logger.error(log, e);
            throw new RepositoryInitializationException(e);
        }
        return this;
    }

}
