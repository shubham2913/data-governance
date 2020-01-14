package com.wipro.data_governance.catalog_engine;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Atlas {
    public static void main(String[] args){
        //List<Map<String,Object>> list=new ArrayList<Map<String, Object>>();
        List<String> list=new ArrayList<String>();
        Map<String,Object> id=new LinkedHashMap<String, Object>();
        id.put("jsonClass","org.apache.atlas.typesystem.json.InstanceSerialization$_Id");
        id.put("version",0);
        Map<String,Object> entity=new LinkedHashMap<String, Object>();
        entity.put("jsonClass","org.apache.atlas.typesystem.json.InstanceSerialization$_Reference");
        entity.put("traitNames",new ArrayList<String>());
        entity.put("traits",new JSONObject());
        entity.put("id",id);
        list.add(JSONValue.toJSONString(entity));
        //JSONObject jsonObject1=new JSONObject();
        System.out.println(list.toString());
    }
    public void generateCatalog(String typeName, String destinationPath,Map<String, Map<String,Object>> fileValuesmap){
        try{
            List<String> entityList = new ArrayList<String>();
            for(String file:fileValuesmap.keySet()){
                Map<String,Object> entity=new LinkedHashMap<String, Object>();
                entity.put("jsonClass","org.apache.atlas.typesystem.json.InstanceSerialization$_Reference");
                entity.put("traitNames",new ArrayList<String>());
                entity.put("traits",new JSONObject());
                entity.put("typeName",typeName);
                entity.put("values",fileValuesmap.get(file));
                Map<String,Object> id=new LinkedHashMap<String, Object>();
                id.put("jsonClass","org.apache.atlas.typesystem.json.InstanceSerialization$_Id");
                id.put("version",0);
                id.put("typeName",typeName);
                entity.put("id",JSONValue.toJSONString(id));
                entityList.add(JSONValue.toJSONString(entity));
            }

            File file1=new File(destinationPath+"/output");
            if(!file1.exists()){
                file1.mkdir();
            }
            File file2=new File(destinationPath+"/output/entities.json");
            FileWriter fileWriter=new FileWriter(file2);
            fileWriter.write(entityList.toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
