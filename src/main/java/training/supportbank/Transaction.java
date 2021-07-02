package training.supportbank;

import java.util.Date;

public class Transaction {
    private Currency amount; // in cents
    private String fromAccount;
    private String toAccount;
    private String narrative;
    private Date date;

    public String getFrom() {
        return fromAccount;
    }

    public String getTo() {
        return toAccount;
    }

    public Currency getAmount() {
        return amount;
    }

    public String getNarrative() {
        return narrative;
    }

    public Date getDate() {
        return date;
    }


    public Transaction(Currency amount, String from, String to, String narrative, Date date) {
        this.amount = amount;
        this.fromAccount = from;
        this.toAccount = to;
        this.narrative = narrative;
        this.date = date;
    }
}
