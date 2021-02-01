package com.example.cycleasy;


import android.app.Activity;
import android.util.Log;
import android.util.Xml;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class XmlParser {

    String issheltered,rackcount,racktype,racklat,racklong;

    public ArrayList<String> parseFile(InputStream mystream) throws ParserConfigurationException, IOException, SAXException {
        ArrayList<String> rackinfo=new ArrayList<>();
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document doc = dBuilder.parse(mystream);

    //optional, but recommended
    //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
	doc.getDocumentElement().normalize();

	System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

    NodeList extendedDataList = doc.getElementsByTagName("SchemaData");
    NodeList coordList=doc.getElementsByTagName("Point");
	System.out.println("----------------------------");
	for (int temp = 0; temp < extendedDataList.getLength(); temp++) {

        Node extendedData = extendedDataList.item(temp);
        System.out.println("\nCurrent Element :" + extendedData.getNodeName());

        if (extendedData.getNodeType() == Node.ELEMENT_NODE) {
            //get rack count, type and sheltered
            Element eElement = (Element) extendedData;
            issheltered=eElement.getElementsByTagName("SimpleData").item(0).getTextContent();
            racktype=eElement.getElementsByTagName("SimpleData").item(1).getTextContent();
            rackcount=eElement.getElementsByTagName("SimpleData").item(2).getTextContent();
        }
        //get rack coordinates

        Node coordNode=coordList.item(temp);
        if (coordNode.getNodeType()==Node.ELEMENT_NODE) {
            Element coordElement = (Element) coordNode;
            String tempcoord = coordElement.getElementsByTagName("coordinates").item(0).getTextContent();
            String[] tempcoordSplited=tempcoord.split("[,]");
            racklong=tempcoordSplited[0];
            racklat=tempcoordSplited[1];


        }


        rackinfo.add(temp,racklat+"|"+racklong+"|"+racktype+"|"+rackcount+"|"+issheltered);



    }
        Log.d("rackcoord",racklat+" "+racklong);

return rackinfo;
    }


}