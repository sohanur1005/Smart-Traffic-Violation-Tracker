@echo off
set "JAVA_EXE=%APPDATA%\Code\User\globalStorage\pleiades.java-extension-pack-jdk\java\latest\bin\java.exe"
if not exist "%JAVA_EXE%" (
    echo Java runtime not found in the VS Code Java extension pack folder.
    echo Searching for 'java' in system PATH...
    where java >nul 2>nul
    if %errorlevel% equ 0 (
        set "JAVA_EXE=java"
    ) else (
        echo Error: Java could not be found. Please ensure Java is installed.
        pause
        exit /b 1
    )
)

echo Starting Smart Traffic Violation Tracker...
"%JAVA_EXE%" -cp "bin;lib/*" app.Launcher
