@echo off
setlocal EnableExtensions

cd /d "%~dp0"

set "EXPECTED_DISTRIBUTION_URL=https\://services.gradle.org/distributions/gradle-9.5.0-bin.zip"
set "EXPECTED_DISTRIBUTION_SHA256=553c78f50dafcd54d65b9a444649057857469edf836431389695608536d6b746"
set "EXPECTED_WRAPPER_JAR_SHA256=497c8c2a7e5031f6aa847f88104aa80a93532ec32ee17bdb8d1d2f67a194a9c7"

set "JAVA_HOME=D:\TOOLS\andrstdio\jbr"
set "ANDROID_HOME=%LOCALAPPDATA%\Android\Sdk"
set "ANDROID_SDK_ROOT=%ANDROID_HOME%"
set "PATH=%JAVA_HOME%\bin;%ANDROID_HOME%\platform-tools;%ANDROID_HOME%\cmdline-tools\latest\bin;%PATH%"

set "WRAPPER_BAT=gradlew.bat"
set "WRAPPER_JAR=gradle\wrapper\gradle-wrapper.jar"
set "WRAPPER_PROPS=gradle\wrapper\gradle-wrapper.properties"
set "VERIFY_META=gradle\verification-metadata.xml"

echo.
echo [PRECHECK] Secure Android build preflight...

if not exist "%WRAPPER_BAT%" (
    echo ERROR: Missing %WRAPPER_BAT%
    exit /b 1
)

if not exist "%WRAPPER_JAR%" (
    echo ERROR: Missing %WRAPPER_JAR%
    exit /b 1
)

if not exist "%WRAPPER_PROPS%" (
    echo ERROR: Missing %WRAPPER_PROPS%
    exit /b 1
)

if not exist "%VERIFY_META%" (
    echo ERROR: Missing %VERIFY_META%
    exit /b 1
)

set "ACTUAL_DISTRIBUTION_URL="
set "ACTUAL_DISTRIBUTION_SHA256="
for /f "usebackq tokens=1,* delims==" %%A in ("%WRAPPER_PROPS%") do (
    if "%%A"=="distributionUrl" set "ACTUAL_DISTRIBUTION_URL=%%B"
    if "%%A"=="distributionSha256Sum" set "ACTUAL_DISTRIBUTION_SHA256=%%B"
)

if not defined ACTUAL_DISTRIBUTION_URL (
    echo ERROR: distributionUrl is missing from %WRAPPER_PROPS%.
    exit /b 1
)

if not "%ACTUAL_DISTRIBUTION_URL%"=="%EXPECTED_DISTRIBUTION_URL%" (
    echo ERROR: Gradle distributionUrl does not match the trusted expected URL.
    echo Expected: %EXPECTED_DISTRIBUTION_URL%
    echo Actual:   %ACTUAL_DISTRIBUTION_URL%
    exit /b 1
)

if not defined ACTUAL_DISTRIBUTION_SHA256 (
    echo ERROR: distributionSha256Sum is missing from %WRAPPER_PROPS%.
    exit /b 1
)

if /I not "%ACTUAL_DISTRIBUTION_SHA256%"=="%EXPECTED_DISTRIBUTION_SHA256%" (
    echo ERROR: Gradle distributionSha256Sum does not match the trusted expected SHA-256.
    echo Expected: %EXPECTED_DISTRIBUTION_SHA256%
    echo Actual:   %ACTUAL_DISTRIBUTION_SHA256%
    exit /b 1
)

set "ACTUAL_WRAPPER_JAR_SHA256="
for /f "delims=" %%H in ('powershell -NoProfile -Command "(Get-FileHash -LiteralPath '%WRAPPER_JAR%' -Algorithm SHA256).Hash.ToLowerInvariant()"') do (
    set "ACTUAL_WRAPPER_JAR_SHA256=%%H"
)

if not defined ACTUAL_WRAPPER_JAR_SHA256 (
    echo ERROR: Could not calculate Gradle wrapper JAR SHA-256.
    exit /b 1
)

if /I not "%ACTUAL_WRAPPER_JAR_SHA256%"=="%EXPECTED_WRAPPER_JAR_SHA256%" (
    echo ERROR: Gradle wrapper JAR checksum mismatch.
    echo Expected: %EXPECTED_WRAPPER_JAR_SHA256%
    echo Actual:   %ACTUAL_WRAPPER_JAR_SHA256%
    exit /b 1
)

if not exist "%JAVA_HOME%\bin\java.exe" (
    echo ERROR: JDK not found at %JAVA_HOME%
    exit /b 1
)

if not exist "%ANDROID_HOME%" (
    echo ERROR: Android SDK not found at %ANDROID_HOME%
    exit /b 1
)

echo [BUILD] Running tests and debug assemble with strict dependency verification...
call "%WRAPPER_BAT%" --dependency-verification strict :app:testDebugUnitTest :app:assembleDebug
if errorlevel 1 (
    echo.
    echo BUILD FAILED
    exit /b 1
)

echo.
echo BUILD SUCCESSFUL
echo APK: %CD%\app\build\outputs\apk\debug\app-debug.apk
exit /b 0
