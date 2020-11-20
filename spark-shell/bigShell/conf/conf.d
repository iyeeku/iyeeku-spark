#!/usr/bin/env bash
SPARK_BASE=spark-submit
LOG_BASE_DIR=/home/shell/log
MAX_RETRY_TIMES=3
LOCAL_BASE_DIR_IN=/home/data/in
LOCAL_BASE_DIR_OUT=/home/data/out
LOG_LEVEL=1
DRIVER_JARS=/home/shell/jars
DRIVER_CLASS=oracle.jdbc.driver.OracleDriver
HDFS_BASE_DIR_IN=/in/data
HDFS_BASE_DIR_OUT=/out/data
EXPORT_MAIN_CLASS=com.iyeeku.spark.common.ExportTableToText
EXPORT_MAIN_CLASS_JAR=/home/shell/jars/iyeeku-spark-common-1.0.0.jar
BASE_MAIN_CLASS_JAR=/home/shell/jars/iyeeku-spark-common-1.0.0.jar
HDFS_WAREHOUSE_DIR=/user/hive/warehouse
EXPORT_SEPARATE="|"
PER_CREATE_DAYS=30
IYEEKU_DB_HOST=49.235.174.225
IYEEKU_DB_SID=iyeeku
IYEEKU_DB_USER=iyeekudev
IYEEKU_DB_PASSWORD=iyeekudev

