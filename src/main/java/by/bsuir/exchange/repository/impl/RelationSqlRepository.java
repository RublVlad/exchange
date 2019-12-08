package by.bsuir.exchange.repository.impl;

import by.bsuir.exchange.bean.RelationBean;
import by.bsuir.exchange.pool.exception.PoolOperationException;
import by.bsuir.exchange.pool.exception.PoolTimeoutException;
import by.bsuir.exchange.provider.DataBaseAttributesProvider;
import by.bsuir.exchange.repository.exception.RepositoryInitializationException;
import by.bsuir.exchange.repository.exception.RepositoryOperationException;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class RelationSqlRepository extends SqlRepository<RelationBean> {

    public RelationSqlRepository() throws RepositoryInitializationException {
        super();
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
    public void add(RelationBean bean) throws RepositoryOperationException {
        String template = "INSERT INTO relations (client_id, courier_id, relation) VALUES (?, ?, ?)";
        try{
            Connection connection = pool.getConnection();
            PreparedStatement statement = connection.prepareStatement(template, Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, bean.getClientId());
            statement.setLong(2, bean.getCourierId());
            statement.setString(3, bean.getRelation());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0){
                throw new RepositoryOperationException("Unable to perform operation");
            }
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()){
                bean.setId(generatedKeys.getLong(1));
            }
            pool.releaseConnection(connection);
        } catch (PoolTimeoutException | SQLException | PoolOperationException e) {
            throw new RepositoryOperationException(e);
        }
    }

    @Override
    public void update(RelationBean entity) throws RepositoryOperationException {
        throw new UnsupportedOperationException();
    }
}
