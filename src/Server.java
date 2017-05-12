import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Server {

    public void run() {
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset( );

        try {
            ServerSocket serverSocket = new ServerSocket(6789);
            Socket socket = serverSocket.accept();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Document doc = (Document) in.readObject();
            System.out.println("Recieved document!");

            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getDocumentElement().getElementsByTagName("rate");
            for (int temp = 0; temp < nodeList.getLength(); temp++) {
                Node nNode = nodeList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    dataset.addValue(Integer.parseInt(eElement.getAttribute("y")), "Energy", eElement.getAttribute("x"));
                }
            }

            in.close();
            createChart(dataset);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Class not found!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createChart(DefaultCategoryDataset dataset) throws IOException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDate localDate = LocalDate.now();
        String date = DateTimeFormatter.ofPattern("yyy/MM/dd").format(localDate);

        JFreeChart lineChartObject = ChartFactory.createLineChart(
                " Daily Energy Usage (" + date + ")","[hour]","[Wh]",
                dataset, PlotOrientation.VERTICAL,
                true,true,false);

        int width = 800; /* Width of the image */
        int height = 600; /* Height of the image */
        File lineChart = new File( "src/LineChart.jpeg" );
        ChartUtilities.saveChartAsJPEG(lineChart ,lineChartObject, width ,height);
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
}