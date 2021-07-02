package training.supportbank;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class XmlLoader implements FileLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    public List<Transaction> loadFile(String filename) throws Exception {
        LOGGER.info("Loading transactions from xml file " + filename);
        ArrayList<Transaction> transactions = new ArrayList<>();

        File inputFile = new File(filename);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc;
        try {
            doc = dBuilder.parse(inputFile);
        } catch (Exception e) {
            LOGGER.error("Failed to parse xml file " + filename);
            throw e;
        }
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("SupportTransaction");
        int errors = 0;
        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            if(nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                int numberOfDays = Integer.parseInt(eElement.getAttribute("Date"));
                LocalDate date = LocalDate.of(1900,1,1).plusDays(numberOfDays);
                String narrative = "";
                try {
                    narrative = eElement
                            .getElementsByTagName("Description")
                            .item(0)
                            .getTextContent();
                } catch (Exception e) {
                    LOGGER.error("Unable to extract narrative in entry " + i);
                    errors++;
                }
                Currency amount = new Currency(0);
                try {
                    String tempAmount = eElement
                            .getElementsByTagName("Value")
                            .item(0)
                            .getTextContent();
                    amount = new Currency(tempAmount);
                } catch (Exception e) {
                    LOGGER.error("Unable to extract amount in entry " + i);
                    errors++;
                }
                String fromAccount = "", toAccount = "";
                try {
                    NodeList parties = eElement.getElementsByTagName("Parties");
                    Element partiesElement = (Element) parties.item(0);
                    fromAccount = partiesElement
                            .getElementsByTagName("From")
                            .item(0)
                            .getTextContent();
                    toAccount = partiesElement
                            .getElementsByTagName("To")
                            .item(0)
                            .getTextContent();
                } catch (Exception e) {
                    LOGGER.error("Unable to extract parties in entry " + i);
                    errors++;
                }
                transactions.add(new Transaction(amount, fromAccount, toAccount, narrative, date));
            }
        }
        if(errors > 0) {
            LOGGER.error("Unable to load " + filename + ", encountered " + errors + " errors");
            throw new Exception();
        }

        return transactions;
    }
}
