package com.iyeeku.spark.common;

import com.iyeeku.spark.util.Schema;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;


import java.util.Properties;

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


}
