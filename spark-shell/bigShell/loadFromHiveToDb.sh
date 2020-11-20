#!/usr/bin/env bash
SHELL_HOME="/home/shell"
. ${SHELL_HOME}/conf/conf.d

if [[ $# -ne 12 || "$1" != "-sql" || "$3" != "-t" || "$5" != "-d" || "$7" != "-c" || "$9" != "-p" || "${11}" != "-b" ]]; then
    echo "Usage: $0 -sql sql -t table -d date -c fieldSetting -p partitions -b batchSize"
    exit 1
fi

#读取脚本参数 解压缩文件
_sql=$2
_table=$4
_date=$6
_fieldSetting=$8
_partitions=${10}
_batchSize=${12}
_loadFromHiveToDbMainClass="com.iyeeku.spark.common.LoadFromHiveToDB"

_spark_submit_base=${SPARK_BASE}

_shellName=$0
_yyyymm=`echo ${_date}|cut -c 1-6`
_localLogDir=${LOG_BASE_DIR}/${_date}
_shellNameLog=`echo ${_shellName} | awk -F/ '{print $NF}' | awk -F. '{print $1}'`".${_table}.log"
_logFile=${_localLogDir}/${_shellNameLog}

URL="jdbc:oracle:thin:@${IYEEKU_DB_HOST}:1521:${IYEEKU_DB_SID}"

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

_appName="loadHiveTabel_${_table}"
outLog "[INFO] ---------------------------------------------------------------------------------------"
outLog "[INFO] start load Hive table data to oracle ${_table}"
${_spark_submit_base} --name ${_appName} --deploy-mode client --jars ${DRIVER_JARS} --class ${_loadFromHiveToDbMainClass} ${BASE_MAIN_CLASS_JAR} -driver ${DRIVER_CLASS} -url ${URL} -user ${IYEEKU_DB_USER} -password ${IYEEKU_DB_PASSWORD} -table "${_table}" -sql "${_sql}" -fieldLength "${_fieldSetting}" -rq "${_date}" -partitions "${_partitions}" -batchSize "${_batchSize}"
ret=$?
if [ ${ret} -eq 0 ]; then
    outLog "[INFO] 从hive库中抽取数据到关系型数据库${_table}表中，成功"
    exit 0
else
    outLog "[ERROR] 从hive库中抽取数据到关系型数据库${_table}表中，失败"
    exit 2
fi

exit 0
