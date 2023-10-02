package com.ca.ceil.marking.svc.camelprocessor;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;
import com.ca.ceil.marking.svc.utility.CEILConstants;

@Component
public class GenerateUniqueIdentifier {

public void generateResponseFileIdentifier(Exchange exchange) throws Exception {
    
  int n = 33;
  String alphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  StringBuilder sb = new StringBuilder(n);
  for (int i = 0; i < n; i++) {
    int index = (int) (alphaNumericString.length() * Math.random());
    sb.append(alphaNumericString.charAt(index));
  }
    exchange.getIn().setHeader(CEILConstants.RESPONSE_FILE_IDENTIFIER, sb.toString());
  }
}
