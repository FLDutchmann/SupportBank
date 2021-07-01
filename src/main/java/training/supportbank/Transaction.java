package training.supportbank;

import java.util.Date;

public class Transaction {
    private int amount; // in cents
    private Account from;
    private Account to;
    private String narrative;
    private Date date;

    public Account getFrom() {
        return from;
    }
    public Account getTo() {
        return to;
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
        this.from = from;
        this.to = to;
        this.narrative = narrative;
        this.date = date;
    }
}
