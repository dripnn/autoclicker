@echo off
echo ========================================
echo AutoClicker Mod Builder for Windows
echo ========================================
echo.

REM Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java is not installed or not in PATH!
    echo.
    echo Please install Java 8 from:
    echo https://adoptium.net/temurin/releases/?version=8
    echo.
    pause
    exit /b 1
)

echo Java found! Checking version...
java -version
echo.

REM Check if Gradle is already downloaded
if exist "gradle-2.14" (
    echo Gradle already downloaded!
    goto :build
)

echo Downloading Gradle 2.14...
echo This may take a few minutes...
echo.

REM Download Gradle using PowerShell
powershell -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-2.14-bin.zip' -OutFile 'gradle-2.14-bin.zip'}"

if not exist "gradle-2.14-bin.zip" (
    echo ERROR: Failed to download Gradle!
    echo Please check your internet connection.
    pause
    exit /b 1
)

echo Extracting Gradle...
powershell -Command "& {Expand-Archive -Path 'gradle-2.14-bin.zip' -DestinationPath '.' -Force}"

if not exist "gradle-2.14" (
    echo ERROR: Failed to extract Gradle!
    pause
    exit /b 1
)

echo Gradle installed successfully!
echo.

:build
echo ========================================
echo Running setupDecompWorkspace...
echo This will take 10-15 minutes on first run!
echo ========================================
echo.

gradle-2.14\bin\gradle setupDecompWorkspace

if errorlevel 1 (
    echo.
    echo ERROR: setupDecompWorkspace failed!
    echo Check the error messages above.
    pause
    exit /b 1
)

echo.
echo ========================================
echo Building the mod...
echo This will take 2-5 minutes!
echo ========================================
echo.

gradle-2.14\bin\gradle build

if errorlevel 1 (
    echo.
    echo ERROR: Build failed!
    echo Check the error messages above.
    pause
    exit /b 1
)

echo.
echo ========================================
echo SUCCESS! Mod built successfully!
echo ========================================
echo.
echo Your mod file is located at:
echo build\libs\autoclicker-1.0.0.jar
echo.
echo Copy this file to your .minecraft\mods folder!
echo.
pause
