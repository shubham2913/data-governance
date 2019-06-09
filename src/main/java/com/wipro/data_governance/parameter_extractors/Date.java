package com.wipro.data_governance.parameter_extractors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Date implements ParameterExtractor {
    public String getValue(String file, String text){
        String value=null;
        try {
            String text1=text.toLowerCase();
            Pattern pattern1=Pattern.compile("order\\s+placed\\s+at:\\s+[^,]+,");
            Matcher matcher1=pattern1.matcher(text1);
            while (matcher1.find()){
                String d = text1.substring(matcher1.start(),matcher1.end());
                value=d.substring(d.length()-11,d.length()-1);
                break;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return value;
    }
}
