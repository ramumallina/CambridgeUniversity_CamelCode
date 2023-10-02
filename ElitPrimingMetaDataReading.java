package com.ca.ceil.marking.svc.camelprocessor;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@SuppressWarnings("unused")
@Component
public class ElitPrimingMetaDataReading implements Processor {

  private Logger logger = LoggerFactory.getLogger(ElitPrimingMetaDataReading.class);

  @Value("${elit.priming.question.folder}")
  private String folderPath;

  @Override
  public void process(Exchange exchange) throws Exception {

    String contextObjectId = (String) exchange.getProperty("contextObjectId");
    String fileName = (String) exchange.getProperty("itemFileName");
    try {
      String payload = "";
      StringBuffer sb = new StringBuffer();
      File xmlFile =
          new File(folderPath + "/extractedFiles2/" + contextObjectId + "/imsmanifest.xml");
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      Document doc = factory.newDocumentBuilder().parse(xmlFile);
      Element resource = getResourceByHref(doc, fileName);
      Element taxonpath = getTaxonpathUnderResource(resource);
      String str = nodeToString(taxonpath);
      DocumentBuilderFactory factory1 = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory1.newDocumentBuilder();
      Document doc1 = builder.parse(new InputSource(new StringReader(str)));
      NodeList taxonNodes = doc1.getElementsByTagName("imsmd:taxon");
      for (int i = 0; i < taxonNodes.getLength(); i++) {
        Element taxon = (Element) taxonNodes.item(i);
        String id = taxon.getElementsByTagName("imsmd:id").item(0).getTextContent();
        if (id.contains("LIBS Rubric") || id.contains("Input Text Type")
            || id.contains("Description") || id.contains("Topic")
            || id.contains("Testing Focus Level")) {
          String langString =
              taxon.getElementsByTagName("imsmd:langstring").item(0).getTextContent();
          payload = "<imsmd:taxon><imsmd:id>" + id + "</imsmd:id><imsmd:entry><imsmd:langstring>"
              + langString + "</imsmd:langstring></imsmd:entry></imsmd:taxon>";
          sb.append(payload);
        }
      }
      
      for (int i = 0; i < taxonNodes.getLength(); i++) {
        Element taxon = (Element) taxonNodes.item(i);
        String id = taxon.getElementsByTagName("imsmd:id").item(0).getTextContent();
        if (id.contentEquals("contentRevisionId")) {
          String revisionId =
              taxon.getElementsByTagName("imsmd:langstring").item(0).getTextContent();
          exchange.setProperty("revisionId", revisionId);
        }
      }
      
      String finalMetadata =
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?><manifest xmlns=\"http://www.imsglobal.org/xsd/imscp_v1p1\" xmlns:imsmd=\"http://www.imsglobal.org/xsd/imsmd_v1p2\" xmlns:java=\"http://xml.apache.org/xalan/java\" version=\"1.1\" identifier=\"MANIFEST\">"
              + sb.toString() + "</manifest>";
      String elitMetaData = finalMetadata.replace("'", "''");
      exchange.setProperty("elitMetaData", elitMetaData);
    } catch (ParserConfigurationException | SAXException | IOException e) {
      throw new Exception("Problem occurs during fetching metadata for ELiT priming");
    }
  }

  private static Element getResourceByHref(Document doc, String hrefValue) {
    NodeList resources = doc.getElementsByTagName("resource");
    for (int i = 0; i < resources.getLength(); i++) {
      Element resource = (Element) resources.item(i);
      String href = resource.getAttribute("href");
      if (href.contains(hrefValue)) {
        return resource;
      }
    }
    return null;
  }

  private static String nodeToString(org.w3c.dom.Node node) throws Exception {
    javax.xml.transform.Transformer transformer =
        javax.xml.transform.TransformerFactory.newInstance().newTransformer();
    transformer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
    java.io.StringWriter sw = new java.io.StringWriter();
    javax.xml.transform.stream.StreamResult sr = new javax.xml.transform.stream.StreamResult(sw);
    transformer.transform(new javax.xml.transform.dom.DOMSource(node), sr);
    return sw.toString();
  }

  private static Element getTaxonpathUnderResource(Element resource) {
    NodeList taxonpaths = resource.getElementsByTagName("imsmd:taxonpath");
    return (Element) taxonpaths.item(0);
  }
}


