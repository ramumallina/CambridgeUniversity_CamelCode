package com.ca.ceil.marking.svc.camelprocessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.camel.Exchange;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.ca.ceil.marking.svc.model.MetaData;
import com.ca.ceil.marking.svc.model.RMARequestPayload;
import com.ca.ceil.marking.svc.model.ResponseFiles;
import com.ca.ceil.marking.svc.utility.CEILConstants;
import com.ca.ceil.marking.svc.utility.CeilUtility;
import com.ca.ceil.marking.svc.utility.RandomIdentifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GenerateJsonPayloadProcessor {

    @Autowired
    RMARequestPayload rmaRequestPayload;

    @Autowired
    CeilUtility ceilUtility;

    @Value("${response.filename.url}")
    private String responseFileNameUrl;

    @Value("${response.filename.url.briefingcomments}")
    private String responseFileNameUrlBriefingComments;

    private Logger logger = LoggerFactory.getLogger(GenerateJsonPayloadProcessor.class);

    /*
     * Method for creating the object of ELiT Template for ELiT Candidate Submission
     */
    @SuppressWarnings("unchecked")
    public void elitTemplatePayload(Exchange exchange) throws Exception {
        List<Map<String, String>> rows = exchange.getIn().getBody(List.class);
        JSONObject templates = new JSONObject();
        for (Map<String, String> row : rows) {
            templates.put("name", row.get("elit_automarking_template_identifier"));
            templates.put("version", row.get("elit_automarking_template_version"));
        }
        exchange.setProperty(CEILConstants.ELIT_SUBMISSION_TEMPLATE, templates);
    }
    
    /*
     * Method for setting the property for ELiT Candidate Submission
     */
    @SuppressWarnings("unchecked")
    public void countryLangCode(Exchange exchange) throws Exception {
        List<Map<String, String>> rows = exchange.getIn().getBody(List.class);
        for (Map<String, String> row : rows) {
            exchange.setProperty("countryCode", row.get("country_code"));
            exchange.setProperty("languageCode", row.get("language_code"));
        }
    }
    
    /*
     * Method for creating the object of Metadata for ELiT Candidate Submission
     */
    @SuppressWarnings("unchecked")
    public void candidateDetailsPayload(Exchange exchange) throws Exception {
        List<Map<String, String>> rows = exchange.getIn().getBody(List.class);
        JSONObject users = new JSONObject();
        for (Map<String, String> row : rows) {
          Object dob = row.get("dob");
          String gender = row.get("gender").toLowerCase().substring(0, 1);
            users.put("id", String.valueOf(row.get("candidatebpid")));
            users.put("l1", exchange.getProperty("languageCode"));
            users.put("country", exchange.getProperty("countryCode"));
            users.put("gender", gender);
            users.put("age", ceilUtility.ageCal(dob.toString()));
        }
        exchange.setProperty(CEILConstants.ELIT_SUBMISSION_USER, users);
    }

    /*
     * Method for creating the ELiT Candidate Submission payload
     */
    @SuppressWarnings("unchecked")
    public void elitSubmissionPayload(Exchange exchange) throws Exception {
        List<Map<String, String>> rows = exchange.getIn().getBody(List.class);

        List<String> answerUuid = new ArrayList<>();
        rows.forEach(unit -> answerUuid.add(unit.get("elit_answer_uuid")));
        String ansUuidList = answerUuid.stream().collect(Collectors.joining("','", "'", "'"));
        exchange.getIn().setHeader(CEILConstants.ANSWER_UUID_LIST, ansUuidList);

        int count = 1;
        List<JSONObject> myJSONObjects = new ArrayList<>();
        for (Map<String, String> row : rows) {
            JSONObject answers = new JSONObject();
            answers.put("id", row.get("elit_answer_uuid"));
            answers.put("text", row.get("candidate_answer"));
            answers.put("question-id", row.get("question_uuid"));
            answers.put("question-number", count++);
            myJSONObjects.add(answers);
        }

        JSONObject submissions = new JSONObject();
        submissions.put("part", CEILConstants.PART);
        submissions.put("answers", myJSONObjects);

        JSONArray submissionArray = new JSONArray();
        submissionArray.add(submissions);

        JSONObject payload = new JSONObject();
        payload.put("submission", submissionArray);
        payload.put("user", exchange.getProperty("usersObject"));
        payload.put("type", "test");
        payload.put("template", exchange.getProperty("templateObject"));
        String elitPayload = payload.toString();
        exchange.getIn().setBody(elitPayload);
    }
    
    /*
     * Method for creating the object of Metadata for RMA Candidate Submission
     */
    @SuppressWarnings("unchecked")
    public void candidateDetailsPayloadRMA(Exchange exchange) throws Exception {
        List<Map<String, String>> rows = exchange.getIn().getBody(List.class);
        for (Map<String, String> row : rows) {
          Object dob = row.get("dob");
          Object testCentreId = row.get("testcentreid");
          Object centreCandidateId = row.get("centrecandidateid");
          exchange.setProperty("firstName", row.get("firstname"));
          exchange.setProperty("dob", dob.toString());
          exchange.setProperty("testCentreId", testCentreId.toString());
          exchange.setProperty("centreCandidateId", centreCandidateId.toString());
        }
    }

    /*
     * Method for creating the RMA Candidate submission payload with and without briefingComments
     */
    @SuppressWarnings("unchecked")
    public void rmaRequestPayload(Exchange exchange) throws Exception {
        List<Map<String, Object>> rows = exchange.getIn().getBody(List.class);
        for (Map<String, Object> row : rows) {
            exchange.getIn().setHeader(CEILConstants.FORMATTED_TEST_START_DATE, (ceilUtility
                    .dateFormattor((String) row.get("test_start_date"), "dd/MM/yyyy", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
            exchange.getIn().setHeader(CEILConstants.FORMATTED_MARKING_TARGET_DATE,
                    (ceilUtility.addDate((String) row.get("test_start_date"))));

            List<MetaData> metadataList = new ArrayList<>();
            MetaData metadata = new MetaData();
            metadata.setKey(CEILConstants.METADATA_KEY);
            metadata.setHidden(CEILConstants.METADATA_HIDDEN);
            metadata.setValue((String) exchange.getIn().getHeader(CEILConstants.QUESTION_ID));
            metadataList.add(metadata);

            String rmaHtmlFileId = (String) exchange.getProperty(CEILConstants.RMA_HTML_FILE_IDENTIFIER);
            exchange.setProperty(CEILConstants.TEST_IDENTIFIER, row.get("test_identifier"));

            List<ResponseFiles> responseFilesList = new ArrayList<>();
            ResponseFiles responseFiles = new ResponseFiles();
            responseFiles.setQuestionItemGroupIdentifier((int) row.get("question_item_group_identifier"));
            responseFiles.setResponseFileIdentifier(
                    (String) exchange.getIn().getHeader(CEILConstants.RESPONSE_FILE_IDENTIFIER));
            responseFiles.setResponseFileName(responseFileNameUrl + rmaHtmlFileId);
            responseFiles.setDisplayName((String) row.get("long_question_no") + CEILConstants.RMAREQUEST_DISPLAYNAME);
            responseFiles.setFileType(CEILConstants.RESPONSEFILES_FILETYPE);
            responseFiles.setSecurityModel(CEILConstants.RESPONSEFILES_SECURITYMODEL);
            responseFiles.setMetadata(metadataList);
            responseFilesList.add(responseFiles);

            String statusBC = (String) exchange.getProperty("briefingCommentsStatus");
            if(statusBC!=null && statusBC.equals("true")) {
              ResponseFiles responseFilesBriefingComments = new ResponseFiles();
              responseFilesBriefingComments.setQuestionItemGroupIdentifier((int) row.get("question_item_group_identifier"));
              responseFilesBriefingComments.setResponseFileIdentifier(RandomIdentifier.alphaNumericString());
              responseFilesBriefingComments.setResponseFileName(responseFileNameUrlBriefingComments+ (String) exchange.getIn().getHeader(CEILConstants.QUESTION_ID));
              responseFilesBriefingComments.setDisplayName(CEILConstants.RMA_BRIEFING_COMMENTS_DISPLAY_NAME);
              responseFilesBriefingComments.setFileType(CEILConstants.RESPONSEFILES_FILETYPE);
              responseFilesBriefingComments.setSecurityModel(CEILConstants.RESPONSEFILES_SECURITYMODEL);
              responseFilesList.add(responseFilesBriefingComments);
            }
            
            rmaRequestPayload.setSourceIdentifier(CEILConstants.SOURCE_IDENTIFIER);
            rmaRequestPayload.setSessionIdentifier((int) row.get("session_identifier"));
            rmaRequestPayload.setAssessmentIdentifier((String) row.get("assessment_name"));
            rmaRequestPayload.setAssessmentVersion(CEILConstants.ASSESSMENT_VERSION);
            rmaRequestPayload.setComponentIdentifier((String) row.get("component_identifier"));
            rmaRequestPayload.setComponentVersion(CEILConstants.COMPONENT_VERSION);
            rmaRequestPayload.setTestIdentifier((int) row.get("test_identifier"));
            rmaRequestPayload.setCentreIdentifier((String) exchange.getProperty("testCentreId"));
            rmaRequestPayload.setCentreCandidateIdentifier((String) exchange.getProperty("centreCandidateId"));
            rmaRequestPayload.setCandidateName((String) exchange.getProperty("firstName"));
            rmaRequestPayload.setUniqueCandidateIdentifier(exchange.getProperty("candidateId").toString());
            rmaRequestPayload.setDob((String) exchange.getProperty("dob"));
            rmaRequestPayload.setAttendanceStatus(CEILConstants.RMAREQUEST_ATTENDANCESTATUS);
            rmaRequestPayload.setMarkingTargetDate(
                    (String) exchange.getIn().getHeader(CEILConstants.FORMATTED_MARKING_TARGET_DATE));
            rmaRequestPayload.setTestDate((String) exchange.getIn().getHeader(CEILConstants.FORMATTED_TEST_START_DATE));
            rmaRequestPayload.setUniqueCourseworkResponseIdentifier(
                    (String) exchange.getIn().getHeader(CEILConstants.UNIQUE_COURSEWORK_RESPONSE_IDENTIFIER));
            rmaRequestPayload.setResponseFiles(responseFilesList);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String payload = objectMapper.writeValueAsString(rmaRequestPayload);
        exchange.getIn().setBody(payload);

    }
    
    /*
     * Method to read briefingCommentsStatus
     */
    @SuppressWarnings("unchecked")
    public void readBriefingComment(Exchange exchange) {
      List<Map<String, Object>> rows = exchange.getIn().getBody(List.class);
      for (Map<String, Object> row : rows) {
          exchange.setProperty("briefingCommentsStatus", row.get("briefing_comments"));
      }
  }

}
