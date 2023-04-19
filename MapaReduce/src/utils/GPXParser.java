package utils;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.*;

/**
 * A simple class that Parses .gpx files
 * @author Kharnifex
 */
public class GPXParser {

    private static Document readGPX(String gpx) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(gpx));
        return builder.parse(is);
    }

    /**
     * Parses a .gpx file and returns a list of GPXWaypoints
     *
     * @param gpx a string containing raw data of a .gpx file
     * @return ArrayList of GPXWaypoints
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws ParseException
     */
    public static ArrayList<GPXWaypoint> parseGPX(String gpx) throws ParserConfigurationException, IOException, SAXException, ParseException {
        Document doc = readGPX(gpx);
        NodeList nodeList = doc.getElementsByTagName("*");
        ArrayList<GPXWaypoint> waypoints = new ArrayList<>();
        String user = null; //not sure where we'll be using this but once we have DAOs its useful

        for (int i = 0; i < nodeList.getLength(); i++) {

            String tempLat;
            String tempLon;
            String tempEle;
            String tempTime;

            Node currNode = nodeList.item(i);

            if (currNode.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) currNode;

                if (currNode.getNodeName().equals("gpx")) {
                    user = element.getAttribute("creator");
                } else if (currNode.getNodeName().equals("wpt")) {
                    tempLat = element.getAttribute("lat");
                    tempLon = element.getAttribute("lon");

                    String textContent = currNode.getTextContent().replace("\n", "");
                    String[] textArr = textContent.split(" ");
                    List<String> filtered = Arrays.stream(textArr).toList().stream().filter(x -> !x.isEmpty()).toList();
                    tempEle = filtered.get(0);
                    tempTime = filtered.get(1).replace("T", " ").replace("Z", "");

                    waypoints.add(new GPXWaypoint(tempLat, tempLon, tempEle, tempTime));
                }
            }
        }
        return waypoints;
    }
}