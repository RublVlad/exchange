package by.bsuir.exchange.repository.impl;

import by.bsuir.exchange.bean.UserBean;
import by.bsuir.exchange.pool.ConnectionPool;
import by.bsuir.exchange.provider.DataBaseAttributesProvider;
import by.bsuir.exchange.repository.exception.RepositoryInitializationException;
import by.bsuir.exchange.tagable.RepositoryTagEnum;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class UserSqlRepository extends SqlRepository<UserBean> {
    private static final String INSERT_QUERY = "INSERT INTO users (email, password, role, archival) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET archival=? WHERE id=?";

    public UserSqlRepository() throws RepositoryInitializationException {
        super();
        this.tag = RepositoryTagEnum.USER_REPOSITORY;
    }

    public UserSqlRepository(ConnectionPool pool){
        super(pool);
    }

    @Override
    public Optional<List<UserBean>> process(ResultSet resultSet) throws SQLException {
        Optional< List< UserBean > > optionList = Optional.empty();
        List< UserBean > users = new LinkedList<>();
        while (resultSet.next()){
            String table = DataBaseAttributesProvider.USER_TABLE;
            String column = DataBaseAttributesProvider.EMAIL;
            String columnName = DataBaseAttributesProvider.getColumnName(table, column);
            String email = resultSet.getString(columnName);

            column = DataBaseAttributesProvider.PASSWORD;
            columnName = DataBaseAttributesProvider.getColumnName(table, column);
            String password = resultSet.getString(columnName);

            column = DataBaseAttributesProvider.ROLE;
            columnName = DataBaseAttributesProvider.getColumnName(table, column);
            String role = resultSet.getString(columnName);

            column = DataBaseAttributesProvider.ID;
            columnName = DataBaseAttributesProvider.getColumnName(table, column);
            long id = resultSet.getLong(columnName);

            column = DataBaseAttributesProvider.ARCHIVAL;
            columnName = DataBaseAttributesProvider.getColumnName(table, column);
            boolean archival = resultSet.getBoolean(columnName);

            UserBean user = new UserBean(id, email, password, role, archival);
            users.add(user);
        }
        if (users.size() != 0 ){
            optionList = Optional.of(users);
        }
        return optionList;
    }

    @Override
    public String getAddQuery() {
        return INSERT_QUERY;
    }

    @Override
    public void populateAddStatement(UserBean user, PreparedStatement statement) throws SQLException {
        statement.setString(1, user.getEmail());
        statement.setString(2, user.getPassword());
        statement.setString(3, user.getRole().toUpperCase());
        statement.setBoolean(4, user.getArchival());
    }

    @Override
    public String getUpdateQuery() {
        return UPDATE_QUERY;
    }

    @Override
    public void populateUpdateStatement(UserBean user, PreparedStatement statement) throws SQLException {
        statement.setBoolean(1, user.getArchival());
        statement.setLong(2, user.getId());
    }

}
