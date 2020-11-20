#!/usr/bin/env bash
SHELL_HOME="/home/shell"
. ${SHELL_HOME}/conf/conf.d

##日期
#读取脚本参数 解压缩文件
rq=
tableName=
partitions=50
batchSize=500
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
    "-p")
        shift 1
        partitions=$1
        shift 1
        ;;
    "-b")
        shift 1
        batchSize=$1
        shift 1
        ;;
    *)
        echo "Usage: ${shellName} -d date -t tableName [ -p partitions | -b batchSize]"
        echo -e "ERROR\t ${shellName}调用错误"
        exit 1
        ;;
    esac

    if [ $# -eq 0 ]; then
        break;
    fi
done

shellName=$0
localLogDir=${LOG_BASE_DIR}/${rq}
shellNameLog=`echo ${shellName} | awk -F/ '{print $NF}' | awk -F. '{print $1}'`".log"
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

result_sql=
result_fieldSetting=
result_prefix=${tableName}

if [ ${result_prefix} == "CCRM_BG_DYXTSXXB" ]; then
    result_sql="select branch_id as branch_no,push_type,regexp_replace(format_number(cust_total,0),',','') as cust_total,regexp_replace(format_number(amount,2),',','') as amount from iyeeku.view_dyxtsxxb"
    result_fieldSetting="branch_no=32,push_type=1,cust_total=32,amount=32"
fi

if [ ${result_prefix} == "PF_CODEINFO_BAK" ]; then
    result_sql="select zj,mblxbh,mbtmz,sjlx,mbtmms,sjsxsy,gjdbm,fjdbm,bmjb,qlj,sfkj,sjsygjhxx,jlzt,kzzd from iyeeku.pf_codeinfo"
    result_fieldSetting="zj=32,mblxbh=64,mbtmz=255,sjlx=32,mbtmms=255,sjsxsy=8,gjdbm=32,fjdbm=32,bmjb=4,qlj=255,sfkj=1,sjsygjhxx=20,jlzt=1,kzzd=64"
fi

if [[ ${result_sql} == "" ]]; then
    outLog "[ERROR] TABLE_PREFIX 错误"
    exit 2
fi

sh ${SHELL_HOME}/loadFromHiveToDb.sh -sql "${result_sql}" -t "${result_prefix}" -d "${rq}" -c "${result_fieldSetting}" -p "${partitions}" -b "${batchSize}"
if [ $? -ne 0 ]; then
    outLog "[ERROR] 导出Hive数据到关系型数据库失败"
    exit 2
fi

exit 0
