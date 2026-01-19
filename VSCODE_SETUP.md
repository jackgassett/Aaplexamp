# VS Code Setup Complete! ✓

Your Aaplexamp project is now configured for building in Visual Studio Code.

## What Was Set Up

### ✅ VS Code Configuration
- **Tasks** (`.vscode/tasks.json`) - Build, install, and debug tasks
- **Extensions** (`.vscode/extensions.json`) - Recommended Kotlin and Gradle extensions
- **Settings** (`.vscode/settings.json`) - Workspace formatting and Java config

### ✅ Build System
- **Gradle Wrapper** (`gradlew.bat`, `gradlew`) - No need to install Gradle
- **Gradle Properties** - Android SDK configuration
- **Build Scripts** - Kotlin-based Gradle configuration

### ✅ Documentation
- **QUICKSTART.md** - Fast setup guide (15-20 minutes)
- **VSCODE_BUILD.md** - Detailed VS Code build instructions
- **check-setup.ps1** - Environment verification script

## Next Steps

### 1. Check Your Environment
Run the setup checker:
```powershell
.\check-setup.ps1
```

This will verify:
- Java JDK 17+ is installed
- Android SDK is configured
- local.properties is set up

### 2. Install Prerequisites (if needed)

**Java JDK 17**:
- Download: https://adoptium.net/temurin/releases/?version=17
- Install and set `JAVA_HOME` environment variable

**Android SDK**:
- Option A: Install Android Studio (easiest)
- Option B: Download Command Line Tools only

**Configure SDK Path**:
Edit `local.properties` (created by check-setup.ps1):
```properties
sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
```

### 3. Install VS Code Extensions

When you open this folder in VS Code, accept the prompt to install recommended extensions:
- Kotlin Language Support
- Gradle for Java
- Java Extension Pack

Or install manually from the Extensions sidebar (`Ctrl+Shift+X`).

### 4. Build the App

**Using VS Code Tasks** (Recommended):
Press `Ctrl+Shift+B` and select:
- **Build Debug APK** - Compiles the app
- **Install Debug APK** - Builds and installs to connected device

**Using Terminal**:
```powershell
# Build
.\gradlew assembleDebug

# Install to phone
.\gradlew installDebug
```

The first build will:
- Download Gradle (~100 MB)
- Download dependencies (~200 MB)
- Take 5-10 minutes

Subsequent builds are much faster (30 seconds - 2 minutes).

### 5. Install on Device

1. Enable USB Debugging on your Android phone:
   - Settings → About Phone → Tap "Build Number" 7 times
   - Settings → Developer Options → Enable "USB Debugging"

2. Connect via USB cable

3. Verify connection:
   ```powershell
   adb devices
   ```

4. Install the app:
   - VS Code: Run task "Install Debug APK"
   - Terminal: `.\gradlew installDebug`

### 6. Configure Plex Connection

1. Open Aaplexamp app on your phone
2. Enter your Plex server URL (e.g., `http://192.168.1.100:32400`)
3. Enter your Plex token (see [CONFIGURATION.md](CONFIGURATION.md))
4. Save settings

### 7. Use with Android Auto

1. Connect phone to car via USB or wireless Android Auto
2. Launch Aaplexamp from Android Auto media menu
3. Music starts in shuffle mode automatically
4. Use custom actions to switch to album mode

## Available VS Code Tasks

Press `Ctrl+Shift+P` → "Tasks: Run Task" to access:

**Build Tasks**:
- Build Debug APK
- Build Release APK
- Clean Build
- Clean and Build Debug
- Run Lint

**Device Tasks**:
- List Connected Devices
- Install Debug APK
- View Logcat (app logs)

## Project Structure

```
Aaplexamp/
├── .vscode/              # VS Code configuration
│   ├── tasks.json        # Build and run tasks
│   ├── extensions.json   # Recommended extensions
│   └── settings.json     # Workspace settings
├── app/                  # Android app source code
│   ├── src/main/
│   │   ├── java/         # Kotlin source files
│   │   │   └── com/aaplexamp/auto/
│   │   │       ├── api/              # Plex API client
│   │   │       ├── model/            # Data models
│   │   │       ├── playback/         # Playlist & player
│   │   │       └── service/          # Android Auto service
│   │   ├── res/          # Resources (layouts, drawables)
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts  # App build configuration
├── gradle/               # Gradle wrapper files
├── gradlew / gradlew.bat # Gradle wrapper scripts
├── build.gradle.kts      # Root build configuration
├── settings.gradle.kts   # Project settings
├── local.properties      # SDK location (not in git)
├── check-setup.ps1       # Environment checker
├── QUICKSTART.md         # Quick setup guide
├── VSCODE_BUILD.md       # Detailed VS Code instructions
├── CONFIGURATION.md      # Plex configuration guide
├── USER_GUIDE.md         # App usage guide
└── README.md             # Project overview
```

## Quick Commands

```powershell
# Check environment
.\check-setup.ps1

# Build debug APK
.\gradlew assembleDebug

# Build and install
.\gradlew installDebug

# Clean and rebuild
.\gradlew clean assembleDebug

# View connected devices
adb devices

# View app logs
adb logcat -s PlexampService:* *:E

# List all Gradle tasks
.\gradlew tasks
```

## Keyboard Shortcuts in VS Code

- `Ctrl+Shift+B` - Run build task
- `Ctrl+Shift+P` - Command palette
- `Ctrl+` ` - Toggle terminal
- `Ctrl+Shift+M` - Problems panel
- `Ctrl+Shift+X` - Extensions
- `Ctrl+P` - Quick open file

## Troubleshooting

**"JAVA_HOME not set"**
- Install JDK 17 from https://adoptium.net/
- Set environment variable
- Restart VS Code

**"SDK location not found"**
- Run `.\check-setup.ps1` to create `local.properties`
- Edit with your SDK path
- Restart build

**"Device not found"**
- Enable USB debugging on phone
- Run `adb devices` to verify
- Try `adb kill-server` then `adb start-server`

**Build is slow**
- First build takes 5-10 minutes (normal)
- Downloads Gradle and dependencies
- Subsequent builds are fast

See [VSCODE_BUILD.md](VSCODE_BUILD.md) for more troubleshooting.

## Documentation

- **[QUICKSTART.md](QUICKSTART.md)** - Get started in 15-20 minutes
- **[VSCODE_BUILD.md](VSCODE_BUILD.md)** - Complete VS Code build guide
- **[CONFIGURATION.md](CONFIGURATION.md)** - Plex server setup
- **[USER_GUIDE.md](USER_GUIDE.md)** - How to use the app
- **[README.md](README.md)** - Project overview

## Why VS Code?

- ✅ Lightweight and fast
- ✅ Excellent Kotlin support
- ✅ Built-in Git integration
- ✅ Powerful search and navigation
- ✅ GitHub Copilot integration
- ✅ No heavy IDE overhead

You can still use Android Studio when needed for:
- Visual layout editor
- Advanced debugging
- Performance profiling
- Device emulators

## Getting Help

1. Run `.\check-setup.ps1` to diagnose environment issues
2. Check the documentation files listed above
3. Look at terminal output for specific error messages
4. Search Gradle/Android build errors online

## Enjoy Your Music! 🎵

Once built and configured, connect to Android Auto and start your musical discovery journey with Aaplexamp!
