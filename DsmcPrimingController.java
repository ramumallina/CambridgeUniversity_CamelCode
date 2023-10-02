package com.ca.ceil.marking.svc.controller;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.common.net.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import static com.ca.ceil.marking.svc.utility.CEILConstants.DIRECT_DSMC_PRIMING_ROUTE;

@RestController
@Slf4j
@RequestMapping("${spring.application.name}/${root.application.path}/dsmc")
public class DsmcPrimingController {
  
private final FluentProducerTemplate fluentProducerTemplate;
  
  @Autowired
  private CamelContext camelContext;
  
  Exchange exchangeResponse = null;
  
  int responsecode = 0;
  
  public DsmcPrimingController(FluentProducerTemplate fluentProducerTemplate) {
      this.fluentProducerTemplate = fluentProducerTemplate;
  }
  
  @GetMapping(value = "/priming")
  public ResponseEntity<String> dsmcPrimingNotifications() {
    Exchange exchange = fluentProducerTemplate.to(DIRECT_DSMC_PRIMING_ROUTE).send();
    int responseCode = exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class) ;
    return ResponseEntity.status(responseCode).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).body(exchange.getIn().getBody().toString());
  }

}
