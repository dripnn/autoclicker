@echo off
echo ========================================
echo Gradle Wrapper Fix Script
echo ========================================
echo.
echo This script will download the missing Gradle wrapper file.
echo.
pause

echo Downloading gradle-wrapper.jar...
powershell -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://raw.githubusercontent.com/gradle/gradle/v2.14.0/gradle/wrapper/gradle-wrapper.jar' -OutFile 'gradle\wrapper\gradle-wrapper.jar'}"

if exist "gradle\wrapper\gradle-wrapper.jar" (
    echo.
    echo SUCCESS! gradle-wrapper.jar downloaded!
    echo.
    echo Now you can run:
    echo .\gradlew setupDecompWorkspace
    echo.
) else (
    echo.
    echo ERROR: Download failed!
    echo.
    echo Alternative: Use build-mod.bat instead, which downloads full Gradle.
    echo.
)

pause
