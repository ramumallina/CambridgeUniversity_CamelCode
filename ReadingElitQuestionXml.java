package com.ca.ceil.marking.svc.camelprocessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import com.ca.ceil.marking.svc.model.ElitQuestionJson;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ReadingElitQuestionXml implements Processor {

  private Logger logger = LoggerFactory.getLogger(ReadingElitQuestionXml.class);

  @Override
  public void process(Exchange exchange) throws Exception {
    List<ElitQuestionJson> elitQuestionJson = new ArrayList<>();
    String elitQuestionXmlPayloadpayload = exchange.getIn().getBody(String.class);
    String formattedXml = trim(elitQuestionXmlPayloadpayload);
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = dbf.newDocumentBuilder();
    Document document = db.parse(new InputSource(new StringReader(formattedXml)));
    NodeList nodeList = document.getElementsByTagName("assessmentItem");
    String identifier="";
    for(int x=0,size= nodeList.getLength(); x<size; x++) {
        identifier=nodeList.item(x).getAttributes().getNamedItem("identifier").getNodeValue();
        identifier=identifier.substring(identifier.indexOf("ID_")+3);
        logger.info("identifier is::"+ identifier);
    }
    exchange.setProperty("elitQuestionId", identifier);
    String elitQuestionInformation = "";
    if (formattedXml.contains("<assessmentItem")) {
      elitQuestionInformation =
          formattedXml.substring(formattedXml.indexOf("question-main-illustration\"></div>") + 34,
              formattedXml.indexOf("<extendedTextInteraction"));
    }
    if (formattedXml.contains("choiceInteraction")) {
      elitQuestionInformation = formattedXml.substring(formattedXml.indexOf("<prompt>") + 8,
          formattedXml.indexOf("</prompt>"));
    }
    exchange.setProperty("elitExactQuestion", elitQuestionInformation);
    ElitQuestionJson elitQuestion = new ElitQuestionJson();
    elitQuestion.setText(elitQuestionInformation);
    ObjectMapper mapper = new ObjectMapper();
    String elitJsonQuestionData =
        mapper.writerWithDefaultPrettyPrinter().writeValueAsString(elitQuestion);
    UUID uuid = UUID.randomUUID();
    String uuidAsString = uuid.toString();
    exchange.getIn().setHeader("UUId", uuidAsString);
    exchange.getIn().setBody(elitJsonQuestionData, String.class);
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
