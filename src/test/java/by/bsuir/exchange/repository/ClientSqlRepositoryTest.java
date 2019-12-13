package by.bsuir.exchange.repository;

import by.bsuir.exchange.bean.ActorBean;
import by.bsuir.exchange.entity.RoleEnum;
import by.bsuir.exchange.pool.ConnectionPool;
import by.bsuir.exchange.repository.exception.RepositoryInitializationException;
import by.bsuir.exchange.repository.exception.RepositoryOperationException;
import by.bsuir.exchange.repository.impl.ActorSqlRepository;
import by.bsuir.exchange.repository.impl.SqlRepository;
import by.bsuir.exchange.specification.Specification;
import by.bsuir.exchange.specification.actor.factory.ActorUserIdSqlSpecificationFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class ClientSqlRepositoryTest extends SqlRepositoryTest{
    private static final String TRUNCATE_QUERY = "TRUNCATE TABLE client";
    private static final String REPOSITORY_ERROR = "Repository error";

    private static final String INSERT_QUERY =
            "INSERT INTO client (name, surname, balance, archival, user_id) VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE_QUERY =
            "UPDATE client SET name=?, surname=?, balance=?, archival=? WHERE id=?";


    private static final ActorBean[] clients = {
          new ActorBean(1, "John", "Travolta", 100, 1, false),
          new ActorBean(2, "Michael", "Caine", 200, 2, false ),
          new ActorBean(1, "Forest", "Gamp", 50, 3, false),
          new ActorBean(2, "Ann", "Hathaway", 200, 4, false ),
    };

    private static final String MARK = "MARK";


    private Connection connection;
    private SqlRepository<ActorBean> repository;
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
            for (ActorBean actor : clients){
                assert connection != null;
                statement = connection.prepareStatement(INSERT_QUERY);
                statement.setString(1, actor.getName());
                statement.setString(2, actor.getSurname());
                statement.setDouble(3, actor.getBalance());
                statement.setBoolean(4, actor.getArchival());
                statement.setLong(5, actor.getUserId());
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
        try {
            repository = new ActorSqlRepository(pool,UPDATE_QUERY, INSERT_QUERY, RoleEnum.CLIENT);
        } catch (RepositoryInitializationException e) {
            logger.fatal("Unable to setup a repository");
        }
    }

    @Test
    public void getClientByUserIdTest(){
        ActorBean expected = clients[0];
        Specification<ActorBean, PreparedStatement, Connection> specification =
                ActorUserIdSqlSpecificationFactory.getSpecification(RoleEnum.CLIENT, expected.getUserId());
        Optional<List<ActorBean>> optionalClients = Optional.empty();
        try {
            optionalClients = repository.find(specification);
        } catch (RepositoryOperationException e) {
            logger.error(REPOSITORY_ERROR, e);
        }
        assert optionalClients.isPresent();
        ActorBean actual = optionalClients.get().get(0);
        Assert.assertEquals(actual.getUserId(), expected.getUserId());
    }

    @Test
    public void addClientTest(){
        ActorBean expected = null;
        ActorBean actual = null;
        try{
            expected = clients[3];
            repository.add(expected);
            Specification<ActorBean, PreparedStatement, Connection> specification =
                    ActorUserIdSqlSpecificationFactory.getSpecification(RoleEnum.CLIENT, expected.getUserId());
            Optional<List<ActorBean>> optionalActors = repository.find(specification);
            assert optionalActors.isPresent();
            actual = optionalActors.get().get(0);
        } catch (RepositoryOperationException e) {
            logger.error(REPOSITORY_ERROR);
        }
        assert actual != null;
        Assert.assertEquals(actual.getUserId(), expected.getUserId());
    }

    @Test
    public void updateClientTest(){
        ActorBean actor = clients[2];
        Specification<ActorBean, PreparedStatement, Connection> specification =
                ActorUserIdSqlSpecificationFactory.getSpecification(RoleEnum.CLIENT, actor.getUserId());
        Optional<List<ActorBean>> optionalClients = Optional.empty();
        try {
            optionalClients = repository.find(specification);
            assert optionalClients.isPresent();
            actor = optionalClients.get().get(0);
            actor.setName(MARK);
            repository.update(actor);
            optionalClients = repository.find(specification);
        } catch (RepositoryOperationException e) {
            logger.error(REPOSITORY_ERROR, e);
        }
        assert optionalClients.isPresent();
        actor= optionalClients.get().get(0);
        String actualName = actor.getName();
        Assert.assertEquals(actualName, MARK);
    }

    @Test
    public void transactionSuccessTest(){
        Specification<ActorBean, PreparedStatement, Connection> specification = null;
        ActorBean foundActor = null;
        try{
            repository.startTransaction();
            for (int i = 2; i < 4; i++){
                ActorBean actorBean = clients[i];
                specification = ActorUserIdSqlSpecificationFactory.getSpecification(RoleEnum.CLIENT, actorBean.getUserId());
                Optional<List<ActorBean>> optionalActors = repository.find(specification);
                assert optionalActors.isPresent();
                actorBean = optionalActors.get().get(0);
                actorBean.setName(MARK);
                repository.update(actorBean);
            }
            repository.finishTransaction();
            Optional<List<ActorBean>> optionalActors = repository.find(specification);
            assert optionalActors.isPresent();
            foundActor = optionalActors.get().get(0);
        } catch (RepositoryOperationException e) {
            e.printStackTrace();
        }
        assert foundActor != null;
        String actual = foundActor.getName();
        Assert.assertEquals(actual, MARK);
    }

    @Test
    public void transactionFailTest(){
        Specification<ActorBean, PreparedStatement, Connection> specification = null;
        ActorBean foundActor = null;
        try{
            repository.startTransaction();
            for (int i = 2; i < 4; i++){
                ActorBean actorBean = clients[i];
                specification = ActorUserIdSqlSpecificationFactory.getSpecification(RoleEnum.CLIENT, actorBean.getUserId());
                Optional<List<ActorBean>> optionalActors = repository.find(specification);
                assert optionalActors.isPresent();
                actorBean = optionalActors.get().get(0);
                actorBean.setName(MARK);
                repository.update(actorBean);
            }
            repository.abortTransaction();
            Optional<List<ActorBean>> optionalActors = repository.find(specification);
            assert optionalActors.isPresent();
            foundActor = optionalActors.get().get(0);
        } catch (RepositoryOperationException e) {
            e.printStackTrace();
        }
        assert foundActor != null;
        String actual = foundActor.getName();
        Assert.assertNotEquals(actual, MARK);
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
