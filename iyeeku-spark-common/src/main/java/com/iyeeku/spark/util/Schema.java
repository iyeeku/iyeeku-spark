package com.iyeeku.spark.util;

import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @ClassName Schema
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/10/5 15:22
 * @Version 1.0
 **/
public class Schema {

    /**
     * 根据字段列表信息生成对应的schema信息，字段类型全部为 StringType
     * @param field_map
     * @return
     */
    public static StructType getSchema(TreeMap<Integer,FileField> field_map){
        List<StructField> fields = new ArrayList<>();
        for (Map.Entry<Integer,FileField> entry : field_map.entrySet()){
            FileField fileField = entry.getValue();
            String fieldType = fileField.getFieldtype();
            String fieldName = fileField.getFieldname();
            StructField field = DataTypes.createStructField(fieldName,DataTypes.StringType,false);
            fields.add(field);
        }
        return DataTypes.createStructType(fields);
    }

    /**
     * 根据字段列表生成对应的schema信息，默认为 StringType
     * @param values
     * @return
     */
    public static StructType getSchema(String... values){
        List<StructField> fields = new ArrayList<>();
        for (String entry : values){
            StructField field = DataTypes.createStructField(entry,DataTypes.StringType,false);
            fields.add(field);
        }
        return DataTypes.createStructType(fields);
    }

    /**
     * 根据schema信息生成Map结构，不含长度
     * @param structType
     * @return
     */
    public static TreeMap<Integer,FileField> getFieldMap(StructType structType){
        TreeMap<Integer,FileField> field_map = new TreeMap<>();
        int size = structType.size();
        for (int i = 1 ; i < size + 1 ; i++){
            StructField structField = structType.apply(i-1);
            FileField fileField = new FileField(i,structField.name().toUpperCase(),structField.dataType().typeName().toUpperCase(),-1,-1,0);
            field_map.put(i,fileField);
        }
        return field_map;
    }

    /**
     * 若schema中存在DataType,TimestampType类型的话，转换成 StringType
     * @param structType
     * @return
     */
    public static StructType tranformDateType(StructType structType){
        List<StructField> fields = new ArrayList<>();
        for (StructField entry : structType.fields()){
            DataType fieldType = entry.dataType();
            String fieldName = entry.name();
            if (fieldType.equals(DataTypes.DateType) || fieldType.equals(DataTypes.TimestampType)){
                fieldType = DataTypes.StringType;
            }
            StructField field = DataTypes.createStructField(fieldName,fieldType,false);
            fields.add(field);
        }
        return DataTypes.createStructType(fields);
    }


}
