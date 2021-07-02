package training.supportbank;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class CSVLoader implements FileLoader{
    private static final Logger LOGGER = LogManager.getLogger();
    @Override
    public List<Transaction> loadFile(String filename) throws Exception {
        LOGGER.info("Loading transactions from csv file " + filename);
        File file = new File(filename);
        Scanner reader = new Scanner(file);

        if (!reader.nextLine().equalsIgnoreCase("Date,From,To,Narrative,Amount")) {
            LOGGER.error("The first line is incorrect; the header should be 'Date,From,To,Narrative,Amount'");
            throw new Exception();
        }

        int lineNumber = 1;
        boolean crashAndBurn = false;

        List<Transaction> transactions = new ArrayList<>();
        while (reader.hasNextLine()) {
            lineNumber++;
            String line = reader.nextLine();
            String[] entries = line.split(",");

            if (entries.length != 5) {
                LOGGER.error("Line " + lineNumber + " does not have exactly 5 entries: " + line);
                crashAndBurn = true;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate date = LocalDate.of(1900,1,1);
            try {
                date = LocalDate.parse(entries[0],formatter);
            } catch (Exception e) {
                LOGGER.error("Line " + lineNumber + " does not have a valid date: " + entries[0]);
                crashAndBurn = true;
            }
            String from = entries[1];
            String to = entries[2];
            String narrative = entries[3];

            Currency amount = new Currency(0);
            try {
                amount = new Currency(entries[4]);
            } catch (NumberFormatException e) {
                LOGGER.error("Line " + lineNumber + " does not have a valid amount: " + entries[4]);
                crashAndBurn = true;
            }
            Transaction transaction = new Transaction(amount, from, to, narrative, date);
            transactions.add(transaction);
        }

        if (crashAndBurn) {
            LOGGER.error("Encountered at least one error while parsing the csv file " + filename + ". Exiting");
            throw new Exception();
        }

        return transactions;
    }
}
