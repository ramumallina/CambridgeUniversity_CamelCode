package com.ca.ceil.marking.svc.camelprocessor;

import java.util.List;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import com.ca.ceil.marking.svc.utility.CEILConstants;

@Component
public class FetchTestIdProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		
		List<Map<String, Object>> sqlRows = exchange.getIn().getBody(List.class);

		if (!sqlRows.isEmpty()) {
			for (Map<String, Object> row : sqlRows) {
				exchange.getIn().setHeader(CEILConstants.TEST_ID, row.get(CEILConstants.DB_TEST_ID));
			}
		}

	}
	
	
	@SuppressWarnings("unchecked")
	  public void fetchSpeakingTestId(Exchange exchange) throws Exception {
	    List<Map<String, Object>> sqlRows = exchange.getIn().getBody(List.class);
	    if(!sqlRows.isEmpty()) {
	    for(Map<String, Object> row: sqlRows) {
	        exchange.getIn().setHeader(CEILConstants.TEST_ID, row.get("insperamoduletestid"));
	       
	     }
	    }
	    else {
	    	throw new NullPointerException("Speaking Test Id is not present in DB");
	    }
	  }
}