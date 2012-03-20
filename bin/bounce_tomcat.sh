#!/bin/bash

USERNAME=`whoami`
CURPATH=`dirname $0`

if [ "${CURPATH:0:1}" != "/" ] ; then
    CURPATH=`pwd`/`dirname $0`
fi

JPDA_ADDRESS=8001
JAVA_HOME=/usr/java6
JAVA_OPTS="$JAVA_OPTS -Xmx1024m -Xms256m -Djava.awt.headless=true -Dfile.encoding=UTF-8 "
CATALINA_HOME=/usr/local/tomcat
CATALINA_BASE=$CURPATH/../tomcat

export JAVA_HOME
export JAVA_OPTS
export CATALINA_HOME
export CATALINA_BASE
export JPDA_ADDRESS

# clear out work directory to make sure that JSPs are accurate
if [ "$1" = "start" ] ; then
    echo "clearing ${CATALINA_BASE}/work/Catalina directory"
    rm -fr "${CATALINA_BASE}/work/Catalina"
fi

exec "$CATALINA_HOME/bin/catalina.sh" "$@"
