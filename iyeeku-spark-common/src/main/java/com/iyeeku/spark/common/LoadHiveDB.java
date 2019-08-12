package com.iyeeku.spark.common;

import org.apache.commons.cli.*;
import org.apache.hadoop.fs.FileSystem;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.SparkHadoopUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * @ClassName LoadHiveDB
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/8/12 23:14
 * @Version 1.0
 **/
public class LoadHiveDB {

    private static String dataDir = "";
    private static String dataGzPath = "";
    private static String dataPath = "";
    private static String flagPath = "";
    private static String filePrefix = "";
    private static String rq = "";

    private static String tableNameInput = "";
    private static String tableNameDb = "";
    private static String tableName = "";

    private static Options opts = new Options();
    private static FileSystem fs = null;
    private static String className = LoadHiveDB.class.getName();

    public static void printUsage(){
        new HelpFormatter().printHelp(className , opts);
        System.exit(1);
    }

    private static void validate(String[] args, SparkConf sparkConf) throws ParseException , IOException{
        opts.addOption("f" , true , "FilePrefix [JYWLXXB]");
        opts.addOption("d" , true , "rq [yyyymmdd]");
        opts.addOption("t" , true , "Load Hive TableName");

        CommandLine commandLine = new GnuParser().parse(opts , args);
        if (commandLine.hasOption("f")){
            filePrefix = commandLine.getOptionValue("f");
        } else {
            printUsage();
        }
        if (commandLine.hasOption("d")){
            rq = commandLine.getOptionValue("d");
        } else {
            printUsage();
        }
        if (commandLine.hasOption("t")){
            tableNameInput = commandLine.getOptionValue("t");
        } else {
            printUsage();
        }

        fs = FileSystem.newInstance(SparkHadoopUtil.get().conf());
        /*Properties appConf = */


    }


}
