package com.ca.ceil.marking.svc.camelprocessor;

import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ca.ceil.marking.svc.utility.CEILConstants;

@Component
public class FetchMarkMappingData{
  
  private Logger logger = LoggerFactory.getLogger(FetchMarkingMode.class);

  @SuppressWarnings("unchecked")
  public void fetchElitData(Exchange exchange) throws Exception {
	logger.info("Started fetching value from DB used for ELiT mark payload mapping");
    List<Map<String, Object>> sqlRows = exchange.getIn().getBody(List.class);
    if(!sqlRows.isEmpty()) {
    for(Map<String, Object> row: sqlRows) {
        exchange.getIn().setHeader(CEILConstants.QUESTION_ID_ELIT, row.get(CEILConstants.ELIT_QUESTION_ID));
        exchange.getIn().setHeader(CEILConstants.INSPERA_CANDIDATE_ID, row.get(CEILConstants.CANDIDATE_INSPERA_ID));
     }
    }
    else {
    	throw new NullPointerException("Required data not available in the DB for ELiT mark payload mapping");
    }
    
  }
  
  @SuppressWarnings("unchecked")
  public void fetchRmaData(Exchange exchange) throws Exception {
	logger.info("Started fetching value from DB used for RMA mark payload mapping");
    List<Map<String, Object>> sqlRows = exchange.getIn().getBody(List.class);
    if(!sqlRows.isEmpty()) {
    for(Map<String, Object> row: sqlRows) {
        exchange.getIn().setHeader(CEILConstants.QUESTION_ID_RMA, row.get(CEILConstants.RMA_QUESTION_ID));
       
     }
    }
    else {
    	throw new NullPointerException("Required data not available in the DB for RMA mark payload mapping");
    }
  }
  
  @SuppressWarnings("unchecked")
  public void fetchDsmcData(Exchange exchange) throws Exception {
	logger.info("Started fetching value from DB used for DSMC mark payload mapping");
    List<Map<String, Object>> sqlRows = exchange.getIn().getBody(List.class);
    if(!sqlRows.isEmpty()) {
    for(Map<String, Object> row: sqlRows) {
        exchange.getIn().setHeader(CEILConstants.DSMC_INSPERA_ID, row.get("candidateinsperaid"));
        //exchange.getIn().setHeader(CEILConstants.DSMC_QUESTION_ID, row.get(CEILConstants.RMA_QUESTION_ID));
        exchange.getIn().setHeader(CEILConstants.ASSESSOR_ID,exchange.getProperty(CEILConstants.ASSESSOR_ID));
               
     }
    }
    else {
    	throw new NullPointerException("Required data not available in the DB for DSMC mark payload mapping");
    }
}
}
