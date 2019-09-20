package com.iyeeku.spark.example;

import com.iyeeku.spark.util.AppConfig;
import org.apache.commons.cli.*;
import org.apache.hadoop.fs.FileSystem;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.SparkHadoopUtil;
import org.apache.spark.sql.*;

import java.io.IOException;
import java.util.Properties;

/**
 * LoadHiveDBNew
 */
public class LoadHiveDB {

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
    private static String className = LoadHiveDB.class.getName();

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

    public LoadHiveDB(){

    }


    public static void main(String[] args) throws ParseException,IOException{

        SparkConf sparkConf = new SparkConf();
        validate(args,sparkConf);

        String deployMode = sparkConf.get("spark.submit.deployMode","client");

        SparkSession spark = SparkSession.builder().getOrCreate();
        //解压缩hdfs文件
        com.iyeeku.spark.util.HdfsUtils.gzipFileToHdfs(dataGzPath,dataPath);




        SQLContext sqlContext = spark.sqlContext();

        Dataset<Row> _dataFrame = null;

        _dataFrame = FixedFileToDataFrame.getDataFrame(sqlContext,"","");

        _dataFrame.write().mode(SaveMode.Overwrite).saveAsTable("");

        spark.stop();

    }

}
