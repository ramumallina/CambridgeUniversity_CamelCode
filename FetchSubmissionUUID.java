package com.ca.ceil.marking.svc.camelprocessor;

import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;

import com.ca.ceil.marking.svc.utility.CEILConstants;

public class FetchSubmissionUUID {
	
	
	
	@SuppressWarnings("unchecked")
	public void getSubmissionUuid(Exchange exchange) throws Exception {
		List<Map<String, Object>> rows = exchange.getIn().getBody(List.class);
	    for(Map<String, Object> row: rows) {
	    	exchange.getIn().setHeader("elitSubmissionUuid", (String)row.get("elit_submission_uuid"));
	     }
	}
}
