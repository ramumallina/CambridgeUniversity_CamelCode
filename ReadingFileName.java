package com.ca.ceil.marking.svc.camelprocessor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ReadingFileName implements Processor {
  private Logger logger = LoggerFactory.getLogger(ReadingFileName.class);

  @Override
  public void process(Exchange exchange) throws Exception {
    String fileNamePayload = exchange.getIn().getBody(String.class);
    String fileName = (String) exchange.getProperty("fileName");
    String questionId = (String) exchange.getProperty("questionId");
    questionId = "ID_" + questionId;
    exchange.getIn().setHeader("questionId", questionId);
    exchange.getIn().setHeader("fileName", fileName);
  }
}
