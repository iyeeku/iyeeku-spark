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
    if [ -z "${sLogFile}" ]; then
        return 1
    else
        typeset iLevel=$2
        if [ ""  = "${iLevel}" ]; then
            echo "${iTime}-[STOPAPP]-[INFO]-$1" | tee -a ${sLogFile}
        else
            echo "${iTime}-[STOPAPP]-[${iLevel}]-$1" | tee -a ${sLogFile}
        fi
        return 0
    fi
}





