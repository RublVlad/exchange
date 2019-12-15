package by.bsuir.exchange.specification.wallet;

import by.bsuir.exchange.bean.WalletBean;
import by.bsuir.exchange.specification.Specification;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class WalletByIdSqlSpecification implements Specification<WalletBean, PreparedStatement, Connection> {
    private String query;
    private Connection connection;
    private long id;

    public WalletByIdSqlSpecification(long id){
        this.id = id;
    }

    @Override
    public boolean specify(WalletBean entity) {
        return id == entity.getId();
    }

    @Override
    public PreparedStatement specify() throws Exception {
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setLong(1, id);
        return statement;
    }

    @Override
    public void setHelperObject(Connection obj) {
        connection = obj;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
