package com.ca.ceil.marking.svc.camelprocessor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ReadingRevisionStatusUpdatedPayload implements Processor {
  private Logger logger = LoggerFactory.getLogger(ReadingRevisionStatusUpdatedPayload.class);

  @Override
  public void process(Exchange exchange) throws Exception {
    String payload = exchange.getIn().getBody(String.class);
    String revisionStatusUpdatedPayload =
        (String) exchange.getProperty("revisionStatusUpdatedPayload");
    exchange.getIn().setBody(revisionStatusUpdatedPayload, String.class);
  }
}
