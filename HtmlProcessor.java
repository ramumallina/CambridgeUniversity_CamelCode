package com.ca.ceil.marking.svc.camelprocessor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.ca.ceil.marking.svc.utility.PresignedURLUtility;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HtmlProcessor {

  @Value("${image.upload.folder}")
  private String fileUploadFolderPath;
  
  @Value("${html.upload.folder}")
  private String htmlUploadFolderPath;
  
  @Value("${question.folder}")
  private String folderPath;
  
  @Autowired
  PresignedURLUtility presignedUrlUtility = new PresignedURLUtility();

  public void HtmlGeneratorProcessor(Exchange exchange) throws Exception {

    String htmlFilesPath = htmlUploadFolderPath;
    String htmlfilename = exchange.getProperty("rmahtmlfileIdentifier", String.class);
    String questionText = exchange.getProperty("questionDescription", String.class);
    String answerText = exchange.getProperty("candidateAnswer", String.class);
    File fileFolder = new File(htmlFilesPath);
    if (!fileFolder.exists()) {
      FileUtils.forceMkdir(fileFolder);
    }
    log.debug("Final Question HTML Created : {} ", questionText);
    File rmaHTMLFile = new File(htmlFilesPath + htmlfilename + "_RMA.html");
    BufferedWriter bw = new BufferedWriter(new FileWriter(rmaHTMLFile));
    try {
      bw.write(
          "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"utf-8\"><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"><meta name=\"viewport\" content=\"width=device-width,initial-scale=1.0\"><style>.container{padding:20px;}.answer-container{margin:50px020px0;}.bold-text{font-weight:bold;}</style><title>Document</title></head><body><div class=\"container\"><section class=\"question\"><p  class=\"bold-text\"><u>Question:</u></p>");
      bw.write(questionText);
      bw.write(
          " </section><section class=\"answer-container\"><p class=\"bold-text\"><u>Candidate Response:</u></p><div>");
      bw.write(answerText);
      bw.write("</div></section></div></body></html>");

    } catch (Exception e) {
      log.error("Unable to Write the Content in File::: " + e.getMessage());
    } finally {
      if (bw != null) {
        bw.close();
      }
    }
  }

  public void ImageUpload(Exchange exchange) throws Exception {

    String questionContentItemId = (String) exchange.getProperty("questionContentItemId");
    String sourcePath = folderPath+"/extractedFiles/"+questionContentItemId+"/resources/";
    String destinationPath = fileUploadFolderPath;
    File fileFolder = new File(destinationPath);
    if (!fileFolder.exists()) {
      FileUtils.forceMkdir(fileFolder);
    }
    List<String> imageList = (List) exchange.getProperty("imageList");
    for (String i : imageList) {
      File sourceFile = new File(sourcePath + i);
      File destinationFile = new File(destinationPath+i);
      try {
        FileUtils.moveFile(sourceFile, destinationFile);
      } catch (Exception e) {
        log.error("Unable to move::: " + e.getMessage());
      }
    }
  }
  
  public void htmlBriefingCommentGeneratorProcessor(Exchange exchange) throws Exception {

	    String htmlFilesPath = htmlUploadFolderPath;
	    String htmlfilename = exchange.getProperty("questionId", String.class);
	    String briefingCommentsText = exchange.getProperty("isBriefingCommentPresent", String.class);
	    String candidateId = exchange.getProperty("candidateId", String.class);
	    htmlfilename=candidateId+htmlfilename;
	    File fileFolder = new File(htmlFilesPath);
	    if (!fileFolder.exists()) {
	      FileUtils.forceMkdir(fileFolder);
	    }
	    File rmaHTMLFile = new File(htmlFilesPath + htmlfilename + "_RMA_BriefingComments.html");
	    BufferedWriter bw = new BufferedWriter(new FileWriter(rmaHTMLFile));
	    try {
	      bw.write(
	          "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"utf-8\"><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"><meta name=\"viewport\" content=\"width=device-width,initial-scale=1.0\"><style>.container{padding:20px;}.answer-container{margin:50px020px0;}.bold-text{font-weight:bold;}</style><title>Document</title></head><body><div class=\"container\"><section class=\"question\"><p  class=\"bold-text\"><u>Briefing Comments:</u></p>");
	      bw.write(briefingCommentsText);
	      bw.write("</section></div></body></html>");

	    } catch (Exception e) {
	      log.error("Unable to Write the Content in File::: " + e.getMessage());
	    } finally {
	      if (bw != null) {
	        bw.close();
	      }
	    }
	  }
}
