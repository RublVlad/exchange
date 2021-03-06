package by.bsuir.exchange.specification.image;

import by.bsuir.exchange.bean.ImageBean;
import by.bsuir.exchange.specification.Specification;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ImageByRoleIdSpecification implements Specification<ImageBean, PreparedStatement, Connection> {
    private final static String QUERY = "SELECT * FROM images WHERE role = ? AND role_id = ? AND archival=0";

    private Connection connection;
    private String role;
    private long roleId;

    public ImageByRoleIdSpecification(String role, long roleId) {
        this.role = role;
        this.roleId = roleId;
    }

    @Override
    public boolean specify(ImageBean entity) {
        return role.equals(entity.getRole()) && entity.getRoleId() == roleId;
    }

    @Override
    public PreparedStatement specify() throws Exception {
        PreparedStatement statement = connection.prepareStatement(QUERY);
        statement.setString(1, role);
        statement.setLong(2, roleId);
        return statement;
    }

    @Override
    public void setHelperObject(Connection obj) {
        connection = obj;
    }
}
