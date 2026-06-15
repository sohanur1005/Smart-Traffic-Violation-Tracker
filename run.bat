@echo off
set "JAVA_HOME=%APPDATA%\Code\User\globalStorage\pleiades.java-extension-pack-jdk\java\latest"
set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
set "JAVAC_EXE=%JAVA_HOME%\bin\javac.exe"

if not exist "%JAVA_EXE%" (
    echo Java runtime not found in the VS Code Java extension pack folder.
    echo Searching for java in system PATH...
    where java >nul 2>nul
    if %errorlevel% equ 0 (
        set "JAVA_EXE=java"
        set "JAVAC_EXE=javac"
    ) else (
        echo Error: Java could not be found. Please ensure Java is installed.
        pause
        exit /b 1
    )
)

echo Compiling Smart Traffic Violation Tracker...
if not exist "bin" mkdir bin
for /r src %%f in (*.java) do set "SRC_FILES=!SRC_FILES! %%f"
setlocal enabledelayedexpansion
set "SRC_FILES="
for /r src %%f in (*.java) do set "SRC_FILES=!SRC_FILES! %%f"
"%JAVAC_EXE%" -cp "lib/*" -d bin %SRC_FILES%
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Copying resources...
xcopy /s /y /q "src\view" "bin\view\" >nul
if exist "src\config.properties" copy /y "src\config.properties" "bin\config.properties" >nul

echo Starting Smart Traffic Violation Tracker...
"%JAVA_EXE%" -cp "bin;lib/*" app.Launcher
