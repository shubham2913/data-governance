package com.wipro.data_governance.parameter_extractors;

public class Qualifiedname implements ParameterExtractor {
    public String getValue(String file, String text) {
        return file;
    }
}
