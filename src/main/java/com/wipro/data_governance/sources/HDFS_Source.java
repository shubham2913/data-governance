package com.wipro.data_governance.sources;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileUtil;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class HDFS_Source {
    public List<String> getData(FileSystem fs, String sourcePath){
        List<String> filesList = new ArrayList();
        try {
            FileStatus[] fileStatus = fs.listStatus(new Path(sourcePath));
            Path[] paths = FileUtil.stat2Paths(fileStatus);
            for(Path path : paths ){
                filesList.add(path.toString());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return filesList;
    }
}
