package com.ca.ceil.marking.svc.controller;

import static com.ca.ceil.marking.svc.utility.CEILConstants.DIRECT_WEBHOOK_NOTIFICATIONS;

import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ca.ceil.marking.svc.model.NotificationModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("${spring.application.name}/${root.application.path}/submission")
public class InsperaController {
	
	private final FluentProducerTemplate fluentProducerTemplate;

	public InsperaController(FluentProducerTemplate fluentProducerTemplate) {
		this.fluentProducerTemplate = fluentProducerTemplate;
	}
 
	@PostMapping(value = "/notification")
	public ResponseEntity<String>  ceilMarkingNotifications(@RequestBody NotificationModel notificationModel) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(notificationModel);
		Exchange exchange = fluentProducerTemplate.withBody(json).to(DIRECT_WEBHOOK_NOTIFICATIONS).send();
		int responseCode = exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class) ;
	    return ResponseEntity.status(responseCode).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).body(exchange.getIn().getBody().toString());
	}
}
