package com.iyeeku.project.iyeeku;

import com.iyeeku.project.iyeeku.util.TransFromDateToString;
import com.iyeeku.project.iyeeku.util.TransFromStringToDate;
import com.iyeeku.spark.common.ExecSql;
import com.iyeeku.spark.util.SqlParseUtil;
import org.apache.commons.cli.*;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @ClassName IyeekuMain
 * @Description TODO
 * @Author YangQuan
 * @Date 2020/4/4 14:52
 * @Version 1.0
 **/
public class IyeekuMain {

    private final static Logger LOGGER = LoggerFactory.getLogger(IyeekuMain.class);

    private String _rq = "";
    private String _file = "";
    private Options opts = new Options();

    private void validate(String[] args){
        if(args.length < 2){
            System.err.println("Usage: IyeekuMain -d {yyyymmdd} -f {sqlfile}");
            System.exit(1);
        }
        opts.addOption("d",true,"YYYYMMDD");
        opts.addOption("f",true,"SQL File");
        CommandLine commandLine = null;
        try {
            commandLine = new GnuParser().parse(opts,args);
            if (commandLine.hasOption("d")){
                _rq = commandLine.getOptionValue("d");
            }else{
                printUsage();
            }
            if (commandLine.hasOption("f")){
                _file = commandLine.getOptionValue("f");
            }else{
                printUsage();
            }
        }catch (ParseException e){
            e.printStackTrace();
        }
    }

    private void printUsage(){
        new HelpFormatter().printHelp("IyeekuMain", opts);
        System.exit(1);
    }

    private static Dataset<Row> runSql(SparkSession spark,String sql){
        System.out.println("runSql:" + sql);
        return spark.sql(sql);
    }

    public static void main(String[] args) {
        IyeekuMain iyeeku = new IyeekuMain();
        iyeeku.validate(args);
        String rq = iyeeku._rq;

        SparkConf conf = new SparkConf();
        conf.setAppName(IyeekuMain.class.getName());
        SparkSession spark = SparkSession.builder().config(conf).getOrCreate();

        spark.udf().register("transFromDateToString", new TransFromDateToString(), DataTypes.StringType);
        spark.udf().register("transFromStringToDate", new TransFromStringToDate(), DataTypes.DateType);

        runSql(spark,"use iyeeku");
        String diffStartDate = runSql(spark,"select transFromDateToString(date_sub(transFromStringToDate('"+rq+"','yyyyMMdd'),10),'yyyyMMdd') as rq").first().getString(0);

        SqlParseUtil.runSqlSegment(spark,"set VAR_ETL_DATE="+rq);
        SqlParseUtil.runSqlSegment(spark,"set VAR_DIFF_START_DATE="+diffStartDate);

        LOGGER.warn("VAR_ETL_DATE = "+ rq + " | VAR_DIFF_START_DATE = " + diffStartDate);

        try {
            ExecSql.runSql(spark,iyeeku._file);
        }catch (IOException e){
            e.printStackTrace();
            LOGGER.error(e.getMessage());
        }

        spark.close();

    }

}
