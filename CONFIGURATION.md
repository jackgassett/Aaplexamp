# Configuration Guide for Aaplexamp

## Getting Started

### 1. Find Your Plex Server Information

#### Server URL
Your Plex server URL follows this format: `http://[IP_ADDRESS]:32400`

- **Local Network**: Use your server's local IP (e.g., `http://192.168.1.100:32400`)
- **Remote Access**: Use your public IP or domain with port forwarding
- **Plex Cloud**: Not currently supported

To find your server's IP:
- Windows: Open Command Prompt and run `ipconfig`
- Mac/Linux: Open Terminal and run `ifconfig`
- Look for the IPv4 address on your local network

#### Plex Token
Your Plex token is required for authentication.

**Method 1: From Web App**
1. Open Plex Web App (app.plex.tv)
2. Play any media
3. Click ⋮ (three dots) → "Get Info"
4. Click "View XML"
5. In the URL bar, find `X-Plex-Token=XXXXX`
6. Copy the token after the equals sign

**Method 2: From Server Settings**
1. Sign in to Plex Web App
2. Settings → Account → scroll to the bottom
3. Look for "Authorized Devices" or use developer tools
4. The token is in network requests

### 2. Update the Service Code

Open [PlexampMediaBrowserService.kt](app/src/main/java/com/aaplexamp/auto/service/PlexampMediaBrowserService.kt#L54-L56) and update:

```kotlin
// TODO: Load these from SharedPreferences or configuration
val serverUrl = "http://your-plex-server:32400"
val token = "your-plex-token"
```

Replace with your actual values or implement SharedPreferences loading.

### 3. Add Missing Drawable Resources

The app references several drawable resources that need to be created. Add these to `app/src/main/res/drawable/`:

- `ic_album.xml` - Icon for "Play Album from Current"
- `ic_album_start.xml` - Icon for "Play Album from Start"
- `ic_music_note.xml` - Notification icon
- `ic_skip_previous.xml` - Previous track button
- `ic_pause.xml` - Pause button
- `ic_play_arrow.xml` - Play button
- `ic_skip_next.xml` - Next track button

**Quick Solution**: Use Android Studio's Vector Asset import:
1. Right-click `drawable` folder → New → Vector Asset
2. Choose Material Icon
3. Search for: music_note, skip_previous, skip_next, pause, play_arrow, album
4. Click Finish for each

### 4. Configure Your Music Library

The app automatically detects your Plex music library. If you have multiple libraries:
1. It will use the first one with type "artist"
2. To change this, modify the logic in [PlexampMediaBrowserService.kt](app/src/main/java/com/aaplexamp/auto/service/PlexampMediaBrowserService.kt#L82-L84)

### 5. Build and Install

#### Using VS Code (Recommended)

See [VSCODE_BUILD.md](VSCODE_BUILD.md) for detailed instructions.

```powershell
# In VS Code terminal
.\gradlew assembleDebug
.\gradlew installDebug

# Or press Ctrl+Shift+B and select "Install Debug APK"
```

#### Using Android Studio

- Build → Build Bundle(s) / APK(s) → Build APK(s)
- Run → Run 'app'

### 6. Test the App

1. Open the app on your phone
2. Enter your server URL and token
3. Tap "Save Settings"
4. Connect to Android Auto (USB or wireless)
5. Select Aaplexamp from the media apps

## Usage

### In Shuffle Mode
- Music plays automatically from your entire library
- Use "Next" to skip through shuffled tracks
- Find a song you like? Use the custom actions below

### Custom Actions (Android Auto)
When in shuffle mode, you'll see two custom action buttons:

1. **Play Album from Current Song**
   - Loads the current song's album
   - Starts playback from the current song
   - Queue becomes the rest of the album

2. **Play Album from Start**
   - Loads the current song's album
   - Starts from track 1
   - Queue becomes the entire album

### Returning to Shuffle
Currently, you need to restart the app to return to shuffle mode. A future update will add this as a custom action in album mode.

## Troubleshooting

### App won't connect to server
- Verify server URL is correct (include `http://` and port `:32400`)
- Check that your phone can reach the server (try opening the URL in a browser)
- Ensure Plex server is running
- Check firewall settings

### No music plays
- Verify your Plex token is correct
- Check that you have at least one music library in Plex
- Look at Android Studio Logcat for error messages

### Custom actions don't appear
- Make sure you're in shuffle mode
- Check that Android Auto is properly connected
- Try disconnecting and reconnecting Android Auto

### Poor audio quality
- Check your Plex server transcoding settings
- Ensure your phone has a good network connection
- Consider using direct play instead of transcoding

## Advanced Configuration

### Loading Settings from SharedPreferences

Update [PlexampMediaBrowserService.kt](app/src/main/java/com/aaplexamp/auto/service/PlexampMediaBrowserService.kt#L54-L56):

```kotlin
val prefs = getSharedPreferences("plexamp_prefs", Context.MODE_PRIVATE)
val serverUrl = prefs.getString("server_url", "") ?: ""
val token = prefs.getString("token", "") ?: ""
```

### Changing Music Library

To use a specific library section ID:

```kotlin
// Replace the auto-detection with a specific section ID
val allTracks = plexApiClient.getAllTracks("YOUR_SECTION_ID")
```

Find your section ID by visiting: `http://your-server:32400/library/sections?X-Plex-Token=YOUR_TOKEN`

### Audio Quality Settings

ExoPlayer will use the direct stream URL from Plex. To modify quality, adjust the Plex API request or implement transcoding options.

## Support

For issues related to:
- **Plex API**: Check [Plex API Documentation](https://support.plex.tv/articles/)
- **Android Auto**: Check [Android Auto Developer Documentation](https://developer.android.com/training/cars)
- **ExoPlayer**: Check [ExoPlayer Documentation](https://exoplayer.dev/)
