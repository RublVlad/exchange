package by.bsuir.exchange.repository.factory;

import by.bsuir.exchange.bean.WalletBean;
import by.bsuir.exchange.entity.RoleEnum;
import by.bsuir.exchange.repository.exception.RepositoryInitializationException;
import by.bsuir.exchange.repository.impl.SqlRepository;
import by.bsuir.exchange.repository.impl.WalletSqlRepository;

public class WalletSqlRepositoryFactory {
    private static final String UPDATE_TEMPLATE = "UPDATE %s SET balance=? WHERE id=?";
    private static final String INSERT_TEMPLATE = "INSERT INTO %s (balance) VALUES (?)";

    public static SqlRepository<WalletBean> getRepository(RoleEnum role) throws RepositoryInitializationException {
        String roleString = role.toString().toLowerCase();
        String roleUpdateTemplate = String.format(UPDATE_TEMPLATE, roleString);
        String roleInsertTemplate = String.format(INSERT_TEMPLATE, roleString);

        return new WalletSqlRepository(roleUpdateTemplate, roleInsertTemplate, role);
    }
}
