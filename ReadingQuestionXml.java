package com.ca.ceil.marking.svc.camelprocessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.ca.ceil.marking.svc.model.QuestionsModel;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ReadingQuestionXml implements Processor {
	private Logger logger = LoggerFactory.getLogger(ReadingQuestionXml.class);

	@Override
	public void process(Exchange exchange) throws Exception {
		List<QuestionsModel> questionsModel = new ArrayList<>();
		String questionXmlPayload = exchange.getIn().getBody(String.class);
		String formattedXml = trim(questionXmlPayload);
		String questionId = (String) exchange.getIn().getHeader("questionId");
		String questionInformation = "";
		if (formattedXml.contains("<assessmentItem")) {
			questionInformation = formattedXml.substring(formattedXml.indexOf("question-main-illustration\"></div>") + 34,
			    formattedXml.indexOf("<extendedTextInteraction"));
			exchange.setProperty("questionAssessmentDesc", questionInformation);
		}else if(formattedXml.contains("<assessmentStimulus")) {
		  questionInformation = formattedXml.substring(formattedXml.indexOf("<div>") + 5,
	            formattedXml.indexOf("</div>"));
		  String imgName=formattedXml.substring(formattedXml.indexOf("/ID_")+4,formattedXml.indexOf("\" width"));
			exchange.setProperty("imageFileName", imgName);
		  exchange.setProperty("questionStimulusDesc", questionInformation);
		}
//		QuestionsModel model = new QuestionsModel();
//		model.setQuestionId(questionId);
//		model.setQuestion(questionInformation);
//		questionsModel.add(model);
//		ObjectMapper mapper = new ObjectMapper();
//		String jsonQuestionData=mapper.writerWithDefaultPrettyPrinter().writeValueAsString(questionsModel);
//		if(jsonQuestionData.contains("[") & jsonQuestionData.contains("]")) {
//			jsonQuestionData=jsonQuestionData.replace("[", "");
//			jsonQuestionData=jsonQuestionData.replace("]", "");
//			}
//		exchange.getIn().setBody(jsonQuestionData, String.class);
	}

	public static String trim(String input) {
		BufferedReader reader = new BufferedReader(new StringReader(input));
		StringBuilder result = new StringBuilder();
		try {
			String line;
			while ((line = reader.readLine()) != null)
				result.append(line.trim());
			return result.toString();
		} catch (IOException e) {
			throw new RuntimeException(e.toString());
		}
	}
}
