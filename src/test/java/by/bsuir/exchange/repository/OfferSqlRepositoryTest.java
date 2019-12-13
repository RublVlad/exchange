package by.bsuir.exchange.repository;


import by.bsuir.exchange.bean.OfferBean;
import by.bsuir.exchange.pool.ConnectionPool;
import by.bsuir.exchange.repository.exception.RepositoryOperationException;
import by.bsuir.exchange.repository.impl.OfferSqlRepository;
import by.bsuir.exchange.repository.impl.SqlRepository;
import by.bsuir.exchange.specification.Specification;
import by.bsuir.exchange.specification.offer.OfferByCourierIdSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class OfferSqlRepositoryTest extends SqlRepositoryTest {
    private static final String TRUNCATE_QUERY = "TRUNCATE TABLE offers";
    private static final String REPOSITORY_ERROR = "Repository error";

    private static final String INSERT_QUERY =
            "INSERT INTO offers (price, transport,  archival, courier_id) VALUES (?, ?, ?, ?)";
    private static final int N_BEANS = 4;

    private static final OfferBean[] offers = {
            new OfferBean(1, "Car", 10, 5,  false),
            new OfferBean(2, "Plane", 25, 7, false ),
            new OfferBean(3, "Scooter", 13, 42, false),
            new OfferBean(4, "Bike", 20, 11, false ),
    };

    private static final String MARK = "MARK";


    private Connection connection;
    private SqlRepository<OfferBean> repository;
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
                OfferBean offer = offers[i];
                statement = connection.prepareStatement(INSERT_QUERY);
                statement.setDouble(1, offer.getPrice());
                statement.setString(2, offer.getTransport());
                statement.setBoolean(3, offer.getArchival());
                statement.setLong(4, offer.getCourierId());
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
        repository = new OfferSqlRepository(pool);
    }

    @Test
    public void getOfferByCourierIdTest(){
        OfferBean expected = offers[0];
        Specification<OfferBean, PreparedStatement, Connection> specification =
                new OfferByCourierIdSpecification(expected.getCourierId());
        Optional<List<OfferBean>> optionalOffers = Optional.empty();
        try {
            optionalOffers = repository.find(specification);
        } catch (RepositoryOperationException e) {
            logger.error(REPOSITORY_ERROR, e);
        }
        assert optionalOffers.isPresent();
        OfferBean actual = optionalOffers.get().get(0);
        Assert.assertEquals(actual.getCourierId(), expected.getCourierId());
    }

    @Test
    public void addOfferTest(){
        OfferBean expected = null;
        OfferBean actual = null;
        try{
            expected = offers[N_BEANS - 1];
            repository.add(expected);
            Specification<OfferBean, PreparedStatement, Connection> specification =
                    new OfferByCourierIdSpecification(expected.getCourierId());
            Optional<List<OfferBean>> optionalImages = repository.find(specification);
            assert optionalImages.isPresent();
            actual = optionalImages.get().get(0);
        } catch (RepositoryOperationException e) {
            logger.error(REPOSITORY_ERROR);
        }
        assert actual != null;
        Assert.assertEquals(actual.getCourierId(), expected.getCourierId());
    }

    @Test
    public void updateOfferTest(){
        OfferBean offer = offers[2];
        Specification<OfferBean, PreparedStatement, Connection> specification =
                new OfferByCourierIdSpecification(offer.getCourierId());
        Optional<List<OfferBean>> optionalOffers = Optional.empty();
        try {
            optionalOffers = repository.find(specification);
            assert optionalOffers.isPresent();
            offer = optionalOffers.get().get(0);
            offer.setTransport(MARK);
            repository.update(offer);
            optionalOffers = repository.find(specification);
        } catch (RepositoryOperationException e) {
            logger.error(REPOSITORY_ERROR, e);
        }
        assert optionalOffers.isPresent();
        OfferBean actual = optionalOffers.get().get(0);
        String actualName = actual.getTransport();
        Assert.assertEquals(actualName, MARK);
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
