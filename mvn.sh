#!/bin/bash
source ./config.sh

# 获取当前工作目录的绝对路径
current_dir=$(pwd)

./gradlew clean build -x test
scp $current_dir/build/libs/$project_name-1.0-SNAPSHOT.jar root@$server_ip:$dir/$project_name/deploy

# 服务器免密登录 ssh test
# 执行服务器 mvn.sh
ssh test "cd $dir/$project_name && bash -s < $dir/$project_name/deploy.sh"