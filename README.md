# Aaplexamp - Android Auto Plexamp Client

An Android Auto app that connects to a Plexamp/Plex server for a unique music listening experience.

## Quick Start

1. **Check your environment**: Run `.\check-setup.ps1` in PowerShell
2. **Build**: Open in VS Code, press `Ctrl+Shift+B`  
3. **Install**: Connect phone and run task "Install Debug APK"

See [QUICKSTART.md](QUICKSTART.md) for detailed setup instructions.

## Features

- **Shuffle Mode**: Starts in shuffle mode with your entire Plexamp music library
- **Album Discovery**: Navigate through shuffled tracks until you find a song you like
- **Smart Album Playback**: Two options when you find a song:
  - Play album from the current song's position
  - Play album from the beginning
- **Seamless Transitions**: Switch from shuffle to album playback on-the-fly

## Setup

### Prerequisites

1. A Plex Media Server with a music library
2. Your Plex authentication token
3. Android device with Android Auto support (Android 9.0+)
4. Android Studio for building the app

### Getting Your Plex Token

1. Sign in to your Plex Web App
2. Play any media item
3. Click the three dots (...) → "Get Info"
4. Click "View XML"
5. Look for `X-Plex-Token` in the URL

### Configuration

1. Build and install the app on your Android device
2. Open the Aaplexamp app
3. Enter your Plex server URL (e.g., `http://192.168.1.100:32400`)
4. Enter your Plex token
5. Tap "Save Settings"

### Using with Android Auto

1. Connect your phone to your car via USB or wireless Android Auto
2. Launch Aaplexamp from the Android Auto media menu
3. Music will start playing in shuffle mode automatically
4. Use the "Next" button to navigate through shuffled tracks
5. When you hear a song you like, use the custom actions:
   - "Play Album from Current" - Continue from this song in the album
   - "Play Album from Start" - Start the album from track 1

## Architecture

- **PlexApiClient**: Handles communication with Plex server
- **PlaylistManager**: Manages shuffle and album playlists
- **ExoPlayerManager**: Controls media playback
- **ExoPlayerManager**: Controls media playback
- **PlexampMediaBrowserService**: Android Auto integration via MediaBrowserService

## Building

### Using VS Code (Recommended)

See [VSCODE_BUILD.md](VSCODE_BUILD.md) for complete VS Code setup instructions.

Quick start:
```powershell
# In VS Code, press Ctrl+Shift+B to build
# Or use terminal:
.\gradlew assembleDebug
.\gradlew installDebug
```

### Using Android Studio

- Open the project in Android Studio
- Build → Build Bundle(s) / APK(s) → Build APK(s)
- Run → Run 'app'

## Project Structure

```
app/
├── src/main/
│   ├── java/com/aaplexamp/auto/
│   │   ├── api/              # Plex API integration
│   │   ├── model/            # Data models
│   │   ├── playback/         # Playback and playlist management
│   │   ├── service/          # MediaBrowserService
│   │   ├── MainActivity.kt
│   │   └── PlexampApplication.kt
│   ├── res/
│   └── AndroidManifest.xml
└── build.gradle.kts
```

## Requirements

- Minimum SDK: 29 (Android 10)
- Target SDK: 34 (Android 14)
- Kotlin 1.9.20+

## Dependencies

- AndroidX Media
- ExoPlayer 2.19.1
- Retrofit 2.9.0
- OkHttp 4.12.0
- Kotlin Coroutines

## TODO

- [ ] Add drawable resources for notification icons
- [ ] Implement proper error handling for network failures
- [ ] Add caching for album art
- [ ] Support for multiple music libraries
- [ ] Add search functionality
- [ ] Implement queue management
- [ ] Add playback history

## License

This project is for personal use. Respect Plex's terms of service.
