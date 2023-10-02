package com.ca.ceil.marking.svc.camelprocessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import com.ca.ceil.marking.svc.model.CandidateLongQuestionDBModel;

@Component
public class CandidateLongQuestionProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {

		ArrayList<Map<String, Object>> dataList = (ArrayList<Map<String, Object>>) exchange.getIn().getBody();
		List<CandidateLongQuestionDBModel> candidateLongQuestionList = new ArrayList<>();
		for (Map<String, Object> map : dataList) {

			CandidateLongQuestionDBModel candidateLongQueModel = new CandidateLongQuestionDBModel();
			candidateLongQueModel.setQuestionContentItemId(map.get("question_content_item_id").toString());
			candidateLongQueModel.setQuestionId(map.get("question_id").toString());
			candidateLongQueModel.setCandidateInsperaId(map.get("candidate_inspera_id").toString());
			candidateLongQueModel.setCandidateAnswer(map.get("candidate_answer").toString());
			candidateLongQueModel.setRmahtmlfileIdentifier(map.get("rma_html_file_identifier").toString());
			candidateLongQueModel.setProductSourceIdentifier(map.get("product_source_identifier").toString());
			candidateLongQueModel.setQuestionTitle(map.get("question_title").toString());
			candidateLongQuestionList.add(candidateLongQueModel);
		}
		exchange.setProperty("candidateDetailsforHtmlFile", candidateLongQuestionList);
		exchange.getIn().setBody(candidateLongQuestionList);
	}
}
