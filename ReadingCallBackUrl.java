package com.ca.ceil.marking.svc.camelprocessor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ReadingCallBackUrl implements Processor {
  private Logger logger = LoggerFactory.getLogger(ReadingCallBackUrl.class);

  @Override
  public void process(Exchange exchange) throws Exception {
    String callBackPayload = exchange.getIn().getBody(String.class);
    JSONObject jsonObj = new JSONObject(callBackPayload);
    String callbackUrl = jsonObj.getString("callbackUrl");
    exchange.getIn().setHeader("callbackUrl", callbackUrl);
  }
}
