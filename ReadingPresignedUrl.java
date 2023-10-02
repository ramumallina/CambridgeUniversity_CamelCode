package com.ca.ceil.marking.svc.camelprocessor;

import java.net.URL;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ReadingPresignedUrl implements Processor {
  private Logger logger = LoggerFactory.getLogger(ReadingPresignedUrl.class);

  @Override
  public void process(Exchange exchange) throws Exception {
    String payload = exchange.getIn().getBody(String.class);
    String exportFinishedPayload = (String) exchange.getProperty("submissionExportFinishedJson");
    JSONObject jsonObj = new JSONObject(exportFinishedPayload);
    JSONObject obj = jsonObj.getJSONObject("extraInfo");
    JSONObject x = obj.getJSONObject("value");
    String presignedUrl = x.get("signedResponseUrl").toString();
    try {
      URL url = new URL(presignedUrl);
      String query = url.getQuery();
      exchange.getIn().setHeader("queryParameters", query);
      String protocol = url.getProtocol();
      String colonAsh = "://";
      String host = url.getHost();
      String path = url.getPath();
      String questionMark = "?";
      String output = protocol + colonAsh + host + path + questionMark;
      exchange.getIn().setHeader("urlStarting", output);
    } catch (Exception e) {
      logger.info(e.toString());
    }
    exchange.getIn().setBody(presignedUrl);
  }
}
