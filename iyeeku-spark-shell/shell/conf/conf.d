#!/usr/bin/env bash
SPARK_BASE=spark-submit
LOG_BASE_DIR=/home/shell/log
MAX_RETRY_TIMES=3
LOCAL_BASE_DIR_IN=/home/data/in
LOCAL_BASE_DIR_OUT=/home/data/out
LOG_LEVEL=1
HDFS_BASE_DIR_IN=/in/data
HDFS_BASE_DIR_OUT=/out/data
EXPORT_MAIN_CLASS=com.iyeeku.spark.common.ExportTableToText
EXPORT_MAIN_CLASS_JAR=/home/shell/jars/iyeeku-spark-common.jar
HDFS_WAREHOUSE_DIR=/user/hive/warehouse
EXPORT_SEPARATE="|"
PER_CREATE_DAYS=30
IYEEKU_HOST=10.7.130.140
IYEEKU_DB_USER=iyeekudev
IYEEKU_DB_PASSWORD=iyeekudev
