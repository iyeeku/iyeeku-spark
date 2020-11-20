#!/usr/bin/env bash
SHELL=/bin/sbin
export SHELL

typeset EXEC_PATH=/weblogic/work/shell
typeset sLogFile=${EXEC_PATH}/log/stopApp.log
check_try_max_time=3

typeset CONF_FILE="/weblogic/work/shell/config.ini"
typeset sTmpConfigFile="setConfig.sh"
sed -n "1,$p" ${CONF_FILE} | grep -v '#' > ${sTmpConfigFile}
chmod +x ${sTmpConfigFile}
. ./${sTmpConfigFile}
rm ${sTmpConfigFile}

log()
{
    typeset iTime
    iTime=`date +"%Y-%m-%d %H:%M:%S"`
    if [[ -z "${sLogFile}" ]]; then
        return 1
    else
        typeset iLevel=$2
        if [[ ""  = "${iLevel}" ]]; then
            echo "${iTime}-[STOPAPP]-[INFO]-$1" | tee -a ${sLogFile}
        else
            echo "${iTime}-[STOPAPP]-[${iLevel}]-$1" | tee -a ${sLogFile}
        fi
        return 0
    fi
}

checkNodeWithDeal(){
    servername=$1
    if [ -n ${servername} ]; then
        if [ ${servername} = "appms3" -o ${servername} = "appms4" -o ${servername} = "appms6" ]; then
            pname=`ps -ef | grep ${servername} | grep -v 'grep' | awk '{print $2}'`
            for name in ${pname}
            do
                log "${servername}节点出现进程僵死... , 即将杀死该节点进程..."
                log "kill ${name}"
                kill -9 ${name} >> ${sLogFile}
                sleep 150
                java -cp ${WLS_HOME}/server/lib/weblogic.jar weblogic.Admin -url t3://${ADMIN_SERVER_IP}:${ADMIN_SERVER_PORT} -username ${WLS_USERNAME} -password ${WLS_PASSWORD} FORCESHUTDOWN ${servername} >> ${sLogFile}
            done
        fi
        if [ ${servername} = "appms1" -o ${servername} = "appms2" -o ${servername} = "appms5" ]; then
            pname=`ps -ef | grep ${servername} | grep -v 'grep' | awk '{print $2}'`
            for name in ${pname}
            do
                log "${servername}节点出现进程僵死... , 即将杀死该节点进程..."
                log "kill ${name}"
                ssh weblogic@IYEEKU-PRO-BAK kill -9 ${name} >> ${sLogFile}
                sleep 150
                java -cp ${WLS_HOME}/server/lib/weblogic.jar weblogic.Admin -url t3://${ADMIN_SERVER_IP}:${ADMIN_SERVER_PORT} -username ${WLS_USERNAME} -password ${WLS_PASSWORD} FORCESHUTDOWN ${servername} >> ${sLogFile}
            done
        fi
    fi
}

#清理和备份日志
if [ -f ${sLogFile} ]; then
    rm ${sLogFile}
fi

log ""
log "开始停止应用服务器..."
typeset count=0
typeset iRet=1
while [ $iRet -ne 0 -a ${count} -lt ${maxTimes} ]
do
    log "开始停止weblogic集群..."
    java -cp ${WLS_HOME}/server/lib/weblogic.jar weblogic.Admin -url t3://${ADMIN_SERVER_IP}:${ADMIN_SERVER_PORT} -username ${WLS_USERNAME} -password ${WLS_PASSWORD} STOPCLUSTER -clusterName Cluster
    iRet=$?
    log "weblogic停集群结构返回值${iRet}"
    count=`expr ${count} + 1`

    log "开始查询是否存在僵死节点并处理（检查3次）..."
    check_try_time=1
    while [ ${check_try_time} -le ${check_try_max_time} ]; do
        log "僵死节点检测处理: 第${check_try_time}次尝试"
        checkNodeWithDeal "appms1"
        checkNodeWithDeal "appms2"
        checkNodeWithDeal "appms3"
        checkNodeWithDeal "appms4"
        checkNodeWithDeal "appms5"
        checkNodeWithDeal "appms6"
        sleep 10
        check_try_max_time=`expr ${check_try_max_time} + 1`
    done

    if [ ${iRet -eq 1} ]; then
        if [ ${count} -lt 5 ]; then
            log "停止应用服务器失败，10秒继续尝试停止!"
            sleep 10
        else
            log "尝试停止应用服务器失败超过5次，将强制Kill应用服务器进程！"
        fi
    else
        log "停止应用服务器成功！"
    fi
done

log "停止应用服务器结束!"
exit 0
