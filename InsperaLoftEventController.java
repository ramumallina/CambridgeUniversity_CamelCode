package com.ca.ceil.marking.svc.controller;

import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ca.ceil.marking.svc.utility.CEILConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.net.HttpHeaders;

@RestController
@RequestMapping("${spring.application.name}/${root.application.path}/notification")
public class InsperaLoftEventController {

	private final FluentProducerTemplate fluentProducerTemplate;

	public InsperaLoftEventController(FluentProducerTemplate fluentProducerTemplate) {
		this.fluentProducerTemplate = fluentProducerTemplate;
	}
 
	@PostMapping(value = "/loft")
	public ResponseEntity<String>  ceilMarkingNotifications(@RequestBody String body) throws JsonProcessingException {
		Exchange exchange = fluentProducerTemplate.withBody(body).to(CEILConstants.LOFT_EVENT_NOTIFICATION_API_ROUTE).send();
		int responseCode = exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class) ;
	    return ResponseEntity.status(responseCode).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).body(exchange.getIn().getBody().toString());
	}
		
}
