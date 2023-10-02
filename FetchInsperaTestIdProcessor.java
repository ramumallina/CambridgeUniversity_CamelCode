package com.ca.ceil.marking.svc.camelprocessor;

import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import static com.ca.ceil.marking.svc.utility.CEILConstants.INSPERA_SPEAKING_TEST_ID;
import static com.ca.ceil.marking.svc.utility.CEILConstants.INSPERA_LISTENING_TEST_ID;
import static com.ca.ceil.marking.svc.utility.CEILConstants.INSPERA_READING_TEST_ID;
import static com.ca.ceil.marking.svc.utility.CEILConstants.INSPERA_WRITING_TEST_ID;

@Component
public class FetchInsperaTestIdProcessor implements Processor {

	@SuppressWarnings("unchecked")
	@Override
	public void process(Exchange exchange) throws Exception {
		List<Map<String, Object>> sqlRows = exchange.getIn().getBody(List.class);
		if (!sqlRows.isEmpty()) {
			for (Map<String, Object> row : sqlRows) {
				exchange.getIn().setHeader("pos", row.get("pos"));
				exchange.getIn().setHeader(INSPERA_SPEAKING_TEST_ID, row.get("inspera_speaking_test_id"));
				exchange.getIn().setHeader(INSPERA_WRITING_TEST_ID, row.get("inspera_writing_test_id"));
				exchange.getIn().setHeader(INSPERA_LISTENING_TEST_ID, row.get("inspera_listening_test_id"));
				exchange.getIn().setHeader(INSPERA_READING_TEST_ID, row.get("inspera_reading_test_id"));
			}
		} 

	}
}
