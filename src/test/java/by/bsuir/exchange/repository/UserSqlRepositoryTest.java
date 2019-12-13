package by.bsuir.exchange.repository;

import by.bsuir.exchange.bean.UserBean;
import by.bsuir.exchange.pool.ConnectionPool;
import by.bsuir.exchange.repository.exception.RepositoryOperationException;
import by.bsuir.exchange.repository.impl.SqlRepository;
import by.bsuir.exchange.repository.impl.UserSqlRepository;
import by.bsuir.exchange.specification.Specification;
import by.bsuir.exchange.specification.user.UserByEmailSqlSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class UserSqlRepositoryTest extends SqlRepositoryTest{
    private static final String TRUNCATE_QUERY = "TRUNCATE TABLE users";
    private static final String REPOSITORY_ERROR = "Repository error";

    private static String INSERT_QUERY = "INSERT INTO users (email, password, role, archival) VALUES (?, ?, ?, ?)";
    private static final int N_BEANS = 4;

    private static final UserBean[] users = {
            new UserBean(1, "wayne@gmail.com", "batman1234", "CLIENT",  false),
            new UserBean(2, "travolta@gmail.com", "fiction99", "COURIER", false ),
            new UserBean(3, "hathaway@gmail.com", "cat1999", "CLIENT", false),
            new UserBean(4, "sidius@gmail.com", "star1234", "COURIER", false ),
    };

    private static final String MARK = "MARK";


    private Connection connection;
    private SqlRepository<UserBean> repository;
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
                UserBean user = users[i];
                statement = connection.prepareStatement(INSERT_QUERY);
                statement.setString(1, user.getEmail());
                statement.setString(2, user.getPassword());
                statement.setString(3, user.getRole());
                statement.setBoolean(4, user.getArchival());
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
        repository = new UserSqlRepository(pool);
    }

    @Test
    public void getUserByEmailTest(){
        UserBean expected = users[0];
        Specification<UserBean, PreparedStatement, Connection> specification =
                new UserByEmailSqlSpecification(expected.getEmail());
        Optional<List<UserBean>> optionalUsers = Optional.empty();
        try {
            optionalUsers = repository.find(specification);
        } catch (RepositoryOperationException e) {
            logger.error(REPOSITORY_ERROR, e);
        }
        assert optionalUsers.isPresent();
        UserBean actual = optionalUsers.get().get(0);
        Assert.assertEquals(actual.getEmail(), expected.getEmail());
    }

    @Test
    public void addUserTest(){
        UserBean expected = null;
        UserBean actual = null;
        try{
            expected = users[N_BEANS - 1];
            repository.add(expected);
            Specification<UserBean, PreparedStatement, Connection> specification =
                    new UserByEmailSqlSpecification(expected.getEmail());
            Optional<List<UserBean>> optionalUsers = repository.find(specification);
            assert optionalUsers.isPresent();
            actual = optionalUsers.get().get(0);
        } catch (RepositoryOperationException e) {
            logger.error(REPOSITORY_ERROR);
        }
        assert actual != null;
        Assert.assertEquals(actual.getEmail(), expected.getEmail());
    }

    @Test
    public void deleteUserTest(){
        UserBean user = users[2];
        Specification<UserBean, PreparedStatement, Connection> specification =
                new UserByEmailSqlSpecification(user.getEmail());
        Optional<List<UserBean>> optionalUsers = Optional.empty();
        try {
            optionalUsers = repository.find(specification);
            assert optionalUsers.isPresent();
            user = optionalUsers.get().get(0);
            user.setArchival(true);
            repository.update(user);
            optionalUsers = repository.find(specification);
        } catch (RepositoryOperationException e) {
            logger.error(REPOSITORY_ERROR, e);
        }
        Assert.assertFalse(optionalUsers.isPresent());
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
