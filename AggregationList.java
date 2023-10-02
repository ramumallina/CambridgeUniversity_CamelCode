package com.ca.ceil.marking.svc.camelprocessor;

import java.util.ArrayList;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;
import com.ca.ceil.marking.svc.model.QuestionsModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;

@Component
public class AggregationList {

  public void aggregateResponses(Exchange ex) throws JsonProcessingException {
   String str="";
     str = ex.getIn().getBody(String.class);
    ex.setProperty("altrAggregationList", (((ArrayList<QuestionsModel>) (ex.getProperty("altrAggregationList"))).add(new Gson().fromJson(str, QuestionsModel.class))));
  }
}
