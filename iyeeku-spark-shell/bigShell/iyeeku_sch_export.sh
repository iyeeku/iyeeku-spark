#!/usr/bin/env bash

SHELL_HOME="/home/shell"
. ${SHELL_HOME}/conf/conf.d

##日期
#读取脚本参数 解压缩文件
rq=
tableName=
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
    "-t")
        shift 1
        tableName=$1
        if [ ${tableName} == "" ]; then
            echo -e "ERROR\t tableName 不能为空"
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

lastDay=${rq}
iyeekuResultSql=
iyeekuResultExportFieldSetting=
iyeekuResultPrefix=${tableName}
localDataDir=${LOCAL_BASE_DIR_OUT}/${lastDay}

if [ ${iyeekuResultPrefix} == "PF_CODEINFO" ];then
    iyeekuResultSql="select zj from iyeeku.pf_codeinfo"
    iyeekuResultExportFieldSetting="zj=32"
fi

dataFile="${iyeekuResultPrefix}.${lastDay}.000000.0000.dat.gz"
flagFile="${iyeekuResultPrefix}.${lastDay}.flg"

sh ${SHELL_HOME}/exportHiveTableToFixed.sh -sql "${iyeekuResultSql}" -t "${iyeekuResultPrefix}" -d "${lastDay}" -c "${iyeekuResultExportFieldSetting}"
if [ $? -ne 0 ];then
    outLog "[ERROR] 导出数据文件到本地${localDataDir}/${dataFile},${flagFile}失败!"
    exit 1
fi

exit 0
