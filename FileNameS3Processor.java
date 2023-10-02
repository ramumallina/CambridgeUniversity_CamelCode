package com.ca.ceil.marking.svc.camelprocessor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class FileNameS3Processor implements Processor{

  @Override
  public void process(Exchange exchange) throws Exception {
    String fileName = (String) exchange.getIn().getHeader("CamelFileNameOnly");
    String newFileName = "rawhtml/"+fileName;
    exchange.getIn().setHeader("CamelKey",newFileName);
  }

}
