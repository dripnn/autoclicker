@echo off
echo ========================================
echo AutoClicker Mod Builder (High Memory)
echo ========================================
echo.

REM Check if Java 8 is installed
java -version 2>&1 | findstr /i "1.8" >nul
if errorlevel 1 (
    echo ERROR: Java 8 is not being used!
    echo.
    echo Current Java version:
    java -version
    echo.
    echo You need Java 8 to build Minecraft 1.8.9 mods.
    echo.
    echo Please install Java 8 from:
    echo https://adoptium.net/temurin/releases/?version=8
    echo.
    echo Then set JAVA_HOME to point to Java 8 installation.
    echo.
    pause
    exit /b 1
)

echo Java 8 detected! Proceeding...
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
echo WITH INCREASED MEMORY (3GB)
echo This will take 10-15 minutes!
echo ========================================
echo.

REM Set Gradle options for more memory
set GRADLE_OPTS=-Xmx3072m -Xms1024m

gradle-2.14\bin\gradle setupDecompWorkspace --stacktrace

if errorlevel 1 (
    echo.
    echo ERROR: setupDecompWorkspace failed!
    echo.
    echo Trying with even more memory (4GB)...
    echo.
    set GRADLE_OPTS=-Xmx4096m -Xms2048m
    gradle-2.14\bin\gradle setupDecompWorkspace --stacktrace
    
    if errorlevel 1 (
        echo.
        echo ERROR: Still failed with 4GB memory!
        echo.
        echo Your computer may not have enough RAM.
        echo Try closing other programs and run this script again.
        echo.
        pause
        exit /b 1
    )
)

echo.
echo ========================================
echo Building the mod...
echo This will take 2-5 minutes!
echo ========================================
echo.

REM Build with increased memory
set GRADLE_OPTS=-Xmx2048m -Xms512m

gradle-2.14\bin\gradle build --stacktrace

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
