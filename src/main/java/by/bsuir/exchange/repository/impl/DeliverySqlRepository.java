package by.bsuir.exchange.repository.impl;

import by.bsuir.exchange.bean.DeliveryBean;
import by.bsuir.exchange.pool.ConnectionPool;
import by.bsuir.exchange.provider.DataBaseAttributesProvider;
import by.bsuir.exchange.repository.exception.RepositoryInitializationException;
import by.bsuir.exchange.tagable.RepositoryTagEnum;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class DeliverySqlRepository extends SqlRepository<DeliveryBean> {
    private static final String INSERT_QUERY =
            "INSERT INTO deliveries (client_id, client_finished, courier_id, courier_finished, archival) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY =
            "UPDATE deliveries SET client_finished=?, courier_finished=?, archival=? WHERE id=?";

    public DeliverySqlRepository() throws RepositoryInitializationException {
        super();
        this.tag = RepositoryTagEnum.DELIVERY_REPOSITORY;
    }

    public DeliverySqlRepository(ConnectionPool pool){
        super(pool);
    }

    @Override
    public Optional<List<DeliveryBean>> process(ResultSet resultSet) throws SQLException {
        Optional< List< DeliveryBean> > optionList = Optional.empty();
        List< DeliveryBean > deliveries = new LinkedList<>();
        while (resultSet.next()){
            String table = DataBaseAttributesProvider.DELIVERY_TABLE;
            String column = DataBaseAttributesProvider.ID;
            String columnName = DataBaseAttributesProvider.getColumnName(table, column);
            long id = resultSet.getLong(columnName);

            column = DataBaseAttributesProvider.CLIENT_ID;
            columnName = DataBaseAttributesProvider.getColumnName(table, column);
            long clientId = resultSet.getLong(columnName);

            column = DataBaseAttributesProvider.CLIENT_FINISHED;
            columnName = DataBaseAttributesProvider.getColumnName(table, column);
            boolean clientFinished = resultSet.getBoolean(columnName);

            column = DataBaseAttributesProvider.COURIER_ID;
            columnName = DataBaseAttributesProvider.getColumnName(table, column);
            long courierId = resultSet.getLong(columnName);

            column = DataBaseAttributesProvider.COURIER_FINISHED;
            columnName = DataBaseAttributesProvider.getColumnName(table, column);
            boolean courierFinised = resultSet.getBoolean(columnName);

            column = DataBaseAttributesProvider.ARCHIVAL;
            columnName = DataBaseAttributesProvider.getColumnName(table, column);
            boolean archival = resultSet.getBoolean(columnName);

            DeliveryBean courier = new DeliveryBean(id, clientId, clientFinished, courierId, courierFinised, archival);
            deliveries.add(courier);
        }

        if (deliveries.size() != 0 ){
            optionList = Optional.of(deliveries);
        }
        return optionList;
    }

    @Override
    public String getAddQuery() {
        return INSERT_QUERY;
    }

    @Override
    public String getTableName() {
        return "deliveries";
    }

    @Override
    public void populateAddStatement(DeliveryBean entity, PreparedStatement statement) throws SQLException {
        statement.setLong(1, entity.getClientId());
        statement.setBoolean(2, entity.getClientFinished());
        statement.setLong(3, entity.getCourierId());
        statement.setBoolean(4, entity.getCourierFinished());
        statement.setBoolean(5, entity.getArchival());
    }

    @Override
    public String getUpdateQuery() {
        return UPDATE_QUERY;
    }

    @Override
    public void populateUpdateStatement(DeliveryBean entity, PreparedStatement statement) throws SQLException {
        statement.setBoolean(1, entity.getClientFinished());
        statement.setBoolean(2, entity.getCourierFinished());
        statement.setBoolean(3, entity.getArchival());
        statement.setLong(4, entity.getId());
    }

}
