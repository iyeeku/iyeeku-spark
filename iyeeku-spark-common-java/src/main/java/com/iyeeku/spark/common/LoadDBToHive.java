package com.iyeeku.spark.common;


import com.iyeeku.spark.util.JdbcTableDataFrame;
import com.iyeeku.spark.util.LogUtil;
import com.iyeeku.spark.util.Schema;
import org.apache.commons.cli.*;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;

/**
 * @ClassName LoadDBToHive
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/12/27 21:00
 * @Version 1.0
 **/
public class LoadDBToHive {

    private static String driver = "";
    private static String url = "";
    private static String user = "";
    private static String password = "";
    private static String table = "";
    private static String sql = "";
    private static String hivetable = "";
    private static String rq = "";
    private static String mode = "0";

    private static Options options = new Options();

    /**
     * 打印帮助信息
     */
    public static void printUsage(){
        new HelpFormatter().printHelp("LoadDBToHive",options);
        System.exit(1);
    }

    /**
     * 验证参数
     * @param args
     * @throws ParseException
     */
    private static void validate(String[] args) throws ParseException{
        options.addOption("driver",true,"driver class [oracle.jdbc.driver.OracleDriver]");
        options.addOption("url",true,"url [jdbc:oracle:thin:@xxx:1521:xx]");
        options.addOption("user",true,"db username [xxx]");
        options.addOption("password",true,"Load Hive TableName");
        options.addOption("table",true,"db table or view [xxx]");
        options.addOption("sql",true,"sql [xxx]");
        options.addOption("hivetable",true,"hive table or view [xxx]");
        options.addOption("d",true,"rq [yyyymmdd]");
        options.addOption("mode",true,"mode [0|1]");
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
            sql = "";
        }
        if (commandLine.hasOption("hivetable")){
            hivetable = commandLine.getOptionValue("hivetable");
        } else {
            printUsage();
        }
        if (commandLine.hasOption("rq")){
            rq = commandLine.getOptionValue("rq");
        } else {
            printUsage();
        }
        if (commandLine.hasOption("mode")){
            mode = commandLine.getOptionValue("mode");
        } else {
            mode = "0";
        }
    }

    public static void main(String[] args) throws ParseException {
        validate(args);

        String logPath = LogUtil.initLogDir(rq);
        String logFullPath = LogUtil.initLogFile(logPath,LoadFromHiveToDB.class.getName()+"-"+table);
        LogUtil.addRootLoggerFileAppender(logFullPath);

        //SparkSession spark = SparkSession.builder().config(conf).getOrCreate();
        SparkSession spark = SparkSession.builder().enableHiveSupport().getOrCreate();

        //得到dataset
        Dataset<Row> df = JdbcTableDataFrame.getDataFrame(spark,driver,url,user,password,table);
        //将日期类型、时间类型转换成string类型
        StructType structType = Schema.tranformDateType(df.schema());
        spark.createDataFrame(spark.emptyDataFrame().rdd() , structType).write().mode(SaveMode.Overwrite).saveAsTable(hivetable);
        //将dataset集合插入到目标表中
        df.write().mode(SaveMode.Overwrite).insertInto(hivetable);

        spark.stop();

    }

}
