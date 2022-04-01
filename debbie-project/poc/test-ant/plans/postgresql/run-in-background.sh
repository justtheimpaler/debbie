#!/bin/bash
LOGS_DIR=./all-logs
LOG_LINK=./log.txt
 
if [ ! -d "$LOGS_DIR" ]; then
  mkdir "$LOGS_DIR"
fi
TIME_ID=`date +%Y%m%d-%H%M%S`
 
LOG_FILE=${LOGS_DIR}/log-${TIME_ID}.txt
touch "$LOG_FILE" 
 
if [ -f "$LOG_LINK" ]; then
  rm "$LOG_LINK"
fi
ln -s "$LOG_FILE" "$LOG_LINK"
nohup ant < /dev/null > $LOG_FILE 2>&1 &
 
echo "[ Tailing log file $LOG_FILE ]"
tail -f "$LOG_FILE"
