package training.supportbank;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger();


    private static HashMap<String, Account> accounts = new HashMap<>(1);
    private static List<Transaction> allTransactions = new ArrayList<>();
    private static HashMap<String, FileLoader> fileLoaders = new HashMap<>(3);
    private static HashMap<String, FileExporter> fileExporters = new HashMap<>(1);

    public static void main(String args[]) throws Exception {
        fileLoaders.put("csv", new CSVLoader());
        fileLoaders.put("json", new JsonLoader());
        fileLoaders.put("xml", new XmlLoader());

        fileExporters.put("csv", new CSVExporter());

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String lineRead = scanner.nextLine();
            if (lineRead.equalsIgnoreCase("list all")) {
                accounts.entrySet().forEach(entry -> {
                    System.out.println(entry.getKey() + ": " + entry.getValue().getBalance());
                });
            } else if (lineRead.length() >= 5 && lineRead.substring(0, 5).equalsIgnoreCase("list ")) {
                String name = lineRead.substring(5);
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
                allTransactions.addAll(transactions);

            } else if (lineRead.length() >= 12 && lineRead.substring(0, 12).equalsIgnoreCase("export file ")) {
                String fileName = lineRead.substring(12);

                Pattern pattern = Pattern.compile("\\.([a-zA-Z]+)$");
                Matcher matcher = pattern.matcher(fileName);
                if(!matcher.find()) {
                    System.out.println("File " + fileName + " does not have an extension.");
                    continue;
                }
                String extension = matcher.group(1);

                if(!fileExporters.containsKey(extension)) {
                    LOGGER.warn("Tried to export file with extension " + extension + " which is not recognised");
                    System.out.println("Files of type " + extension + " are not accepted.");
                    continue;
                }

                List<Transaction> sortedTransactions = allTransactions.stream().sorted(
                        (t1, t2) -> t1.getDate().compareTo(t2.getDate())
                ).collect(Collectors.toList());

                if(!fileExporters.get(extension).exportFile(fileName, sortedTransactions)) {
                    LOGGER.warn("Tried to export to file " + extension + " which already exists");
                    System.out.println("A file named " + fileName + " already exists.");
                } else {
                    System.out.println("Exported transactions to " + fileName + " successfully.");
                }

            }else if (lineRead.equalsIgnoreCase("exit")) {
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
