package com.iyeeku.spark.common;

import com.iyeeku.spark.util.DataFrameToFile;
import com.iyeeku.spark.util.FileField;
import com.iyeeku.spark.util.LogUtil;
import com.iyeeku.spark.util.Schema;
import org.apache.hadoop.fs.*;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.deploy.SparkHadoopUtil;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPOutputStream;

/**
 * @ClassName ExportTableFixToText
 * @Description TODO
 * @Author YangQuan
 * @Date 2020/4/3 10:44
 * @Version 1.0
 **/
public class ExportTableFixToText {

    private final static Logger LOGGER = Logger.getLogger(ExportTableFixToText.class);

    /**
     * 将字符串(字段名称1=长度1,字段名称2=长度2)转换成map结构
     * @param str
     * @return
     */
    public static Map<String,Integer> toMap(String str){
        Map<String,Integer> map = new HashMap<>();
        String[] arr = str.split(",");
        for(String column : arr){
            String[] column_arr = column.split("=");
            if (column_arr.length != 2){
                System.err.println("请检查" + column + "设置!");
                System.exit(1);
            }
            String columnName = column_arr[0].toUpperCase();
            map.put(columnName,Integer.parseInt(column_arr[1]));
        }
        return map;
    }

    /**
     * 检查sql生成的schema与字段长度是否一致，若sql中的字段长度映射中不存在则抛出异常
     * @param schema
     * @param _fieldLength
     * @return
     * @throws Exception
     */
    public static TreeMap<Integer,FileField> checkMap(TreeMap<Integer,FileField> schema, Map<String,Integer> _fieldLength) throws Exception{
        TreeMap<Integer,FileField> fieldMap = new TreeMap<>();
        for (Map.Entry<Integer,FileField> entry : schema.entrySet()){
            FileField fileField = entry.getValue();
            int fieldID = fileField.getFieldid();
            String fieldName = fileField.getFieldname();
            String fieldType = fileField.getFieldtype();
            if(!_fieldLength.containsKey(fieldName)){
                throw new Exception(fieldName + " is not exists!");
            }else {
                int length = _fieldLength.get(fieldName);
                fieldMap.put(entry.getKey(), new FileField(fieldID,fieldName,fieldType + "(" + length + ")" , -1 , -1 , length));
            }
        }
        return DataFrameToFile.getAllFieldMapByLength(fieldMap);
    }

    /**
     * 将hdfs中拷贝目录下的文件压缩到本地文件
     * @param srcFs
     * @param srcPath
     * @param dstFs
     * @param dstPath
     * @param buffSize
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void copyPathToDstGz(FileSystem srcFs, Path srcPath,FileSystem dstFs, Path dstPath, int buffSize) throws FileNotFoundException,IOException{
        RemoteIterator<LocatedFileStatus> files = srcFs.listFiles(srcPath,true);
        GZIPOutputStream out = new GZIPOutputStream(dstFs.create(dstPath));
        while (files.hasNext()){
            Path path = files.next().getPath();
            FSDataInputStream in = srcFs.open(path);
            org.apache.hadoop.io.IOUtils.copyBytes(in,out,buffSize,false);
        }
        out.close();
    }

    public static void main(String[] args) {
        if (args.length < 4){
            System.err.println("Usage: ExportTableFixToText <sql> <prefix> <fieldLength> <date>");
            System.exit(1);
        }

        String sql = args[0];
        String prefix = args[1];
        String fieldLength = args[2];
        String rq = args[3];
        String compressFlg = "1";

        String logPath = LogUtil.initLogDir(rq);
        String logFullPath = LogUtil.initLogFile(logPath,ExportTableFixToText.class.getName() + "-" + prefix);
        LogUtil.addRootLoggerFileAppender(logFullPath);

        try {
            FileSystem fs = FileSystem.newInstance(SparkHadoopUtil.get().conf());
            Path homePath = fs.getHomeDirectory();
            String tempDirName = java.util.UUID.randomUUID().toString();
            Path outputTempDir = new Path(homePath,"temp/" + tempDirName);

            //若是压缩则以dat.gz结尾，否则以dat结尾
            Path outputFile = null;
            if(compressFlg.equals("1")){
                outputFile = new Path(prefix + ".dat.gz");
            }else {
                outputFile = new Path(prefix + ".dat");
            }
            String localFlag = prefix + ".flg";

            SparkConf conf = new SparkConf();
            conf.setAppName(ExportTableFixToText.class.getName() + "_" + prefix);

            // Clouder Manager
            //SparkSession spark = SparkSession.builder().config(conf).getOrCreate();

            SparkSession spark = SparkSession.builder().enableHiveSupport().getOrCreate();

            SparkContext sc = spark.sparkContext();
            int buffSize = sc.hadoopConfiguration().getInt("io.file.buffer.size",4096);
            Dataset<Row> dataFrame = spark.sql(sql);

            Map<String,Integer> srcMap = toMap(fieldLength);
            TreeMap<Integer,FileField> fieldMap = checkMap(Schema.getFieldMap(dataFrame.schema()), srcMap);

            if(!fs.exists(outputTempDir)){
                fs.mkdirs(outputTempDir);
            }

            long rowCount = dataFrame.count();
            //导出文件到hdfs中
            DataFrameToFile.dataFrameToFixedFile(dataFrame, fieldMap, outputTempDir.toString());
            //将集群停止后拷贝文件
            sc.stop();

            FileSystem srcFs = fs;
            FileSystem dstFs = FileSystem.getLocal(SparkHadoopUtil.get().conf());

            //若存在本地文件则直接删除
            if(dstFs.exists(outputFile)){
                dstFs.delete(outputFile, true);
            }

            if(compressFlg.equals("1")){
                copyPathToDstGz(srcFs, outputTempDir, dstFs, outputFile, buffSize);
            }else{
                //将目录下的文件Merge成一个文件
                org.apache.hadoop.fs.FileUtil.copyMerge(srcFs,outputTempDir,dstFs,outputFile,true,SparkHadoopUtil.get().conf(),null);
            }

            long fileLength = dstFs.listStatus(outputFile)[0].getLen();
            StructType schema = dataFrame.schema();
            Map<String,String> property = new HashMap<>();
            property.put("FILENAME",outputFile.getName());
            property.put("FILESIZE",fileLength + "");
            property.put("SQL",sql);
            property.put("ROWCOUNT",rowCount + "");
            property.put("UNZIPFILENAME",prefix + ".dat");
            DataFrameToFile.dataFrameToFlagFile(fieldMap,property,localFlag);

            //将临时目录删除
            if (fs.exists(outputTempDir)){
                fs.delete(outputTempDir,true);
            }

        }catch (Exception e){
            LOGGER.error(e);
            e.printStackTrace();
            System.exit(1);
        }

    }

}
