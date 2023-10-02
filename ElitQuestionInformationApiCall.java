package com.ca.ceil.marking.svc.camelprocessor;

import static com.ca.ceil.marking.svc.utility.CEILConstants.ELIT_ACCOUNT_ID;
import static com.ca.ceil.marking.svc.utility.CEILConstants.ELIT_QUESTION_ID;
import static com.ca.ceil.marking.svc.utility.CEILConstants.ELIT_QUESTION_UUID;
import static com.ca.ceil.marking.svc.utility.CEILConstants.QUESTION_UUID;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unchecked")
public class ElitQuestionInformationApiCall implements Processor {

  private Logger logger = LoggerFactory.getLogger(ElitQuestionInformationApiCall.class);

  @Value("${ceil.elit.priming.accountId}")
  private String ceilElitPrimingAccountId;

  @Override
  public void process(Exchange exchange) throws Exception {
    List<Map<String, Object>> rows = exchange.getIn().getBody(List.class);
    for (Map<String, Object> row : rows) {
      String mediaUuid = (String) row.get("media_uuid");
      String qDescription = (String) row.get("question_desc");
      String questionDescription = qDescription.replace("\"", "\\\"");
      String metadata = (String) row.get("metadata");
      metadata = metadata.replace("''", "'");
      exchange.setProperty(ELIT_QUESTION_ID, row.get("question_id"));
      exchange.setProperty("elitQuestionUuid", row.get("question_uuid"));
      exchange.getIn().setHeader(ELIT_ACCOUNT_ID, ceilElitPrimingAccountId);
      JSONObject payload = new JSONObject();
      payload.put("text", questionDescription);
      payload.put("metadata", metadata);
      JSONArray id = new JSONArray();
      if (mediaUuid != null) {
        String[] imageUuids = mediaUuid.split(", ");
        for (String imageUuid : imageUuids) {
          JSONObject ids = new JSONObject();
          ids.put("id", imageUuid);
          id.add(ids);
        }
        payload.put("realia", id);
      }
      String elitPayload = payload.toString();
      exchange.setProperty("elitPrimingPayload", elitPayload);
    }
  }
}
