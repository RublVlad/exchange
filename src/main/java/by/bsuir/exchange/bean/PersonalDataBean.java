package by.bsuir.exchange.bean;

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
