package by.bsuir.exchange.bean;

/**
 * The class PersonalDataBean was created to split actor entry in the database
 * into multiple beans, to make validation of user input easier.
 * It uses the same table in the database as the ActorBean does.
 */
public class PersonalDataBean implements Markable{
    public static final String DEFAULT = "NONE";

    private long id;
    private long age;
    private String city;

    public PersonalDataBean() {
        city = DEFAULT;
    }

    public PersonalDataBean(long id, long age, String city) {
        this.id = id;
        this.age = age;
        this.city = city;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAge() {
        return age;
    }

    public void setAge(long age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
