#!/usr/bin/env bash
SHELL_HOME="/home/shell"
. ${SHELL_HOME}/conf/conf.d

##日期
#读取脚本参数 解压缩文件
filePrefix=
rq=
tableName=
shellName=$0
##################################参数赋值##############################
while [ 1 ]
do
    case $1 in
    "-f")
        shift 1
        filePrefix=$1
        if [ ${filePrefix} == "" ]; then
            echo "ERROR\t filePrefix 不能为空"
            exit 1
        else
            shift 1
        fi
        ;;
    "-d")
        shift 1
        rq=$1
        if [ ${rq} == "" ]; then
            echo "ERROR\t rq 不能为空"
            exit 1
        else
            shift 1
        fi
        ;;
    "-t")
        shift 1
        tableName=$1
        if [ ${tableName} == "" ]; then
            echo "ERROR\t tableName 不能为空"
            exit 1
        else
            shift 1
        fi
        ;;
    *)
        usage
        echo "ERROR\t ${shellName}调用错误"
        exit 1
        ;;
    esac

    if [ $# -eq 0 ]; then
        break;
    fi
done

sleepTime=10

##部署模式和历史表、配置文件在hdfs的位置
deployMode="cluster"
hisTableName="${tableName}__HIS"
appHdfsConf=/user/iyeeku/cfg/base.cfg
loadMainClass=com.iyeeku.spark.common.LoadHiveDB
jarsDir="${SHELL_HOME}/jars"
sqlDir="${SHELL_HOME}/sql"

shellNameLog=`echo ${shellName} | awk -F/ '{print $NF}' | awk -F. '{print $1}'`".log"
localLogDir=${LOG_BASE_DIR}/${rq}
logFile=${localLogDir}/${shellNameLog}
localDataDir=${LOCAL_BASE_DIR_IN}/${rq}

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
outLog "[INFO] 开始加载数据文件--------------------${filePrefix} ${rq}---------------------------------"


##搜索对应的数据文件及标识文件，若数据文件及标识文件不存在的话，循环
while [ true ];
do
    fileFlg=`file_condition ${filePrefix} ${rq}`

    if [ ${fileFlg} == "1" ]; then
        outLog "[INFO] 文件${filePrefix} 已经到达，数据进行处理!"
        break
    else
        outLog "[INFO] 文件${filePrefix} 未到达，等待进行处理!"
    fi

    sleep ${sleepTime}
done

#清理目录下的文件
outLog "[INFO] 清理目录下的历史文件 ${filePrefix}"

#解压缩本地文件，并上传到hdfs中
outLog "[INFO] 解压缩本地文件 ${filePrefix} ,并把文件上传到hdfs中"
sh ${SHELL_HOME}/copyLocalToHdfs.sh -f ${filePrefix} -d ${rq}
if [ $? -ne 0 ]; then
    outLog "[ERROR] 解压缩本地文件 ${filePrefix} ,并把文件上传到hdfs中失败!"
    exit 1
fi

##将压缩文件上传hdfs，解压缩文件，将文件加载到临时表中，同时将数据加载到历史分区表
spark-submit --name "${loadMainClass}-${tableName}" --class com.iyeeku.spark.common.LoadHiveDB --conf spark.app.conf=${appHdfsConf} --deploy-mode ${deployMode} ${jarsDir}/iyeeku-spark-common-1.0.0.jar -f ${filePrefix} -d ${rq} -t ${tableName}
if [ $? -ne 0 ]; then
    outLog "[ERROR] 加载 ${filePrefix}文件到hive表中 ${tableName}失败!"
    exit 1
fi

#刷新impala原数据和历史表
#impala-shell -q "invalidate metadata ${tableName}" > /dev/null
#impala-shell -q "invalidate metadata ${hisTableName}" > /dev/null

exit 0
