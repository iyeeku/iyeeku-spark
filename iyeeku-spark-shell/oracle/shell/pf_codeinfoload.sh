#!/usr/bin/env bash
. /oracle/shell/proc.ini

#定义参数
today=$1
ctlFileName=PF_CODEINFO
dataFileName=${ctlFileName}.${today}.000000.0000
shellName=$0

#获取时间
startDate=`date +'%Y-%m-%d %H:%M:%S'`
sh ${SHELL_PATH}/commonload.sh ${ctlFileName} ${dataFileName} ${USER} ${DEC_PASSWORD} ${today} ${shellName}


