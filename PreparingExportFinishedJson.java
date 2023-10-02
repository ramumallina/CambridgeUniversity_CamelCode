package com.ca.ceil.marking.svc.camelprocessor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.ca.ceil.marking.svc.model.SubDelivered;
import com.ca.ceil.marking.svc.model.SubDeliveredModel;
import com.ca.ceil.marking.svc.model.SubDeliveredParameters;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class PreparingExportFinishedJson implements Processor {
  private Logger logger = LoggerFactory.getLogger(PreparingExportFinishedJson.class);

  @Override
  public void process(Exchange exchange) throws Exception {
    Object body;
    body = exchange.getIn().getBody();
    String payload = exchange.getIn().getBody(String.class);
    SubDeliveredModel subDeliveredModel =
        new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .readValue(body.toString(), SubDeliveredModel.class);
    int assessmentRunId = subDeliveredModel.getContextObjectId();
    int userId = subDeliveredModel.getAssociatedObjectId();
    SubDelivered subDelivered = new SubDelivered();
    SubDeliveredParameters subDeliveredParameters = new SubDeliveredParameters();
    subDeliveredParameters.setAssessmentRunId(assessmentRunId);
    subDeliveredParameters.setUserId(userId);
    subDelivered.setExpiryPeriod(3600);
    subDelivered.setResourceType("CandidateSubmission");
    subDelivered.setParameters(subDeliveredParameters);
    ObjectMapper mapper = new ObjectMapper();
    String beforeExportFinishedPayload =
        mapper.writerWithDefaultPrettyPrinter().writeValueAsString(subDelivered);
    exchange.getIn().setBody(beforeExportFinishedPayload, String.class);
  }
}
