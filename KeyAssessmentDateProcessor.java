package com.ca.ceil.marking.svc.camelprocessor;

import java.time.LocalDate;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import com.ca.ceil.marking.svc.utility.CEILConstants;

@Component
public class KeyAssessmentDateProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		String kad = exchange.getIn().getHeader(CEILConstants.KAD_Date, String.class);
		LocalDate kadDate = LocalDate.parse(kad);
		LocalDate currentDate = LocalDate.now();
		if (kadDate.isAfter(currentDate)) {

			exchange.setProperty(CEILConstants.SENT_TO_QUEUE, false);

		} else {

			exchange.setProperty(CEILConstants.SENT_TO_QUEUE, true);
		}
	}
}
