@echo off

chcp 65001

set "MyAppExeName=ValorantApp_jar.exe"
set "ServiceName=ValorantAppDistributed"

net stop %ServiceName%
sc delete %ServiceName%
echo %ServiceName% 服务卸载完成。

@pause
