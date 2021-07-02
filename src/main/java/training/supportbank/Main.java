package training.supportbank;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
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
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger();


    private static HashMap<String, Account> accounts = new HashMap<>(1);
    private static HashMap<String, FileLoader> fileLoaders = new HashMap<>(2);

    public static void main(String args[]) throws Exception {
        fileLoaders.put("csv", new CSVLoader());
        fileLoaders.put("json", new JsonLoader());

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String lineRead = scanner.nextLine();
            if (lineRead.equalsIgnoreCase("list all")) {
                accounts.entrySet().forEach(entry -> {
                    System.out.println(entry.getKey() + ": " + entry.getValue().getBalance());
                });
            } else if (lineRead.length() >= 5 && lineRead.substring(0, 5).equalsIgnoreCase("list ")) {
                String name = lineRead.substring(5, lineRead.length());
                if (accounts.containsKey(name)) {
                    accounts.get(name).printTransactions();
                } else System.out.println("Account doesn't exist.");
            } else if (lineRead.length() >= 12 && lineRead.substring(0, 12).equalsIgnoreCase("import file ")){
                String fileName = lineRead.substring(12);
                File f = new File(fileName);
                if(!f.exists()) {
                    System.out.println("The file does not exist.");
                    LOGGER.warn("Tried to open a file that does not exist: " + fileName);
                    continue;
                }
                Pattern pattern = Pattern.compile("\\.([a-zA-Z]+)$");
                Matcher matcher = pattern.matcher(fileName);
                if(!matcher.find()) {
                    System.out.println("File " + fileName + " does not have an extension.");
                    continue;
                }
                String extension = matcher.group(1);
                if(!fileLoaders.containsKey(extension)) {
                    LOGGER.warn("Tried to load file with extension " + extension + " which is not recognised");
                    System.out.println("Files of type " + extension + " are not accepted.");
                    continue;
                }
                List<Transaction> transactions = fileLoaders.get(extension).loadFile(fileName);
                System.out.println("Successfully loaded " + fileName);

                transactions.forEach(Main::addTransactionToAccounts);

            } else if (lineRead.equalsIgnoreCase("exit")) {
                break;
            } else {
                System.out.println("Command not recognised.");
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

    public static void addTransactionToAccounts(Transaction transaction){
        getAccount(transaction.getFrom()).addTransaction(transaction);
        getAccount(transaction.getTo()).addTransaction(transaction);
    }
}
