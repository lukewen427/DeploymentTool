@echo off
setlocal EnableDelayedExpansion

if [%1]==[-dy] (
  set DEBUG=-Xrunjdwp:transport=dt_socket,address=5004,server=y,suspend=y
) else if [%1]==[-d] (
  set DEBUG=-Xrunjdwp:transport=dt_socket,address=5004,server=y,suspend=n
) else (
  set DEBUG=
)

set DIR=%~dp0..\lib

FOR /F %%P IN ('dir /B %DIR%\*.jar') DO (
  set CP=!CP!;%%P
)

ECHO "CP IS: %CP%"

pushd "%DIR%"
ECHO %DIR%
java -cp "%CP%" %DEBUG% com.connexience.server.workflow.cloud.cmd.RebuildCoreLibrary "%DIR%"
popd "%DIR%"
