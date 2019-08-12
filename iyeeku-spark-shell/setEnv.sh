#!/usr/bin/env bash
#export LANG=zh_CN
export hs=`hostname`

export WORK_HOME=/weblogic
export JAVA_HOME=/usr/java/jdk1.8.0_152
export BEA_HOME=/weblogic/weblogic1213
export WLS_HOME=${BEA_HOME}/wlserver
export WL_HOME=${BEA_HOME}/wlserver
export PATH=${JAVA_HOME}/bin:${WLS_HOME}/server/bin:${WORK_HOME}/script:${PATH}
export CLASSPATH=${JAVA_HOME}/lib/tools.jar:${WLS_HOME}/server/lib/weblogic.jar

export DOMAIN_HOME=${BEA_HOME}/appdomain
export ADMIN_SERVER_IP=`hostname`
export ADMIN_SERVER_PORT=7200
export WLS_USERNAME=weblogic
export WLS_PASSWORD=weblogic123

alias l='ls -alrt'

#启动管理器节点
alias startadm='cd ${DOMAIN_HOME};nohup sh startWebLogic.sh > "/dev/null" 2>&1 &'

#启动NodeManager
alias startnm='cd ${WLS_HOME}/server/bin;nohup sh startNodeManager.sh > "/dev/null" 2>&1 &'

#停止管理服务器
alias stopadm='java weblogic.Admin -url t3://${ADMIN_SERVER_IP}:${ADMIN_SERVER_PORT} -username ${WLS_USERNAME} -password ${WLS_PASSWORD} FORCESHUTDOWN'




