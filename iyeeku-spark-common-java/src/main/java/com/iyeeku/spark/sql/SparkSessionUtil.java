package com.iyeeku.spark.sql;

import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.hive.HiveExternalCatalog;
import org.apache.spark.sql.internal.SessionState;
import org.apache.spark.sql.internal.SharedState;


/**
 * 获取SessionState和SharedState用于调试程序
 * @ClassName SparkSessionUtil
 * @Description TODO
 * @Author YangQuan
 * @Date 2019/9/24 21:54
 * @Version 1.0
 **/
public class SparkSessionUtil {


    public static SharedState getSharedState(SparkSession sparkSession){
        return sparkSession.sharedState();
    }

    public static SessionState getSessionState(SparkSession sparkSession){
        return sparkSession.sessionState();
    }

/*    public static HiveExternalCatalog getHiveExternalCatalog(SparkSession sparkSession){
        return (org.apache.spark.sql.hive.HiveExternalCatalog)getSharedState(sparkSession).externalCatalog();
    }*/



}
