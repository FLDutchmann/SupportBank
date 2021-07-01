package training.supportbank;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    private static HashMap<String, Account> accounts = new HashMap<>(1);

    public static void main(String args[]) throws Exception {
        loadFromCSV();

    }

    private static Account getAccount(String name) {
        if (accounts.containsKey(name)) {
            return accounts.get(name);
        } else {
            Account account = new Account(name);
            accounts.put(name, account);
            return account;
        }
    }

    private static void loadFromCSV() throws Exception {
        File file = new File("Transactions2014.csv");
        Scanner reader = new Scanner(file);
        reader.nextLine();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        while (reader.hasNextLine()) {
            String line = reader.nextLine();
            String[] entries = line.split(",");

            Date date = dateFormat.parse(entries[0]);
            System.out.println(date);
            Account from = getAccount(entries[1]);
            Account to = getAccount(entries[2]);
            String narrative = entries[3];
            int amount = Integer.parseInt(entries[4]);

            Transaction transaction = new Transaction(amount, from, to, narrative, date);

            from.addTransaction(transaction);
            to.addTransaction(transaction);
        }
    }
}
