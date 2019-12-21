package by.bsuir.exchange.repository.impl;

import by.bsuir.exchange.bean.ActorBean;
import by.bsuir.exchange.entity.RoleEnum;
import by.bsuir.exchange.pool.ConnectionPool;
import by.bsuir.exchange.provider.DataBaseAttributesProvider;
import by.bsuir.exchange.repository.exception.RepositoryInitializationException;
import by.bsuir.exchange.tagable.RepositoryTagEnum;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        this.tag = RepositoryTagEnum.ACTOR_REPOSITORY;
    }

    public ActorSqlRepository(ConnectionPool pool, String updateTemplate, String insertTemplate, RoleEnum role) {
        super(pool);
        this.updateTemplate = updateTemplate;
        this.insertTemplate = insertTemplate;
        this.role = role;
        this.tag = RepositoryTagEnum.ACTOR_REPOSITORY;
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

            column = DataBaseAttributesProvider.USER_ID;
            columnName = DataBaseAttributesProvider.getColumnName(table, column);
            long user_id = resultSet.getLong(columnName);

            column = DataBaseAttributesProvider.ARCHIVAL;
            columnName = DataBaseAttributesProvider.getColumnName(table, column);
            boolean archival = resultSet.getBoolean(columnName);

            ActorBean actor = new ActorBean(id, name, surname, user_id, archival);
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

    @Override
    public String getAddQuery() {
        return insertTemplate;
    }

    @Override
    public String getTableName() {
        return role.toString().toLowerCase();
    }

    @Override
    public void populateAddStatement(ActorBean entity, PreparedStatement statement) throws SQLException {
        if (role == RoleEnum.CLIENT){
            populateClientInsert(statement, entity);
        }else{
            populateCourierInsert(statement, entity);
        }
    }

    @Override
    public String getUpdateQuery() {
        return updateTemplate;
    }

    @Override
    public void populateUpdateStatement(ActorBean entity, PreparedStatement statement) throws SQLException {
        if (role == RoleEnum.CLIENT){
            populateClientUpdate(statement, entity);
        }else{
            populateCourierUpdate(statement, entity);
        }
    }

    private void populateCourierInsert(PreparedStatement statement, ActorBean actor) throws SQLException {
        statement.setString(1, actor.getName());
        statement.setString(2, actor.getSurname());
        statement.setLong(3, actor.getLikes());
        statement.setBoolean(4, actor.getArchival());
        statement.setLong(5, actor.getUserId());
    }

    private void populateClientInsert(PreparedStatement statement, ActorBean actor) throws SQLException {
        statement.setString(1, actor.getName());
        statement.setString(2, actor.getSurname());
        statement.setBoolean(3, actor.getArchival());
        statement.setLong(4, actor.getUserId());
    }


    private void populateClientUpdate(PreparedStatement statement, ActorBean client) throws SQLException {
        statement.setString(1, client.getName());
        statement.setString(2, client.getSurname());
        statement.setBoolean(3, client.getArchival());
        statement.setLong(4, client.getId());
    }

    private void populateCourierUpdate(PreparedStatement statement, ActorBean courier) throws SQLException {
        statement.setString(1, courier.getName());
        statement.setString(2, courier.getSurname());
        statement.setLong(3, courier.getLikes());
        statement.setBoolean(4, courier.getArchival());
        statement.setLong(5, courier.getId());
    }
}
