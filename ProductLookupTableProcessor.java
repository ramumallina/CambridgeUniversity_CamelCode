package com.ca.ceil.marking.svc.camelprocessor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;
import com.ca.ceil.marking.svc.model.ProductLookupStructure;
import com.ca.ceil.marking.svc.utility.CEILConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ProductLookupTableProcessor {
  
  @SuppressWarnings("unused")
  public void productLookupPayload(Exchange exchange) throws Exception {
    List<Map<String, Object>> rows = exchange.getIn().getBody(List.class);
    List<ProductLookupStructure> productLookupStructureList = new  ArrayList<>();
    for(Map<String, Object> row: rows) {
      ProductLookupStructure productLookupStructure = new ProductLookupStructure();
      
      productLookupStructure.setProgrammeOfStudyIdentifier((String) row.get("programme_of_study_identifier"));
      productLookupStructure.setProductName((String) row.get("product_name"));
      productLookupStructure.setComponent((String) row.get("component"));
      productLookupStructure.setCeilMarkingMode((String) row.get("ceil_marking_mode"));
      productLookupStructure.setHybridMarkingFloor((Integer) row.get("hybrid_marking_floor"));
      productLookupStructure.setAcceptableConfidence((BigDecimal) row.get("acceptable_confidence"));
      productLookupStructure.setElitAutomarkingTemplateIdentifier((String) row.get("elit_automarking_template_identifier"));
      productLookupStructure.setElitAutomarkingTemplateVersion((Integer) row.get("elit_automarking_template_version"));
      
      productLookupStructureList.add(productLookupStructure);
    }
    ObjectMapper objectMapper = new ObjectMapper();
    String payload = objectMapper.writeValueAsString(productLookupStructureList);
    exchange.getIn().setBody(payload);
  }
  
  public void posIdListMaker(Exchange exchange) throws Exception {
    List<Map<String, Object>> rows = exchange.getIn().getBody(List.class);
    List<ProductLookupStructure> input = new ArrayList<>();
    for(Map<String, Object> row: rows){
      ProductLookupStructure productLookupStructure = new ProductLookupStructure();
      
      productLookupStructure.setProgrammeOfStudyIdentifier((String) row.get("programmeOfStudyIdentifier"));
      productLookupStructure.setProductName((String) row.get("productName"));
      productLookupStructure.setComponent((String) row.get("component"));
      productLookupStructure.setCeilMarkingMode((String) row.get("ceilMarkingMode"));
      productLookupStructure.setHybridMarkingFloor((Integer) row.get("hybridMarkingFloor"));
      productLookupStructure.setAcceptableConfidence((BigDecimal) row.get("acceptableConfidence"));
      productLookupStructure.setElitAutomarkingTemplateIdentifier((String) row.get("elitAutomarkingTemplateIdentifier"));
      productLookupStructure.setElitAutomarkingTemplateVersion((Integer) row.get("elitAutomarkingTemplateVersion"));
      
      input.add(productLookupStructure);
    }
    ArrayList<String> posId = new ArrayList<>();
    int count = 0;
    for(ProductLookupStructure obj : input) {
      posId.add(obj.getProgrammeOfStudyIdentifier());
      count++;
    }
    String posIdList =posId.stream().collect(Collectors.joining("','","'","'"));
    exchange.getIn().setHeader(CEILConstants.POS_ID_LIST,posIdList);
    exchange.getIn().setHeader(CEILConstants.ACTUAL_COUNT,count);
  }
}