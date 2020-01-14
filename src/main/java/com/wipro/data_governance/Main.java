package com.wipro.data_governance;
import com.wipro.data_governance.catalog_engine.Atlas;
import com.wipro.data_governance.parameter_extractors.ParameterExtractor;
import com.wipro.data_governance.sources.HDFS_Source;
import com.wipro.data_governance.text_extractors.Text_Extractor;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


public class Main {

   private static final Logger logger = Logger.getLogger("com.wipro.data_governance.Main");

   public static void main(String[] args) throws Exception {
         try {
               //HDFS URI
               String hdfsuri = "hdfs://sandbox-hdp.hortonworks.com:8020";
               String userName="root";
               // ====== Init HDFS File System Object
               Configuration conf = new Configuration();
               // Set FileSystem URI
               conf.set("fs.defaultFS", hdfsuri);
               // Because of Maven
               conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
               conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
               // Set HADOOP user
               System.setProperty("HADOOP_USER_NAME", userName);
               System.setProperty("hadoop.home.dir", "/");
               //Get the filesystem - HDFS
               FileSystem fs = FileSystem.get(URI.create(hdfsuri), conf);
               String sourcePath=args[0];
               String destinationPath=args[1];
               String homeDirectory=args[2];
               String typeName=args[3];
               String extractor="Tika";
               List<String> files = new HDFS_Source().getData(fs,sourcePath);
               Map<String,String> userParameters=getUserParameters(destinationPath);
               List<String> parameters=getParameters(destinationPath);
               Map<String,Map<String,Object>> fileValuesmap=new LinkedHashMap<String, Map<String, Object>>();
               for(String file : files){
                     Map<String,Object> fileMap=new LinkedHashMap<String, Object>();
                     String text="";
                     if(file.contains(".pdf")){
                           String className=extractor+"PDFExtractor";
                           Class extractorClass=Class.forName(className);
                           Text_Extractor text_extractor=(Text_Extractor)extractorClass.newInstance();
                           text=text_extractor.getText(fs,file);
                     }
                     for(String parameter : parameters){
                           Object parameterValue=getParameterValue(file, text, parameter,userParameters,homeDirectory);
                           fileMap.put(parameter.split(":")[0],parameterValue);
                     }
                     fileValuesmap.put(file,fileMap);
               }
               new Atlas().generateCatalog(typeName,destinationPath,fileValuesmap);
         }
         catch (Exception e){
               e.printStackTrace();
         }

   }
   private static Object getParameterValue(String file, String text, String parameter , Map<String,String> userParameters, String homeDirectory){
         Object value=new Object();
         String value1="";
         try {
               int flag=0;
               String name=parameter.split(":")[0];
               String dataType=parameter.split(":")[1];
               String multiplicity=parameter.split(":")[2];
               for(String userParameter:userParameters.keySet()){
                     if(userParameter.equals(name)) {
                           flag=1;
                           value1=userParameters.get(userParameter);
                           break;
                     }
               }
               if(flag==0){
                     String className=name.substring(0,1).toUpperCase()+name.substring(1).toLowerCase();
                     File file1=new File(homeDirectory + "src/main/java/com/wipro/data_governance/parameter_extractors/" + className+".java");
                     if(file1.exists()) {
                         Class parameterExtractorClass = Class.forName(className);
                         ParameterExtractor parameterExtractor = (ParameterExtractor) parameterExtractorClass.newInstance();
                         value1 = parameterExtractor.getValue(file, text);
                     }
                     else {
                         if(multiplicity.equals("required")){
                             throw new Exception("value of required parameter " + name + " cannot be extracted as extractor not defined");
                         }
                     }
               }
               if(dataType.equals("int") || dataType.equals("short") || dataType.equals("long") || dataType.equals("biginteger")){
                     value=Long.parseLong(value1);
               }
               else if(dataType.equals("float") || dataType.equals("double") || dataType.equals("bigdecimal")){
                     value=Double.parseDouble(value1);
               }
               System.out.println("value of " + name + " is " + value);
         }
         catch (Exception e){
               e.printStackTrace();
         }
         return value;
   }
   private static List<String> getParameters(String destinationPath){
         List<String> parameters=new ArrayList<String>();
         //parameters=null;
         try {
               FileReader fileReader=new FileReader(new File(destinationPath+"/input/parameters.txt"));
               BufferedReader br=new BufferedReader(fileReader);
               String line;
               while ((line=br.readLine()) !=null){
                     String parameter=line.split(",")[0].trim();
                     String dataType=line.split(",")[1].trim();
                     String multiplicity=line.split(",")[2].trim();
                     parameters.add(parameter+":"+dataType+":"+multiplicity);
               }
         }
         catch (Exception e){
               e.printStackTrace();
         }
         return parameters;
   }
   private static Map<String,String> getUserParameters(String destinationPath){
         Map<String,String> userParameters=new LinkedHashMap<String, String>();
         //userParameters=null;
         try{
               File userFile=new File(destinationPath+"/user_metadata.txt");
               if(userFile.exists()) {
                   FileReader fileReader = new FileReader(new File(destinationPath + "/user_metadata.txt"));
                   BufferedReader br = new BufferedReader(fileReader);
                   String line;
                   while ((line = br.readLine()) != null) {
                       String key = line.split("=")[0].trim();
                       String value = line.split("=")[1].trim();
                         System.out.println("key is " + key + " and value is " + value);
                       userParameters.put(key, value);
                   }
               }
               else {
                   userParameters.put("none","none");
               }
         }
         catch (Exception e){
               e.printStackTrace();
         }
         return userParameters;
   }
}
