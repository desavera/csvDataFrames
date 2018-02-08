#!/bin/bash

source /root/.bashrc

rm -f /tmp/*.pid

# derby requires a PATH oriented procedure
cd $HIVE_HOME

echo "=== STARTING SSHD ==="
service ssh restart

echo "=== STARTING HADOOP ==="
$HADOOP_HOME/sbin/start-dfs.sh

echo "=== INITIALIZING PRICING DATABASE ==="
$HIVE_HOME/bin/schematool -dbType derby -initSchema

echo "=== STARTING ZEPPELIN ==="
cd /opt/zeppelin-0.6.2-bin-all
ln -s $HIVE_HOME/metastore_db metastore_db
/opt/zeppelin-0.6.2-bin-all/bin/zeppelin-daemon.sh start

echo "=== STARTING CRON ==="
service cron restart

if [[ $1 == "-d" ]]; then
  while true; do sleep 1000; done
fi

if [[ $1 == "-bash" ]]; then
  /bin/bash
fi
