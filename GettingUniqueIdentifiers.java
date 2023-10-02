package com.ca.ceil.marking.svc.camelprocessor;

import java.util.ArrayList;
import java.util.Arrays;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GettingUniqueIdentifiers implements Processor {
  private Logger logger = LoggerFactory.getLogger(GettingUniqueIdentifiers.class);

  @Override
  public void process(Exchange exchange) throws Exception {
    String payload = exchange.getIn().getBody(String.class);
    String uniqueIdentifiers = (String) exchange.getProperty("uniqueIdentifiersList");
    exchange.getIn().setBody(Arrays.asList(uniqueIdentifiers.split(",")));
  }
}
