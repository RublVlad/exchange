package by.bsuir.exchange.repository;

import by.bsuir.exchange.bean.ImageBean;
import by.bsuir.exchange.pool.ConnectionPool;
import by.bsuir.exchange.repository.exception.RepositoryOperationException;
import by.bsuir.exchange.repository.impl.ImageSqlRepository;
import by.bsuir.exchange.repository.impl.SqlRepository;
import by.bsuir.exchange.specification.Specification;
import by.bsuir.exchange.specification.image.ImageByRoleIdSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class ImageSqlRepositoryTest extends SqlRepositoryTest {
    private static final String TRUNCATE_QUERY = "TRUNCATE TABLE images";
    private static final String REPOSITORY_ERROR = "Repository error";

    private static final String INSERT_QUERY =
            "INSERT INTO images (role, role_id, file_name, archival) VALUES (?, ?, ?, ?)";
    private static final int N_BEANS = 4;

    private static final ImageBean[] images = {
            new ImageBean(1, "CLIENT", 1, "logo.jpg",  false),
            new ImageBean(2, "COURIER", 2, "avatar.jpeg", false ),
            new ImageBean(3, "COURIER", 3, "summer.png", false),
            new ImageBean(4, "CLIENT", 2, "moon.jpg", false ),
    };

    private static final String MARK = "MARK";


    private Connection connection;
    private SqlRepository<ImageBean> repository;
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
                ImageBean image = images[i];
                statement = connection.prepareStatement(INSERT_QUERY);
                statement.setString(1, image.getRole());
                statement.setLong(2, image.getRoleId());
                statement.setString(3, image.getFileName());
                statement.setBoolean(4, image.getArchival());
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
        repository = new ImageSqlRepository(pool);
    }

    @Test
    public void getImageByRoleIdTest(){
        ImageBean expected = images[0];
        Specification<ImageBean, PreparedStatement, Connection> specification =
                new ImageByRoleIdSpecification(expected.getRole(), expected.getRoleId());
        Optional<List<ImageBean>> optionalImages = Optional.empty();
        try {
            optionalImages = repository.find(specification);
        } catch (RepositoryOperationException e) {
            logger.error(REPOSITORY_ERROR, e);
        }
        assert optionalImages.isPresent();
        ImageBean actual = optionalImages.get().get(0);
        Assert.assertEquals(actual.getRoleId(), expected.getRoleId());
    }

    @Test
    public void addImageTest(){
        ImageBean expected = null;
        ImageBean actual = null;
        try{
            expected = images[N_BEANS - 1];
            repository.add(expected);
            Specification<ImageBean, PreparedStatement, Connection> specification =
                    new ImageByRoleIdSpecification(expected.getRole(), expected.getRoleId());
            Optional<List<ImageBean>> optionalImages = repository.find(specification);
            assert optionalImages.isPresent();
            actual = optionalImages.get().get(0);
        } catch (RepositoryOperationException e) {
            logger.error(REPOSITORY_ERROR);
        }
        assert actual != null;
        Assert.assertEquals(actual.getRoleId(), expected.getRoleId());
    }

    @Test
    public void updateImageTest(){
        ImageBean image = images[2];
        Specification<ImageBean, PreparedStatement, Connection> specification =
                new ImageByRoleIdSpecification(image.getRole(), image.getRoleId());
        Optional<List<ImageBean>> optionalImages = Optional.empty();
        try {
            optionalImages = repository.find(specification);
            assert optionalImages.isPresent();
            image = optionalImages.get().get(0);
            image.setFileName(MARK);
            repository.update(image);
            optionalImages = repository.find(specification);
        } catch (RepositoryOperationException e) {
            logger.error(REPOSITORY_ERROR, e);
        }
        assert optionalImages.isPresent();
        ImageBean actual = optionalImages.get().get(0);
        String actualName = actual.getFileName();
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
