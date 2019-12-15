package by.bsuir.exchange.repository;

import by.bsuir.exchange.bean.ActorBean;
import by.bsuir.exchange.bean.PersonalDataBean;
import by.bsuir.exchange.entity.RoleEnum;
import by.bsuir.exchange.pool.ConnectionPool;
import by.bsuir.exchange.repository.exception.RepositoryOperationException;
import by.bsuir.exchange.repository.impl.PersonalDataSqlRepository;
import by.bsuir.exchange.repository.impl.SqlRepository;
import by.bsuir.exchange.specification.Specification;
import by.bsuir.exchange.specification.personaldata.PersonalDataIdSqlSpecificationFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class PersonalDataSqlRepositoryTest extends SqlRepositoryTest {
    private static final String TRUNCATE_QUERY = "TRUNCATE TABLE client";
    private static final String REPOSITORY_ERROR = "Repository error";

    private static final String INSERT_QUERY_CLIENT =
            "INSERT INTO client (name, surname, archival, user_id) VALUES (?, ?, ?, ?)";

    private static final String UPDATE_QUERY =
            "UPDATE client SET city=?, age=? WHERE id=?";

    private static final int N_BEANS = 4;

    private static final PersonalDataBean[] personalData = {
            new PersonalDataBean(1, 18, "Samara"),
            new PersonalDataBean(1, 21, "Vilnius"),
            new PersonalDataBean(1, 29, "Minsk"),
            new PersonalDataBean(1, 23, "Moscow"),

    };

    private static final ActorBean[] clients = {
            new ActorBean(1, "John", "Travolta", 100, 1, false),
            new ActorBean(2, "Michael", "Caine", 150, 2, false ),
            new ActorBean(1, "Forest", "Gamp", 73, 3, false),
    };

    private static final String MARK = "MARK";

    private Connection connection;
    private SqlRepository<PersonalDataBean> repository;
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
                ActorBean actor = clients[i];
                statement = connection.prepareStatement(INSERT_QUERY_CLIENT);
                statement.setString(1, actor.getName());
                statement.setString(2, actor.getSurname());
                statement.setBoolean(3, actor.getArchival());
                statement.setLong(4, actor.getUserId());
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
        repository = new PersonalDataSqlRepository(pool, UPDATE_QUERY, RoleEnum.CLIENT);
    }

    @Test
    public void getPersonalDataByIdTest(){
        PersonalDataBean expected = personalData[0];
        Specification<PersonalDataBean, PreparedStatement, Connection> specification =
                PersonalDataIdSqlSpecificationFactory.getSpecification(RoleEnum.CLIENT, expected.getId());
        Optional<List<PersonalDataBean>> optionalPersonalData = Optional.empty();
        try {
            optionalPersonalData = repository.find(specification);
        } catch (RepositoryOperationException e) {
            logger.error(REPOSITORY_ERROR, e);
        }
        assert optionalPersonalData.isPresent();
        PersonalDataBean actual = optionalPersonalData.get().get(0);
        Assert.assertEquals(actual.getId(), expected.getId());
    }

    @Test
    public void updatePersonalDataTest(){
        PersonalDataBean personalDataBean = personalData[2];
        Specification<PersonalDataBean, PreparedStatement, Connection> specification =
                PersonalDataIdSqlSpecificationFactory.getSpecification(RoleEnum.CLIENT, personalDataBean.getId());
        Optional<List<PersonalDataBean>> optionalPersonalData = Optional.empty();
        try {
            optionalPersonalData = repository.find(specification);
            assert optionalPersonalData.isPresent();
            personalDataBean = optionalPersonalData.get().get(0);
            personalDataBean.setCity(MARK);
            repository.update(personalDataBean);
            optionalPersonalData = repository.find(specification);
        } catch (RepositoryOperationException e) {
            logger.error(REPOSITORY_ERROR, e);
        }
        assert optionalPersonalData.isPresent();
        personalDataBean= optionalPersonalData.get().get(0);
        String actualCity = personalDataBean.getCity();
        Assert.assertEquals(actualCity, MARK);
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
