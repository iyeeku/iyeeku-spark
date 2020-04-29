#!/usr/bin/env bash

SHELL_HOME="/home/shell"
. ${SHELL_HOME}/conf/conf.d

##日期
#读取脚本参数 解压缩文件
rq=
shellName=$0
##################################参数赋值##############################
while [ 1 ]
do
    case $1 in
    "-d")
        shift 1
        rq=$1
        if [ ${rq} == "" ]; then
            echo -e "ERROR\t rq 不能为空"
            exit 1
        else
            shift 1
        fi
        ;;
    *)
        echo "Usage: ${shellName} -f filePrefix -t tableName -d dataDate"
        echo -e "ERROR\t ${shellName}调用错误"
        exit 1
        ;;
    esac

    if [ $# -eq 0 ]; then
        break;
    fi
done


jarsDir="${SHELL_HOME}/jars"
sqlDir="${SHELL_HOME}/sql"

_spark_submit_bash=${SPARK_BASE}

shellNameLog=`echo ${shellName} | awk -F/ '{print $NF}' | awk -F. '{print $1}'`".log"
localLogDir=${LOG_BASE_DIR}/${rq}
logFile=${localLogDir}/${shellNameLog}

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

v_times=1
max_times=2
while [ true ];
do
    if [ ${v_times} -gt ${max_times} ];then
        break
        exit 2
    else
        echo "${v_times}"
        outLog "[INFO] 开始执行iyeeku数据处理【循环次数${v_times}】"
        #${_spark_submit_bash} --master yarn --class com.iyeeku.project.iyeeku.IyeekuMain --jars ${jarsDir}/iyeeku-spark-common-1.0.0.jar ${jarsDir}/task.jar -d ${rq} -f ${sqlDir}/iyeeku_batch.sql
        ${_spark_submit_bash} --class com.iyeeku.project.iyeeku.IyeekuMain --jars ${jarsDir}/iyeeku-spark-common-1.0.0.jar ${jarsDir}/task.jar -d ${rq} -f ${sqlDir}/iyeeku_batch.sql
        ret=$?
        if [ ${ret} -ne 0 ];then
            outLog "[ERROR] 开始执行iyeeku数据处理【循环次数${v_times}】失败！重新执行"
            #impala-shell -q "invalidate metadata"
        else
            outLog "[INFO] 开始执行iyeeku数据处理【循环次数${v_times}】成功！"
            #impala-shell -q "invalidate metadata"
            break
            exit 0
        fi
        v_times=`expr ${v_times} + 1`
    fi
done
