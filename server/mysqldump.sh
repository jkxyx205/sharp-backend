#!/bin/bash
SCRIPT_DIR=$(cd $(dirname ${BASH_SOURCE[0]}); pwd)
source $SCRIPT_DIR/config.sh
# db info
username="root"
password="123456"
database="timeline"
# backup folder
mysqldump="mysqldump"

echo "---------------------------------------------------" >> $project_path/$mysqldump/dbBackLog.log
echo $(date +"%Y-%m-%d %H:%M:%S") "Database backup start"  >> $project_path/$mysqldump/dbBackLog.log

mysqldump -u$username -p$password --single-transaction --databases $database > $project_path/$mysqldump/$database-$(date +"%Y-%m-%d").sql

if [ 0 -eq $? ];then

    if [ -f "$project_path/$mysqldump/backup-$(date +"%Y-%m-%d").sql" ];then
        echo $(date +"%Y-%m-%d %H:%M:%S") "Database backup success!" >> $project_path/$mysqldump/dbBackLog.log
    else
        echo $(date +"%Y-%m-%d %H:%M:%S") "Database backup fail!" >> $project_path/$mysqldump/dbBackLog.log
    fi

else
    echo $(date +"%Y-%m-%d %H:%M:%S") "Database backup error!" >> $project_path/$mysqldump/dbBackLog.log

fi

echo "---------------------------------------------------" >> $project_path/$mysqldump/dbBackLog.log
find $project_path/$mysqldump/ -mtime +7 -name "${database}-*.sql" -exec rm -rf {} \;
