package com.iyeeku.spark.util;

import com.iyeeku.spark.sql.SparkSessionUtil;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalog.Catalog;
import org.apache.spark.sql.catalyst.TableIdentifier;
import org.apache.spark.sql.catalyst.parser.ParseException;
import org.apache.spark.sql.catalyst.parser.ParserInterface;
import org.apache.spark.sql.types.StructType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * 得到表的列，是否包含分区字段
     * @throws ParseException
     */
    public static List<org.apache.spark.sql.catalog.Column> getTableColumns(SparkSession spark,String tableName,boolean cotainPartitionColumn) throws ParseException{
        scala.Tuple2<String,String> tableIdentifierTableName = formatTable(spark,tableName);
        Catalog catalog = spark.catalog();
        String currentDb = catalog.currentDatabase();

        List<org.apache.spark.sql.catalog.Column> allColumnList = new ArrayList<org.apache.spark.sql.catalog.Column>();
        List<org.apache.spark.sql.catalog.Column> returnColumnList = new ArrayList<org.apache.spark.sql.catalog.Column>();

        try {
            catalog.setCurrentDatabase(tableIdentifierTableName._1);
            allColumnList = spark.catalog().listColumns(tableIdentifierTableName._2).collectAsList();
            catalog.setCurrentDatabase(currentDb);
        }catch (AnalysisException e){
            e.printStackTrace();
        }

        if (cotainPartitionColumn == true){
            return allColumnList;
        }else{
            for (org.apache.spark.sql.catalog.Column column:allColumnList){
                if (column.isPartition() == false) returnColumnList.add(column);
            }
            return returnColumnList;
        }
    }

    /**
     *加载表的数据到历史表中，若历史表不存在则覆盖，若存在则新增数据到历史表
     */
    public static void loadHisTable(SparkSession spark, String tableName, String hisTableName) throws ParseException{
        StructType _schema_1 = spark.table(tableName).schema();
        if (tableExists(spark,hisTableName)){
            StructType _schema_2 = spark.table(hisTableName).schema();
            String columns = Schema.getDiffFieldSql(_schema_2,_schema_1);
            String sql = "select " + columns + " from " + tableName;
            spark.sql(sql).write().insertInto(hisTableName);
        }else{
            spark.table(tableName).write().mode("overwrite").saveAsTable(hisTableName);
        }
    }

}
