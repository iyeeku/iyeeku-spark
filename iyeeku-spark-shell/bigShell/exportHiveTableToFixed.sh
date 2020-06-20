#!/usr/bin/env bash
set -x

SHELL_HOME="/home/shell"
. ${SHELL_HOME}/conf/conf.d

if [ $# -ne 8 -o "$1" != "-sql" -o "$3" != "-t" -o "$5" != "-d" -o "$7" != "-c" ];then
    echo "usage: $0 -sql sql -t fileNameFix -d date -c FieldSetting"
    exit 1
fi


#读取脚本参数 解压缩文件
_sql=$2
_prefix=$4
_date=$6
_fieldSetting=$8
shellName=$0
_spark_submit_bash=${SPARK_BASE}
exportMainClass=com.iyeeku.spark.common.ExportTableFixToText

##对应月份
_yyyymm=`echo ${_date} | cut -c 1-6`
localLogDir=${LOG_BASE_DIR}/${_date}
shellNameLog=`echo ${shellName} | awk -F/ '{print $NF}' | awk -F. '{print $1}'`".${_prefix}.log"
logFile=${localLogDir}/${shellNameLog}
hdfsDataDir=${HDFS_BASE_DIR_IN}/${_prefix}/${_yyyymm}
localDataDir=${LOCAL_BASE_DIR_OUT}/${_date}
##真正需要的前缀
_realPrefix="${localDataDir}/${_prefix}.${_date}.000000.0000"
outputDataFile="${_realPrefix}.dat.gz"
outputFlgFile="${_realPrefix}.flg"

outLog()
{
    logContent=$1
    iTime=`date +"%Y-%m-%d %H:%M:%S"`
    if [ ${LOG_LEVEL} -eq 0 ]; then
        echo "[${iTime}] ${logContent}" >> ${logFile}
    else
        echo "[${iTime}] ${logContent}" | tee -a ${logFile}
    fi
}

##创建日志目录
mkdir -p ${localLogDir}

##########################################加载##########################################
outLog "[INFO] 开始导出数据文件--------------------${filePrefix} ${rq}---------------------------------"

${_spark_submit_bash} --class ${exportMainClass} ${EXPORT_MAIN_CLASS_JAR} "${_sql}" "${_realPrefix}" "${_fieldSetting}" "${_date}"
ret=$?
if [ ${ret} -eq 0 -a -f ${outputDataFile} -a -f ${outputFlgFile} ]; then
    outLog "[INFO] succedd export data file to ${outputFlgFile}"
else
    exit 1
fi

exit 0
