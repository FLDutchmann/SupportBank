package training.supportbank;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger();


    private static HashMap<String, Account> accounts = new HashMap<>(1);

    public static void main(String args[]) throws Exception {
        loadFromCSV("DodgyTransactions2015.csv");
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

    private static void loadFromCSV(String filename) throws Exception {
        LOGGER.info("Loading transactions from csv file " + filename);
        File file = new File(filename);
        Scanner reader = new Scanner(file);

        if(!reader.nextLine().equalsIgnoreCase("Date,From,To,Narrative,Amount")) {
            LOGGER.error("The first line is incorrect; the header should be 'Date,From,To,Narrative,Amount'");
            throw new Exception();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        int lineNumber = 1;
        boolean crashAndBurn = false;

        while (reader.hasNextLine()) {
            lineNumber++;
            String line = reader.nextLine();
            String[] entries = line.split(",");

            if(entries.length != 5) {
                LOGGER.error("Line " + lineNumber + " does not have exactly 5 entries: " + line);
                crashAndBurn = true;
            }

            Date date = new Date();
            try {
                date = dateFormat.parse(entries[0]);
            } catch (ParseException e) {
                LOGGER.error("Line " + lineNumber + " does not have a valid date: " + entries[0]);
                crashAndBurn = true;
            }
            Account from = getAccount(entries[1]);
            Account to = getAccount(entries[2]);
            String narrative = entries[3];
            int amount = 0;
            try {
                amount = (int) Math.round(100 * Double.parseDouble(entries[4]));
            } catch (NumberFormatException e) {
                LOGGER.error("Line " + lineNumber + " does not have a valid amount: " + entries[4]);
                crashAndBurn = true;
            }
            Transaction transaction = new Transaction(amount, from, to, narrative, date);

            from.addTransaction(transaction);
            to.addTransaction(transaction);
        }

        if(crashAndBurn) {
            LOGGER.error("Encountered at least one error while parsing the csv file " + filename + ". Exiting");
            throw new Exception();
        }
    }
}
