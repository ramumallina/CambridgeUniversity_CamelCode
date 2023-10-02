package com.ca.ceil.marking.svc.camelprocessor;

import java.net.URL;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SignedResponseUrlReading implements Processor {
  private Logger logger = LoggerFactory.getLogger(SignedResponseUrlReading.class);

  @Override
  public void process(Exchange exchange) throws Exception {
    String signedResponsePayload = exchange.getIn().getBody(String.class);
    JSONObject jsonObj = new JSONObject(signedResponsePayload);
    String signedResponseUrl = jsonObj.getJSONObject("exportInfo").getString("signedResponseUrl");
    exchange.getIn().setHeader("signedResponseUrl", signedResponseUrl);
    try {
      URL url = new URL(signedResponseUrl);
      String query = url.getQuery();
      exchange.getIn().setHeader("queryParameterssignedResponseUrl", query);
      String protocol = url.getProtocol();
      String colonAsh = "://";
      String host = url.getHost();
      String path = url.getPath();
      String questionMark = "?";
      String output = protocol + colonAsh + host + path + questionMark;
      String fileName = path.substring(32);
      String questionId=(String) exchange.getProperty("questionId");
      exchange.setProperty("fileName", fileName);
      exchange.setProperty("questionId", questionId);
      exchange.getIn().setHeader("urlStartingsignedResponseUrl", output);
    } catch (Exception e) {
      logger.info(e.toString());
    }
    exchange.getIn().setBody(signedResponseUrl);
  }
}
