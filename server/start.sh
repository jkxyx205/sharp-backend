#!/bin/bash
# 等待 MySQL 就绪
echo "Waiting for MySQL..."
until mysqladmin ping -u root -p123456 --silent; do
    sleep 2
done
echo "MySQL is ready!"

# 先切换到程序目录
#cd $dir/$project_name
cd "$(dirname "$0")"
source ./config.sh

nohup java -Xms512m -Xmx1024m -Dspring.profiles.active=prod -jar $project_name-1.0-SNAPSHOT.jar --server.port=$port &

echo "Started! PID: $!"
