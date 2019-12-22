package by.bsuir.exchange.listener;

import by.bsuir.exchange.pool.GlobalConnectionPool;
import by.bsuir.exchange.pool.exception.PoolDestructionException;
import by.bsuir.exchange.pool.exception.PoolInitializationException;
import by.bsuir.exchange.provider.DataBaseAttributesProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

@WebListener
public class ContextListener implements ServletContextListener {
    private GlobalConnectionPool pool;
    private Logger logger = LogManager.getRootLogger();

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            String driverName = DataBaseAttributesProvider.getProperty(DataBaseAttributesProvider.DRIVER_NAME);
            Class.forName(driverName);
            pool = GlobalConnectionPool.getInstance();
        } catch (PoolInitializationException | ClassNotFoundException e) {
            logger.fatal("Unable to create the global connection pool", e);
            throw new RuntimeException(e);
        }

        try {
            String chainFactoryClassName = "by.bsuir.exchange.chain.ChainFactory";
            Class.forName(chainFactoryClassName);
        } catch (ClassNotFoundException e) {
            Logger logger = LogManager.getRootLogger();
            logger.fatal("Unable to load the chain factory class");
        }

        try {
            String chainFactoryClassName = "by.bsuir.exchange.chain.ManagerFactory";
            Class.forName(chainFactoryClassName);
        } catch (ClassNotFoundException e) {
            Logger logger = LogManager.getRootLogger();
            logger.fatal("Unable to load the chain manager factory class");
        }

        try {
            String chainFactoryClassName = "by.bsuir.exchange.chain.ValidatorFactory";
            Class.forName(chainFactoryClassName);
        } catch (ClassNotFoundException e) {
            Logger logger = LogManager.getRootLogger();
            logger.fatal("Unable to load the chain validator factory class");
        }

        try {
            String chainFactoryClassName = "by.bsuir.exchange.chain.BeanCreatorFactory";
            Class.forName(chainFactoryClassName);
        } catch (ClassNotFoundException e) {
            Logger logger = LogManager.getRootLogger();
            logger.fatal("Unable to load the chain bean creator factory class");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        try {
            pool.closePool();
        } catch (PoolDestructionException e) {
            logger.fatal("Unable to  close global connection pool", e);
            throw new RuntimeException(e);
        }
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                Logger logger = LogManager.getRootLogger();
                logger.info( String.format("deregistering jdbc driver: %s", driver));
            } catch (SQLException e) {
                Logger logger = LogManager.getRootLogger();
                logger.fatal(String.format("Error deregistering driver %s", driver), e);
            }

        }
    }
}
