package by.bsuir.exchange.specification.delivery;

import by.bsuir.exchange.bean.DeliveryBean;
import by.bsuir.exchange.provider.ConfigurationProvider;
import by.bsuir.exchange.specification.Specification;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class DeliveryByCourierIdSpecification implements Specification<DeliveryBean, PreparedStatement, Connection> {
    private final static String QUERY_UNLIMITED = "SELECT * FROM deliveries WHERE courier_id = ? AND archival=0";
    private final static String QUERY_LIMITED = "SELECT * FROM deliveries WHERE courier_id = ? AND archival=0 LIMIT ?, ?";

    private Connection connection;
    private String query;
    private long courierId;
    private long offset;

    public DeliveryByCourierIdSpecification(long courierId) {
        this.courierId = courierId;
        this.query = QUERY_UNLIMITED;
    }

    public DeliveryByCourierIdSpecification(long courierId, long offset) {
        this.courierId = courierId;
        this.offset = offset;
        this.query = QUERY_LIMITED;
    }

    @Override
    public boolean specify(DeliveryBean entity) {
        return courierId == entity.getClientId();
    }

    @Override
    public PreparedStatement specify() throws Exception {
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setLong(1, courierId);
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
