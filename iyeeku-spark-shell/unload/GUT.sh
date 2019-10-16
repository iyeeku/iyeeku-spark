#!/usr/bin/env bash
CURRENT_DIR=${PWD}
cd ..
GUT_HOME=${PWD}
PROCESS_PATH=${GUT_HOME}
JRE_HOME=${GUT_HOME}/jre
cd ${CURRENT_DIR}

[ -x ${JRE_HOME}/bin/java ]
if [[ ${?} == 1 ]]; then
    chmod 766 ${JRE_HOME}/bin/java
fi

TEMP=`ps -ef | grep DGUT_HOME=${GUT_HOME} | grep -v grep`

echo ${TEMP}

if [[ $TEMP == "" ]]; then
    export CLASSPATH=.:${JRE_HOME}/lib/rt.jar:${JRE_HOME}/lib/tool.jar:${GUT_HOME}/lib/castor-1.2.jar:${GUT_HOME}/lib/commons-beanutils-1.7.0.jar:${GUT_HOME}/lib/commons-codec-1.10.jar:${GUT_HOME}/lib/commons-logging-1.0.4.jar:${GUT_HOME}/lib/dom4j-1.6.1.jar:${GUT_HOME}/lib/log4j-1.2.8.jar:${GUT_HOME}/lib/iyeeku-unload-1.0.0.jar:${GUT_HOME}/lib/mysql-connector-java-5.1.32.jar:${GUT_HOME}/lib/ojdbc6-11.2.0.1.0.jar:${GUT_HOME}/lib/xercesImpl-2.11.0.jar:${GUT_HOME}/lib/xml-apis-1.4.01.jar
    JAVA_OPTS="-Xms512M -Xmx1024M -XX:MetaspaceSize=80m -XX:MaxMetaspaceSize=100m"
    ${JRE_HOME}/bin/java ${JAVA_OPTS} -DGUT_HOME=${GUT_HOME} -DPROCESS_PATH=${PROCESS_PATH} com.iyeeku.gut.main.GUT $1 $2 $3 $4 $5 $6 $7 $8 $9 ${10}
else
    echo "This GUT is running! Only one instance can be started at one time!"
fi