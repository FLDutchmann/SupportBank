package training.supportbank;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Account {
    private String name;
    private int balance = 0;
    private List<Transaction> transactions = new ArrayList<>();

    public Account(String name) {
        this.name = name;
    }

    public void addTransaction (Transaction transaction) {
        transactions.add(transaction);
        if(this.equals(transaction.getFrom())) balance -= transaction.getAmount();
        if(this.equals(transaction.getTo())) balance += transaction.getAmount();
    }
    public int getBalance() {
        return balance;
    }
    public String getName() {
        return name;
    }
    public void printTransactions() {
        for(Transaction transaction: transactions) {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            System.out.println(dateFormat.format(transaction.getDate()) + ": " + transaction.getNarrative());
            System.out.println(
                    "From " + transaction.getFrom().getName() + " to " + transaction.getTo().getName() + ": "
                     + transaction.getAmount()
                    );
        }
    }
}
