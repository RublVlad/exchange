package by.bsuir.exchange.bean;

import by.bsuir.exchange.entity.RelationEnum;

/**
 * The class RelationBean is used to represent state of a relation
 * between courier and client.
 * It has a corresponding table in the database.
 */
public class RelationBean implements Markable{
    private long id;
    private long clientId;
    private long courierId;
    private String relation;

    public RelationBean() {
        relation = RelationEnum.NONE.toString();
    }

    public RelationBean(long id, long clientId, long courierId, String relation) {
        this.id = id;
        this.clientId = clientId;
        this.courierId = courierId;
        this.relation = relation;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public long getCourierId() {
        return courierId;
    }

    public void setCourierId(long courierId) {
        this.courierId = courierId;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }
}
