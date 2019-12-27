package com.iyeeku.spark.common;


import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.spark.SparkConf;

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
    private static String hivetable = "";
    private static String rq = "";

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
        options.addOption("d",true,"rq [yyyymmdd]");
        options.addOption("t",true,"Load Hive TableName");
    }


}
