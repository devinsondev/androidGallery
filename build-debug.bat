@echo off
setlocal EnableExtensions

cd /d "%~dp0"

set "WRAPPER_PROPS=gradle\wrapper\gradle-wrapper.properties"
if not exist "%WRAPPER_PROPS%" (
    echo ERROR: %WRAPPER_PROPS% is missing. 1>&2
    exit /b 1
)

findstr /B /C:"distributionSha256Sum=" "%WRAPPER_PROPS%" >NUL
if errorlevel 1 (
    echo ERROR: distributionSha256Sum is missing from %WRAPPER_PROPS%. 1>&2
    exit /b 1
)

set "JAVA_HOME=D:\TOOLS\andrstdio\jbr"
if not exist "%JAVA_HOME%\bin\java.exe" (
    echo ERROR: Android Studio JDK not found at %JAVA_HOME%. 1>&2
    exit /b 1
)

set "ANDROID_HOME=%LOCALAPPDATA%\Android\Sdk"
set "ANDROID_SDK_ROOT=%ANDROID_HOME%"
if not exist "%ANDROID_HOME%" (
    echo ERROR: Android SDK not found at %ANDROID_HOME%. 1>&2
    exit /b 1
)

call "%~dp0gradlew.bat" :app:testDebugUnitTest :app:assembleDebug
exit /b %ERRORLEVEL%
