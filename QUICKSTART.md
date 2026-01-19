# Quick Start - Building in VS Code

This guide gets you building the Aaplexamp Android app in VS Code quickly.

## 1. Prerequisites Setup (5-10 minutes)

### Install Java JDK
1. Download [JDK 17](https://adoptium.net/temurin/releases/?version=17)
2. Install and note the installation path
3. Set environment variable:
   ```powershell
   # Open PowerShell as Administrator
   [System.Environment]::SetEnvironmentVariable('JAVA_HOME', 'C:\Program Files\Eclipse Adoptium\jdk-17.0.x-hotspot', 'Machine')
   ```
4. Restart your terminal

### Install Android SDK
**Option 1: Quick (via Android Studio)**
1. Download [Android Studio](https://developer.android.com/studio)
2. Install Android Studio
3. Open it → More Actions → SDK Manager
4. Install: Android SDK Platform 34, Android SDK Build-Tools 34

**Option 2: Command Line Only**
1. Download [Command Line Tools](https://developer.android.com/studio#command-tools)
2. Extract to `C:\Android\cmdline-tools\latest`
3. Set ANDROID_HOME: `C:\Android`

### Set Android Environment Variable
```powershell
# Open PowerShell as Administrator
[System.Environment]::SetEnvironmentVariable('ANDROID_HOME', 'C:\Users\<YourUsername>\AppData\Local\Android\Sdk', 'Machine')
```

Replace `<YourUsername>` with your actual Windows username.

### Configure the Project
1. Copy `local.properties.example` to `local.properties`
2. Edit `local.properties` and update the SDK path:
   ```properties
   sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
   ```

## 2. Install VS Code Extensions (2 minutes)

Open this folder in VS Code. You'll see a notification to install recommended extensions. Click "Install All" or install manually:

- Kotlin Language
- Gradle for Java  
- Java Extension Pack

## 3. Build the App (First Time)

### Open Terminal in VS Code
Press `` Ctrl+` `` (backtick) or View → Terminal

### Run Your First Build
```powershell
.\gradlew assembleDebug
```

This will:
- Download Gradle (first time only, ~100MB)
- Download dependencies (~200MB)
- Build the APK (2-5 minutes first time)

## 4. Using VS Code Tasks

After the first successful build, use built-in tasks:

### Build
Press `Ctrl+Shift+B` or run:
- **Build Debug APK** - Compiles the app
- **Clean and Build Debug** - Fresh build
- **Build Release APK** - Production build

### Install to Device
1. Connect your Android phone via USB
2. Enable USB Debugging (see instructions below)
3. Run task: **Install Debug APK**

Or use terminal:
```powershell
.\gradlew installDebug
```

## 5. Enable USB Debugging on Android

1. Settings → About Phone
2. Tap "Build Number" 7 times
3. Go back → Developer Options
4. Enable "USB Debugging"
5. Connect USB cable
6. Accept "Allow USB Debugging" prompt on phone

## 6. Verify Device Connection

```powershell
adb devices
```

You should see your device listed.

## Quick Commands Reference

```powershell
# Build
.\gradlew assembleDebug

# Install to phone
.\gradlew installDebug  

# Clean build
.\gradlew clean

# Build and install
.\gradlew clean assembleDebug installDebug

# Check connected devices
adb devices

# View app logs
adb logcat -s PlexampService:* *:E
```

## Troubleshooting

### "java: command not found"
- Install JDK 17
- Set JAVA_HOME environment variable
- Restart terminal

### "SDK location not found"
- Create `local.properties` file
- Add: `sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk`

### "Device not found"
- Enable USB Debugging on phone
- Try different USB cable/port
- Run: `adb kill-server` then `adb start-server`

### Build is slow
First build takes 5-10 minutes. Subsequent builds are much faster (30 seconds - 2 minutes).

## Next Steps

1. **Configure the app**: Open app on phone, enter Plex server URL and token
2. **Read the guides**:
   - [CONFIGURATION.md](CONFIGURATION.md) - Setting up Plex connection
   - [USER_GUIDE.md](USER_GUIDE.md) - How to use the app
   - [VSCODE_BUILD.md](VSCODE_BUILD.md) - Detailed build instructions

3. **Test with Android Auto**: Connect to your car's Android Auto system

## Quick Video Tutorial (Concept)

1. Install JDK and Android SDK (5 min)
2. Open project in VS Code (30 sec)
3. Install extensions (1 min)
4. Run `.\gradlew assembleDebug` (5 min first time)
5. Connect phone and run `.\gradlew installDebug` (30 sec)
6. Configure Plex settings in app (1 min)
7. Connect to Android Auto and enjoy! 🎵

---

**Total time to first build**: 15-20 minutes
**Total time to first install**: 20-25 minutes

Having issues? Check [VSCODE_BUILD.md](VSCODE_BUILD.md) for detailed troubleshooting.
