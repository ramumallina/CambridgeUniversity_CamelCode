package com.ca.ceil.marking.svc.camelprocessor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GenerateMediaPayloadProcessor implements Processor {

  private Logger logger = LoggerFactory.getLogger(GenerateMediaPayloadProcessor.class);
  
  @Value("${elit.priming.question.folder}")
  private String folderPath;
  
	@Override
	public void process(Exchange exchange) throws Exception {

		RestTemplate restTemplate = new RestTemplate();
		String elitApi = exchange.getContext().resolvePropertyPlaceholders("{{ceil.elit.media.uri}}");
		List<String> imageList = (List) exchange.getProperty("imageList");
		List<String> pathList = new ArrayList<>();
		List<String> uuidList = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		String questionContentItemId = (String) exchange.getProperty("contextObjectId");
		int questionId = (int) exchange.getIn().getHeader("elitQuestionId");

		for(String i:imageList) {
		Object object = "/"+questionContentItemId;
		UUID uuid = UUID.randomUUID();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);
		headers.set("Authorization",
				"Token eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXUyJ9.eyJzdWIiOnt9LCJleHAiOjQxMDI0NDQ4MDAsImF1ZCI6IlVYbzJ1U0JyUUx6YzVFQUYifQ.ZyE2YGQFRzzlJdjGwbUx0rBpdMfaoVsmr5niyfdQSUE");

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		File file = new File(folderPath + "/extractedFiles"+object+"/resources/"+i+".svg");
		byte[] array = null;
		if (file.exists()) {
			File[] f = file.listFiles();
					array = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
					body.add("media", array);
					body.add("metadata", "{\"description\":\"test image\"}");
					map.put("imagePath", file.getPath());
					map.put("mediaUuid", uuid);
		}
		pathList.add(file.getPath());
		uuidList.add(uuid.toString());
		HttpEntity<byte[]> requestEntity = new HttpEntity<>(array, headers);
		String url = elitApi + uuid;
		ResponseEntity<?> response = null;
		try {
			response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Void.class);
			log.info("Elit Media Status Code :::" + response.getStatusCodeValue() + " for Question Content Item Id is "+questionContentItemId+" and Question Id is "+questionId);
			exchange.getIn().setBody(map);
		} catch (Exception e) {
			log.info("Elit Media Error Status Code:::" + e.getMessage()+ " for Question Content Item Id is "+questionContentItemId+" and Question Id is "+questionId);
			exchange.getIn().setBody(new HashMap<>());
		}
		}
		String filePath = pathList.toString().replace("[", "").replace("]", "");
		String uuid = uuidList.toString().replace("[", "").replace("]", "");
      exchange.setProperty("imagepathList",filePath);
      exchange.setProperty("mediaUuidList",uuid);
	}
	
}