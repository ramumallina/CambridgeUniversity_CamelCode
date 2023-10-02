package com.ca.ceil.marking.svc.controller;

import static com.ca.ceil.marking.svc.utility.CEILConstants.ELIT_RESPONSE_WEBHOOK_NOTIFICATION;
import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.common.net.HttpHeaders;

@RestController
@RequestMapping("${spring.application.name}/${root.application.path}/elitresponse")
public class ElitResponseApiController {
  
  private final FluentProducerTemplate fluentProducerTemplate;

  public ElitResponseApiController(FluentProducerTemplate fluentProducerTemplate) {
      this.fluentProducerTemplate = fluentProducerTemplate;
  }

  @PostMapping(value = "/notification")
  public ResponseEntity<String> elitMarkingNotifications(@RequestBody String body) {
    Exchange exchange = fluentProducerTemplate.withBody(body).to(ELIT_RESPONSE_WEBHOOK_NOTIFICATION).send();
    int responseCode = exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class) ;
    return ResponseEntity.status(responseCode).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).body(exchange.getIn().getBody().toString());
  }
}
