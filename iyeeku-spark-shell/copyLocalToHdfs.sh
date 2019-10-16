#!/usr/bin/env bash
SHELL_HOME="/home/shell"
. ${SHELL_HOME}/conf/conf.d

if [ $# -ne 4 -o "$1" != "-f" -o "$3" != "-d" ]; then
    echo "Usage: $0 -f filePrefix -d dataDate"
    exit 1
fi

#读取脚本参数 文件名前缀 数据日期
filePrefix=$2
dataDate=$4
shellName=$0
shellNameLog=`echo ${shellName} | awk -F/ '{print $NF}' | awk -F. '{print $1}'`".${filePrefix}.log"

##对应月份 YYYYMM 本地数据目录、本地日志、HDFS目录、本地文件
yyyyMM=`echo ${dataDate} | cut -c 1-6`
localDataDir=${LOCAL_BASE_DIR_IN}/${dataDate}
localLogDir=${LOG_BASE_DIR}/${dataDate}
logFile=${localLogDir}/${shellNameLog}
hdfsDataDir=${HDFS_BASE_DIR_IN}/${filePrefix}/${yyyyMM}
jarsDir=${SHELL_HOME}/jars

if [ ! -d ${localDataDir} ]; then
    mkdir -p ${localDataDir}
fi

if [ ! -d ${localLogDir} ]; then
    mkdir -p ${localLogDir}
fi

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

##搜索对应的数据文件及标识文件、若数据文件及标识文件不存在的话、退出
localDataGzFileCount=`ls ${localDataDir} | grep ^${filePrefix}".${dataDate}.*.gz" | wc -l`
localFlgFileCount=`ls ${localDataDir} | grep ^${filePrefix}".${dataDate}.*.flg" | wc -l`

if [ ${localDataGzFileCount} -eq 1 -a ${localFlgFileCount} -eq 1 ]; then
    outLog "[INFO] yes : ${localDataDir} has ${filePrefix} one"
else
    outLog "[ERROR] no : ${localDataDir} no only one ${filePrefix}"
    exit 1
fi

localDataGzFile=`ls ${localDataDir} | grep ^${filePrefix}".${dataDate}.*.gz"`
localFlgFile=`ls ${localDataDir} | grep ^${filePrefix}".${dataDate}.*.flg"`
localDataFile=`echo ${localDataGzFile} | sed "s/.gz//g"`




outLog "[INFO ] -------------------------------------------------------------------------------------------------------"

################################ hdfs 创建目录 ##############################
outLog "[INFO ] mkdir hdfs data dir:${hdfsDataDir}"
hdfs dfs -mkdir -p ${hdfsDataDir}

cd ${localDataDir}


################################ 上传压缩数据文件 #############################
outLog "[INFO ] start copy data file:${localDataGzFile} to HDFS:${hdfsDataDir}"
iCount=1
dataGzUploadFlg="1"
while [ ${dataGzUploadFlg} == "1" -a ${iCount} -le ${MAX_RETRY_TIMES} ]
do
    hdfs dfs -copyFromLocal -f ${localDataGzFile} ${hdfsDataDir}
    ret=$?
    if [[ ${ret} -eq 0 ]]; then
        dataGzUploadFlg="0"
    else
        iCount=`expr ${iCount} + 1`
    fi
done

################################ 检查hdfs中是否存在data文件 ###########################
hdfs dfs -test -f ${hdfsDataDir}/${localDataGzFile}
if [ $? -eq 0 ]; then
    dataGzUploadFlg="0"
else
    outLog "[ERROR ] copy data file:${localDataGzFile} to HDFS failed after ${iCount} times"
    exit 1
fi

################################ 上传flg文件 #############################
outLog "[INFO ] start copy flg file:${localFlgFile} to HDFS:${hdfsDataDir}"
iCount=1
flagUploadFlg="1"
while [ ${flagUploadFlg} == "1" -a ${iCount} -le ${MAX_RETRY_TIMES} ]
do
    hdfs dfs -copyFromLocal -f ${localFlgFile} ${hdfsDataDir}
    ret=$?
    if [[ ${ret} -eq 0 ]]; then
        flagUploadFlg="0"
    else
        iCount=`expr ${iCount} + 1`
    fi
done

################################ 检查hdfs中是否存在flg文件 ###########################
hdfs dfs -test -f ${hdfsDataDir}/${localFlgFile}
if [ $? -eq 0 ]; then
    flagUploadFlg="0"
else
    outLog "[ERROR ] copy flg file:${localFlgFile} to HDFS failed after ${iCount} times"
    exit 1
fi

exit 0
