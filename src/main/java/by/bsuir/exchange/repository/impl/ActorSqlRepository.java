package by.bsuir.exchange.repository.impl;

import by.bsuir.exchange.bean.ActorBean;
import by.bsuir.exchange.entity.RoleEnum;
import by.bsuir.exchange.pool.exception.PoolOperationException;
import by.bsuir.exchange.pool.exception.PoolTimeoutException;
import by.bsuir.exchange.provider.DataBaseAttributesProvider;
import by.bsuir.exchange.repository.exception.RepositoryInitializationException;
import by.bsuir.exchange.repository.exception.RepositoryOperationException;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ActorSqlRepository extends SqlRepository<ActorBean> {
    private String updateTemplate;
    private String insertTemplate;
    private RoleEnum role;

    public ActorSqlRepository(String updateTemplate, String insertTemplate, RoleEnum role) throws RepositoryInitializationException {
        super();
        this.updateTemplate = updateTemplate;
        this.insertTemplate = insertTemplate;
        this.role = role;
    }

    @Override
    public Optional<List<ActorBean>> process(ResultSet resultSet) throws SQLException {
        Optional< List<ActorBean> > optionList = Optional.empty();
        List<ActorBean> actors = new LinkedList<>();
        while (resultSet.next()){
            String table = role == RoleEnum.CLIENT? DataBaseAttributesProvider.CLIENT_TABLE
                                                    : DataBaseAttributesProvider.COURIER_TABLE;
            String column = DataBaseAttributesProvider.NAME;
            String columnName = DataBaseAttributesProvider.getColumnName(table, column);
            String name = resultSet.getString(columnName);

            column = DataBaseAttributesProvider.SURNAME;
            columnName = DataBaseAttributesProvider.getColumnName(table, column);
            String surname = resultSet.getString(columnName);

            column = DataBaseAttributesProvider.ID;
            columnName = DataBaseAttributesProvider.getColumnName(table, column);
            long id = resultSet.getLong(columnName);

            column = DataBaseAttributesProvider.BALANCE;
            columnName = DataBaseAttributesProvider.getColumnName(table, column);
            double balance = resultSet.getDouble(columnName);

            column = DataBaseAttributesProvider.USER_ID;
            columnName = DataBaseAttributesProvider.getColumnName(table, column);
            long user_id = resultSet.getLong(columnName);

            column = DataBaseAttributesProvider.ARCHIVAL;
            columnName = DataBaseAttributesProvider.getColumnName(table, column);
            boolean archival = resultSet.getBoolean(columnName);

            ActorBean actor = new ActorBean(id, name, surname, balance, user_id, archival);
            if (role == RoleEnum.COURIER){
                column = DataBaseAttributesProvider.LIKES;
                columnName = DataBaseAttributesProvider.getColumnName(table, column);
                long likes = resultSet.getLong(columnName);
                actor.setLikes(likes);
            }

            actors.add(actor);
        }
        if (actors.size() != 0 ){
            optionList = Optional.of(actors);
        }
        return optionList;
    }

    /*Sets id of the bean argument on success*/
    @Override
    public void add(ActorBean actor) throws RepositoryOperationException {
        try{
            Connection connection = pool.getConnection();
            PreparedStatement statement = connection.prepareStatement(insertTemplate, Statement.RETURN_GENERATED_KEYS);
            if (role == RoleEnum.CLIENT){
                populateClientInsert(statement, actor);
            }else{
                populateCourierInsert(statement, actor);
            }
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0){
                throw new RepositoryOperationException("Unable to perform operation");
            }
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()){
                actor.setId(generatedKeys.getLong(1));
            }
            pool.releaseConnection(connection);
        } catch (PoolTimeoutException | SQLException | PoolOperationException e) {
            throw new RepositoryOperationException(e);
        }
    }

    private void populateCourierInsert(PreparedStatement statement, ActorBean actor) throws SQLException {
        statement.setString(1, actor.getName());
        statement.setString(2, actor.getSurname());
        statement.setDouble(3, actor.getBalance());
        statement.setLong(4, actor.getLikes());
        statement.setBoolean(5, actor.getArchival());
        statement.setLong(6, actor.getUserId());
    }

    private void populateClientInsert(PreparedStatement statement, ActorBean actor) throws SQLException {
        statement.setString(1, actor.getName());
        statement.setString(2, actor.getSurname());
        statement.setDouble(3, actor.getBalance());
        statement.setBoolean(4, actor.getArchival());
        statement.setLong(5, actor.getUserId());
    }

    @Override
    public void update(ActorBean actor) throws RepositoryOperationException {
        try {
            Connection connection = pool.getConnection();
            PreparedStatement statement = connection.prepareStatement(updateTemplate);

            if (role == RoleEnum.CLIENT){
                populateClientUpdate(statement, actor);
            }else{
                populateCourierUpdate(statement, actor);
            }

            statement.executeUpdate();
            pool.releaseConnection(connection);
        }catch (PoolTimeoutException | SQLException | PoolOperationException e) {
            throw new RepositoryOperationException(e);
        }
    }

    private void populateClientUpdate(PreparedStatement statement, ActorBean client) throws SQLException {
        statement.setString(1, client.getName());
        statement.setString(2, client.getSurname());
        statement.setDouble(3, client.getBalance());
        statement.setBoolean(4, client.getArchival());
        statement.setLong(5, client.getId());
    }

    private void populateCourierUpdate(PreparedStatement statement, ActorBean courier) throws SQLException {
        statement.setString(1, courier.getName());
        statement.setString(2, courier.getSurname());
        statement.setDouble(3, courier.getBalance());
        statement.setLong(4, courier.getLikes());
        statement.setBoolean(5, courier.getArchival());
        statement.setLong(6, courier.getId());
    }
}
