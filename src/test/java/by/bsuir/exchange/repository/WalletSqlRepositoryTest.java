package by.bsuir.exchange.repository;

import by.bsuir.exchange.bean.ActorBean;
import by.bsuir.exchange.bean.WalletBean;
import by.bsuir.exchange.entity.RoleEnum;
import by.bsuir.exchange.pool.ConnectionPool;
import by.bsuir.exchange.repository.exception.RepositoryOperationException;
import by.bsuir.exchange.repository.impl.SqlRepository;
import by.bsuir.exchange.repository.impl.WalletSqlRepository;
import by.bsuir.exchange.specification.Specification;
import by.bsuir.exchange.specification.wallet.WalletIdSqlSpecificationFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class WalletSqlRepositoryTest extends SqlRepositoryTest{
    private static final String TRUNCATE_QUERY = "TRUNCATE TABLE client";
    private static final String REPOSITORY_ERROR = "Repository error";

    private static final String INSERT_QUERY_WALLET =
            "INSERT INTO client (balance) VALUES (?)";

    private static final String INSERT_QUERY_CLIENT =
            "INSERT INTO client (name, surname, archival, user_id) VALUES (?, ?, ?, ?)";


    private static final String UPDATE_QUERY =
            "UPDATE client SET balance=? WHERE id=?";

    private static final int N_BEANS = 4;

    private static final WalletBean[] wallets = {
            new WalletBean(1,  100),
            new WalletBean(2,  150),
            new WalletBean(3,  100.17),
    };

    private static final ActorBean[] clients = {
            new ActorBean(1, "John", "Travolta", 100, 1, false),
            new ActorBean(2, "Michael", "Caine", 150, 2, false ),
            new ActorBean(1, "Forest", "Gamp", 73, 3, false),
    };

    private static final double MARK = -42;

    private Connection connection;
    private SqlRepository<WalletBean> repository;
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
        repository = new WalletSqlRepository(pool, UPDATE_QUERY, INSERT_QUERY_WALLET, RoleEnum.CLIENT);
    }

    @Test
    public void getWalletByIdTest(){
        WalletBean expected = wallets[0];
        Specification<WalletBean, PreparedStatement, Connection> specification =
                WalletIdSqlSpecificationFactory.getSpecification(RoleEnum.CLIENT, expected.getId());
        Optional<List<WalletBean>> optionalWallets = Optional.empty();
        try {
            optionalWallets = repository.find(specification);
        } catch (RepositoryOperationException e) {
            logger.error(REPOSITORY_ERROR, e);
        }
        assert optionalWallets.isPresent();
        WalletBean actual = optionalWallets.get().get(0);
        Assert.assertEquals(actual.getId(), expected.getId());
    }

    @Test
    public void updateWalletTest(){
        WalletBean wallet = wallets[2];
        Specification<WalletBean, PreparedStatement, Connection> specification =
                WalletIdSqlSpecificationFactory.getSpecification(RoleEnum.CLIENT, wallet.getId());
        Optional<List<WalletBean>> optionalWallets = Optional.empty();
        try {
            optionalWallets = repository.find(specification);
            assert optionalWallets.isPresent();
            wallet = optionalWallets.get().get(0);
            wallet.setBalance(MARK);
            repository.update(wallet);
            optionalWallets = repository.find(specification);
        } catch (RepositoryOperationException e) {
            logger.error(REPOSITORY_ERROR, e);
        }
        assert optionalWallets.isPresent();
        wallet= optionalWallets.get().get(0);
        double actualBalance = wallet.getBalance();
        Assert.assertEquals(actualBalance, MARK);
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
