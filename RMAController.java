package com.ca.ceil.marking.svc.controller;

import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ca.ceil.marking.svc.utility.CEILConstants;
import com.google.common.net.HttpHeaders;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("${spring.application.name}/${root.application.path}")
public class RMAController {

  private final FluentProducerTemplate fluentProducerTemplate;

  public RMAController(FluentProducerTemplate fluentProducerTemplate) {
      this.fluentProducerTemplate = fluentProducerTemplate;
  }
  
  @GetMapping("/candidates/testResponse/{rmaHtmlFileId}")
  public ResponseEntity<String> getPresignedURL(@PathVariable("rmaHtmlFileId") String rmaHtmlFileId) {
    Exchange exchange = fluentProducerTemplate.withBody(rmaHtmlFileId).to(CEILConstants.DIRECT_HTML_PRESIGNED_URL_GENERATION_ROUTE).send();
    int responseCode = exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class) ;
    return ResponseEntity.status(responseCode).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).body(exchange.getIn().getBody().toString());
  }

	@PostMapping(value = "/rmaMarks")
	public ResponseEntity<String> getMarksDetails(@RequestBody String elitXmlData) {
		Exchange exchange = fluentProducerTemplate.withBody(elitXmlData).to(CEILConstants.DIRECT_RMA_MARKS).send();
		int responseCode = exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
		return ResponseEntity.status(responseCode).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
				.body(exchange.getIn().getBody().toString());
	}
	
	  @GetMapping("/candidates/briefingComments/{questionId}")
	  public ResponseEntity<String> getBriefingCommentsPresignedURL(@PathVariable("questionId") String questionId) {
	    Exchange exchange = fluentProducerTemplate.withBody(questionId).to(CEILConstants.HTML_BRIEFING_COMMENTS_PRESIGNEDURL_GENERATION_ROUTE).send();
	    int responseCode = exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class) ;
	    return ResponseEntity.status(responseCode).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).body(exchange.getIn().getBody().toString());
	  }
}