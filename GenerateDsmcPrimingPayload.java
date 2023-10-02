package com.ca.ceil.marking.svc.camelprocessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ca.ceil.marking.svc.model.CategoryModel;
import com.ca.ceil.marking.svc.model.DsmcMarkTypesModel;
import com.ca.ceil.marking.svc.model.MarkTypeModel;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class GenerateDsmcPrimingPayload {

	DsmcMarkTypesModel dsmcMarkTypesModel = new DsmcMarkTypesModel();

	@Autowired
	MarkTypeModel markTypeModel;

	@SuppressWarnings("unchecked")
	public void dsmcPrimingPayload(Exchange exchange) throws Exception {
		List<Map<String, Object>> rows = exchange.getIn().getBody(List.class);
		List<MarkTypeModel> markTypeModelList = new ArrayList<>();
		List<CategoryModel> categoryModelList = new ArrayList<>();
		List<String> marksList = Arrays.asList("0.0", "1.0", "1.5", "2.0", "2.5", "3.0", "3.5", "4.0", "4.5", "5.0");

		Map<String, MarkTypeModel> markTypeMap = new HashMap<String, MarkTypeModel>();
		for (Map<String, Object> row : rows) {
			MarkTypeModel markTypes = null;
			String primingId = row.get("priming_id").toString();

			if (markTypeMap != null && !markTypeMap.isEmpty() && markTypeMap.get(primingId) != null) {
				markTypes = markTypeMap.get(primingId);
				CategoryModel categoryModel = new CategoryModel();
				categoryModel.setId(Integer.parseInt(row.get("category_id").toString()));
				categoryModel.setDisplayName(row.get("display_name").toString());
				categoryModel.setCode(row.get("code").toString());
				categoryModel.setCreatedAt(row.get("created_at").toString());
				categoryModel.setUpdatedAt(row.get("updated_at").toString());
				categoryModel.setMarks(marksList);
				categoryModel.setSequence(Integer.parseInt(row.get("sequence_id").toString()));
				categoryModel.setName(row.get("name").toString());
				categoryModel.setMinValue(row.get("min_value").toString());
				categoryModel.setMaxValue(row.get("max_value").toString());
				markTypes.getCategories().add(categoryModel);
			} else {
				markTypes = new MarkTypeModel();
				markTypes.setId(Integer.valueOf(primingId));
				markTypes.setProgramOfStudy(row.get("program_of_study").toString());
				markTypes.setExamType(Integer.parseInt(row.get("exam_type").toString()));
				markTypes.setVersion(Integer.parseInt(row.get("version").toString()));

				CategoryModel categoryModel = new CategoryModel();
				categoryModel.setId(Integer.parseInt(row.get("category_id").toString()));
				categoryModel.setDisplayName(row.get("display_name").toString());
				categoryModel.setCode(row.get("code").toString());
				categoryModel.setCreatedAt(row.get("created_at").toString());
				categoryModel.setUpdatedAt(row.get("updated_at").toString());
				categoryModel.setMarks(marksList);
				categoryModel.setSequence(Integer.parseInt(row.get("sequence_id").toString()));
				categoryModel.setName(row.get("name").toString());
				categoryModel.setMinValue(row.get("min_value").toString());
				categoryModel.setMaxValue(row.get("max_value").toString());
				if (Objects.isNull(markTypes.getCategories())) {
					markTypes.setCategories(new ArrayList());
				}
				markTypes.getCategories().add(categoryModel);
			}
			markTypeMap.put(primingId, markTypes);
		}
		if (markTypeMap != null && !markTypeMap.isEmpty()) {
			for (String mtm : markTypeMap.keySet()) {
				markTypeModelList.add(markTypeMap.get(mtm));
			}
		}
		dsmcMarkTypesModel.setMarkTypes(markTypeModelList);
		ObjectMapper objectMapper = new ObjectMapper();
		String payload = objectMapper.writeValueAsString(dsmcMarkTypesModel);
		exchange.getIn().setBody(payload);
	}
}
