#!/bin/sh
NAME=soapproxy
PID=`cat ${NAME}.pid`
PS=`ps -p $PID | grep $PID | awk '{ print $1 }' | head -1`
if [ "$PS" = "" ]
then

  echo Starting ${NAME}
  JAVA="java -server -Xmx256m"
  JAVA_OPTS="-Djetty.port=8833"
  JARS=`find war/WEB-INF/lib -name '*.jar' | gawk 'BEGIN { ORS=":"; } { print $0 }'`

  $JAVA $JAVA_OPTS -classpath war/WEB-INF/classes:${JARS}${JAVA_HOME}/lib/tools.jar Launcher 1>> logs/${NAME}.out 2>&1 &
  echo $! > ${NAME}.pid

  echo ${NAME} started
else
  echo ${NAME} already running
fi
