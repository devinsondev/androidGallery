@echo off
setlocal EnableExtensions

cd /d "%~dp0"

call build-debug.bat
if errorlevel 1 exit /b 1

echo.
echo [BUILD] Building optimized local release...
call gradlew.bat --dependency-verification strict :app:assembleLocalRelease
if errorlevel 1 (
    echo.
    echo LOCAL RELEASE BUILD FAILED
    exit /b 1
)

echo.
echo LOCAL RELEASE BUILD SUCCESSFUL
echo APK: %CD%\app\build\outputs\apk\localRelease\app-localRelease.apk
exit /b 0
