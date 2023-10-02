package com.ca.ceil.marking.svc.camelprocessor;

import java.util.List;
import java.util.Map;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class ReadingBriefingComments {

	public void readBriefingComment(Exchange exchange) throws Exception {
		List<Map<String, Object>> rows = exchange.getIn().getBody(List.class);
		for (Map<String, Object> row : rows) {
			exchange.setProperty("isBriefingCommentPresent", row.get("briefing_comments"));
		}
	}
}
