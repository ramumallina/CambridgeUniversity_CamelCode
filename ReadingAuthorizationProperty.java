package com.ca.ceil.marking.svc.camelprocessor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ReadingAuthorizationProperty implements Processor {
  private Logger logger = LoggerFactory.getLogger(ReadingAuthorizationProperty.class);

  @Override
  public void process(Exchange exchange) throws Exception {
    String authorPayload = exchange.getIn().getBody(String.class);
    String authorization = (String) exchange.getProperty("Authorization");
    exchange.getIn().setHeader("Authorization", authorization);
  }
}
