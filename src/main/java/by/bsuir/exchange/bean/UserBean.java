package by.bsuir.exchange.bean;

import by.bsuir.exchange.entity.RoleEnum;

/**
 * The class UserBean is used to provide access to fields of a user object,
 * which is used for authentication.
 * It has a corresponding table in the database.
 */
public class UserBean implements Markable{
    private long id;
    private String email;
    private String password;
    private String role;
    private boolean archival;

    public UserBean() {
        role = RoleEnum.GUEST.toString();
    }

    public UserBean(long id, String email, String password, String role, boolean archival) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.archival = archival;
    }

    public boolean getArchival() {
        return archival;
    }

    public void setArchival(boolean archival) {
        this.archival = archival;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
