package by.bsuir.exchange.pool;

import by.bsuir.exchange.pool.exception.PoolDestructionException;
import by.bsuir.exchange.pool.exception.PoolInitializationException;
import by.bsuir.exchange.pool.exception.PoolTimeoutException;
import by.bsuir.exchange.provider.DataBaseAttributesProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public class GlobalConnectionPool extends ConnectionPool {
    private static final int CAPACITY = 20;

    private static volatile GlobalConnectionPool INSTANCE;
    private static ReentrantLock instanceLock = new ReentrantLock();
    private static Logger logger = LogManager.getRootLogger();

    private volatile boolean isClosed;

    private LinkedBlockingQueue<Connection> connections;

    public static GlobalConnectionPool getInstance() throws PoolInitializationException {
        if (INSTANCE == null){
            instanceLock.lock();
            if (INSTANCE == null){
                try {
                    INSTANCE = new GlobalConnectionPool();
                } catch (SQLException e) {
                    logger.fatal("Unable to initialize global connection pool");
                    throw new PoolInitializationException(e);
                }
            }
            instanceLock.unlock();
        }

        return INSTANCE;
    }


    private GlobalConnectionPool() throws SQLException {
        String url = DataBaseAttributesProvider.getProperty(DataBaseAttributesProvider.DATABASE_URL);
        connections = new LinkedBlockingQueue<>(CAPACITY);
        for (int i = 0; i < CAPACITY; i++){
            Connection connection =  DriverManager.getConnection(url);
            connections.add(connection);
        }
    }

    public Connection getConnection() throws PoolTimeoutException {
        Connection connection;
        try {
            connection = connections.take();
        } catch (InterruptedException e) {
            throw new PoolTimeoutException(e);
        }
        return connection;
    }

    public void releaseConnection(Connection connection) throws PoolTimeoutException {
        try {
            connections.put(connection);
        } catch (InterruptedException e) {
            throw new PoolTimeoutException(e);
        }
    }

    public void closePool() throws PoolDestructionException {
        instanceLock.lock();
        try{
            if (!isClosed){
                for (Connection connection : connections){
                    connection.close();
                }
                isClosed = true;
            }
        instanceLock.unlock();
        } catch (SQLException e) {
            logger.fatal("Unable to close global connection pool");
            throw  new PoolDestructionException(e);
        }
    }
}
