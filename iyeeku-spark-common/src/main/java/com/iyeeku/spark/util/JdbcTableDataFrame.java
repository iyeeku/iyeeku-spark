package com.iyeeku.spark.util;

import com.iyeeku.spark.util.Schema;
import org.apache.spark.api.java.function.ForeachPartitionFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.*;

/**
 * @ClassName JdbcTableDataFrame
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/12/27 20:50
 * @Version 1.0
 **/
public class JdbcTableDataFrame {

    public static int batchFetchSize = 100;

    /**
     * 读取数据库中的表数据
     * @param spark
     * @param driver
     * @param url
     * @param user
     * @param passwd
     * @param table
     * @return
     */
    public static Dataset<Row> getDataFrame(SparkSession spark,String driver,String url,String user,String passwd,String table){
        Properties prop = new Properties();
        prop.setProperty("user",user);
        prop.setProperty("password",passwd);
        prop.setProperty("driver",driver);
        prop.setProperty(org.apache.spark.sql.execution.datasources.jdbc.JDBCOptions.JDBC_BATCH_FETCH_SIZE(),""+batchFetchSize);
        Dataset<Row> df = spark.read().jdbc(url,table,prop);
        return df;
    }

    public static Dataset<Row> getDataFrame(SparkSession spark,String driver,String url,String user,String passwd,String table,String sql,String mode){
        return spark.read().format("jdbc")
                .option("driver",driver)
                .option("user",user)
                .option("password",passwd)
                .option("url",url)
                .option(org.apache.spark.sql.execution.datasources.jdbc.JDBCOptions.JDBC_BATCH_FETCH_SIZE(),""+batchFetchSize)
                .option("dbtable","0".equals(mode) ? table : "(" + sql + ") ttt")
                .load();
    }

    public static void fromDataFrameToJdbc(Dataset<Row> df,String driver,String url,String user,String passwd,String table){
        Properties prop = new Properties();
        prop.setProperty("user",user);
        prop.setProperty("password",passwd);
        prop.setProperty("driver",driver);
        //TODO
        //Dataset<Row> df_new = df.sparkSession().createDataFrame(df.rdd());

        Dataset<Row> df_new = null;
        df_new.write().mode("overwrite").jdbc(url,table,prop);
    }

    public static Map<String,Object> getInsertInfo(List<FileField> fileFieldList,String table){
        StringBuffer sb = new StringBuffer("INSERT INTO ").append(table.toUpperCase()).append("(");
        int count = 0;
        int[] lenArr = new int[fileFieldList.size()];
        for (FileField fileField : fileFieldList){
            sb.append(fileField.getFieldname().toUpperCase()).append(",");
            lenArr[count++] = fileField.getFieldlength();
        }
        sb = sb.deleteCharAt(sb.length() -1);
        sb.append(") VALUES(");
        for (int i = 0 ; i < count ; i++){
            sb.append("?,");
        }
        sb = sb.deleteCharAt(sb.length() -1);
        sb.append(")");
        Map<String,Object> rtnMap = new HashMap<>();
        rtnMap.put("SQL", sb.toString());
        rtnMap.put("LENARR", lenArr);
        rtnMap.put("COUNT", count);
        return rtnMap;
    }

    public static void fromDataFrameInsertDB(Dataset<Row> df,List<FileField> fileFieldList,final String driver,final String url,final String user,final String password,final String table,final int partitions,final int batchSize) throws Exception{
        final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("loadFromHiveToDB");
        final Map<String,Object> paramMap = getInsertInfo(fileFieldList,table);
        Class.forName(driver);
        Connection conn = DriverManager.getConnection(url,user,password);
        conn.setAutoCommit(true);
        conn.createStatement().execute("DELETE FROM " + table.toUpperCase());
        if (conn != null){
            conn.close();
        }
        long startTime = System.currentTimeMillis();
        logger.warn("runSql:【" + String.valueOf(paramMap.get("SQL")) + "】");
        //df.coalesce(partitions).foreachPartition(new ForeachPartitionFunction<Row>() {
        df.repartition(partitions).foreachPartition(new ForeachPartitionFunction<Row>() {
            Connection conn = null;
            PreparedStatement psmt = null;
            @Override
            public void call(Iterator<Row> iter) throws Exception {
                Class.forName(driver);
                conn = DriverManager.getConnection(url,user,password);
                psmt = conn.prepareStatement(String.valueOf(paramMap.get("SQL")));
                int count = 1;
                int paramCount = (int) paramMap.get("COUNT");
                int[] lenArr = (int[]) paramMap.get("LENARR");
                while (iter.hasNext()){
                    Row row = iter.next();
                    for (int i = 0 ; i < paramCount ; i++){
                        psmt.setString( i + 1 , row.getString(i) == null ? "" : row.getString(i).length() > lenArr[i] ? row.getString(i).substring(0,lenArr[i]) : row.getString(i));
                    }
                    psmt.addBatch();
                    if (count++ % batchSize == 0){
                        psmt.executeBatch();
                    }
                }
                psmt.executeBatch();
                if (psmt != null){psmt.close();}
                if (conn != null){conn.close();}
            }
        });
        long endTime = System.currentTimeMillis();
        logger.warn("runSql:【" + (endTime - startTime) + "】 ms");
    }

    public static void main(String[] args) {

    }


}
