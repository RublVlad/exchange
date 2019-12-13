package by.bsuir.exchange.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

public class SqlRepositoryTest {
    static final String URL = "jdbc:mysql://localhost:3306/exchange_test?user=datagrip&password=DATAGRIP&useUnicode=yes&characterEncoding=UTF-8";
    private static final String DRIVER_NAME = "com.mysql.cj.jdbc.Driver";
    private static final String ENVIRONMENT_SETUP = "Start to setup environment";
    private static final String ENVIRONMENT_DESTRUCTION = "Start to tear down environment";

    private Logger logger = LogManager.getRootLogger();

    @BeforeTest
    public void setUpEnvironment(){
        logger.info(ENVIRONMENT_SETUP);
        try {
            Class.forName(DRIVER_NAME);
        } catch (ClassNotFoundException e) {
            Logger logger = LogManager.getRootLogger();
            logger.fatal("Unable to load a sql driver", e);
        }
    }

    @AfterTest
    public void tearDownEnvironment(){
        logger.info(ENVIRONMENT_DESTRUCTION);
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
