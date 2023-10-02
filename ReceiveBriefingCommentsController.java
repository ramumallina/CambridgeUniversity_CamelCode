package com.ca.ceil.marking.svc.controller;

import java.io.File;
import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ca.ceil.marking.svc.utility.CEILConstants;
import com.ca.ceil.marking.svc.utility.CeilUtility;
import com.google.common.net.HttpHeaders;

@RestController
@RequestMapping("${spring.application.name}/${root.application.path}")
public class ReceiveBriefingCommentsController {
  
  @Value("${image.upload.folder}")
  private String path;
  
  @Autowired
  CeilUtility ceilUtility;
  
  private final FluentProducerTemplate fluentProducerTemplate;

  public ReceiveBriefingCommentsController(FluentProducerTemplate fluentProducerTemplate) {
      this.fluentProducerTemplate = fluentProducerTemplate;
  }

  @SuppressWarnings("unused")
  @PostMapping(value = "/briefingCommentsForRma/upload")
  public ResponseEntity<String> uploadBriefingComments(@RequestParam(name="file", required=false) MultipartFile file) throws NullPointerException{
    String body=null;
    int responseCode=0;
    if(file!=null && !file.isEmpty()) {
    if(!file.getContentType().equalsIgnoreCase("application/pdf")) {
      responseCode=400;
      body = ceilUtility.simpleDTO(responseCode,"Only PDF is accepted");
    }
    else {
    try {
      String fileName = file.getOriginalFilename();
      String briefingCommentsFileName =  fileName.substring(0, fileName.indexOf(".pdf"));
      file.transferTo(new File(path+briefingCommentsFileName+"_BriefingComments.pdf"));
      Exchange exchange = fluentProducerTemplate.withBody(briefingCommentsFileName).to(CEILConstants.RECEIVE_BRIEFING_COMMENTS_FOR_RMA).send();
      responseCode=200;
      body = ceilUtility.simpleDTO(responseCode);
    }catch(Exception e) {
      responseCode=500;
      body = ceilUtility.simpleDTO(responseCode);
    }
    }
  }
  else {
    responseCode=400;
    body = ceilUtility.simpleDTO(responseCode,"File is not available");
  }
    return ResponseEntity.status(responseCode).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).body(body);
  }
  
  @GetMapping("/briefingComments/{questionId}")
  public ResponseEntity<String> getBriefingCommentsPresignedURL(@PathVariable("questionId") String questionId) {
    Exchange exchange = fluentProducerTemplate.withBody(questionId).to(CEILConstants.HTML_BRIEFING_COMMENTS_PRESIGNEDURL_GENERATION_ROUTE).send();
    int responseCode = exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class) ;
    return ResponseEntity.status(responseCode).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).body(exchange.getIn().getBody().toString());
  }

}
