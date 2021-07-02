package training.supportbank;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger();


    private static HashMap<String, Account> accounts = new HashMap<>(1);

    public static void main(String args[]) throws Exception {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String lineRead = scanner.nextLine();
            if (lineRead.equalsIgnoreCase("list all")) {
                accounts.entrySet().forEach(entry -> {
                    double temp = entry.getValue().getBalance() + 0.0001;
                    System.out.printf(entry.getKey() + ": %.2f", temp / 100);
                    System.out.println();
                });
            } else if (lineRead.substring(0, 5).equals("list ")) {
                String name = lineRead.substring(5, lineRead.length());
                if (accounts.containsKey(name)) {
                    accounts.get(name).printTransactions();
                } else System.out.println("Account doesn't exist.");
            } else if (lineRead.substring(0, 12).equals("import file ")){
                String fileName = lineRead.substring(12);
                File f = new File(fileName);
                if(!f.exists()) {
                    System.out.println("The file does not exist.");
                    LOGGER.warn("Tried to open a file that does not exist: " + fileName);
                } else if(fileName.substring(fileName.length()-4).equals(".csv")){
                    loadFromCSV(fileName);
                    break;
                } else if(fileName.substring(fileName.length()-5,fileName.length()).equals(".json")){
                    loadFromJSON(fileName);
                    break;
                }
            }
            else if (lineRead.equalsIgnoreCase("exit")) {
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

    private static void loadFromJSON(String filename) throws Exception {
        LOGGER.info("Loading transactions from json file " + filename);
        Transaction[] file = new Transaction[0];
        GsonBuilder gsonBuilder = new GsonBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        gsonBuilder.registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (jsonElement, type, jsonDeserializationContext) ->
                {
                    Date date = new Date();
                    try{date = dateFormat.parse(jsonElement.getAsString());} catch(Exception e) {};
                    return date;
                }
        );
        gsonBuilder.registerTypeAdapter(Account.class, (JsonDeserializer<Account>) (jsonElement, type, jsonDeserializationContext) -> getAccount(jsonElement.getAsString())
        );
        Gson gson = gsonBuilder.create();
        try {
            Reader reader = Files.newBufferedReader(Paths.get(filename));
            file = gson.fromJson(reader, Transaction[].class);
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        for(int i = 0; i < file.length; i++){
            addTransaction(file[i]);
        }
    }

    private static void loadFromCSV(String filename) throws Exception {
        LOGGER.info("Loading transactions from csv file " + filename);
        File file = new File(filename);
        Scanner reader = new Scanner(file);

        if (!reader.nextLine().equalsIgnoreCase("Date,From,To,Narrative,Amount")) {
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

            if (entries.length != 5) {
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
            addTransaction(transaction);
        }

        if (crashAndBurn) {
            LOGGER.error("Encountered at least one error while parsing the csv file " + filename + ". Exiting");
            throw new Exception();
        }
    }

    public static void addTransaction(Transaction transaction){
        transaction.getFrom().addTransaction(transaction);
        transaction.getTo().addTransaction(transaction);
    }
}
