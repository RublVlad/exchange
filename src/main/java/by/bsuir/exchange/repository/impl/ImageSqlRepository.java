package by.bsuir.exchange.repository.impl;

import by.bsuir.exchange.bean.ImageBean;
import by.bsuir.exchange.pool.ConnectionPool;
import by.bsuir.exchange.provider.DataBaseAttributesProvider;
import by.bsuir.exchange.repository.exception.RepositoryInitializationException;
import by.bsuir.exchange.tagable.RepositoryTagEnum;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ImageSqlRepository extends SqlRepository<ImageBean> {
    private static final String INSERT_QUERY =
            "INSERT INTO images (role, role_id, file_name, archival) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY =
            "UPDATE images SET file_name=?, archival=? WHERE role = ? AND role_id = ?";

    public ImageSqlRepository() throws RepositoryInitializationException {
        super();
        this.tag = RepositoryTagEnum.IMAGE__REPOSITORY;
    }

    public ImageSqlRepository(ConnectionPool pool){
        super(pool);
    }

    @Override
    public Optional<List<ImageBean>> process(ResultSet resultSet) throws SQLException {
        Optional< List< ImageBean> > optionList = Optional.empty();
        List< ImageBean > images = new LinkedList<>();
        while (resultSet.next()){
            String table = DataBaseAttributesProvider.IMAGES_TABLE;
            String column = DataBaseAttributesProvider.ID;
            String columnName = DataBaseAttributesProvider.getColumnName(table, column);
            long id = resultSet.getLong(columnName);

            column = DataBaseAttributesProvider.ROLE;
            columnName = DataBaseAttributesProvider.getColumnName(table, column);
            String role = resultSet.getString(columnName);

            column = DataBaseAttributesProvider.ROLE_ID;
            columnName = DataBaseAttributesProvider.getColumnName(table, column);
            long roleId = resultSet.getLong(columnName);

            column = DataBaseAttributesProvider.FILE_NAME;
            columnName = DataBaseAttributesProvider.getColumnName(table, column);
            String fileName = resultSet.getString(columnName);

            column = DataBaseAttributesProvider.ARCHIVAL;
            columnName = DataBaseAttributesProvider.getColumnName(table, column);
            boolean archival = resultSet.getBoolean(columnName);

            ImageBean image = new ImageBean(id, role, roleId, fileName, archival);
            images.add(image);
        }
        if (images.size() != 0 ){
            optionList = Optional.of(images);
        }
        return optionList;
    }

    @Override
    public String getAddQuery() {
        return INSERT_QUERY;
    }

    @Override
    public String getTableName() {
        return "images";
    }

    @Override
    public void populateAddStatement(ImageBean entity, PreparedStatement statement) throws SQLException {
        statement.setString(1, entity.getRole().toUpperCase());
        statement.setLong(2, entity.getRoleId());
        statement.setString(3, entity.getFileName());
        statement.setBoolean(4, entity.getArchival());
    }

    @Override
    public String getUpdateQuery() {
        return UPDATE_QUERY;
    }

    @Override
    public void populateUpdateStatement(ImageBean entity, PreparedStatement statement) throws SQLException {
        statement.setString(1, entity.getFileName());
        statement.setBoolean(2, entity.getArchival());
        statement.setString(3, entity.getRole());
        statement.setLong(4, entity.getRoleId());
    }
}
