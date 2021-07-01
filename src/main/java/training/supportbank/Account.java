package training.supportbank;

import java.util.List;

public class Account {
    private String name;
    private int balance = 0;
    private List<Transaction> transactions;

    public Account(String name) {
        this.name = name;
    }

    public void addTransaction (Transaction transaction) {

    }
}
