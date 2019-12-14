package by.bsuir.exchange.repository.impl;

import by.bsuir.exchange.bean.OfferBean;
import by.bsuir.exchange.pool.ConnectionPool;
import by.bsuir.exchange.provider.DataBaseAttributesProvider;
import by.bsuir.exchange.repository.exception.RepositoryInitializationException;
import by.bsuir.exchange.tag.RepositoryTagEnum;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class OfferSqlRepository extends SqlRepository<OfferBean> {
    private static final String INSERT_QUERY =
            "INSERT INTO offers (price, transport,  archival, courier_id) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY =
            "UPDATE offers SET price=?, transport=?, archival=? WHERE id=?";


    public OfferSqlRepository() throws RepositoryInitializationException {
        super();
    }

    public OfferSqlRepository(ConnectionPool pool){
        super(pool);
        this.tag = RepositoryTagEnum.OFFER_REPOSITORY;
    }

    @Override
    public Optional<List<OfferBean>> process(ResultSet resultSet) throws SQLException {
        Optional< List< OfferBean> > optionList = Optional.empty();
        List< OfferBean > offers = new LinkedList<>();
        while (resultSet.next()){
            String table = DataBaseAttributesProvider.OFFER_TABLE;
            String column = DataBaseAttributesProvider.ID;
            String columnName = DataBaseAttributesProvider.getColumnName(table, column);
            long id = resultSet.getLong(columnName);

            column = DataBaseAttributesProvider.TRANSPORT;
            columnName = DataBaseAttributesProvider.getColumnName(table, column);
            String transport = resultSet.getString(columnName);

            column = DataBaseAttributesProvider.PRICE;
            columnName = DataBaseAttributesProvider.getColumnName(table, column);
            double price = resultSet.getDouble(columnName);

            column = DataBaseAttributesProvider.COURIER_ID;
            columnName = DataBaseAttributesProvider.getColumnName(table, column);
            long courierId = resultSet.getLong(columnName);

            column = DataBaseAttributesProvider.ARCHIVAL;
            columnName = DataBaseAttributesProvider.getColumnName(table, column);
            boolean archival = resultSet.getBoolean(columnName);


            OfferBean courier = new OfferBean(id, transport, price, courierId, archival);
            offers.add(courier);
        }

        if (offers.size() != 0 ){
            optionList = Optional.of(offers);
        }
        return optionList;
    }

    @Override
    public String getAddQuery() {
        return INSERT_QUERY;
    }

    @Override
    public void populateAddStatement(OfferBean entity, PreparedStatement statement) throws SQLException {
        statement.setDouble(1, entity.getPrice());
        statement.setString(2, entity.getTransport());
        statement.setBoolean(3, entity.getArchival());
        statement.setLong(4, entity.getCourierId());
    }

    @Override
    public String getUpdateQuery() {
        return UPDATE_QUERY;
    }

    @Override
    public void populateUpdateStatement(OfferBean entity, PreparedStatement statement) throws SQLException {
        statement.setDouble(1, entity.getPrice());
        statement.setString(2, entity.getTransport());
        statement.setBoolean(3, entity.getArchival());
        statement.setLong(4, entity.getId());
    }
}
