package com.example.cycleasy;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.File;

public class XmlWriter {

    private static DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    private static DocumentBuilder dBuilder;

    static {
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    private static Document doc = dBuilder.newDocument();
    private Element rootFavPath;
    private Element rootCychist;

    // root element



    public void writeXML(int indexNo, String startpt,String endpt) throws ParserConfigurationException{
        rootFavPath = doc.createElement("favpaths");
        rootCychist = doc.createElement("cychistories");

        doc.appendChild(rootFavPath);
        doc.appendChild(rootCychist);

        try {
            Element favpath = doc.createElement("favpath");
            // setting attribute to element
            Attr attr = doc.createAttribute("index");
            attr.setValue(String.valueOf(indexNo));
            favpath.setAttributeNode(attr);

            // carname element
            Element startpoint= doc.createElement("startpt");
            startpoint.appendChild(doc.createTextNode(startpt));
            favpath.appendChild(startpoint);

            Element endpoint= doc.createElement("endpt");
            endpoint.appendChild(doc.createTextNode(endpt));
            favpath.appendChild(endpoint);
            rootFavPath.appendChild(favpath);

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("D:\\userhistory.xml"));
            transformer.transform(source, result);

            // Output to console for testing
            StreamResult consoleResult = new StreamResult(System.out);
            transformer.transform(source, consoleResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}