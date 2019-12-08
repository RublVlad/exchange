package by.bsuir.exchange.repository.factory;

import by.bsuir.exchange.bean.ActorBean;
import by.bsuir.exchange.entity.RoleEnum;
import by.bsuir.exchange.repository.exception.RepositoryInitializationException;
import by.bsuir.exchange.repository.impl.ActorSqlRepository;
import by.bsuir.exchange.repository.impl.SqlRepository;

public class ActorSqlRepositoryFactory {
    private static final String UPDATE_TEMPLATE_CLIENT = "UPDATE %s SET name=?, surname=?, balance=?, archival=? WHERE id=?";
    private static final String INSERT_TEMPLATE_CLIENT = "INSERT INTO %s (name, surname, balance, archival, user_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_TEMPLATE_COURIER = "UPDATE %s SET name=?, surname=?, balance=?, likes=?, archival=? WHERE id=?";
    private static final String INSERT_TEMPLATE_COURIER = "INSERT INTO %s (name, surname, balance, likes, archival, user_id) VALUES (?, ?, ?, ?, ?, ?)";


    public static SqlRepository<ActorBean> getRepository(RoleEnum role) throws RepositoryInitializationException {
        String roleString = role.toString().toLowerCase();
        String updateTemplate = role == RoleEnum.CLIENT? UPDATE_TEMPLATE_CLIENT : UPDATE_TEMPLATE_COURIER;
        String insertTemplate = role == RoleEnum.CLIENT? INSERT_TEMPLATE_CLIENT : INSERT_TEMPLATE_COURIER;
        String roleUpdateTemplate = String.format(updateTemplate, roleString);
        String roleInsertTemplate = String.format(insertTemplate, roleString);

        return new ActorSqlRepository(roleUpdateTemplate, roleInsertTemplate, role);
    }
}
