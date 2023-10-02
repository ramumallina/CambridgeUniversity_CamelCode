package com.ca.ceil.marking.svc.camelprocessor;

import java.util.ArrayList;
import java.util.List;
import org.apache.camel.Exchange;
import com.ca.ceil.marking.svc.model.FeedbackModel;
import com.ca.ceil.marking.svc.model.RmaMarksModel;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GenerateRmaMarksJsonPayload {

	@SuppressWarnings("unchecked")
	public void rmaMarksJsonPayload(Exchange exchange) throws Exception {
		List<FeedbackModel> feedbackModelList = new ArrayList<>();
		FeedbackModel feedBackModel = new FeedbackModel();
		feedBackModel.setPart(1);
		feedBackModel.setExaminerId(exchange.getProperty("markerId",Integer.class));
		feedBackModel.setArbitrary((Integer) exchange.getProperty("totalMarksFromRma"));
		feedbackModelList.add(feedBackModel);
		RmaMarksModel rmaMarksModel = new RmaMarksModel();
		rmaMarksModel.setFeedback(feedbackModelList);

		ObjectMapper objectMapper = new ObjectMapper();
		String payload = objectMapper.writeValueAsString(rmaMarksModel);
		exchange.getIn().setBody(payload);
	}
}
