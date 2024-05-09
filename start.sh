#!/bin/bash

# move jar file
mv /var/lib/jenkins/workspace/payment/target/Payments-0.0.1-SNAPSHOT.jar /svc/payment/payment.jar

#search
process_ids=($(ps -ef | grep 'payment' | awk '{print $2}'))

if [ "${#process_ids[@]}" -gt 0 ]; then
  for process_id in "${process_ids[@]}"; do
  sudo kill -9 "$process_id"
  echo "$process_id Process kill "
  done
  echo "starting now"
fi

export JAVA_HOME=/app/jdk-21.0.2
export PATH=$JAVA_HOME/bin:$PATH
# start
nohup java -Xms8192m -Xmx8192m -jar -Dspring.config.location=file:/svc/payment/config/application.yml /svc/payment/payment.jar > /svc/payment/log/consoleLog.log 2>&1 &
