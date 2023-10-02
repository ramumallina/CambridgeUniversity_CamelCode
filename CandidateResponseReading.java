package com.ca.ceil.marking.svc.camelprocessor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CandidateResponseReading implements Processor {

  private Logger logger = LoggerFactory.getLogger(CandidateResponseReading.class);

  @Override
  public void process(Exchange exchange) throws Exception {
    String payload = exchange.getIn().getBody(String.class);
    String candidateResponsePayload = (String) exchange.getProperty("candidateResponse");
    exchange.getIn().setBody(candidateResponsePayload, String.class);
  }
}
