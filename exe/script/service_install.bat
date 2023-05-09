@echo off

chcp 65001

set "MyAppExeName=ValorantApp_jar.exe"
set "ServiceName=ValorantAppDistributed"

cd /d %~dp0
cd ..
sc create %ServiceName% binpath= "%cd%\%MyAppExeName%"
sc config %ServiceName% start=auto
net start %ServiceName%
echo %ServiceName% 服务安装完成。

@pause
