package com.wipro.data_governance.text_extractors;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;

import java.io.InputStream;

public class TikaPDFExtractor implements Text_Extractor {
    public String getText(FileSystem fs, String fileName){
        String text=null;
        try {
            Path p=new Path(fileName);
            InputStream is=fs.open(p);
            Metadata metadata=new Metadata();
            AutoDetectParser parser=new AutoDetectParser();
            BodyContentHandler handler=new BodyContentHandler(-1);
            parser.parse(is,handler,metadata,new ParseContext());
            //System.out.println(handler.toString());
            text=handler.toString();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return text;
    }
}
