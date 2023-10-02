package com.ca.ceil.marking.svc.camelprocessor;

import java.util.List;
import java.util.Map;
import org.apache.camel.Exchange;
import com.ca.ceil.marking.svc.utility.CEILConstants;

public class FetchCeilMarkingMode {

	@SuppressWarnings("unchecked")
	public void ceilMarkingMode(Exchange exchange) throws Exception {
		List<Map<String, Object>> rows = exchange.getIn().getBody(List.class);
	    for(Map<String, Object> row: rows) {
	    	//exchange.getIn().setHeader(CEILConstants.CEIL_MARKING_MODE, row.get(CEILConstants.CEIL_MARKING_MODE));
	    	exchange.setProperty(CEILConstants.CEIL_MARKING_MODE, row.get(CEILConstants.CEIL_MARKING_MODE));
	     }
	}
}
