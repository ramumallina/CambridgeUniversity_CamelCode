package com.ca.ceil.marking.svc.camelprocessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.UUID;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

@Component
public class ReadStimulusQuestionDetails implements Processor {

  private Logger logger = LoggerFactory.getLogger(ReadStimulusQuestionDetails.class);

  @Override
  public void process(Exchange exchange) throws Exception {
    String elitQuestionXmlPayloadpayload = exchange.getIn().getBody(String.class);
    String formattedXml = trim(elitQuestionXmlPayloadpayload);
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = dbf.newDocumentBuilder();
    Document document = db.parse(new InputSource(new StringReader(formattedXml)));
    NodeList nodeList = document.getElementsByTagName("assessmentStimulus");
    Map<String, Object> map = new HashedMap<>();
    for (int x = 0, size = nodeList.getLength(); x < size; x++) {
      String identifier1 =
          nodeList.item(x).getAttributes().getNamedItem("identifier").getNodeValue();
      identifier1 = identifier1.substring(identifier1.indexOf("ID_") + 3);
      map.put("elitStiquestionId", Integer.parseInt(identifier1));
    }

    String questionInformation = "";
    if (formattedXml.contains("<assessmentStimulus")) {
      questionInformation =
          formattedXml.substring(formattedXml.indexOf("<div>") + 5, formattedXml.indexOf("</div>"));
      map.put("elitquestionDes", questionInformation);
    }
    map.put("uuidStatus", "ready");
    map.put("uuid", UUID.randomUUID().toString());
    exchange.getIn().setBody(map);
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
