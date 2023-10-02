package com.ca.ceil.marking.svc.camelprocessor;

import static com.ca.ceil.marking.svc.utility.CEILConstants.DSMC_AGGREGATED_MARKS;
import static com.ca.ceil.marking.svc.utility.CEILConstants.DSMC_MARK_PAYLOAD;
import static com.ca.ceil.marking.svc.utility.CEILConstants.JOINEDKEY;
import static com.ca.ceil.marking.svc.utility.CEILConstants.PRODUCT_ID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.camel.Exchange;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ca.ceil.marking.svc.exception.InvalidCriteriaCodeException;

@Component
public class GetDsmcAggregatedMark{
  
	private Logger logger = LoggerFactory.getLogger(GetDsmcAggregatedMark.class);

	  @SuppressWarnings({ "unchecked", "rawtypes" })
	  public void calculateAggregatedMarks(Exchange exchange) throws Exception {
	    JSONObject inputjsonPayload = (JSONObject) JSONValue.parseWithException(exchange.getProperty(DSMC_MARK_PAYLOAD).toString());
	    JSONArray markArray = (JSONArray) inputjsonPayload.get("marks");
	    String product_id=exchange.getIn().getHeader(PRODUCT_ID).toString();
	    double aggregatedMark=0;
		List<Map<String, Object>> markList = new ArrayList();
	    List<Map<String, String>> sqlLookupData =exchange.getIn().getBody(List.class);
	    Map<Object, Object> lookupDataMap = sqlLookupData.stream().filter(i->i.get("weightage")!=null).collect(Collectors.toMap(map -> map.get(JOINEDKEY),map -> map.get("weightage")));
	    logger.info("Calculating the dsmc aggregated marks for the product ::"+product_id);
		for(int i = 0; i < markArray.size(); i++)
		  {
			JSONObject objects = (JSONObject) markArray.get(i);
			String code=(String) objects.get("code");
			Float value= Float.parseFloat(objects.get("value").toString());
			int weightage;
			if(!lookupDataMap.containsKey(product_id+'-'+code)){
				throw new InvalidCriteriaCodeException("Invalid Criteria code or weightage value is Empty");
			}else {
				weightage=Integer.parseInt(lookupDataMap.get(product_id+'-'+code).toString());
			}
			double mark=(value*weightage);
			aggregatedMark=aggregatedMark+mark;
			
			Map<String, Object> m = new HashMap<>();
			m.put("criteriaCode",code);
			m.put("mark", mark);
			m.put("examiner_id", (String) objects.get("examinerId"));
			markList.add(m);

		  }  
	  exchange.setProperty("dsmcMarkList",markList);
	  exchange.getIn().setHeader(DSMC_AGGREGATED_MARKS, aggregatedMark);
	  }
	  
}
