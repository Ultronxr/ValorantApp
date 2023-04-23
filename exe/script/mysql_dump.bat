@echo off

chcp 65001

echo 正在导出数据库 valorant_dev

mysqldump -uroot -proot -h127.0.0.1 -P3306 valorant_dev > .\valorant_dev.db

echo 导出数据库 valorant_dev 完成。

@pause

