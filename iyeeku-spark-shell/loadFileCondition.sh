#!/usr/bin/env bash
SHELL_HOME="/home/shell"
. ${SHELL_HOME}/conf/conf.d


spark-submit --name "com.iyeeku.spark.example.LoadHiveDB-IYEEKU_TEST_HX_JYWLXXWJ" --class com.iyeeku.spark.example.LoadHiveDB --conf spark.app.conf=/user/iyeeku/cfg/base.cfg /home/shell/iyeeku-spark-common-1.0.0.jar -f IYEEKU_TEST_HX_JYWLXXWJ -d 20191004 -t IYEEKU_TEST_HX_JYWLXXWJ

