package com.iyeeku.spark.common;

import com.iyeeku.spark.util.SqlParseUtil;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @ClassName ExecSql
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/9/20 20:09
 * @Version 1.0
 **/
public class ExecSql {

    public static String sqlFileName = "";
    public static Options opts = new Options();
    public  static Logger logger = LoggerFactory.getLogger(ExecSql.class);

    public static void printHelp(){
        new HelpFormatter().printHelp("ExecSql" , opts);
        System.exit(1);
    }

    public static void validateArgs(String[] args) throws ParseException{
        opts.addOption("f" , true , "Run Sql File");

        CommandLine commandLine = new GnuParser().parse(opts , args);
        if (commandLine.hasOption("f")){
            sqlFileName = commandLine.getOptionValue("f");
        } else {
            printHelp();
        }

        if (!FileUtils.getFile(sqlFileName).exists()){
            System.err.println("Sql File:[" + sqlFileName + "] Not Found!");
            System.exit(1);
        }
    }

    public static void runSql(SparkSession session , String sqlFileName) throws IOException{
        String allSql = SqlParseUtil.readLinesExceptBlank(new File(sqlFileName));
        SqlParseUtil.runSqlSegment(session , allSql);
    }

    public static void main(String[] args) throws IOException {
        try {
            validateArgs(args);
        }catch (ParseException pe){
            pe.printStackTrace();
        }

        SparkSession session = SparkSession.builder()
                .appName("ExecSql")
                .master("local[*]")
                .enableHiveSupport()
                .getOrCreate();

        SparkContext context = session.sparkContext();
        context.setLogLevel("WARN");
        runSql(session , sqlFileName);
        session.stop();
    }

}
