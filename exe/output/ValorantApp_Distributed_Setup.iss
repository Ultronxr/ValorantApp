; �ű��� Inno Setup �ű��� ���ɣ�
; �йش��� Inno Setup �ű��ļ�����ϸ��������İ����ĵ���

#define MyAppName "ValorantAppDistributed"
#define MyAppVersion "1.3"
#define MyAppPublisher "Ultronxr"
#define MyAppURL "https://github.com/Ultronxr/ValorantApp"
#define MyAppExeName "ValorantApp_jar.exe"
#define WorkPath "D:\AllFilesCode\workspace\IntelliJIDEA\ValorantApp\exe"

[Setup]
; ע: AppId��ֵΪ������ʶ��Ӧ�ó���
; ��ҪΪ������װ����ʹ����ͬ��AppIdֵ��
; (��Ҫ�����µ� GUID�����ڲ˵��е�� "����|���� GUID"��)
AppId={{23F85710-96D0-42BD-AB11-FEF0F3F782CD}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
;AppVerName={#MyAppName} {#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={autopf}\{#MyAppName}
DefaultGroupName={#MyAppName}
; DisableProgramGroupPage=yes
; ������ȡ��ע�ͣ����ڷǹ���װģʽ�����У���Ϊ��ǰ�û���װ����
;PrivilegesRequired=lowest
OutputBaseFilename=ValorantAppDistributedSetup
Compression=lzma
SolidCompression=yes
WizardStyle=modern

[Languages]
Name: "chinesesimp"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "{#WorkPath}\output\{#MyAppExeName}"; DestDir: "{app}"; Flags: ignoreversion
Source: "{#WorkPath}\openjdk-11.0.10+9-jre\*"; DestDir: "{app}\openjdk-11.0.10+9-jre"; Flags: ignoreversion recursesubdirs createallsubdirs
;Source: "{#WorkPath}\script\service_install.bat"; DestDir: "{app}\script"; Flags: ignoreversion recursesubdirs createallsubdirs
;Source: "{#WorkPath}\script\service_uninstall.bat"; DestDir: "{app}\script"; Flags: ignoreversion recursesubdirs createallsubdirs
; ע��: ��Ҫ���κι���ϵͳ�ļ���ʹ�á�Flags: ignoreversion��

[INI]

[Icons]
; ��ʼ�˵��ļ��У�����Ӧ�ó����ݷ�ʽ����������ӡ�����ж�ؿ�ݷ�ʽ
Name: "{group}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"
Name: "{group}\{cm:ProgramOnTheWeb,{#MyAppName}}"; Filename: "{#MyAppURL}"
Name: "{group}\{cm:UninstallProgram,{#MyAppName}}"; Filename: "{uninstallexe}"
; ������ͼ�꣺��ʼ�˵���ݷ�ʽ�������ݷ�ʽ
Name: "{autoprograms}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"
Name: "{autodesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon

[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(MyAppName, '&', '&&')}}"; Flags: nowait postinstall skipifsilent
;Filename: "{app}\script\service_install.bat"

[UninstallRun]
;Filename: "{app}\script\service_uninstall.bat"

[UninstallDelete]
Type:filesandordirs;Name:"{app}"
