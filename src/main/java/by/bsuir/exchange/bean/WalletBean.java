package by.bsuir.exchange.bean;

/**
 * The class WalletBean is used to represent state of actor's balance.
 * It has a corresponding table in the database.
 * It uses the same table in the database as the ActorBean does.
 */
public class WalletBean implements Markable{
    private long id;
    private double balance;

    public WalletBean() {
    }

    public WalletBean(long id, double balance) {
        this.id = id;
        this.balance = balance;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
