package training.supportbank;

import java.util.List;

public interface FileLoader {
    List<Transaction> loadFile(String filename);
}
