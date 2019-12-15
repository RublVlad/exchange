package by.bsuir.exchange.specification.wallet;

import by.bsuir.exchange.bean.WalletBean;
import by.bsuir.exchange.entity.RoleEnum;
import by.bsuir.exchange.specification.Specification;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class WalletIdSqlSpecificationFactory {
    private static final String QUERY = "SELECT id, balance FROM %s WHERE id = ? AND archival=0";

    public static Specification<WalletBean, PreparedStatement, Connection> getSpecification(RoleEnum role, long id) {
        String roleString = role.toString().toLowerCase();
        String template = String.format(QUERY, roleString);
        WalletByIdSqlSpecification specification = new WalletByIdSqlSpecification(id);
        specification.setQuery(template);
        return specification;
    }
}
