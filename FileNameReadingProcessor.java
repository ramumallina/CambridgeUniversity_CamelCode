package com.ca.ceil.marking.svc.camelprocessor;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class FileNameReadingProcessor {

  public void xmlFileName(Exchange exchange) {
    String name = (String) exchange.getProperty("camelFileNameConsumed");
    String questionId = name.substring(name.indexOf("ID_")+3, name.indexOf("-item.xml"));
    exchange.setProperty("elitQuestionId",questionId);
  }
}
