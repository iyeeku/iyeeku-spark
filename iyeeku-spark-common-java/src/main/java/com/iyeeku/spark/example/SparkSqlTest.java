package com.iyeeku.spark.example;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;

import java.util.ArrayList;
import java.util.List;

public class SparkSqlTest {

    public static void main(String[] args) {

        //SparkSession - Spark SQL 的 入口
        //Note：在 Spark 2.0 中， SparkSession 合并了 SQLContext 和 HiveContext。

        SparkSession session = SparkSession.builder()
                .appName("My SparkSql Application")
                .master("local[*]")
                .enableHiveSupport()
                .getOrCreate();

        session.sql("drop database if exists iyeeku cascade");
        session.sql("create database iyeeku");
        session.sql("use iyeeku");

        String createTableSql = " create table staff(name String,card String,address String) stored as parquet";

        session.sql(createTableSql).show();

        ArrayList<String[]> data = new ArrayList<String[]>();
        data.add(new String[]{"zhangshang","123456","xxx-xxx-asdasd中文"});
        data.add(new String[]{"lisi","9823489327598","dadd83812edasdasd广兰路"});

        JavaSparkContext sc = new JavaSparkContext(session.sparkContext());
        JavaRDD<String[]> rdd = sc.parallelize(data);
        JavaRDD<Row> rdd_row = rdd.map(new Function<String[], Row>() {
            public Row call(String[] v1) throws Exception {
                return RowFactory.create(v1);
            }
        });

        List<StructField> fields = new ArrayList<StructField>();
        StructField structField1 = DataTypes.createStructField("name" , DataTypes.StringType , false);
        StructField structField2 = DataTypes.createStructField("card" , DataTypes.StringType , false);
        StructField structField3 = DataTypes.createStructField("address" , DataTypes.StringType , false);
        fields.add(structField1);
        fields.add(structField2);
        fields.add(structField3);

        Dataset<Row> datadf = session.createDataFrame(rdd_row,DataTypes.createStructType(fields));
        datadf.write().mode(SaveMode.Overwrite).saveAsTable("staff");



        Dataset<Row> sqlDF = session.sql("select * from staff");

        Dataset<String> stringDS = sqlDF.map(new MapFunction<Row, String>() {
            public String call(Row value) throws Exception {
                return value.getString(0) + "  ,  " +  value.getString(1) + "  ,  " + value.getString(2);
            }
        } , Encoders.STRING());

        stringDS.show();

        session.stop();

    }

}
