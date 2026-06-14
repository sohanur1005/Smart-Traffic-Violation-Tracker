$javaPath = "$env:APPDATA\Code\User\globalStorage\pleiades.java-extension-pack-jdk\java\latest\bin\java.exe"
if (-not (Test-Path $javaPath)) {
    if (Get-Command java -ErrorAction SilentlyContinue) {
        $javaPath = "java"
    } else {
        Write-Error "Java runtime not found. Please install JDK/JRE or add it to your PATH."
        Read-Host "Press Enter to exit..."
        exit
    }
}

Write-Host "Starting Smart Traffic Violation Tracker..." -ForegroundColor Green
& $javaPath -cp "bin;lib/*" app.Launcher
