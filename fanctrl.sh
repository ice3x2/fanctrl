#!/bin/bash

echo 'fanCtrl v{version}'
echo '2023. ice3x2@gmail.com'

PRG="$0"



while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '.*/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

PRGDIR=`dirname "$PRG"`
DIR_ROOT=`cd "$PRGDIR" ; pwd`
#if [$# -eq '0' -o $1 -ne 'stop' -a $1 -ne 'start']; then
if echo $1 | egrep -v "(start)|(stop)"
then
  echo $0 '<start|stop>'
  exit
fi

if echo $1 | egrep -q "stop"
then
pid=`cat .pid 2>&1`
ps -ef | grep $pid
kill $pid
exit
fi

if echo $1 | egrep -q "start"
then
nohup java -jar $DIR_ROOT"/fanCtrl-{version}.jar" $@ 1> /dev/null 2>&1 &
sleep 1
pid=`cat .pid 2>&1`
echo 'PID: '$pid
tail -f $DIR_ROOT/*.log
fi
