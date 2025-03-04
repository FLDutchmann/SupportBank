package training.supportbank;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class JsonLoader implements FileLoader{
    private static final Logger LOGGER = LogManager.getLogger();
    @Override
    public List<Transaction> loadFile(String filename) throws Exception {
        LOGGER.info("Loading transactions from json file " + filename);
        GsonBuilder gsonBuilder = new GsonBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        gsonBuilder.registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (jsonElement, type, jsonDeserializationContext) ->
                {
                    LocalDate date = LocalDate.of(1900,1,1);
                    try{
                        date = LocalDate.parse(jsonElement.getAsString(),formatter);
                    } catch(Exception e) {
                        LOGGER.error("Unable to interpret date " + jsonElement.getAsString() + ". Using default value instead.");
                    };
                    return date;
                }
        );
        gsonBuilder.registerTypeAdapter(Currency.class, (JsonDeserializer<Currency>) (jsonElement, type, jsonDeserializationContext) ->
                new Currency(jsonElement.getAsString())
        );
        Gson gson = gsonBuilder.create();
        Transaction[] transactions = new Transaction[0];
        try {
            Reader reader = Files.newBufferedReader(Paths.get(filename));
            transactions = gson.fromJson(reader, Transaction[].class);
            reader.close();
        } catch (Exception ex) {
            LOGGER.error("Failed to file " + filename);
            throw ex;
        }

        return Arrays.asList(transactions);
    }
}
