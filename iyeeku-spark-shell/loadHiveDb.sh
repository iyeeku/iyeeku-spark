#!/usr/bin/env bash
SHELL_HOME="/home/shell"
. ${SHELL_HOME}/conf/conf.d

if [[ $# -ne 6 || "$1" != "-f" || "$3" != "-d" || "$5" != "-t" ]]; then
    echo "Usage: $0 -f filePrefix -d date -t tableName"
    exit 1
fi

#读取脚本参数 解压缩文件
filePrefix=$2
dateDate=$4
tableName=$6

_spark_submit_base=${SPARK_BASE}

_shellName=$0
_shellNameLog=`echo ${_shellName} | awk -F/ '{print $NF}' | wak -F. '{print $1}'`".${filePrefix}.log"
##处理tableName，先转化成小写，判断表名中是否存在字符，若存在则将如下转换 xxx.aa =>xxx.db/aa/

