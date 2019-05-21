package com.iyeeku.spark.example;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

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

    public static TreeMap<Integer,FileField> getFieldMap(JavaSparkContext jsc, String flgPath){

        JavaRDD<String> flgRdd = jsc.textFile(flgPath , 1);
        TreeMap<Integer,FileField> field_map = new TreeMap<Integer, FileField>();

        List<String> _fileContent = flgRdd.filter(new Function<String, Boolean>() {
            public Boolean call(String v1) throws Exception {
                return v1.contains("$$");
            }
        }).collect();

        for (String line : _fileContent){
            String[] array = line.split("\\$\\$");
            int _a = array[3].indexOf("(");
            int _b = array[3].indexOf(",");
            int _c = array[3].indexOf(")");
            String s = array[3].substring(_a + 1 , _b);
            String e = array[3].substring(_b + 1 , _c);

            int fieldid = Integer.parseInt(array[0]);
            String fieldname = array[1].toLowerCase();
            String fieldtype = array[2];
            int fieldstart = Integer.parseInt(s);
            int fieldend = Integer.parseInt(e);
            int fieldlength = fieldend - fieldstart + 1;
            field_map.put(fieldid , new FileField(fieldid,fieldname,fieldtype,fieldstart,fieldend,fieldlength));
        }
        return field_map;
    }

    public static Dataset<Row> getDataFrame(SQLContext sqlContext , String flgPath , String dataPath){
        JavaSparkContext jsc = new JavaSparkContext(sqlContext.sparkContext());
        TreeMap<Integer,FileField> field_map = getFieldMap(jsc , flgPath);
        return getDataFrame(sqlContext,field_map,dataPath);
    }

    public static int getLineLength(TreeMap<Integer,FileField> field_map){
        return field_map.lastEntry().getValue().getFieldend() + 1;
    }


    private static Dataset<Row> getDataFrame(SQLContext sqlContext , final TreeMap<Integer,FileField> field_map , String dataPath){
        JavaSparkContext sc = new JavaSparkContext(sqlContext.sparkContext());
        int _max_length = getLineLength(field_map);
        StructType schema = getSchema(field_map);
        JavaRDD<byte[]> dataRdd = sc.binaryRecords(dataPath , _max_length);
        JavaRDD<Row> dataRdd_row = dataRdd.map(new Function<byte[], Row>() {
            public Row call(byte[] v1) throws Exception {
                String[] array = getFixArray(v1,field_map);
                return RowFactory.create(array);
            }
        });
        Dataset<Row> datadf = sqlContext.createDataFrame(dataRdd_row,schema);
        return datadf;
    }


    public static StructType getSchema(TreeMap<Integer,FileField> field_map){
        List<StructField> fields = new ArrayList<StructField>();
        for (Map.Entry<Integer,FileField> entry : field_map.entrySet()){
            FileField fileField = entry.getValue();
            String _fieldType = fileField.getFieldtype();
            String _fieldName = fileField.getFieldname();
            StructField structField = DataTypes.createStructField(_fieldName , DataTypes.StringType , false);
            fields.add(structField);
        }
        return DataTypes.createStructType(fields);
    }

}
