package training.supportbank;

import java.io.IOException;
import java.util.List;

public interface FileExporter {
    /* Returns false if the file already exists */
    Boolean exportFile(String filename, List<Transaction> transactions) throws IOException;
}
