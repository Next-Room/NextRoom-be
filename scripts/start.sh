#!/usr/bin/env bash

PROJECT_ROOT="/home/ubuntu/app"
JAR_FILE="$PROJECT_ROOT/application.jar"

APP_LOG="$PROJECT_ROOT/application.log"
ERROR_LOG="$PROJECT_ROOT/error.log"
DEPLOY_LOG="$PROJECT_ROOT/deploy.log"
SPRING_PROFILES_ACTIVE=$(aws ssm get-parameters --output text --region ap-northeast-2 --names SPRING_PROFILES_ACTIVE --query Parameters[0].Value)

TIME_NOW=$(date +%c)

echo "$TIME_NOW > $JAR_FILE file copied." >> $DEPLOY_LOG
cp $PROJECT_ROOT/build/libs/*.jar $JAR_FILE

echo "$TIME_NOW > $JAR_FILE running application." >> $DEPLOY_LOG
nohup java -jar -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE $JAR_FILE > $APP_LOG 2> $ERROR_LOG &

CURRENT_PID=$(pgrep -f $JAR_FILE)
echo "$TIME_NOW > running process: $CURRENT_PID" >> $DEPLOY_LOG
