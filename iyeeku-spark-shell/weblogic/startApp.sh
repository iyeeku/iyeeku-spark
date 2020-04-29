#!/usr/bin/env bash
SHELL=/bin/sbin
export SHELL

typeset EXEC_PATH=/weblogic/work/shell
typeset sLogPath=${EXEC_PATH}/log/
typeset mainLogFile=${EXEC_PATH}/log/startApp.log
typeset sLogFile=startApp.log
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
    if [[ -z "${mainLogFile}" ]]; then
        return 1
    else
        typeset iLevel=$2
        if [[ ""  = "${iLevel}" ]]; then
            echo "${iTime}-[STARTAPP]-[INFO]-$1" | tee -a ${mainLogFile}
        else
            echo "${iTime}-[STARTAPP]-[${iLevel}]-$1" | tee -a ${mainLogFile}
        fi
        return 0
    fi
}

#清理和备份日志
if [ -f ${sLogFile} ]; then
    rm ${sLogFile}
fi

log ""
log "开始停启动应用服务程序..."

java -cp ${WLS_HOME}/server/lib/weblogic.jar weblogic.Admin -url t3://${ADMIN_SERVER_IP}:${ADMIN_SERVER_PORT} -username ${WLS_USERNAME} -password ${WLS_PASSWORD} STARTCLUSTER -clusterName Cluster

log "启动应用服务器成功!"
exit 0
