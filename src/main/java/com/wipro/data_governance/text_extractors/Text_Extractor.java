package com.wipro.data_governance.text_extractors;

import org.apache.hadoop.fs.FileSystem;

public interface Text_Extractor {
    public String getText(FileSystem fs, String fileName);
}
