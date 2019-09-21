package com.iyeeku.spark.util;

import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.SparkSessionExtensions;
import org.apache.spark.sql.catalog.Catalog;
import org.apache.spark.sql.catalyst.parser.ParserInterface;
import org.apache.spark.sql.internal.SessionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName TableOps
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/9/20 20:34
 * @Version 1.0
 **/
public class TableOps {

    private final static Logger LOGGER = LoggerFactory.getLogger(TableOps.class);

    public static boolean tableExists(SparkSession sparkSession , String tableName){
//        scala.Tuple2<String,String> tableIdentifierTableName =


return false;
    }

/*    public static scala.Tuple2<String,String> formatTable(SparkSession sparkSession,String tableName){
        Catalog catalog = sparkSession.catalog();
        ParserInterface sparkParser = SparkSession

    }*/


}
