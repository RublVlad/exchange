package by.bsuir.exchange.repository.impl;

import by.bsuir.exchange.bean.RelationBean;
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

public class RelationSqlRepository extends SqlRepository<RelationBean> {
    private static final String INSERT_QUERY =
            "INSERT INTO relations (client_id, courier_id, relation) VALUES (?, ?, ?)";

    public RelationSqlRepository() throws RepositoryInitializationException {
        super();
    }

    public RelationSqlRepository(ConnectionPool pool){
        super(pool);
        this.tag = RepositoryTagEnum.RELATION_REPOSITORY;
    }

    @Override
    public Optional<List<RelationBean>> process(ResultSet resultSet) throws SQLException {
        Optional< List< RelationBean> > optionList = Optional.empty();
        List< RelationBean > relations = new LinkedList<>();
        while (resultSet.next()){
            String table = DataBaseAttributesProvider.RELATION_TABLE;
            String column = DataBaseAttributesProvider.ID;
            String columnName = DataBaseAttributesProvider.getColumnName(table, column);
            long id = resultSet.getLong(columnName);

            column = DataBaseAttributesProvider.CLIENT_ID;
            columnName = DataBaseAttributesProvider.getColumnName(table, column);
            long clientId = resultSet.getLong(columnName);

            column = DataBaseAttributesProvider.COURIER_ID;
            columnName = DataBaseAttributesProvider.getColumnName(table, column);
            long courierId = resultSet.getLong(columnName);

            column = DataBaseAttributesProvider.RELATION;
            columnName = DataBaseAttributesProvider.getColumnName(table, column);
            String relation = resultSet.getString(columnName);

            RelationBean courier = new RelationBean(id, clientId, courierId, relation);
            relations.add(courier);
        }

        if (relations.size() != 0 ){
            optionList = Optional.of(relations);
        }
        return optionList;
    }

    @Override
    public String getAddQuery() {
        return INSERT_QUERY;
    }

    @Override
    public String getTableName() {
        return "relations";
    }

    @Override
    public void populateAddStatement(RelationBean entity, PreparedStatement statement) throws SQLException {
        statement.setLong(1, entity.getClientId());
        statement.setLong(2, entity.getCourierId());
        statement.setString(3, entity.getRelation());
    }

    @Override
    public String getUpdateQuery() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void populateUpdateStatement(RelationBean entity, PreparedStatement statement) throws SQLException {
        throw new UnsupportedOperationException();
    }


}
