package com.ca.ceil.marking.svc.camelprocessor;

import java.util.List;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ca.ceil.marking.svc.model.CandidateResponseModel;
import com.ca.ceil.marking.svc.utility.CEILConstants;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FetchMarkingMode implements Processor{
  
  private Logger logger = LoggerFactory.getLogger(FetchMarkingMode.class);

@SuppressWarnings("unchecked")
@Override
  public void process(Exchange exchange) throws Exception {
    List<Map<String, Object>> rows = exchange.getIn().getBody(List.class);
    for(Map<String, Object> row: rows) {
        exchange.setProperty(CEILConstants.CEIL_MARKING_MODE, row.get("ceil_marking_mode"));
        exchange.setProperty(CEILConstants.POS_ID, row.get("programofstudy"));
     }
    
  }
}
