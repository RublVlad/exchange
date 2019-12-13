package by.bsuir.exchange.repository;

import by.bsuir.exchange.bean.RelationBean;
import by.bsuir.exchange.pool.ConnectionPool;
import by.bsuir.exchange.repository.exception.RepositoryOperationException;
import by.bsuir.exchange.repository.impl.ImageSqlRepository;
import by.bsuir.exchange.repository.impl.RelationSqlRepository;
import by.bsuir.exchange.repository.impl.SqlRepository;
import by.bsuir.exchange.specification.Specification;
import by.bsuir.exchange.specification.image.ImageByRoleIdSpecification;
import by.bsuir.exchange.specification.relation.RelationByActorIdSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class RelationSqlRepositoryTest extends SqlRepositoryTest{
    private static final String TRUNCATE_QUERY = "TRUNCATE TABLE relations";
    private static final String REPOSITORY_ERROR = "Repository error";

    private static final String INSERT_QUERY =
            "INSERT INTO relations (client_id, courier_id, relation) VALUES (?, ?, ?)";
    private static final int N_BEANS = 4;

    private static final RelationBean[] relations = {
            new RelationBean(1, 1, 1, "LIKE"),
            new RelationBean(2, 2, 17, "LIKE"),
            new RelationBean(3, 11, 15, "NONE"),
            new RelationBean(4, 100, 97, "LIKE"),
    };

    private static final String MARK = "MARK";


    private Connection connection;
    private SqlRepository<RelationBean> repository;
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
                RelationBean relation = relations[i];
                statement = connection.prepareStatement(INSERT_QUERY);
                statement.setLong(1, relation.getClientId());
                statement.setLong(2, relation.getCourierId());
                statement.setString(3, relation.getRelation());
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
        repository = new RelationSqlRepository(pool);
    }

    @Test
    public void getRelationByActorIdTest(){
        RelationBean expected = relations[0];
        Specification<RelationBean, PreparedStatement, Connection> specification =
                new RelationByActorIdSpecification(expected.getClientId(), expected.getCourierId());
        Optional<List<RelationBean>> optionalRelations = Optional.empty();
        try {
            optionalRelations = repository.find(specification);
        } catch (RepositoryOperationException e) {
            logger.error(REPOSITORY_ERROR, e);
        }
        assert optionalRelations.isPresent();
        RelationBean actual = optionalRelations.get().get(0);
        Assert.assertEquals(actual.getClientId(), expected.getClientId());
    }

    @Test
    public void addRelationTest(){
        RelationBean expected = null;
        RelationBean actual = null;
        try{
            expected = relations[N_BEANS - 1];
            repository.add(expected);
            Specification<RelationBean, PreparedStatement, Connection> specification =
                    new RelationByActorIdSpecification(expected.getClientId(), expected.getCourierId());
            Optional<List<RelationBean>> optionalRelations = repository.find(specification);
            assert optionalRelations.isPresent();
            actual = optionalRelations.get().get(0);
        } catch (RepositoryOperationException e) {
            logger.error(REPOSITORY_ERROR);
        }
        assert actual != null;
        Assert.assertEquals(actual.getClientId(), expected.getClientId());
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
