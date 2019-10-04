package com.iyeeku.spark.util;

import com.iyeeku.spark.sql.SparkSessionUtil;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.SparkSessionExtensions;
import org.apache.spark.sql.catalog.Catalog;
import org.apache.spark.sql.catalyst.TableIdentifier;
import org.apache.spark.sql.catalyst.parser.ParseException;
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

    public static boolean tableExists(SparkSession sparkSession , String tableName) throws ParseException{
        scala.Tuple2<String,String> tableIdentifierTableName = formatTable(sparkSession,tableName);
        Catalog catalog = sparkSession.catalog();
        String current_db = catalog.currentDatabase();
        catalog.setCurrentDatabase(tableIdentifierTableName._1);
        boolean tableExists = sparkSession.catalog().tableExists(tableIdentifierTableName._2);
        catalog.setCurrentDatabase(current_db);
        return tableExists;
    }

    public static scala.Tuple2<String,String> formatTable(SparkSession sparkSession,String tableName) throws ParseException{
        Catalog catalog = sparkSession.catalog();
        ParserInterface sparkParser = SparkSessionUtil.getSessionState(sparkSession).sqlParser();
        TableIdentifier tableIdentifier = sparkParser.parseTableIdentifier(tableName);
        scala.Option<String> rowdb = tableIdentifier.database();
        String db = rowdb.isEmpty() ? catalog.currentDatabase() : rowdb.get();
        String table = tableIdentifier.table();
        return scala.Tuple2.apply(db,table);
    }


}
