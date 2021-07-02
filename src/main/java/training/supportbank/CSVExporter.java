package training.supportbank;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CSVExporter implements FileExporter {
    @Override
    public Boolean exportFile(String filename, List<Transaction> transactions) throws IOException {
        File file = new File(filename);
        if(!file.createNewFile()) {
            return false;
        }

        FileWriter writer = new FileWriter(filename);
        writer.write("Date,From,To,Narrative,Amount\n");
        for(Transaction transaction: transactions) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String date = formatter.format(transaction.getDate());
            writer.write(date + ","
                    + transaction.getFrom() + ","
                    + transaction.getTo() + ","
                    + transaction.getNarrative() + ","
                    + transaction.getAmount() + "\n");
        }
        writer.close();

        return true;
    }
}
