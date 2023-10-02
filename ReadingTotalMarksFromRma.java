package com.ca.ceil.marking.svc.camelprocessor;

import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.camel.Exchange;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class ReadingTotalMarksFromRma {

	@SuppressWarnings("unchecked")
	public void readingRmaMarks(Exchange exchange) throws Exception {
		String rmaPayload = exchange.getIn().getBody(String.class);
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new InputSource(new StringReader(rmaPayload)));
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("MarkScheme");
			Integer sumOfNumeric = 0;
			String numeric = "";
			int numericSum = 0;
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					numeric = eElement.getAttribute("numeric");
					Integer numericValue = Integer.parseInt(eElement.getAttribute("numeric"));
					sumOfNumeric += numericValue;
				}
			}
			exchange.setProperty("totalMarksFromRma", sumOfNumeric);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
