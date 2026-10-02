@echo off
setlocal EnableExtensions

cd /d "%~dp0"

call build-local-release.bat
if errorlevel 1 exit /b 1

set "ADB=%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe"
set "APK=app\build\outputs\apk\localRelease\app-localRelease.apk"

if not exist "%ADB%" (
    echo ERROR: adb.exe not found at %ADB%
    exit /b 1
)

if not exist "%APK%" (
    echo ERROR: APK not found at %APK%
    exit /b 1
)

"%ADB%" get-state >nul 2>&1
if errorlevel 1 (
    echo ERROR: No authorized Android device is available through ADB.
    echo Check USB debugging and run: "%ADB%" devices
    exit /b 1
)

echo [INSTALL] Installing optimized local release...
"%ADB%" install -r "%APK%"
if errorlevel 1 exit /b 1

echo [RUN] Launching gallery...
"%ADB%" shell am force-stop dev.devinsondev.gallery
"%ADB%" shell am start -n dev.devinsondev.gallery/.MainActivity
if errorlevel 1 exit /b 1

echo.
echo LOCAL RELEASE INSTALLED AND STARTED
exit /b 0
