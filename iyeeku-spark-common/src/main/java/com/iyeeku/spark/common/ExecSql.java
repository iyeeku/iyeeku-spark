package com.iyeeku.spark.common;

import org.apache.spark.sql.SparkSession;

public class ExecSql {

    public static void main(String[] args) {
        SparkSession session = SparkSession.builder()
                .appName("ExecSql")
                .master("local[*]")
                .enableHiveSupport()
                .getOrCreate();
    }

}
