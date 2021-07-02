package training.supportbank;

import com.google.gson.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class JsonExporter implements FileExporter{
    @Override
    public Boolean exportFile(String filename, List<Transaction> transactions) throws IOException {
        File file = new File(filename);
        if(!file.createNewFile()) {
            return false;
        }
        GsonBuilder gsonBuilder = new GsonBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        gsonBuilder.registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (date, type, jsonSerializationContext) ->
                new JsonPrimitive(formatter.format(date))
        );
        gsonBuilder.registerTypeAdapter(Currency.class, (JsonSerializer<Currency>) (amount, type, jsonSerializationContext) ->
                new JsonPrimitive(amount.toString())
        );

        Gson gson = gsonBuilder.setPrettyPrinting().create();
        String output = gson.toJson(transactions);
        FileWriter writer = new FileWriter(filename);
        writer.write(output);

        writer.close();

        return true;
    }
}
