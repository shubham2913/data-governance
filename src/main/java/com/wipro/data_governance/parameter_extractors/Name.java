package com.wipro.data_governance.parameter_extractors;

public class Name implements ParameterExtractor {
    public String getValue(String file, String text) {
        return file;
    }
}
