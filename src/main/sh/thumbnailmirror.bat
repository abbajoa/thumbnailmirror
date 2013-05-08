@echo off

set CUR_DIR=%~dp0
set CAND=%CUR_DIR%..\..\..\target
if exist "%CAND%" set JAR_DIR=%CAND%
if not exist "%CAND%" set JAR_DIR=%CUR_DIR%\..\lib
for %%a in (%JAR_DIR%\*-jar-with-dependencies.jar) do set JAR_PATH=%%a
java -jar "%JAR_PATH%" %1 %2 %3 %4 %5 %6 %7
