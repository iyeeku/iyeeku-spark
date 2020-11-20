package com.iyeeku.spark.util;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.deploy.SparkHadoopUtil;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * @ClassName DataFrameToFile
 * @Description TODO
 * @Author YangQuan
 * @Date 2020/4/3 11:01
 * @Version 1.0
 **/
public class DataFrameToFile {

    private final static Logger LOGGER = LoggerFactory.getLogger(DataFrameToFile.class);

    public static final byte blankByte = " ".getBytes()[0];

    /**
     * 根据字段长度生成截取开始点、截取结束点
     * @param fieldMap
     * @return
     */
    public static TreeMap<Integer,FileField> getAllFieldMapByLength(TreeMap<Integer,FileField> fieldMap){
        TreeMap<Integer,FileField> _fieldMap = new TreeMap<>();
        int mid = 1;
        for (Map.Entry<Integer,FileField> entry : fieldMap.entrySet()){
            FileField fileField = entry.getValue();
            int length = fileField.getFieldlength();
            int m = mid;
            mid = mid + length;
            fileField.setFieldstart(m);
            fileField.setFieldend(m + length - 1);
            _fieldMap.put(entry.getKey(),fileField);
        }
        return _fieldMap;
    }

    /**
     * 根据字符串和长度返回字节数组，不足的右补足空格，超过部分则截取
     * @param str
     * @param length
     * @return
     */
    public static byte[] getFiexedBytes(String str, int length){
        byte[] _byte = new byte[length];
        if(str == null || str.equals("")){
            Arrays.fill(_byte,blankByte);
            return _byte;
        }
        byte[] _arr = str.getBytes(Charset.forName("gbk"));
        int size = _arr.length;
        if (size >= length){
            return Arrays.copyOf(_arr, length);
        }else{
            //先填充空格，再将_arr中的数据拷贝到_byte中
            Arrays.fill(_byte,blankByte);
            System.arraycopy(_arr, 0, _byte, 0, size);
            return _byte;
        }
    }

    public static JavaRDD<byte[]> dataFrameToRddBytesOne(Dataset<Row> dataFrame, final TreeMap<Integer,FileField> fieldMap){
        SQLContext sqlContext = dataFrame.sqlContext();
        final int length = FixedFileToDataFrame.getLineLength(fieldMap) - 1;
        JavaRDD<byte[]> rdd = dataFrame.javaRDD().map(new Function<Row, byte[]>() {
            @Override
            public byte[] call(Row row) throws Exception {
                ByteBuffer bb = ByteBuffer.allocate(length);
                for (Map.Entry<Integer,FileField> entry : fieldMap.entrySet()){
                    FileField fileField = entry.getValue();
                    int _fileFieldID = fileField.getFieldid();
                    int _length = fileField.getFieldlength();
                    byte[] columnValue = getFiexedBytes(row.getString(_fileFieldID -1), _length);
                    bb.put(columnValue);
                }
                bb.flip();
                return bb.array();
            }
        });
        return rdd;
    }

    /**
     * 将dataFrame存成纯文本文件，定长文件
     * @param dataFrame
     * @param fieldMap
     * @param path
     */
    public static void dataFrameToFixedFile(Dataset<Row> dataFrame,TreeMap<Integer,FileField> fieldMap,String path){
        JavaRDD<byte[]> rdd = dataFrameToRddBytesOne(dataFrame,fieldMap);
        dataFrameToFixedFile(rdd,path);
    }

    /**
     * 将RDD存成纯文本文件，定长文件
     * @param rdd
     * @param path
     */
    public static void dataFrameToFixedFile(JavaRDD<byte[]> rdd,final String path){
        SparkContext sc = rdd.context();
        rdd.foreachPartition(new VoidFunction<Iterator<byte[]>>() {
            @Override
            public void call(Iterator<byte[]> partition) throws Exception {
                //得到FileSystem及对应的用户所在hdfs目录，hdfs中输出临时目录
                FileSystem fs = FileSystem.newInstance(SparkHadoopUtil.get().conf());
                String part_path = "part-" + java.util.UUID.randomUUID().toString();
                FSDataOutputStream fsDataOutputStream = fs.create(new Path(path, part_path));
                while (partition.hasNext()){
                    fsDataOutputStream.write(partition.next());
                    fsDataOutputStream.write(10);
                }
                fsDataOutputStream.flush();
                fsDataOutputStream.close();
            }
        });
    }

    /**
     * 写入标志文件
     * @param fileMap
     * @param property
     * @param path
     */
    public static void dataFrameToFlagFile(TreeMap<Integer,FileField> fileMap,Map<String,String> property,String path){
        String createTime = JavaDateUtils.getCurrentTime();
        String fileName = property.get("FILENAME");
        String fileSize = property.get("FILESIZE");
        long rowCount = Integer.parseInt(property.get("ROWCOUNT"));
        long rowLength = FixedFileToDataFrame.getLineLength(fileMap) - 1;
        String sql = property.get("SQL");
        String unzipFileName = property.get("UNZIPFILENAME");

        try{
            BufferedWriter bfw = new BufferedWriter(new FileWriter(path));
            bfw.write(unzipFileName + " " + rowCount * (rowLength + 1) + " " + rowCount + " " + createTime);
            bfw.newLine();
            bfw.newLine();

            bfw.write("FILENAME=" + fileName);
            bfw.newLine();
            bfw.newLine();

            bfw.write("FILESIZE=" + fileSize);
            bfw.newLine();
            bfw.newLine();

            bfw.write("SQL=" + sql);
            bfw.newLine();
            bfw.newLine();

            bfw.write("CREATEDATETIME=" + createTime);
            bfw.newLine();
            bfw.newLine();

            bfw.write("ROWCOUNT=" + rowCount);
            bfw.newLine();
            bfw.newLine();

            bfw.write("ROWLENGTH=" + rowLength);
            bfw.newLine();
            bfw.newLine();

            bfw.write("COLUMNCOUNT=" + fileMap.size());
            bfw.newLine();
            bfw.newLine();

            bfw.write("COLUMNDESCRIPTION=");
            bfw.newLine();

            for (Map.Entry<Integer,FileField> entry : fileMap.entrySet()){
                FileField fileField = entry.getValue();
                String fieldName = fileField.getFieldname();
                int fieldID = fileField.getFieldid();
                String fieldType = fileField.getFieldtype();
                int fieldStart = fileField.getFieldstart();
                int fieldEnd = fileField.getFieldend();
                String str = fieldID + "$$" + fieldName + "$$" + fieldType + "$$(" + fieldStart +"," + fieldEnd + ")";
                bfw.write(str);
                bfw.newLine();
            }
            bfw.newLine();
            bfw.flush();
            bfw.close();
        }catch (IOException e){
            e.printStackTrace();
        }


    }



}
