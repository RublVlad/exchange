package by.bsuir.exchange.repository.impl;

import by.bsuir.exchange.bean.PersonalDataBean;
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

public class PersonalDataSqlRepository extends SqlRepository<PersonalDataBean>{
    private String updateTemplate;
    private RoleEnum role;

    public PersonalDataSqlRepository(String updateTemplate, RoleEnum role) throws RepositoryInitializationException {
        super();
        this.updateTemplate = updateTemplate;
        this.role = role;
        this.tag = RepositoryTagEnum.WALLET_REPOSITORY;
    }

    public PersonalDataSqlRepository(ConnectionPool pool, String updateTemplate, RoleEnum role) {
        super(pool);
        this.updateTemplate = updateTemplate;
        this.role = role;
        this.tag = RepositoryTagEnum.WALLET_REPOSITORY;
    }

    @Override
    public Optional<List<PersonalDataBean>> process(ResultSet resultSet) throws SQLException {
        Optional< List<PersonalDataBean> > optionList = Optional.empty();
        List<PersonalDataBean> personalData = new LinkedList<>();
        while (resultSet.next()){
            String table = role == RoleEnum.CLIENT? DataBaseAttributesProvider.CLIENT_TABLE
                    : DataBaseAttributesProvider.COURIER_TABLE;

            String column = DataBaseAttributesProvider.ID;
            String columnName  = DataBaseAttributesProvider.getColumnName(table, column);
            long id = resultSet.getLong(columnName);

            column = DataBaseAttributesProvider.CITY;
            columnName = DataBaseAttributesProvider.getColumnName(table, column);
            String city = resultSet.getString(columnName);

            column = DataBaseAttributesProvider.AGE;
            columnName = DataBaseAttributesProvider.getColumnName(table, column);
            long age = resultSet.getLong(columnName);

            PersonalDataBean personalDataBean = new PersonalDataBean(id, age, city);
            personalData.add(personalDataBean);
        }
        if (personalData.size() != 0 ){
            optionList = Optional.of(personalData);
        }
        return optionList;
    }

    @Override
    public String getAddQuery() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void populateAddStatement(PersonalDataBean personalData, PreparedStatement statement) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getUpdateQuery() {
        return updateTemplate;
    }

    @Override
    public void populateUpdateStatement(PersonalDataBean personalData, PreparedStatement statement) throws SQLException {
        statement.setString(1, personalData.getCity());
        statement.setLong(2, personalData.getAge());
        statement.setLong(3, personalData.getId());
    }
}
