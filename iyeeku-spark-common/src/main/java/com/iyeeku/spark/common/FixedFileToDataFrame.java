package com.iyeeku.spark.common;

import com.iyeeku.spark.util.Schema;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class FixedFileToDataFrame {

    /**
     * 截取字节数组，解码，gut卸数时采用gbk解码，所以将字节数组进行gbk编码
     * @param b
     * @param start
     * @param end
     * @return
     */
    public static String getDecodeString(byte[] b , int start , int end){
        int size = end - start + 1;
        byte[] new1 = new byte[size];
        System.arraycopy(b , start -1 , new1 , 0 , size );
        try {
            return new String(new1 , "gbk").trim();
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 根据字段开始、截止位置将字节数组转换成字符串数组（字段值数组）
     * @param b
     * @param field_map
     * @return
     */
    public static String[] getFixArray(byte[] b, TreeMap<Integer, FileField> field_map){
        int s = field_map.size();
        String[] new_array = new String[s];
        for (Map.Entry<Integer,FileField> entry : field_map.entrySet()){
            FileField fileField = entry.getValue();
            int k = fileField.getFieldid();
            int _s = fileField.getFieldstart();
            int _e = fileField.getFieldend();
            new_array[k-1] = getDecodeString(b,_s , _e);
        }
        return new_array;
    }

    /**
     * 得到每行的定长长度，最大截止符+换行符
     * @param field_map
     * @return
     */
    public static int getLineLength(TreeMap<Integer,FileField> field_map){
        return field_map.lastEntry().getValue().getFieldend() + 1;
    }

    /**
     * 读取hdfs中标志文件生成的各字段的相关信息、字段序号FileField【顺序号、名称、字段类型、截取开始点、截取结束点、字段长度】
     * @param jsc
     * @param flgPath
     * @return
     */
    public static TreeMap<Integer,FileField> getFieldMap(JavaSparkContext jsc, String flgPath){

        JavaRDD<String> flgRdd = jsc.textFile(flgPath , 1);
        TreeMap<Integer,FileField> fieldMap = new TreeMap<Integer, FileField>();

        List<String> fileContent = flgRdd.filter(new Function<String, Boolean>() {
            public Boolean call(String line) throws Exception {
                return line.contains("$$");
            }
        }).collect();
        return getFieldMap(fieldMap,fileContent);
    }

    /**
     * 读取本地文件系统中标志文件生成的各字段的相关信息、字段序号FileField【顺序号、名称、字段类型、截取开始点、截取结束点、字段长度】
     * @param localFlagPath
     * @return
     */
    public static TreeMap<Integer,FileField> getFieldMap(String localFlagPath){
        TreeMap<Integer,FileField> fieldMap = new TreeMap<Integer, FileField>();
        List<String> fileContent = null;
        try {
            fileContent = org.apache.commons.io.FileUtils.readLines(new File(localFlagPath));
        }catch (IOException e){
            e.printStackTrace();
            fileContent = new ArrayList<String>();
        }
        return getFieldMap(fieldMap,fileContent);
    }

    private static TreeMap<Integer,FileField> getFieldMap(TreeMap<Integer,FileField> fieldMap,List<String> fileContent){
        // 顺序号、名称、字段类型、截取开始点、截取结束点
        for (String line : fileContent){
            String[] array = line.split("\\$\\$");
            int _a = array[3].indexOf("(");
            int _b = array[3].indexOf(",");
            int _c = array[3].indexOf(")");
            String s = array[3].substring(_a + 1 , _b);
            String e = array[3].substring(_b + 1 , _c);

            int fieldId = Integer.parseInt(array[0]);
            String fieldName = array[1].toLowerCase();
            String fieldType = array[2];
            int fieldStart = Integer.parseInt(s);
            int fieldEnd = Integer.parseInt(e);
            int fieldLength = fieldEnd - fieldStart + 1;
            fieldMap.put(fieldId , new FileField(fieldId,fieldName,fieldType,fieldStart,fieldEnd,fieldLength));
        }
        return fieldMap;
    }

    /**
     * 根据每行字段长度、各字段长度信息、data文件生成dataset信息
     * @param sqlContext
     * @param fieldMap
     * @param dataPath
     * @return
     */
    private static Dataset<Row> getDataFrame(SQLContext sqlContext , final TreeMap<Integer,FileField> fieldMap , String dataPath){
        JavaSparkContext sc = new JavaSparkContext(sqlContext.sparkContext());
        int _max_length = getLineLength(fieldMap);
        StructType schema = Schema.getSchema(fieldMap);
        JavaRDD<byte[]> dataRdd = sc.binaryRecords(dataPath , _max_length);
        JavaRDD<Row> dataRdd_row = dataRdd.map(new Function<byte[], Row>() {
            public Row call(byte[] v1) throws Exception {
                String[] array = getFixArray(v1,fieldMap);
                return RowFactory.create(array);
            }
        });
        Dataset<Row> datadf = sqlContext.createDataFrame(dataRdd_row,schema);
        return datadf;
    }

    /**
     * 根据hdfs上的flag文件、data文件生成dataset信息
     * @param sqlContext
     * @param flgPath
     * @param dataPath
     * @return
     */
    public static Dataset<Row> getDataFrame(SQLContext sqlContext , String flgPath , String dataPath){
        JavaSparkContext jsc = new JavaSparkContext(sqlContext.sparkContext());
        TreeMap<Integer,FileField> field_map = getFieldMap(jsc , flgPath);
        return getDataFrame(sqlContext,field_map,dataPath);
    }


}
