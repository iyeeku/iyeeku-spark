package com.iyeeku.spark.common;

import com.iyeeku.spark.util.AppConfig;
import com.iyeeku.spark.util.FixedFileToDataFrame;
import com.iyeeku.spark.util.LogUtil;
import com.iyeeku.spark.util.TableOps;
import org.apache.commons.cli.*;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.SparkHadoopUtil;
import org.apache.spark.sql.*;

import java.io.IOException;
import java.util.Properties;

/**
 * LoadHiveDBNew
 */
public class LoadFileToHive {

    private static String PARAM_OPTION_F = "f";
    private static String PARAM_OPTION_D = "d";
    private static String PARAM_OPTION_T = "t";

    /**
     * 数据目录
     */
    private static String dataDir = "";
    /**
     * 压缩文件目录
     */
    private static String dataGzPath = "";
    private static String dataPath = "";
    private static String flgPath = "";
    private static String filePrefix = "";
    private static String rq = "";


    private static String tableNameInput = "";
    private static String tableNameDb = "";
    private static String tableName = "";

    private static Options options = new Options();
    private static FileSystem fs = null;
    private static String className = LoadFileToHive.class.getName();

    /**
     * 打印帮助信息
     */
    public static void printUsage(){
        new HelpFormatter().printHelp(className,options);
        System.exit(1);
    }

    /**
     * 验证参数
     * @param args
     * @param sparkConf
     * @throws ParseException
     * @throws IOException
     */
    private static void validate(String[] args, SparkConf sparkConf) throws ParseException,IOException{
        options.addOption("f",true,"FilePrefix [IYEEKU_XXX]");
        options.addOption("d",true,"rq [yyyymmdd]");
        options.addOption("t",true,"Load Hive TableName");

        CommandLine commandLine = new GnuParser().parse(options,args);
        if (commandLine.hasOption(PARAM_OPTION_F)){
            filePrefix = commandLine.getOptionValue(PARAM_OPTION_F);
        } else {
            printUsage();
        }
        if (commandLine.hasOption(PARAM_OPTION_D)){
            rq = commandLine.getOptionValue(PARAM_OPTION_D);
        } else {
            printUsage();
        }
        if (commandLine.hasOption(PARAM_OPTION_T)){
            tableNameInput = commandLine.getOptionValue(PARAM_OPTION_T);
        } else {
            printUsage();
        }

        //得到FileSystem和App配置文件
        fs = FileSystem.newInstance(SparkHadoopUtil.get().conf());
        Properties appConf = AppConfig.getDefaultAppConfig(fs,sparkConf);
        //获取数据目录
        dataDir = appConf.getProperty("HDFS_BASE_DIR_IN");
        scala.Tuple2<String,String> hdfsGzAndFile = com.iyeeku.spark.util.HdfsUtils.searchHdfsGzAndFlg(fs,dataDir,filePrefix,rq);
        validateHdfsGzAndFile(hdfsGzAndFile);

    }

    public static void validateHdfsGzAndFile(scala.Tuple2<String,String> hdfsGzAndFile){
        dataGzPath = hdfsGzAndFile._1;
        flgPath = hdfsGzAndFile._2;
        dataPath = dataGzPath.replace(".gz","");
    }

    public LoadFileToHive(){

    }


    public static void main(String[] args) throws ParseException,IOException{

        SparkConf sparkConf = new SparkConf();
        validate(args,sparkConf);

        String deployMode = sparkConf.get("spark.submit.deployMode","client");

        /**
         * TODO spark读取Hive出错： Database 'xxx' not found
         *  错误找不到数据库,spark默认是不读hive-site.xml , 加上enableHiveSupport()即可
         */
        SparkSession spark = SparkSession.builder().enableHiveSupport().getOrCreate();

        try {
            //获取输入表对应的数据库和表名
            scala.Tuple2<String,String> tableIdentifier_tableName = TableOps.formatTable(spark,tableNameInput);
            tableNameDb = tableIdentifier_tableName._1;
            tableName = tableIdentifier_tableName._2;
        }catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }

        //初始化日志目录
        String logPath = LogUtil.initLogDir(rq);
        String logFullPath = LogUtil.initLogFile(logPath,className+"-"+tableName);
        LogUtil.addRootLoggerFileAppender(logFullPath);

        //解压缩hdfs文件
        com.iyeeku.spark.util.HdfsUtils.gzipFileToHdfs(dataGzPath,dataPath);


        Dataset<Row> df = FixedFileToDataFrame.getDataFrame(spark.sqlContext(),flgPath,dataPath);
        df.write().mode(SaveMode.Overwrite).saveAsTable(tableNameInput);

        //将解压缩后的文件删除
        if (fs.exists(new Path(dataPath))){
            fs.delete(new Path(dataPath),true);
        }

        spark.stop();

    }

}
