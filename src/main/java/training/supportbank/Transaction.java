package training.supportbank;

import java.util.Date;

public class Transaction {
    private int amount; // in cents
    private Account fromAccount;
    private Account toAccount;
    private String narrative;
    private Date date;

    public Account getFrom() {
        return fromAccount;
    }

    public Account getTo() {
        return toAccount;
    }

    public int getAmount() {
        return amount;
    }

    public String getNarrative() {
        return narrative;
    }

    public Date getDate() {
        return date;
    }


    public Transaction(int amount, Account from, Account to, String narrative, Date date) {
        this.amount = amount;
        this.fromAccount = from;
        this.toAccount = to;
        this.narrative = narrative;
        this.date = date;
    }
}
