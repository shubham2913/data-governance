package com.wipro.data_governance.parameter_extractors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Total implements ParameterExtractor {
    public String getValue(String file, String text){
        String value=null;
        try {
            String text1=text.toLowerCase();
            Pattern pattern1=Pattern.compile("\\s*grand\\s+total[^\\d]+\\d{1,10}");
            Matcher matcher1=pattern1.matcher(text1);
            String total=null;
            while(matcher1.find()){
                total=text1.substring(matcher1.start(),matcher1.end());
            }
            Pattern pattern2=Pattern.compile("\\d{1,10}");
            Matcher matcher2=pattern2.matcher(total);
            while (matcher2.find()){
                value=total.substring(matcher2.start(),matcher2.end());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return value;
    }
}
