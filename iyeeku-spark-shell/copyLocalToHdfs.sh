#!/usr/bin/env bash
SHELL_HOME="/home/shell"
. ${SHELL_HOME}/conf/conf.d

if [[ $# -ne 4 -o "$1" != "-f" -o "$3" != "-d" ]]; then
    echo "Usage: $0 -f filePrefix -d dataDate"
    exit 1
fi

#读取脚本参数 文件名前缀 数据日期
filePrefix=$2
dateDate=$4
shellName=$0
shellNameLog=`echo ${shellName} | awk -F/ '{print $NF}' | awk -F. '{print $1}'`".${filePrefix}.log"

##对应月份 YYYYMM 本地数据目录、本地日志、HDFS目录、本地文件
yyyyMM=`echo ${dateDate} | cut -c 1-6`
localDataDir=${LOCAL_BASE_DIR_IN}/${dateDate}
localLogDir=${LOG_BASE_DIR}/${dateDate}
logFile=${localLogDir}/${shellNameLog}
hdfsDataDir=${HDFS_BASE_DIR_IN}/${filePrefix}/${yyyyMM}
jarsDir=${SHELL_HOME}/jars

if [[ ! -d ${localDataDir} ]]; then
    mkdir -p ${localDataDir}
fi

if [[ ! -d ${localLogDir} ]]; then
    mkdir -p ${localLogDir}
fi

##搜索对应的数据文件及标识文件、若数据文件及标识文件不存在的话、退出
localDataGzFileCount=`ls ${localDataDir} | grep ^${filePrefix}".${dateDate}.*.gz" | wc -l`
localFlgFileCount=`ls ${localDataDir} | grep ^${filePrefix}".${dateDate}.*.flg" | wc -l`

if [[ ${localDataGzFileCount} -eq 1 -a ${localFlgFileCount} -eq 1 ]]; then
    echo "yes"
else
    echo "[INFO] ${localDataDir} no only one ${filePrefix}"
    exit 1
fi







