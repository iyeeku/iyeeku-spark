#!/usr/bin/env bash
. /oracle/shell/proc.ini

#定义参数
ctlFileName=$1
dataFileName=$2
user=$3
decPassWd=$4
today=$5
shellName=$6
forceSuccess=$7

ctlFile=${BASE}/ctl/${ctlFileName}
dataFile=${BASE}/data/in/${today}/${dataFileName}.dat
flagFile=${BASE}/data/in/${today}/${dataFileName}.flg
logFile=${BASE}/logs/${ctlFileName}.${today}.log
badFile=${BASE}/logs/${ctlFileName}.${today}.bad
shellPath=${BASE}/shell

#判断gz文件夹是否存在,存在就解压
if [ -f ${dataFileName}.gz ];then
    echo "gzip gzDataFile["${dataFileName}".gz]"
    gzip -f -d ${dataFileName}.gz
else
    echo "Not find gzDataFile["${dataFileName}".gz]"
fi

if [ -f ${dataFile} ];then
    echo "find dataFile["${dataFile}"]"
else
    echo "Not find dataFile["${dataFile}"]"
    exit 1
fi

if [ -f ${ctlFile} ];then
    echo "find ctlFile["${ctlFile}"]"
else
    echo "Not find ctlFile["${ctlFile}"]"
    exit 1
fi

startDate=`date '+%Y-%m-%d %H:%M:%S'`
echo "======================================================================================="
echo " Begin load data[${dataFile}][ExecuteTime:`date '+%Y%m%d%H%M%S'`][Business:${today}]"
echo "======================================================================================="

#使用sqlldr加载数据文件到数据库表中...
sqlldr userid=${user}/${decPassWd} control=${ctlFile} errors=500 data=${dataFile} log=${logFile} bad=${badFile} direct=true
if [ $? != 0 ];then
    #获取bad文件中的条数
    fileBadNo=`cat ${badFile} | wc -l`
    fileNum=`ls -l ${badFile} | awk '{print $9}' | wc -l`
    if [ ${fileNum} -gt 0 ];then
        #作业信息写入
        sqlplus -s ${user}/${decPassWd}<<EOF
            set feed off;
            set heading off;
            set feedback off;
            set verify off;
            ecec ORACLE_PROC_TRACE_BATCH();
            commit;
exit
EOF
    echo "sqlldr命令执行成功，数据文件[${dataFile}]中的部分数据有异常，请查看日志文件!"
    cd ${BASE}/logs && mv ${badFile} ${badFile}.`date '+%H%M%S'`
    fi
else
    echo "sqlldr命令执行成功！"
fi

#作业信息写入
sqlplus -s ${user}/${decPassWd}<<EOF
            set feed off;
            set heading off;
            set feedback off;
            set verify off;
            ecec ORACLE_PROC_TRACE_BATCH();
            commit;
exit
EOF