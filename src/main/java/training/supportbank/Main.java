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
        Scanner scanner = new Scanner(System.in);
        while(true) {
            String lineRead = scanner.nextLine();
            if (lineRead.equalsIgnoreCase("list all")) {
                accounts.entrySet().forEach(entry -> {
                    double temp = entry.getValue().getBalance() + 0.0001;
                    System.out.printf(entry.getKey() + ": %.2f", temp/100);
                    System.out.println();
                });
            } else if (lineRead.substring(0,5).equals("list ")) {
                String name = lineRead.substring(5,lineRead.length());
                if(accounts.containsKey(name)){
                    accounts.get(name).printTransactions();
                } else System.out.println("Account doesn't exist.");
            } else if (lineRead.equalsIgnoreCase("exit")) {
                break;
            }
        }
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
            Account from = getAccount(entries[1]);
            Account to = getAccount(entries[2]);
            String narrative = entries[3];
            int amount = (int) Math.round(100*Double.parseDouble(entries[4]));
            Transaction transaction = new Transaction(amount, from, to, narrative, date);

            from.addTransaction(transaction);
            to.addTransaction(transaction);
        }
    }
}
