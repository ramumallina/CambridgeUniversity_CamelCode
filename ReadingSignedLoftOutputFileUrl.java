package com.ca.ceil.marking.svc.camelprocessor;

import static com.ca.ceil.marking.svc.utility.CEILConstants.LOFT_OUTPUT_URLS;
import static com.ca.ceil.marking.svc.utility.CEILConstants.QUERY_PARAMERTERS;

import java.net.URL;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ReadingSignedLoftOutputFileUrl implements Processor {
  private Logger logger = LoggerFactory.getLogger(ReadingSignedLoftOutputFileUrl.class);

  @Override
  public void process(Exchange exchange) throws Exception {
    String loftOutputUrls = (String) exchange.getIn().getHeader(LOFT_OUTPUT_URLS);
    
    try {
      URL url = new URL(loftOutputUrls);
      String query = url.getQuery();
      exchange.getIn().setHeader(QUERY_PARAMERTERS, query);
      String protocol = url.getProtocol();
      String colonAsh = "://";
      String host = url.getHost();
      String path = url.getPath();
      String questionMark = "?";
      String output = protocol + colonAsh + host + path + questionMark;
      exchange.getIn().setHeader("signedLoftOutputFileUrl", output);
    } catch (Exception e) {
      logger.info(e.toString());
    }
  }
}
