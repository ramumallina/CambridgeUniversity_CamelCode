package com.ca.ceil.marking.svc.camelprocessor;

import java.io.File;
import java.nio.charset.StandardCharsets;
import org.apache.camel.Exchange;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.ca.ceil.marking.svc.utility.CEILConstants;
import com.ca.ceil.marking.svc.utility.PresignedURLUtility;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PresignedURLGeneratorProcessor {

    @Value("${image.upload.folder}")
    private String fileUploadFolderPath;

    @Value("${html.download.folder}")
    private String fileDownloadFolderPath;

    @Autowired
    PresignedURLUtility presignedUrlUtility = new PresignedURLUtility();

    public void fileUploadToBucket(Exchange exchange) throws Exception {

        String fileName = exchange.getProperty(CEILConstants.FILE_NAME, String.class);
        presignedUrlUtility.getHtmlFile(fileName + "_RMA.html");
        String filePath = fileDownloadFolderPath + fileName + "_RMA.html";
        String htmlContent = null;
        try {
            htmlContent = FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        if (null != htmlContent) {
            if (htmlContent.contains("<span") && htmlContent.contains("<img")) {
                Document doc = null;
                doc = Jsoup.parse(new File(filePath));
                if (null != doc) {
                    Elements imgTags = doc.select("img");
                    for (Element imgTag : imgTags) {
                        String srcAttri = imgTag.attr("src");
                        String imageName = srcAttri.substring(srcAttri.indexOf("ID_"));
                        String preSignedUrl = presignedUrlUtility.generatePresignedUrl(imageName).toString();
                        imgTag.attr("src", preSignedUrl);
                    }
                }
                File fileFolder = new File(fileUploadFolderPath);
                if (!fileFolder.exists()) {
                    FileUtils.forceMkdir(fileFolder);
                }
                try {
                    FileUtils.writeStringToFile(new File(fileUploadFolderPath + fileName + "_RMA.html"),
                            doc.outerHtml(), StandardCharsets.UTF_8);
                } catch (Exception e) {
                    log.error("Unable to move::: " + e.getMessage());
                }
            } else {
                String sourcePath = filePath;
                String destinationPath = fileUploadFolderPath;
                File fileFolder = new File(destinationPath);
                if (!fileFolder.exists()) {
                    FileUtils.forceMkdir(fileFolder);
                }
                File sourceFile = new File(sourcePath);
                File destinationFile = new File(destinationPath + fileName + "_RMA.html");
                try {
                    FileUtils.copyFile(sourceFile, destinationFile);
                } catch (Exception e) {
                    log.error("Unable to move::: " + e.getMessage());
                }
            }
        }
    }

    public void preSignedUrl(Exchange exchange) throws Exception {

        String fileName = exchange.getProperty(CEILConstants.FILE_NAME, String.class);
        if (!fileName.chars().allMatch(Character::isDigit)) {
            throw new NumberFormatException("The unique id format is wrong. Expected digits only.");
        }
        String Url = "{\"content\": \"" + presignedUrlUtility.generatePresignedUrl(fileName + "_RMA.html").toString()
                + "\"}";
        exchange.getIn().setBody(Url);
    }

    public void briefingCommentsPreSignedUrl(Exchange exchange) throws Exception {

        String fileName = exchange.getProperty(CEILConstants.FILE_NAME, String.class);
        if (!fileName.chars().allMatch(Character::isDigit)) {
            throw new NumberFormatException("The unique id format is wrong. Expected digits only.");
        }
        String Url = "{\"content\": \"" + presignedUrlUtility.generatePresignedUrl(fileName + "_BriefingComments.pdf").toString() + "\"}";
        exchange.getIn().setBody(Url);
    }
}
