#!/usr/bin/env bash

PROJECT_ROOT="/home/ubuntu/app"
JAR_FILE="$PROJECT_ROOT/application.jar"

DEPLOY_LOG="$PROJECT_ROOT/deploy.log"

TIME_NOW=$(date +%c)

CURRENT_PID=$(pgrep -f $JAR_FILE)

if [ -z $CURRENT_PID ]; then
  echo "$TIME_NOW > There is no running applications now." >> $DEPLOY_LOG
else
  kill -15 $CURRENT_PID
  echo "$TIME_NOW > $CURRENT_PID: application terminated. " >> $DEPLOY_LOG
fi