#!/usr/bin/env bash
. /oracle/shell/proc.ini

num=`ls -l /oracle/uploadfiles/spdownload/ | wc -l`

if [ ${num} -gt 1 ]; then
    rm -rf /oracle/uploadfiles/spdownload/*
fi

getFileNameByDB(){
    unset filename
    filename=`sqlplus -s ${USER}/${DEC_PASSWORD}<<EOF
    set heading off
    set echo off
    set feedback off
    set term off
    select xxx from pf_codeinfo;
    exit
EOF`
    if [ $? != 0 ]; then
        echo "数据库连接错误!"
        filename="pjcmx"
        exit 1
    fi
}

getALLCodeInfoSpool(){
sqlplus -s ${USER}/${DEC_PASSWORD}<<EOF
set colsep'';
set echo off;
set trimout on;
set trimspool on;
set linesize 1000;
set pagesize 0;
set heading off;
set term off;
set termout off;
set feedback off;

spool /oracle/uploadfiles/spdownload/pf_codeinfo.csv
@/oracle/sql/pf_codeinfo.sql
spool off
exit
EOF
}


getALLCodeInfoSpoolByID(){

i=${1}
filename=${2}
sqlplus -s ${USER}/${DEC_PASSWORD}<<EOF
set colsep'';
set echo off;
set trimout on;
set trimspool on;
set linesize 1000;
set pagesize 0;
set heading off;
set term off;
set termout off;
set feedback off;

spool /oracle/uploadfiles/spdownload/${filename}.csv
@/oracle/sql/pf_codeinfo.sql ${i}
spool off
exit
EOF
}

getFileNameByDB
getALLCodeInfoSpool
getALLCodeInfoSpoolByID 123456

