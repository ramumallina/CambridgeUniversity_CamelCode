package com.ca.ceil.marking.svc.camelprocessor;

import java.util.UUID;

import org.apache.camel.Exchange;
import com.ca.ceil.marking.svc.utility.CEILConstants;
import static com.ca.ceil.marking.svc.utility.CEILConstants.FEEDBACK_UUID;

public class ReadingFeedBackUuid {

	@SuppressWarnings("unchecked")
	public void getFeedBackUuid(Exchange exchange) throws Exception {
		UUID uuid = UUID.randomUUID();
	    exchange.getIn().setHeader(FEEDBACK_UUID, uuid.toString());
	}
}
