@echo off

chcp 65001

echo 正在更新数据库 valorant_dev

mysql -uroot -proot -h127.0.0.1 -P3306 valorant_dev < schema_update_20230425.sql

echo 更新数据库 valorant_dev 完成。

@pause

