package by.bsuir.exchange.pool;

import by.bsuir.exchange.pool.exception.PoolDestructionException;
import by.bsuir.exchange.pool.exception.PoolInitializationException;
import by.bsuir.exchange.pool.exception.PoolOperationException;
import by.bsuir.exchange.pool.exception.PoolTimeoutException;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class ConnectionPool {
    ConnectionPool outer;

    public abstract Connection getConnection() throws PoolTimeoutException, PoolOperationException;
    public abstract void releaseConnection(Connection con) throws PoolTimeoutException;
    public abstract void closePool() throws PoolDestructionException;

    public static ConnectionPool getLocalPool() throws PoolInitializationException {
        ConnectionPool localPool = new ConnectionPool(){
            private Connection connection;
            private int nConnectionHold;

            @Override
            public Connection getConnection() throws PoolOperationException, PoolTimeoutException {
                if (connection == null){
                    connection = outer.getConnection();
                }
                ++nConnectionHold;
                return connection;
            }

            @Override
            public void releaseConnection(Connection con) throws PoolTimeoutException {
                --nConnectionHold;
            }

            @Override
            public void closePool() throws PoolDestructionException {
                if (nConnectionHold != 0) {
                    throw new PoolDestructionException("Number of given connections is not equal to returned");
                }
                if (connection != null){
                    try {
                        outer.releaseConnection(connection);
                    } catch ( PoolTimeoutException e) {
                        throw new PoolDestructionException(e);
                    }
                }
            }
        };
        localPool.outer = GlobalConnectionPool.getInstance();
        return localPool;
    }

    public ConnectionPool combine(ConnectionPool other){
        other.outer = this;
        return this;
    }
}
