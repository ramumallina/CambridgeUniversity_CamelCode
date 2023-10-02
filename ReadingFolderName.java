package com.ca.ceil.marking.svc.camelprocessor;

import java.io.File;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class ReadingFolderName implements Processor{
  
  @Value("${elit.priming.question.folder}")
  private String folderPath;
  
  private Logger logger = LoggerFactory.getLogger(ReadingFolderName.class);

  @Override
  public void process(Exchange exchange) throws Exception {
    String contextObjectId = (String) exchange.getIn().getHeader("CamelFileName");
    String [] cObjectId = contextObjectId.split("[\\\\/]");
    
    boolean isItem = false;
    exchange.setProperty("isItem", isItem);
    if(contextObjectId.contains("-item.xml")) {
      isItem = true;
      exchange.setProperty("isItem", isItem);
    }
    exchange.setProperty("contextObjectId", cObjectId[0]);
    exchange.setProperty("itemFileName", cObjectId[1]);
  }
}
