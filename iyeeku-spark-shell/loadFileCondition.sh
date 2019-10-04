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
loadMainClass=com.iyeeku.spark.example.LoadHiveDB
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




#spark-submit --name "com.iyeeku.spark.example.LoadHiveDB-IYEEKU_TEST_HX_JYWLXXWJ" --class com.iyeeku.spark.example.LoadHiveDB --conf spark.app.conf=/user/iyeeku/cfg/base.cfg /home/shell/iyeeku-spark-common-1.0.0.jar -f IYEEKU_TEST_HX_JYWLXXWJ -d 20191004 -t IYEEKU_TEST_HX_JYWLXXWJ

