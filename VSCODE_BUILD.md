# Building with VS Code

This project can be built using Visual Studio Code instead of Android Studio.

## Prerequisites

1. **Install Java Development Kit (JDK)**
   - Download JDK 17 or higher from [Adoptium](https://adoptium.net/)
   - Add to PATH: `JAVA_HOME` environment variable

2. **Install Android SDK**
   
   **Option A: Via Android Studio (Recommended)**
   - Install Android Studio
   - Open SDK Manager (Tools → SDK Manager)
   - Install Android SDK Platform 34 and Build Tools
   - Note the SDK location (usually `C:\Users\<YourName>\AppData\Local\Android\Sdk`)
   
   **Option B: Command Line Tools**
   - Download [Android Command Line Tools](https://developer.android.com/studio#command-tools)
   - Extract to `C:\Android\cmdline-tools\latest\` (must include the `latest` subfolder)
   - Set ANDROID_HOME: `C:\Android`
   - Run from the `bin` folder:
     ```powershell
     cd C:\Android\cmdline-tools\latest\bin
     .\sdkmanager.bat "platforms;android-34" "build-tools;34.0.0"
     ```
   - Or specify SDK root explicitly:
     ```powershell
     .\sdkmanager.bat --sdk_root=C:\Android "platforms;android-34" "build-tools;34.0.0"
     ```

3. **Set ANDROID_HOME Environment Variable**
   ```powershell
   # In PowerShell (as Administrator)
   [System.Environment]::SetEnvironmentVariable('ANDROID_HOME', 'C:\Users\<YourName>\AppData\Local\Android\Sdk', 'Machine')
   ```

4. **Install ADB (Android Debug Bridge)**
   - Included with Android SDK
   - Add to PATH: `%ANDROID_HOME%\platform-tools`

5. **Install VS Code Extensions**
   - Open VS Code in this folder
   - Accept the prompt to install recommended extensions, or:
     - Kotlin Language (`fwcd.kotlin`)
     - Gradle for Java (`vscjava.vscode-gradle`)
     - Java Extension Pack (`vscjava.vscode-java-pack`)

## Building the App

### Using VS Code Tasks (Recommended)

Press `Ctrl+Shift+B` or `Ctrl+Shift+P` → "Tasks: Run Build Task"

Available tasks:
- **Build Debug APK** (default) - Builds debug version
- **Build Release APK** - Builds release version
- **Install Debug APK** - Builds and installs to connected device
- **Clean Build** - Cleans build artifacts
- **Clean and Build Debug** - Full clean build
- **Run Lint** - Check for code issues
- **List Connected Devices** - Show ADB devices
- **View Logcat** - View Android logs

### Using Terminal

Open VS Code terminal (`Ctrl+\``) and run:

```powershell
# Build debug APK
.\gradlew assembleDebug

# Build release APK
.\gradlew assembleRelease

# Install to connected device
.\gradlew installDebug

# Clean build
.\gradlew clean

# Run all tasks
.\gradlew clean assembleDebug installDebug
```

### First Build Setup

1. The first build will download Gradle and dependencies (may take several minutes)
2. If you get "SDK location not found", create `local.properties`:
   ```properties
   sdk.dir=C\:\\Users\\<YourName>\\AppData\\Local\\Android\\Sdk
   ```

## Running the App

1. **Enable USB Debugging on your Android device**
   - Settings → About Phone → Tap "Build Number" 7 times
   - Settings → Developer Options → Enable "USB Debugging"

2. **Connect your device via USB**

3. **Verify connection**
   - Run task: "List Connected Devices"
   - Or in terminal: `adb devices`

4. **Install the app**
   - Run task: "Install Debug APK"
   - Or in terminal: `.\gradlew installDebug`

5. **View logs**
   - Run task: "View Logcat"
   - Or in terminal: `adb logcat -s PlexampService:* *:E`

## Output Location

Built APKs are located at:
- **Debug**: `app\build\outputs\apk\debug\app-debug.apk`
- **Release**: `app\build\outputs\apk\release\app-release.apk`

## Debugging

### View Build Output
- Build output appears in the VS Code terminal
- Check for errors in the "Problems" panel (`Ctrl+Shift+M`)

### View Android Logs
```powershell
# All logs
adb logcat

# App logs only
adb logcat -s PlexampService:* *:E

# Clear and follow logs
adb logcat -c && adb logcat
```

### Common Issues

**"SDK location not found"**
- Create `local.properties` with your SDK path
- Or set `ANDROID_HOME` environment variable

**"gradlew: command not found"**
- On Windows, use `.\gradlew.bat` instead of `./gradlew`
- Or run `.\gradlew` from PowerShell

**"JAVA_HOME not set"**
- Install JDK 17+
- Set environment variable: `JAVA_HOME=C:\Program Files\Java\jdk-17`

**Build fails with "Could not resolve dependencies"**
- Check internet connection
- Try: `.\gradlew clean build --refresh-dependencies`

**Device not recognized**
- Install device USB drivers
- Enable USB debugging on device
- Run: `adb kill-server` then `adb start-server`

## Gradle Commands Reference

```powershell
# Build
.\gradlew assembleDebug          # Build debug APK
.\gradlew assembleRelease        # Build release APK
.\gradlew build                  # Build all variants

# Install
.\gradlew installDebug           # Install debug to device
.\gradlew uninstallDebug         # Uninstall debug from device

# Clean
.\gradlew clean                  # Remove build files

# Analyze
.\gradlew lint                   # Run lint checks
.\gradlew dependencies           # Show dependency tree

# Info
.\gradlew tasks                  # List all tasks
.\gradlew projects               # List all projects
```

## Quick Start Workflow

1. Open VS Code in the `Aaplexamp` folder
2. Install recommended extensions
3. Connect Android device via USB
4. Press `Ctrl+Shift+B` to build
5. Run task "Install Debug APK"
6. Open app on device and configure Plex settings

## Advantages of VS Code

- Lightweight and fast
- Excellent Kotlin support
- Integrated Git
- Powerful search and navigation
- Works great with GitHub Copilot
- No heavy IDE overhead

## When to Use Android Studio

Consider Android Studio if you need:
- Visual layout editor
- Advanced debugging tools
- Profiler for performance analysis
- Device emulators (AVD Manager)
- Integrated SDK manager

You can use both! Build in VS Code, and open in Android Studio when needed.
