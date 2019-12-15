package by.bsuir.exchange.repository.factory;

import by.bsuir.exchange.bean.PersonalDataBean;
import by.bsuir.exchange.entity.RoleEnum;
import by.bsuir.exchange.repository.exception.RepositoryInitializationException;
import by.bsuir.exchange.repository.impl.PersonalDataSqlRepository;
import by.bsuir.exchange.repository.impl.SqlRepository;

public class PersonalDataSqlRepositoryFactory {
    private static final String UPDATE_TEMPLATE = "UPDATE %s SET city=?, age=? WHERE id=?";

    public static SqlRepository<PersonalDataBean> getRepository(RoleEnum role) throws RepositoryInitializationException {
        String roleString = role.toString().toLowerCase();
        String roleUpdateTemplate = String.format(UPDATE_TEMPLATE, roleString);

        return new PersonalDataSqlRepository(roleUpdateTemplate, role);
    }
}
