package com.ca.ceil.marking.svc.camelprocessor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AddingArraySymbol implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		String payload = exchange.getIn().getBody(String.class);
		log.info("payload in AddingArraySymbol Java Class is:::" + payload);

		String str1 = "[";
		String str2 = "]";
		String finalPayload = str1 + payload + str2;
		log.info("finalPayload in AddingArraySymbol is:::" + finalPayload);
		exchange.getIn().setBody(finalPayload, String.class);
	}

}
