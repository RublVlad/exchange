package by.bsuir.exchange.specification.offer;

import by.bsuir.exchange.bean.OfferBean;
import by.bsuir.exchange.provider.ConfigurationProvider;
import by.bsuir.exchange.specification.Specification;

import java.lang.module.Configuration;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class OfferAllSpecification implements Specification<OfferBean, PreparedStatement, Connection> {
    private static final String QUERY = "SELECT * FROM offers WHERE archival=0 LIMIT ?, ?";

    private Connection connection;
    private long offset;

    public OfferAllSpecification(long offset) {
        this.offset = offset;
    }

    @Override
    public boolean specify(OfferBean entity) {
        return true;
    }

    @Override
    public PreparedStatement specify() throws Exception {
        PreparedStatement statement = connection.prepareStatement(QUERY);
        statement.setLong(1, offset);
        statement.setLong(2, ConfigurationProvider.PAGINATION_FACTOR);
        return statement;
    }

    @Override
    public void setHelperObject(Connection obj) {
        connection = obj;
    }
}
