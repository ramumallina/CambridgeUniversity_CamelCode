package com.ca.ceil.marking.svc.camelprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Component
public class ReadingQuestionInformationProcessorRma {

  @Value("${question.folder}")
  private String folderPath;
  
  @SuppressWarnings("unchecked")
  public void readQuestionDescription(Exchange exchange) throws Exception {

    String readQuestionDsecriptionBody = exchange.getIn().getBody(String.class);
    String formattedXml = trim(readQuestionDsecriptionBody);
    String questionInformation = "";
    List imageList = new ArrayList<>();
    try {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse(new InputSource(new StringReader(readQuestionDsecriptionBody)));
      doc.getDocumentElement().normalize();
      questionInformation = formattedXml.substring(formattedXml.indexOf("<div>"), formattedXml.indexOf("</stimulusBody>"));
      exchange.setProperty("questionDescription", questionInformation);
      if (questionInformation.contains("<span") && questionInformation.contains("<img")) {
        NodeList taxonList = doc.getElementsByTagName("img");
        for (int i = 0; i < taxonList.getLength(); i++) {
          Element taxon = (Element) taxonList.item(i);
          String attribute = taxon.getAttribute("src");
          imageList.add(attribute.substring(attribute.indexOf("ID_"), attribute.indexOf(".svg")+4));
        }
        exchange.setProperty("imageList", imageList);
      }
    } catch (ParserConfigurationException | SAXException | IOException e) {
      e.printStackTrace();
    }
  }
  
  @SuppressWarnings("unchecked")
  public void readQuestionDescriptionProcess(Exchange exchange) throws Exception {
    
    String questionContentItemId = (String) exchange.getProperty("questionContentItemId");
    String readQuestionDsecriptionBody = exchange.getIn().getBody(String.class);
    List imageList = new ArrayList<>();
    String formattedXml = trim(readQuestionDsecriptionBody);
    DocumentBuilderFactory dbFac = DocumentBuilderFactory.newInstance();
    DocumentBuilder dB = dbFac.newDocumentBuilder();
    Document document = dB.parse(new InputSource(new StringReader(readQuestionDsecriptionBody)));
    document.getDocumentElement().normalize();
    String questionInformation = "";
    StringBuffer sB = new StringBuffer();
    
    questionInformation = formattedXml.substring(formattedXml.indexOf("<itemBody>") + 41,
        formattedXml.indexOf("<extendedTextInteraction") - 19);
    sB.append(questionInformation);
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
          imageList.add(attribute.substring(attribute.indexOf("ID_"), attribute.indexOf(".svg")+4));
        }
      } catch (ParserConfigurationException | SAXException | IOException e) {
        e.printStackTrace();
      }
      exchange.setProperty("imageList", imageList);
      File file = new File(folderPath+"/extractedFiles2/"+questionContentItemId+"/imsmanifest.xml");
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
                  questionInformation = langstring.substring(langstring.indexOf(dataLibsItemtype) + 6);
                  String qInfo = "<p>"+questionInformation+"</p>";
                  sB.append(qInfo);
                }
              }
            }
          }
        } 
        catch (ParserConfigurationException | SAXException | IOException e) {
          throw new Exception("Problem occurs during fetching metadata for ELiT priming");
        }
        exchange.setProperty("questionDescription", sB);
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
