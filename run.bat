@echo off
cd /d "%~dp0"
javac -encoding UTF-8 *.java
if errorlevel 1 (
    echo Compilation failed.
    pause
    exit /b 1
)
java Main
pause
