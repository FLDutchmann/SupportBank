package training.supportbank;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Account {
    private String name;
    private Currency balance = new Currency(0);
    private List<Transaction> transactions = new ArrayList<>();

    public Account(String name) {
        this.name = name;
    }

    public void addTransaction (Transaction transaction) {
        transactions.add(transaction);
        if(name.equals(transaction.getFrom())) balance.add(-transaction.getAmount().getValue());
        if(name.equals(transaction.getTo())) balance.add(transaction.getAmount().getValue());
    }
    public Currency getBalance() {
        return balance;
    }
    public String getName() {
        return name;
    }
    public void printTransactions() {
        for(Transaction transaction: transactions) {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            System.out.println(formatter.format(transaction.getDate()) + ": " + transaction.getNarrative());
            System.out.println(
                    "From " + transaction.getFrom() + " to " + transaction.getTo() + ": "
                     + transaction.getAmount()
                    );
        }
    }
}
