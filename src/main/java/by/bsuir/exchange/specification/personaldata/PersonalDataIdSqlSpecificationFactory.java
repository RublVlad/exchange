package by.bsuir.exchange.specification.personaldata;

import by.bsuir.exchange.bean.PersonalDataBean;
import by.bsuir.exchange.entity.RoleEnum;
import by.bsuir.exchange.specification.Specification;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class PersonalDataIdSqlSpecificationFactory {
    private static final String QUERY = "SELECT id, city, age FROM %s WHERE id = ? AND archival=0";

    public static Specification<PersonalDataBean, PreparedStatement, Connection> getSpecification(RoleEnum role, long id) {
        String roleString = role.toString().toLowerCase();
        String template = String.format(QUERY, roleString);
        PersonalDataByIdSpecification specification = new PersonalDataByIdSpecification(id);
        specification.setQuery(template);
        return specification;
    }
}
