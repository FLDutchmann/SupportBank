package training.supportbank;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class XmlExporter implements FileExporter {
    private static final Logger LOGGER = LogManager.getLogger();
    public Boolean exportFile(String filename, List<Transaction> transactions) throws IOException {
        File file = new File(filename);
        if(!file.createNewFile()) {
            LOGGER.error("Tried to overwrite an existing file.");
            return false;
        }
        FileWriter writer = new FileWriter(filename);
        Document doc;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.newDocument();
        } catch (Exception e) {
            LOGGER.error("An error while creating a new document.");
            return false;
        }
        Element rootElement = doc.createElement("TransactionList");
        for(Transaction transaction : transactions) {
            rootElement.appendChild(createTransaction(doc, transaction.getFrom(),transaction.getTo(),transaction.getAmount().toString(),transaction.getNarrative(),transaction.getDate()));
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transf = transformerFactory.newTransformer();
        transf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transf.setOutputProperty(OutputKeys.INDENT, "yes");
        transf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        return true;
    }

    private static Node createTransaction(Document doc, String fromAccount, String toAccount, String amount, String narrative, LocalDate date){
        Element element = doc.createElement("SupportTransaction");
        LocalDate referenceDate = LocalDate.of(1900,1,1);
        long daysBetween = ChronoUnit.DAYS.between(referenceDate, date);
        element.setAttribute("SupportTransaction",Long.toString(daysBetween));
        element.appendChild(createElement(doc, "Description", narrative));
        element.appendChild(createElement(doc, "Value", amount));
        Element parties = doc.createElement("Parties");
        parties.appendChild(createElement(doc,"From",fromAccount));
        parties.appendChild(createElement(doc,"To",toAccount));
        element.appendChild(parties);
        return element;
    }

    private static Node createElement(Document doc, String name, String value){
        Element node = doc.createElement(name);
        node.appendChild(doc.createTextNode(value));
        return node;
    }
}
