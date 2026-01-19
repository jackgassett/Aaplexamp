# Aaplexamp Setup Script for Windows
# Run this in PowerShell to check your environment

Write-Host "==================================" -ForegroundColor Cyan
Write-Host "  Aaplexamp Environment Check" -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""

# Check Java
Write-Host "Checking Java..." -ForegroundColor Yellow
if ($env:JAVA_HOME) {
    Write-Host "[OK] JAVA_HOME is set: $env:JAVA_HOME" -ForegroundColor Green
    try {
        $javaCmd = Join-Path $env:JAVA_HOME "bin\java.exe"
        if (Test-Path $javaCmd) {
            $javaVersion = & $javaCmd -version 2>&1 | Select-Object -First 1
            Write-Host "     Version: $javaVersion" -ForegroundColor Green
        }
    } catch {
        Write-Host "     Could not execute java" -ForegroundColor Yellow
    }
}
else {
    Write-Host "[MISSING] JAVA_HOME is not set" -ForegroundColor Red
    Write-Host "          Download JDK 17: https://adoptium.net/" -ForegroundColor Yellow
}
Write-Host ""

# Check Android SDK
Write-Host "Checking Android SDK..." -ForegroundColor Yellow
if ($env:ANDROID_HOME) {
    Write-Host "[OK] ANDROID_HOME is set: $env:ANDROID_HOME" -ForegroundColor Green
    
    $adbPath = Join-Path $env:ANDROID_HOME "platform-tools\adb.exe"
    if (Test-Path $adbPath) {
        Write-Host "[OK] ADB found" -ForegroundColor Green
    }
    else {
        Write-Host "[MISSING] ADB not found in platform-tools" -ForegroundColor Red
    }
    
    $platform34 = Join-Path $env:ANDROID_HOME "platforms\android-34"
    if (Test-Path $platform34) {
        Write-Host "[OK] Android SDK Platform 34 installed" -ForegroundColor Green
    }
    else {
        Write-Host "[MISSING] Android SDK Platform 34 not found" -ForegroundColor Red
    }
}
else {
    Write-Host "[MISSING] ANDROID_HOME is not set" -ForegroundColor Red
    Write-Host "          Install Android Studio or Command Line Tools" -ForegroundColor Yellow
}
Write-Host ""

# Check local.properties
Write-Host "Checking local.properties..." -ForegroundColor Yellow
$localProps = Join-Path $PSScriptRoot "local.properties"
if (Test-Path $localProps) {
    Write-Host "[OK] local.properties exists" -ForegroundColor Green
    $content = Get-Content $localProps -Raw
    if ($content -match "sdk\.dir") {
        Write-Host "[OK] sdk.dir is configured" -ForegroundColor Green
    }
    else {
        Write-Host "[MISSING] sdk.dir not found in local.properties" -ForegroundColor Red
    }
}
else {
    Write-Host "[MISSING] local.properties not found" -ForegroundColor Red
    Write-Host "          Creating from example..." -ForegroundColor Yellow
    
    $examplePath = Join-Path $PSScriptRoot "local.properties.example"
    if (Test-Path $examplePath) {
        Copy-Item $examplePath $localProps
        Write-Host "[OK] Created local.properties from example" -ForegroundColor Green
        Write-Host "     Please edit it with your SDK path!" -ForegroundColor Yellow
    }
}
Write-Host ""

# Check for connected devices
Write-Host "Checking for connected Android devices..." -ForegroundColor Yellow
if ($env:ANDROID_HOME) {
    $adbPath = Join-Path $env:ANDROID_HOME "platform-tools\adb.exe"
    if (Test-Path $adbPath) {
        try {
            $deviceOutput = & $adbPath devices 2>&1
            $devices = $deviceOutput | Select-Object -Skip 1 | Where-Object { $_ -match "\tdevice$" }
            if ($devices) {
                Write-Host "[OK] Connected devices found:" -ForegroundColor Green
                Write-Host $deviceOutput
            }
            else {
                Write-Host "[INFO] No devices connected (this is OK for now)" -ForegroundColor Yellow
                Write-Host "       Connect your Android device with USB debugging enabled" -ForegroundColor Gray
            }
        }
        catch {
            Write-Host "[INFO] Could not run adb" -ForegroundColor Yellow
        }
    }
}
Write-Host ""

# Check Gradle wrapper
Write-Host "Checking Gradle wrapper..." -ForegroundColor Yellow
$gradlewPath = Join-Path $PSScriptRoot "gradlew.bat"
if (Test-Path $gradlewPath) {
    Write-Host "[OK] gradlew.bat exists" -ForegroundColor Green
}
else {
    Write-Host "[MISSING] gradlew.bat not found" -ForegroundColor Red
}
Write-Host ""

# Summary
Write-Host "==================================" -ForegroundColor Cyan
Write-Host "  Summary" -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan

$javaOk = $null -ne $env:JAVA_HOME
$androidOk = $null -ne $env:ANDROID_HOME
$localPropsOk = Test-Path $localProps

if ($javaOk -and $androidOk -and $localPropsOk) {
    Write-Host "[READY] Environment is ready!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Next steps:" -ForegroundColor Cyan
    Write-Host "  1. Run: .\gradlew assembleDebug" -ForegroundColor White
    Write-Host "  2. Or press Ctrl+Shift+B in VS Code" -ForegroundColor White
    Write-Host ""
    Write-Host "See QUICKSTART.md for more details" -ForegroundColor Gray
}
else {
    Write-Host "[SETUP NEEDED] Environment needs configuration" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Required actions:" -ForegroundColor Cyan
    if (-not $javaOk) {
        Write-Host "  • Install JDK 17 and set JAVA_HOME" -ForegroundColor Yellow
    }
    if (-not $androidOk) {
        Write-Host "  • Install Android SDK and set ANDROID_HOME" -ForegroundColor Yellow
    }
    if (-not $localPropsOk) {
        Write-Host "  • Configure local.properties with your SDK path" -ForegroundColor Yellow
    }
    Write-Host ""
    Write-Host "See QUICKSTART.md for detailed instructions" -ForegroundColor Gray
}

Write-Host ""
