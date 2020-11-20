package com.iyeeku.spark.common;

import com.iyeeku.spark.util.FileField;
import com.iyeeku.spark.util.JdbcTableDataFrame;
import com.iyeeku.spark.util.LogUtil;
import org.apache.commons.cli.*;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName LoadFromHiveToDB
 * @Description TODO
 * @Author YangQuan
 * @Date 2020/4/29 19:12
 * @Version 1.0
 **/
public class LoadFromHiveToDB {

    static Logger LOGGER = LoggerFactory.getLogger(LoadFromHiveToDB.class.getName());

    private static String driver = "";
    private static String url = "";
    private static String user = "";
    private static String password = "";
    private static String table = "";
    private static String sql = "";
    private static String fieldLength = "";
    private static String rq = "";
    private static int partitions = 50;
    private static int batchSize = 500;

    private static Options options = new Options();

    public static void printUsage(){
        new HelpFormatter().printHelp("LoadFromHiveToDB",options);
        System.exit(1);
    }

    private static void validate(String[] args) throws Exception {
        options.addOption("driver",true,"driver class [oracle.jdbc.driver.OracleDriver]");
        options.addOption("url",true,"url [jdbc:oracle:thin:@xxx:1521:xx]");
        options.addOption("user",true,"db username [xxx]");
        options.addOption("password",true,"db password [xxx]");
        options.addOption("table",true,"db table or view [xxx]");
        options.addOption("sql",true,"sql [select xxx from yyy]");
        options.addOption("fieldLength",true,"fieldLength [xxx=?,xxx=?]");
        options.addOption("rq",true,"rq");
        options.addOption("partitions",true,"partitions [etc num 50]");
        options.addOption("batchSize",true,"batchSize [etc num 500]");

        CommandLine commandLine = new GnuParser().parse(options , args);
        if (commandLine.hasOption("driver")){
            driver = commandLine.getOptionValue("driver");
        } else {
            printUsage();
        }
        if (commandLine.hasOption("url")){
            url = commandLine.getOptionValue("url");
        } else {
            printUsage();
        }
        if (commandLine.hasOption("user")){
            user = commandLine.getOptionValue("user");
        } else {
            printUsage();
        }
        if (commandLine.hasOption("password")){
            password = commandLine.getOptionValue("password");
        } else {
            printUsage();
        }
        if (commandLine.hasOption("table")){
            table = commandLine.getOptionValue("table");
        } else {
            printUsage();
        }
        if (commandLine.hasOption("sql")){
            sql = commandLine.getOptionValue("sql");
        } else {
            printUsage();
        }
        if (commandLine.hasOption("fieldLength")){
            fieldLength = commandLine.getOptionValue("fieldLength");
        } else {
            printUsage();
        }
        if (commandLine.hasOption("rq")){
            rq = commandLine.getOptionValue("rq");
        } else {
            printUsage();
        }
        if (commandLine.hasOption("partitions")){
            try {
                partitions = Integer.parseInt(commandLine.getOptionValue("partitions"));
            }catch (NumberFormatException e){
                LOGGER.error(e.getMessage());
                throw new Exception(e);
            }
        } else {
            partitions = 50;
        }
        if (commandLine.hasOption("batchSize")){
            try {
                batchSize = Integer.parseInt(commandLine.getOptionValue("batchSize"));
            }catch (NumberFormatException e){
                LOGGER.error(e.getMessage());
                throw new Exception(e);
            }
        } else {
            batchSize = 500;
        }
    }

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
                LOGGER.error("请检查" + column + "设置!");
                System.err.println("请检查" + column + "设置!");
                System.exit(1);
            }
            String columnName = column_arr[0].toUpperCase();
            map.put(columnName,Integer.parseInt(column_arr[1]));
        }
        return map;
    }

    public static List<FileField> checkMap(StructType structType,Map<String,Integer> _fieldLength) throws Exception{
        List<FileField> fieldList = new ArrayList<>();
        int count = 1;
        for (StructField entry : structType.fields()){
            String columName = entry.name().toUpperCase();
            if (!_fieldLength.containsKey(columName)){
                throw new Exception(entry.name() + " is not exists!");
            } else {
              int length = _fieldLength.get(columName);
              fieldList.add(new FileField(count++,columName,entry.dataType()+"("+length+")",-1,-1,length));
            }
        }
        return fieldList;
    }

    public static void main(String[] args) {
        try {
            validate(args);
            String logPath = LogUtil.initLogDir(rq);
            String logFullPath = LogUtil.initLogFile(logPath,LoadFromHiveToDB.class.getName()+"-"+table);
            LogUtil.addRootLoggerFileAppender(logFullPath);

            LOGGER.warn("ETL_DATA = " + rq + " | partitions = " + partitions + " | batchSize = " + batchSize);

            Map<String,Integer> srcMap = toMap(fieldLength);
            SparkConf conf = new SparkConf();
            conf.setAppName(LoadFromHiveToDB.class.getName() + "_" + table);
            //SparkSession spark = SparkSession.builder().config(conf).getOrCreate();
            SparkSession spark = SparkSession.builder().enableHiveSupport().getOrCreate();

            LOGGER.warn("SQL = " + sql);
            Dataset<Row> dataFrame = spark.sql(sql);
            List<FileField> fieldList = checkMap(dataFrame.schema(),srcMap);
            //插入到关系型数据库
            JdbcTableDataFrame.fromDataFrameInsertDB(dataFrame,fieldList,driver,url,user,password,table,partitions,batchSize);
            spark.stop();
        }catch (Exception e){
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }


}
