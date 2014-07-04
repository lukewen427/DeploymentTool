@echo off
setlocal EnableDelayedExpansion

if [%1]==[-dy] (
  set DEBUG=-agentlib:jdwp=transport=dt_socket,address=5004,server=y,suspend=y
) else if [%1]==[-d] (
  set DEBUG=-agentlib:jdwp=transport=dt_socket,address=5004,server=y,suspend=n
) else (
  set DEBUG=
)

set DIR=%~dp0..\lib
FOR /F %%P IN ('dir /B %DIR%\*.jar') DO (
  set CP=!CP!;%DIR%\%%P
)

set LOGCONF=-Dlog4j.configuration=enginedebug.properties
rem set LOGCONF=-Dlog4j.configuration=file:///%~dp0..\src\main\resources\log4j-debug.properties 

rem pushd %DIR%
java %DEBUG% %LOGCONF% -Djava.library.path=%~dp0lib -cp %CP% com.connexience.server.workflow.cloud.CloudWorkflowEngine
rem popd %DIR%