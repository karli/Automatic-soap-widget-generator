#!/bin/sh
NAME=soapproxy
PID=`cat ${NAME}.pid`
PS=`ps -p $PID | grep $PID | awk '{ print $1 }' | head -1`
if [ "$PS" = "" ]
then
  echo ${NAME} not running
else
  kill `cat ${NAME}.pid`
  echo ${NAME} stopped
fi

