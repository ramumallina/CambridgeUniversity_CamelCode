package com.ca.ceil.marking.svc.controller;

import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ca.ceil.marking.svc.model.ProductLookupStructure;
import com.ca.ceil.marking.svc.utility.CEILConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("${spring.application.name}/${root.application.path}/product")
public class ProductController {

  private final FluentProducerTemplate fluentProducerTemplate;

  public ProductController(FluentProducerTemplate fluentProducerTemplate) {
      this.fluentProducerTemplate = fluentProducerTemplate;
  }
  
  @GetMapping()
  public ResponseEntity<String> getDetails() {
    Exchange exchange = fluentProducerTemplate.to(CEILConstants.DIRECT_READ_PRODUCT_ROUTE).send();
    int responseCode = exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class) ;
    return ResponseEntity.status(responseCode).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).body(exchange.getIn().getBody().toString());
  }
  
  @CrossOrigin(origins="*")
  @PostMapping("/create")
  public ResponseEntity<String> createDetails(@RequestBody ProductLookupStructure productLookupStructure) throws JsonProcessingException {
    String posId = productLookupStructure.getProgrammeOfStudyIdentifier();
    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(productLookupStructure);
    Exchange exchange = fluentProducerTemplate.withBody(json).withHeader(CEILConstants.POS_ID, posId).to(CEILConstants.DIRECT_CREATE_PRODUCT_ROUTE).send();
    int responseCode = exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class) ;
    return ResponseEntity.status(responseCode).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).body(exchange.getIn().getBody().toString());
  }
  
  @DeleteMapping()
  public ResponseEntity<String> deleteDetails(@RequestBody String productLookupStructure)  throws JsonProcessingException{
    Exchange exchange = fluentProducerTemplate.withBody(productLookupStructure).to(CEILConstants.DIRECT_DELETE_PRODUCT_ROUTE).send();
    int responseCode = exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class) ;
    return ResponseEntity.status(responseCode).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).body(exchange.getIn().getBody().toString());
  }
  
  @PutMapping("/update")
  public ResponseEntity<String> updateDetails(@RequestBody ProductLookupStructure productLookupStructure) throws JsonProcessingException{
    String posId = productLookupStructure.getProgrammeOfStudyIdentifier();
    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(productLookupStructure);
    Exchange exchange = fluentProducerTemplate.withBody(json).withHeader(CEILConstants.POS_ID, posId).to(CEILConstants.DIRECT_UPDATE_PRODUCT_ROUTE).send();
    int responseCode = exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class) ;
    return ResponseEntity.status(responseCode).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).body(exchange.getIn().getBody().toString());
  }
}
