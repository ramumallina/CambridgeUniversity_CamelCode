package com.ca.ceil.marking.svc.camelprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.camel.Exchange;
import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Component
public class ReadingQuestionInformationProcessor {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(ReadingQuestionInformationProcessor.class);
  
  @Value("${elit.priming.question.folder}")
  private String folderPath;

  @SuppressWarnings("unchecked")
  public void readQuestionDescription(Exchange exchange) throws Exception {

    String readQuestionDsecriptionBody = exchange.getIn().getBody(String.class);
    LOGGER.info("exchange is "+ exchange.getAllProperties().toString());
    LOGGER.info("readQuestionDsecriptionBody is "+ readQuestionDsecriptionBody );
    String formattedXml = trim(readQuestionDsecriptionBody);
    String questionInformation = formattedXml.substring(formattedXml.indexOf("<div>"),
        formattedXml.indexOf("</stimulusBody>"));
    exchange.setProperty("questionDescription", questionInformation);
    UUID uuid = UUID.randomUUID();
    exchange.setProperty("uuid", uuid.toString());
    List imageList = new ArrayList<>();

    try {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse(new InputSource(new StringReader(readQuestionDsecriptionBody)));
      doc.getDocumentElement().normalize();
      NodeList nodeList = doc.getElementsByTagName("assessmentStimulus");
      for (int x = 0, size = nodeList.getLength(); x < size; x++) {
        String identifier1 =
            nodeList.item(x).getAttributes().getNamedItem("identifier").getNodeValue();
        identifier1 = identifier1.substring(identifier1.indexOf("ID_") + 3);
      }
      if (questionInformation.contains("<span") && questionInformation.contains("<img")) {
        NodeList taxonList = doc.getElementsByTagName("img");
        for (int i = 0; i < taxonList.getLength(); i++) {
          Element taxon = (Element) taxonList.item(i);
          String attribute = taxon.getAttribute("src");
          imageList.add(attribute.substring(attribute.indexOf("ID_"), attribute.indexOf(".svg")));
        }
        exchange.setProperty("imageList", imageList);
      }
    } catch (ParserConfigurationException | SAXException | IOException e) {
      e.printStackTrace();
    }
  }

  @SuppressWarnings("unchecked")
  public void readQuestionDescriptionProcess(Exchange exchange) throws Exception {

    String readQuestionDsecriptionBody = exchange.getIn().getBody(String.class);
    List imageList = new ArrayList<>();
    String formattedXml = trim(readQuestionDsecriptionBody);
    String contextObjectId = (String) exchange.getProperty("contextObjectId");
    DocumentBuilderFactory dbFac = DocumentBuilderFactory.newInstance();
    DocumentBuilder dB = dbFac.newDocumentBuilder();
    Document document = dB.parse(new InputSource(new StringReader(readQuestionDsecriptionBody)));
    document.getDocumentElement().normalize();
    UUID uuid = UUID.randomUUID();
    exchange.setProperty("uuid", uuid.toString());
    NodeList nodeList = document.getElementsByTagName("assessmentItem");
    for (int x = 0, size = nodeList.getLength(); x < size; x++) {
      String identifier =
          nodeList.item(x).getAttributes().getNamedItem("identifier").getNodeValue();
      identifier = identifier.substring(identifier.indexOf("ID_") + 3);
    }

    String questionInformation = formattedXml.substring(formattedXml.indexOf("</rubricBlock>") + 45,
        formattedXml.indexOf("<extendedTextInteraction") - 19);
    exchange.setProperty("questionDescription", questionInformation);
    if (questionInformation.contains("<span") && questionInformation.contains("<img")) {
      try {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc =
            dBuilder.parse(new InputSource(new StringReader(readQuestionDsecriptionBody)));
        doc.getDocumentElement().normalize();
        NodeList taxonList = doc.getElementsByTagName("img");
        for (int i = 0; i < taxonList.getLength(); i++) {
          Element taxon = (Element) taxonList.item(i);
          String attribute = taxon.getAttribute("src");
          imageList.add(attribute.substring(attribute.indexOf("ID_"), attribute.indexOf(".svg")));
        }
      } catch (ParserConfigurationException | SAXException | IOException e) {
        e.printStackTrace();
      }
      exchange.setProperty("imageList", imageList);
//      File file = new File("C:\\input_new\\extractedFiles2\\imsmanifest.xml");
      File file = new File(folderPath+"/extractedFiles2/"+contextObjectId+"/imsmanifest.xml");
      if (file.exists()) {
        Reader fileReader = new FileReader(file);
        BufferedReader bufReader = new BufferedReader(fileReader);
        StringBuilder sb = new StringBuilder();
        String line = bufReader.readLine();
        while (line != null) {
          sb.append(line).append("\n");
          line = bufReader.readLine();
        }
        String manifestPayload = sb.toString();
        String dataLibsItemtype = (String) exchange.getProperty("dataLibsItemtype");
        try {
          DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
          DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
          Document doc = dBuilder.parse(new InputSource(new StringReader(manifestPayload)));
          doc.getDocumentElement().normalize();
          String langstring = "";

          NodeList taxonList = doc.getElementsByTagName("imsmd:taxon");
          for (int i = 0; i < taxonList.getLength(); i++) {
            Element taxon = (Element) taxonList.item(i);
            if (taxon.getElementsByTagName("imsmd:id").item(0) != null) {
              String id = taxon.getElementsByTagName("imsmd:id").item(0).getTextContent();
              if (id.contains("LIBS Rubric")) {
                Element entry = (Element) taxon.getElementsByTagName("imsmd:entry").item(0);
                langstring =
                    entry.getElementsByTagName("imsmd:langstring").item(0).getTextContent();
                if (langstring.contains(dataLibsItemtype)) {
                  exchange.setProperty("questionDescription",
                      langstring.substring(langstring.indexOf("Part 7") + 6));
                }
              }
            }
          }
        } catch (ParserConfigurationException | SAXException | IOException e) {
          throw new Exception("Problem occurs during fetching metadata for ELiT priming");
        }
      }
    }
  }

  public static String trim(String input) {
    BufferedReader reader = new BufferedReader(new StringReader(input));
    StringBuilder result = new StringBuilder();
    try {
      String line;
      while ((line = reader.readLine()) != null)
        result.append(line.trim());
      return result.toString();
    } catch (IOException e) {
      throw new RuntimeException(e.toString());
    }
  }

}
