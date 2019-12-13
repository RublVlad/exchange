package by.bsuir.exchange.repository;

import by.bsuir.exchange.bean.DeliveryBean;
import by.bsuir.exchange.pool.ConnectionPool;
import by.bsuir.exchange.repository.exception.RepositoryOperationException;
import by.bsuir.exchange.repository.impl.DeliverySqlRepository;
import by.bsuir.exchange.repository.impl.SqlRepository;
import by.bsuir.exchange.specification.Specification;
import by.bsuir.exchange.specification.delivery.DeliveryByActorIdSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class DeliverySqlRepositoryTest extends SqlRepositoryTest {
    private static final String TRUNCATE_QUERY = "TRUNCATE TABLE deliveries";
    private static final String REPOSITORY_ERROR = "Repository error";

    private static final String INSERT_QUERY =
            "INSERT INTO deliveries (client_id, client_finished, courier_id, courier_finished, archival) VALUES (?, ?, ?, ?, ?)";
    private static final int N_BEANS = 4;

    private static final DeliveryBean[] deliveries = {
            new DeliveryBean(1, 1,   false, 7,   false, false),
            new DeliveryBean(2, 5,   true, 19,   true, false),
            new DeliveryBean(3, 13,   false, 1,   true, false),
            new DeliveryBean(4, 11,   true, 13,   true, false),
    };


    private Connection connection;
    private SqlRepository<DeliveryBean> repository;
    private Logger logger = LogManager.getRootLogger();

    @BeforeClass
    public void setUp(){
        try {
            connection = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            logger.fatal("Unable to get a connection", e);
        }
        try{
            PreparedStatement statement = null;
            for (int i = 0; i < N_BEANS - 1; i++){
                assert connection != null;
                DeliveryBean delivery = deliveries[i];
                statement = connection.prepareStatement(INSERT_QUERY);
                statement.setLong(1, delivery.getClientId());
                statement.setBoolean(2, delivery.getClientFinished());
                statement.setLong(3, delivery.getCourierId());
                statement.setBoolean(4, delivery.getCourierFinished());
                statement.setBoolean(5, delivery.getArchival());
                statement.executeUpdate();
            }
            assert connection != null;
            statement.close();
        }catch (SQLException e) {
            logger.fatal("Unable to set up a test table");
        }
        ConnectionPool pool = new ConnectionPool() {
            @Override
            public Connection getConnection() {
                logger.info("Get connection");
                return connection;
            }

            @Override
            public void releaseConnection(Connection con) {
                logger.info("Release connection");
            }

            @Override
            public void closePool() {
                logger.info("Close pool");
            }
        };
        repository = new DeliverySqlRepository(pool);
    }

    @Test
    public void getDelieryByActorIdTest(){
        DeliveryBean expected = deliveries[0];
        Specification<DeliveryBean, PreparedStatement, Connection> specification
                = new DeliveryByActorIdSpecification(expected.getClientId(), expected.getCourierId());
        Optional<List<DeliveryBean>> optionalDeliveries = Optional.empty();
        try {
            optionalDeliveries = repository.find(specification);
        } catch (RepositoryOperationException e) {
            logger.error(REPOSITORY_ERROR, e);
        }
        assert optionalDeliveries.isPresent();
        DeliveryBean actual = optionalDeliveries.get().get(0);
        Assert.assertEquals(actual.getClientId(), expected.getClientId());
    }

    @Test
    public void addDeliveryTest(){
        DeliveryBean expected = null;
        DeliveryBean actual = null;
        try{
            expected = deliveries[N_BEANS - 1];
            repository.add(expected);
            Specification<DeliveryBean, PreparedStatement, Connection> specification
                    = new DeliveryByActorIdSpecification(expected.getClientId(), expected.getCourierId());
            Optional<List<DeliveryBean>> optionalDeliveries = repository.find(specification);
            assert optionalDeliveries.isPresent();
            actual = optionalDeliveries.get().get(0);
        } catch (RepositoryOperationException e) {
            logger.error(REPOSITORY_ERROR);
        }
        assert actual != null;
        Assert.assertEquals(actual.getClientId(), expected.getClientId());
    }

    @Test
    public void deleteDeliveryTest(){
        DeliveryBean delivery = deliveries[2];
        Specification<DeliveryBean, PreparedStatement, Connection> specification
                = new DeliveryByActorIdSpecification(delivery.getClientId(), delivery.getCourierId());
        Optional<List<DeliveryBean>> optionalDeliveries = Optional.empty();
        try {
            optionalDeliveries = repository.find(specification);
            assert optionalDeliveries.isPresent();
            delivery = optionalDeliveries.get().get(0);
            delivery.setArchival(true);
            repository.update(delivery);
            optionalDeliveries = repository.find(specification);
        } catch (RepositoryOperationException e) {
            logger.error(REPOSITORY_ERROR, e);
        }
        Assert.assertFalse(optionalDeliveries.isPresent());
    }

    @AfterClass
    public void tearDown(){
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(TRUNCATE_QUERY);
            statement.close();
        } catch (SQLException e) {
            logger.fatal("Unable to restore state of a database's table");
        }
    }
}
