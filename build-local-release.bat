@echo off
setlocal EnableExtensions

cd /d "%~dp0"

call build-debug.bat
if errorlevel 1 exit /b 1

set "JAVA_HOME=D:\TOOLS\andrstdio\jbr"
set "ANDROID_HOME=%LOCALAPPDATA%\Android\Sdk"
set "ANDROID_SDK_ROOT=%ANDROID_HOME%"
set "PATH=%JAVA_HOME%\bin;%ANDROID_HOME%\platform-tools;%ANDROID_HOME%\cmdline-tools\latest\bin;%PATH%"

if not exist "%JAVA_HOME%\bin\java.exe" (
    echo ERROR: JDK not found at %JAVA_HOME%
    exit /b 1
)

if not exist "%ANDROID_HOME%" (
    echo ERROR: Android SDK not found at %ANDROID_HOME%
    exit /b 1
)

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
