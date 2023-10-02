package com.ca.ceil.marking.svc.camelprocessor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TokenReading implements Processor {
  private Logger logger = LoggerFactory.getLogger(TokenReading.class);

  @Override
  public void process(Exchange exchange) throws Exception {
    String payload = exchange.getIn().getBody(String.class);
    JSONObject jsonObj = new JSONObject(payload);
    String token = jsonObj.getString("access_token");
    exchange.getIn().setHeader("Authorization", "Bearer" + " " + token);
  }
}
