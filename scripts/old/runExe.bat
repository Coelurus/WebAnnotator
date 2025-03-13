@echo off

:: Change current working directory so relative path works
cd /d "%~dp0"

:: Run the exe
start "" ".\Aurea.exe"