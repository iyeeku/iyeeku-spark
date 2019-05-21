package com.iyeeku.spark.example;

import org.apache.spark.sql.*;

public class loadHiveDB {

    public static void main(String[] args) {

        SparkSession spark = SparkSession.builder().getOrCreate();

        SQLContext sqlContext = spark.sqlContext();

        Dataset<Row> _dataFrame = null;

        _dataFrame = FixedFileToDataFrame.getDataFrame(sqlContext,"","");

        _dataFrame.write().mode(SaveMode.Overwrite).saveAsTable("");

        spark.stop();

    }

}
