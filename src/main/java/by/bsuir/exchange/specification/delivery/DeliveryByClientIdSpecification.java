package by.bsuir.exchange.specification.delivery;

import by.bsuir.exchange.bean.DeliveryBean;
import by.bsuir.exchange.provider.ConfigurationProvider;
import by.bsuir.exchange.specification.Specification;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class DeliveryByClientIdSpecification implements Specification<DeliveryBean, PreparedStatement, Connection> {
    private final static String QUERY_LIMITED = "SELECT * FROM deliveries WHERE client_id = ? AND archival = 0 LIMIT ?, ?";
    private final static String QUERY_UNLIMITED = "SELECT * FROM deliveries WHERE client_id = ? AND archival = 0";

    private Connection connection;
    private String query;
    private long clientId;
    private long offset;


    public DeliveryByClientIdSpecification(long clientId) {
        this.clientId = clientId;
        this.query = QUERY_UNLIMITED;
    }

    public DeliveryByClientIdSpecification(long clientId, long offset) {
        this.clientId = clientId;
        this.offset = offset;
        this.query = QUERY_LIMITED;
    }

    @Override
    public boolean specify(DeliveryBean entity) {
        return clientId == entity.getClientId();
    }

    @Override
    public PreparedStatement specify() throws Exception {
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setLong(1, clientId);
        if (query == QUERY_LIMITED){
            statement.setLong(2, offset);
            statement.setLong(3, ConfigurationProvider.PAGINATION_FACTOR);
        }
        return statement;
    }

    @Override
    public void setHelperObject(Connection obj) {
        connection = obj;
    }
}
