package by.bsuir.exchange.specification.user;

import by.bsuir.exchange.bean.UserBean;
import by.bsuir.exchange.provider.ConfigurationProvider;
import by.bsuir.exchange.specification.Specification;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserAllSpecification implements Specification<UserBean, PreparedStatement, Connection> {
    private final static String LOGIN_QUERY = "SELECT * FROM users WHERE role <> 'ADMIN' AND archival=0 LIMIT ?, ?";

    private Connection connection;
    private long offset;

    public UserAllSpecification(long offset) {
        this.offset = offset;
    }

    @Override
    public boolean specify(UserBean bean){
        return true;
    }

    @Override
    public PreparedStatement specify() throws SQLException {
        PreparedStatement statement = connection.prepareStatement(LOGIN_QUERY);
        statement.setLong(1, offset);
        statement.setLong(2, ConfigurationProvider.PAGINATION_FACTOR);
        return statement;
    }

    @Override
    public void setHelperObject(Connection connection){
        this.connection = connection;
    }
}
