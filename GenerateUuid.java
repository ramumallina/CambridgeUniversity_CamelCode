package com.ca.ceil.marking.svc.camelprocessor;

import java.util.UUID;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.ca.ceil.marking.svc.utility.CEILConstants;

@Component
public class GenerateUuid {
  private Logger logger = LoggerFactory.getLogger(GenerateUuid.class);

  public void generateAnswerUUID(Exchange exchange) throws Exception {
    UUID uuid = UUID.randomUUID();
    exchange.getIn().setHeader(CEILConstants.ANSWER_UUID, uuid.toString());
  }

  public void generateSubmissionUUID(Exchange exchange) throws Exception {
    
    UUID uuid = UUID.randomUUID();
    exchange.getIn().setHeader(CEILConstants.SUBMISSION_UUID, uuid.toString());
  }
  
  public void generateUniqueCourseworkResponseIdentifier(Exchange exchange) throws Exception {
    
    UUID uuid = UUID.randomUUID();
    exchange.getIn().setHeader(CEILConstants.UNIQUE_COURSEWORK_RESPONSE_IDENTIFIER, uuid.toString());
  }
}
