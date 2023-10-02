package com.ca.ceil.marking.svc.camelprocessor;

import java.util.UUID;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GetUuid implements Processor {
  private Logger logger = LoggerFactory.getLogger(GetUuid.class);

  @Override
  public void process(Exchange exchange) throws Exception {
    String uuid = (String) exchange.getProperty("UUID");
    exchange.getIn().setHeader("UUID", uuid);
  }
}
