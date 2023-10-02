package com.ca.ceil.marking.svc.camelprocessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ca.ceil.marking.svc.model.Annotations;
import com.ca.ceil.marking.svc.model.Characteristics;
import com.ca.ceil.marking.svc.model.MarkScheme;
import com.ca.ceil.marking.svc.model.MarkTypes;
import com.ca.ceil.marking.svc.model.MarkingStructure;
import com.ca.ceil.marking.svc.model.QPMSSD;
import com.ca.ceil.marking.svc.model.QPPD;
import com.ca.ceil.marking.svc.model.QuestionItemGroups;
import com.ca.ceil.marking.svc.utility.CEILConstants;
import com.ca.ceil.marking.svc.utility.CeilUtility;
import com.ca.ceil.marking.svc.utility.RandomIdentifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GenerateQppdQpmssdPayload {
  
  @Autowired
  QPPD qppdPayload;
  
  @Autowired
  MarkScheme markScheme;
  
  @Autowired
  QPMSSD qpmssdPayload;
  
  @Autowired
  MarkingStructure markingStructure;
  
  @Autowired
  RandomIdentifier randomIdentifier;
  
  @Autowired
  Annotations annotations;
  
  @Autowired
  Characteristics characteristics;
  
  @Autowired
  QuestionItemGroups questionItemGroups;
  
  @Autowired
  CeilUtility ceilUtility;
  
  @SuppressWarnings("unchecked")
  public void qppdPayloads(Exchange exchange) throws Exception {
    List<Map<String, Object>> rows = exchange.getIn().getBody(List.class);
    for(Map<String, Object> row: rows) {
        exchange.getIn().setHeader(CEILConstants.FORMATTED_TEST_START_DATE, (ceilUtility.dateFormattor((String) row.get("test_start_date"), "dd/MM/yyyy",  "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
        exchange.setProperty(CEILConstants.TEST_IDENTIFIER, row.get("test_identifier"));
        exchange.setProperty(CEILConstants.ASSESSMENT_NAME, row.get("assessment_name"));
        exchange.setProperty(CEILConstants.MAX_MARKS, row.get("max_marks"));
        exchange.setProperty(CEILConstants.LONG_QUESTION_NO, row.get("long_question_no"));
        exchange.setProperty(CEILConstants.QUESTION_CONTENT_ITEM_ID, row.get("question_item_group_identifier"));
        exchange.setProperty(CEILConstants.MARKING_IDENTIFIER, row.get("marking_identifier"));
        exchange.setProperty("qppdSentStatus", row.get("qppd_sent_status"));
        exchange.setProperty("qpmssdSentStatus", row.get("qpmssd_sent_status"));
        
        qppdPayload.setSourceIdentifier(CEILConstants.SOURCE_IDENTIFIER);
        qppdPayload.setSessionIdentifier(String.valueOf(row.get("session_identifier")));
        qppdPayload.setSessionName((String) row.get("session_name"));
        qppdPayload.setAssessmentIdentifier((String) row.get("assessment_name"));
        qppdPayload.setAssessmentName((row.get("assessment_name") +" "+ row.get("component_name")));
        qppdPayload.setAssessmentVersion(CEILConstants.ASSESSMENT_VERSION);
        qppdPayload.setComponentIdentifier((String) row.get("component_identifier"));
        qppdPayload.setComponentName((String) row.get("component_name"));
        qppdPayload.setComponentVersion(CEILConstants.COMPONENT_VERSION);
        qppdPayload.setTestIdentifier(String.valueOf(row.get("test_identifier")));
        qppdPayload.setTestName((row.get("assessment_name") +" "+ row.get("component_name")));
        qppdPayload.setTestSyllabusCode((String) row.get("assessment_name"));
        qppdPayload.setTestFormat(CEILConstants.TEST_FORMAT);
        qppdPayload.setTestMarkingType(CEILConstants.TEST_MARKING_TYPE);
        qppdPayload.setTestStartDate((String) exchange.getIn().getHeader(CEILConstants.FORMATTED_TEST_START_DATE));
        
        ObjectMapper objectMapper = new ObjectMapper();
        String payload = objectMapper.writeValueAsString(qppdPayload);
        exchange.getIn().setBody(payload);
    }
  }
  
  @SuppressWarnings("unchecked")
  public void qpmssdPayloads(Exchange exchange) throws Exception {
    List<Map<String, Object>> rows = exchange.getIn().getBody(List.class);
    
    List<MarkTypes> markTypeList = new  ArrayList<>();
    List<MarkScheme> markSchemeList = new  ArrayList<>();
    List<QuestionItemGroups> questionItemGroupsList = new  ArrayList<>();
    List<Characteristics> characteristicsList = new  ArrayList<>();
    List<Annotations> annotationsList = new  ArrayList<>();
    List<MarkingStructure> markingStructureList = new  ArrayList<>();
    
    for(Map<String, Object> row: rows) {
      MarkTypes markTypes = new MarkTypes();
      markTypes.setMarkTypeIdentifier((int) row.get("mark_type_identifier"));
      markTypes.setMaximumNumericMark((int) row.get("max_mark"));
      markTypes.setMinimumNumericMark((int) row.get("min_mark"));
      markTypes.setStep(CEILConstants.STEP);
      markTypes.setNumericIndicator(CEILConstants.NUMERIC_INDICATOR);
      markTypes.setAllowNoResponse(CEILConstants.ALLOW_NO_RESPONSE);
      markTypes.setDefaultMark(CEILConstants.DEFAULT_MARK);
      markTypeList.add(markTypes);
      
      MarkScheme markScheme = new MarkScheme();
      markScheme.setMarkSchemeIdentifier((int) row.get("mark_scheme_identifier"));
      markScheme.setMarkSchemeName((String) row.get("mark_scheme_name"));
      markScheme.setMarkTypeIdentifier((int) row.get("mark_type_identifier"));
      markScheme.setWholeNumberIndicator(CEILConstants.WHOLE_NUMBER_INDICATOR);
      markScheme.setMandateComment(CEILConstants.MANDATE_COMMENT);
      markSchemeList.add(markScheme);
    }
    String quesNo = exchange.getProperty(CEILConstants.LONG_QUESTION_NO, String.class);
    String newQuesNo = quesNo.substring(quesNo.lastIndexOf(" ")+1);
    
    markingStructure.setMarkingIdentifier(exchange.getProperty(CEILConstants.MARKING_IDENTIFIER, Integer.class));
    markingStructure.setMarkingName(quesNo);
    markingStructure.setQuestionNumber(newQuesNo);
    markingStructure.setMarkSchemes(markSchemeList);
    markingStructureList.add(markingStructure);
    
    annotations.setDescriptor(CEILConstants.DESCRIPTOR);
    annotationsList.add(annotations);
    
    characteristics.setDescriptor(CEILConstants.CHARACTERISTICS_DESCRIPTOR);
    characteristics.setValue(CEILConstants.CHARACTERISTICS_VALUE);
    characteristicsList.add(characteristics);
    
    questionItemGroups.setQuestionItemGroupIdentifier(exchange.getProperty(CEILConstants.QUESTION_CONTENT_ITEM_ID, Integer.class));
    questionItemGroups.setQigName(exchange.getProperty(CEILConstants.ASSESSMENT_NAME, String.class)+" IG"+newQuesNo);
    questionItemGroups.setDefaultGrace(CEILConstants.DEFAULT_GRACE);
    questionItemGroups.setMarkingApplicationIdentifier(CEILConstants.MARKING_APPLICATION_IDENTIFIER);
    questionItemGroups.setMarkingCount(CEILConstants.MARKING_COUNT);
    questionItemGroups.setMaximumCacheLimit(CEILConstants.MAXIMUM_CACHE_LIMIT);
    questionItemGroups.setMaximumConcurrentMarkingLimit(exchange.getProperty(CEILConstants.MAX_MARKS, Integer.class));
    questionItemGroups.setRemarkPercentage(CEILConstants.REMARK_PERCENTAGE);
    questionItemGroups.setAmdMarkingTolerance(CEILConstants.AMD_MARKING_TOLERANCE);
    questionItemGroups.setCharacteristics(characteristicsList);
    questionItemGroups.setAnnotations(annotationsList);
    questionItemGroups.setEStandardisationIndicator(CEILConstants.E_STANDARDISATION_INDICATOR);
    questionItemGroups.setShowQuestionTotals(CEILConstants.SHOW_QUESTION_TOTALS);
    questionItemGroups.setMarkingStructure(markingStructureList);
    questionItemGroupsList.add(questionItemGroups);
    
    qpmssdPayload.setSourceIdentifier(CEILConstants.SOURCE_IDENTIFIER);
    qpmssdPayload.setTestIdentifier(String.valueOf(exchange.getProperty(CEILConstants.TEST_IDENTIFIER)));
    qpmssdPayload.setMaximumMark(exchange.getProperty(CEILConstants.MAX_MARKS, Integer.class));
    qpmssdPayload.setQuestionItemGroups(questionItemGroupsList);
    qpmssdPayload.setMarkTypes(markTypeList);
   
    ObjectMapper objectMapper = new ObjectMapper();
    String payload = objectMapper.writeValueAsString(qpmssdPayload);
    exchange.getIn().setBody(payload);
  }
}