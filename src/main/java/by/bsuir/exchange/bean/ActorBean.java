package by.bsuir.exchange.bean;

/**
 * The class ActorBean is used to represent state of an actor entity.
 * It has a corresponding table in the database.
 */
public class ActorBean implements Markable{
    private long id;
    private String name;
    private String surname;
    private long userId;
    private long likes;
    private boolean archival;

    public ActorBean() {
    }

    public ActorBean(long id, String name, String surname, long userId, boolean archival) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.userId = userId;
        this.archival = archival;
    }

    public ActorBean(long id, String name, String surname, long userId, long likes, boolean archival) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.userId = userId;
        this.likes = likes;
        this.archival = archival;
    }

    public boolean getArchival() {
        return archival;
    }

    public void setArchival(boolean archival) {
        this.archival = archival;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
