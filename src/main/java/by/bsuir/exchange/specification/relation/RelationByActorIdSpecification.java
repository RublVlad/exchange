package by.bsuir.exchange.specification.relation;

import by.bsuir.exchange.bean.RelationBean;
import by.bsuir.exchange.specification.Specification;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class RelationByActorIdSpecification implements Specification<RelationBean, PreparedStatement, Connection> {
    private static final String QUERY = "SELECT * FROM relations WHERE client_id = ? AND courier_id = ?";

    private Connection connection;

    private long clientId;
    private long courierId;

    public RelationByActorIdSpecification(long clientId, long courierId) {
        this.clientId = clientId;
        this.courierId = courierId;
    }

    @Override
    public boolean specify(RelationBean entity) {
        return entity.getClientId() == clientId && entity.getCourierId() == courierId;
    }

    @Override
    public PreparedStatement specify() throws Exception {
        PreparedStatement preparedStatement = connection.prepareStatement(QUERY);
        preparedStatement.setLong(1, clientId);
        preparedStatement.setLong(2, courierId);
        return preparedStatement;
    }

    @Override
    public void setHelperObject(Connection obj) {
        connection = obj;
    }
}
