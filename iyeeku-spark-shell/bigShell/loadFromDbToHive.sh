#!/usr/bin/env bash
SHELL_HOME="/home/shell"
. ${SHELL_HOME}/conf/conf.d

#读取脚本参数, 解压缩文件
srcTableName=
targeTableName=
rq=
shellName=$0
while [ 1 ]
do
    case $1 in
    "-src")
        shift 1
        srcTableName=$1
        if [ ${srcTableName} == "" ]; then
            echo -e "ERROR\t srcTableName 不能为空"
            exit 1
        else
            shift 1
        fi
        ;;
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
    "-tgt")
        shift 1
        targeTableName=$1
        if [ ${targeTableName} == "" ]; then
            echo -e "ERROR\t targeTableName 不能为空"
            exit 1
        else
            shift 1
        fi
        ;;
    *)
        echo "Usage: ${0} -src srcTableName -tgt targeTableName -d date"
        echo -e "ERROR\t ${shellName}调用错误"
        exit 1
        ;;
    esac

    if [ $# -eq 0 ]; then
        break;
    fi
done

shellName=$0
spark_submit_base=${SPARK_BASE}
shellNameLog=`echo ${shellName} | awk -F/ '{print $NF}' | wak -F. '{print $1}'`".${srcTableName}-${targeTableName}.log"

##处理tableName，先转成小写，判读表名中是否存在字符，若存在则将如下转换 iyeeku.test => iyeeku.db/test/
tableRealPath=${HDFS_WAREHOUSE_DIR}
tableNameToLower=`echo ${targeTableName} | awk '{print tolower($0)}'`
ifDefaultDatabase=`echo ${tableNameToLower} | grep '\.' | wc -l`
if [ ${ifDefaultDatabase} == 1 ]; then
    v=`echo ${tableNameToLower} | awk -F. '{print $1".db/"$2}'`
    tableRealPath="${HDFS_WAREHOUSE_DIR}/${v}"
else
    tableRealPath=${HDFS_WAREHOUSE_DIR}"/"${tableNameToLower}
fi

dataDate=${rq}
yyyymm=`echo ${dataDate} | cut -c 1-6`
localLogDir=${LOG_BASE_DIR}/${dataDate}
logFile=${localLogDir}/${shellNameLog}
URL="jdbc:oracle:thin:@${IYEEKU_DB_HOST}:1521:${IYEEKU_DB_SID}"

if [ ! -d "${localLogDir}" ]; then
    mkdir -p "${localLogDir}"
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

outLog "[INFO] -------------------从${srcTableName}加载到${targeTableName}----------------"
appName="loadTable_${srcTableName}->${targeTableName}"

outLog "[INFO] start load data file:${srcTableName} to ${targeTableName}"

##首先删除 _SUCESS文件
hdfs dfs -rm -f ${tableRealPath}/_SUCCESS

${spark_submit_base} --master yarn --name ${appName} --deploy-mode cluster --jars ${DRIVER_JARS} --class ${LOAD_DB_MAIN_CLASS} ${LOAD_MAIN_CLASS_JAR} -driver ${DRIVER_CLASS} -url ${URL} -user ${IYEEKU_DB_USER} -password ${IYEEKU_DB_PASSWORD} -table "${srcTableName}" -hivetable ${targeTableName} -d ${dataDate}
ret=$?

#判断写入的表是否存在（看其下对应的hdfs中是否存在 _SUCCESS 文件）
hdfs dfs -test -f ${tableRealPath}/_SUCCESS
fileExist=$?
if [ ${ret} -eq 0 -a ${fileExist} -eq 0 ]; then
    outLog "[INFO] succedd finish load data file:${srcTableName} to ${targeTableName}"
    exit 0
else
    outLog "[ERROR] hdfs data file:${srcTableName} not exists!"
    exit 2
fi
