package com.ca.ceil.marking.svc.camelprocessor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DeliveredNotificationReading implements Processor {
  private Logger logger = LoggerFactory.getLogger(DeliveredNotificationReading.class);

  @Override
  public void process(Exchange exchange) throws Exception {
    String payload = exchange.getIn().getBody(String.class);
    String deliveredNotificationPayload = (String) exchange.getProperty("submissionDeliveredJson");
    exchange.getIn().setBody(deliveredNotificationPayload);
  }
}
