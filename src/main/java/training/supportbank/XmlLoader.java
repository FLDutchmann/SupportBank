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
        try {
            File inputFile = new File(filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("SupportTransaction");
            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                if(nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    int numberOfDays = Integer.parseInt(eElement.getAttribute("Date"));
                    LocalDate date = LocalDate.of(1900,1,1).plusDays(numberOfDays);
                    String narrative = eElement
                            .getElementsByTagName("Description")
                            .item(0)
                            .getTextContent();
                    String tempAmount = eElement
                            .getElementsByTagName("Value")
                            .item(0)
                            .getTextContent();
                    Currency amount = new Currency(tempAmount);
                    NodeList parties = eElement.getElementsByTagName("Parties");
                    Element partiesElement = (Element) parties.item(0);
                    String fromAccount = partiesElement
                            .getElementsByTagName("From")
                            .item(0)
                            .getTextContent();
                    String toAccount = partiesElement
                            .getElementsByTagName("To")
                            .item(0)
                            .getTextContent();
                    transactions.add(new Transaction(amount, fromAccount, toAccount, narrative, date));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return transactions;
    }
}
