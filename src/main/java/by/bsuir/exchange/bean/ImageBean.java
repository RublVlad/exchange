package by.bsuir.exchange.bean;

/**
 * The class ImageBean is used to provide access to fields of an image object,
 * which is used for managing profile images of the users.
 * It has a corresponding table in the database.
 */
public class ImageBean implements Markable{
    private long id;
    private String role;
    private long roleId;
    private String fileName;
    private boolean archival;

    public ImageBean(long id, String role, long roleId, String fileName, boolean archival) {
        this.id = id;
        this.role = role;
        this.roleId = roleId;
        this.fileName = fileName;
        this.archival = archival;
    }


    public ImageBean() {
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

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
