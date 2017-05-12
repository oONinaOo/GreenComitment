import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {

    public void run() {
        try {
            Document document = createXML();

            Socket socket = new Socket("localhost", 6789);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(document);
            System.out.println("Send document!");
            out.flush();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Document createXML() {
        Integer valueX;
        Integer valueY;
        Document document = null;

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            document = dBuilder.newDocument();

            Element rootElement = document.createElement("measure");
            document.appendChild(rootElement);

            for(int i = 1; i <= 24; i++){
                Element rate = document.createElement("rate");
                Attr y = document.createAttribute("y");
                valueY = (int) (Math.random()*1000);
                y.setValue(valueY.toString());
                rate.setAttributeNode(y);

                Attr x = document.createAttribute("x");
                valueX = i;
                x.setValue(valueX.toString());
                rate.setAttributeNode(x);
                rootElement.appendChild(rate);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File("src/sensordata.xml"));
            transformer.transform(source, result);
            return document;

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return document;
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }
}