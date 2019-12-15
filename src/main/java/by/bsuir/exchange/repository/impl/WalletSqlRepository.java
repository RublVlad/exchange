package by.bsuir.exchange.repository.impl;

import by.bsuir.exchange.bean.WalletBean;
import by.bsuir.exchange.entity.RoleEnum;
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

public class WalletSqlRepository extends SqlRepository<WalletBean>{
    private String updateTemplate;
    private String insertTemplate;
    private RoleEnum role;

    public WalletSqlRepository(String updateTemplate, String insertTemplate, RoleEnum role) throws RepositoryInitializationException {
        super();
        this.updateTemplate = updateTemplate;
        this.insertTemplate = insertTemplate;
        this.role = role;
        this.tag = RepositoryTagEnum.WALLET_REPOSITORY;
    }

    public WalletSqlRepository(ConnectionPool pool, String updateTemplate, String insertTemplate, RoleEnum role) {
        super(pool);
        this.updateTemplate = updateTemplate;
        this.insertTemplate = insertTemplate;
        this.role = role;
        this.tag = RepositoryTagEnum.WALLET_REPOSITORY;
    }

    @Override
    public Optional<List<WalletBean>> process(ResultSet resultSet) throws SQLException {
        Optional< List<WalletBean> > optionList = Optional.empty();
        List<WalletBean> wallets = new LinkedList<>();
        while (resultSet.next()){
            String table = role == RoleEnum.CLIENT? DataBaseAttributesProvider.CLIENT_TABLE
                    : DataBaseAttributesProvider.COURIER_TABLE;

            String column = DataBaseAttributesProvider.ID;
            String columnName  = DataBaseAttributesProvider.getColumnName(table, column);
            long id = resultSet.getLong(columnName);

            column = DataBaseAttributesProvider.BALANCE;
            columnName = DataBaseAttributesProvider.getColumnName(table, column);
            double balance = resultSet.getDouble(columnName);

            WalletBean actor = new WalletBean(id, balance);
            wallets.add(actor);
        }
        if (wallets.size() != 0 ){
            optionList = Optional.of(wallets);
        }
        return optionList;
    }

    @Override
    public String getAddQuery() {
        return insertTemplate;
    }

    @Override
    public void populateAddStatement(WalletBean entity, PreparedStatement statement) throws SQLException {
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
    public void populateUpdateStatement(WalletBean entity, PreparedStatement statement) throws SQLException {
        if (role == RoleEnum.CLIENT){
            populateClientUpdate(statement, entity);
        }else{
            populateCourierUpdate(statement, entity);
        }
    }

    private void populateCourierInsert(PreparedStatement statement, WalletBean wallet) throws SQLException {
        statement.setDouble(1, wallet.getBalance());
    }

    private void populateClientInsert(PreparedStatement statement, WalletBean wallet) throws SQLException {
        statement.setDouble(1, wallet.getBalance());
    }

    private void populateClientUpdate(PreparedStatement statement, WalletBean wallet) throws SQLException {
        statement.setDouble(1, wallet.getBalance());
        statement.setLong(2, wallet.getId());
    }

    private void populateCourierUpdate(PreparedStatement statement, WalletBean wallet) throws SQLException {
        statement.setDouble(1, wallet.getBalance());
        statement.setLong(2, wallet.getId());
    }
}
