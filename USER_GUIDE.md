# Aaplexamp User Guide

## The Shuffle-to-Album Workflow

This app is designed for a specific music discovery workflow that makes car listening more enjoyable.

### How It Works

#### 1. **Start with Shuffle** (Automatic)
When you launch Aaplexamp in Android Auto, it automatically:
- Loads your entire Plexamp music library
- Shuffles all tracks
- Starts playing immediately

This gives you access to your complete music collection in random order.

#### 2. **Navigate Through Shuffled Tracks**
Use the standard Android Auto controls:
- **Next button**: Skip to the next shuffled track
- **Previous button**: Go back to the previous track
- Keep hitting next until you hear something you want to hear more of

Think of this as "musical discovery mode" - you're exploring your library to find what matches your mood.

#### 3. **Switch to Album Mode** (When You Find a Song)
When you hear a song you like and want to hear more from that album, you have two custom actions available:

##### Option A: Play Album from Current Song
**When to use**: You're partway through a great song and want to continue with the rest of the album.

- Look for the **"Play Album from Current"** button
- The current song keeps playing
- Next track will be the next song on the album
- You'll hear the album from this point forward

**Example**: You're 2 minutes into "Bohemian Rhapsody" and loving it. Hit this button, and when it finishes, you'll hear "God Save the Queen" and the rest of A Night at the Opera.

##### Option B: Play Album from Start
**When to use**: You hear a song from an album you want to experience from the beginning.

- Look for the **"Play Album from Start"** button
- Playback immediately switches to track 1 of the album
- You'll hear the complete album in order

**Example**: You hear "Comfortably Numb" from The Wall. Hit this button, and you'll jump to "In the Flesh?" (track 1) to experience the full album concept.

#### 4. **Album Playback Mode**
Once you're in album mode:
- The playlist is now the selected album
- Previous/Next buttons navigate within the album
- The album plays in order (no shuffle)
- When the album ends, playback stops

#### 5. **Return to Shuffle Mode**
To go back to shuffle mode and discover more music:
- Stop playback
- Disconnect and reconnect to Android Auto, or
- Restart the Aaplexamp app

(A future update will add a "Return to Shuffle" button in album mode)

## Visual Guide

```
┌─────────────────────────────────────────┐
│         SHUFFLE MODE (Initial)          │
│  Playing: Random song from library      │
│                                         │
│  [Prev]  [Play/Pause]  [Next]          │
│                                         │
│  Custom Actions Available:              │
│  • Play Album from Current Song         │
│  • Play Album from Start                │
└─────────────────────────────────────────┘
              │
              │ (User finds song they like)
              │ (Selects custom action)
              ▼
┌─────────────────────────────────────────┐
│          ALBUM MODE                     │
│  Playing: Album containing that song    │
│                                         │
│  [Prev]  [Play/Pause]  [Next]          │
│                                         │
│  (Album tracks only)                    │
└─────────────────────────────────────────┘
```

## Android Auto Interface

### Where to Find Custom Actions

In your Android Auto display:
1. Open the Now Playing screen (tap the music notification)
2. Look for action buttons at the bottom of the screen
3. You may need to scroll or tap "More" to see custom actions
4. The two album actions appear with distinct icons:
   - 📀 Play Album from Current
   - ▶️📀 Play Album from Start

### Typical Use Cases

**Discovery Mode** (Shuffle)
- "I want to explore my music library"
- "I'm not sure what I want to hear"
- "Let's see what comes up"

**Album Experience** (After finding a song)
- "I love this song, what else is on this album?"
- "I want to hear this album as the artist intended"
- "This sounds like a concept album"

**Perfect for Long Drives**
- Start with shuffle to set the mood
- When you find something good, dive into the full album
- Enjoy cohesive listening experiences

## Tips & Best Practices

### 1. **Be Patient with Shuffle**
Your library might be large. Hit next several times until you find the right vibe.

### 2. **Know Your Library**
The better curated your Plex library, the better the shuffle experience:
- Accurate metadata (artist, album, track numbers)
- Complete albums (not just singles)
- Properly tagged genres

### 3. **Use in Safe Conditions**
- Set up the app before driving
- Use voice commands when possible
- Pull over if you need to configure settings

### 4. **Album Discovery**
This workflow is great for rediscovering albums in your collection you haven't heard in years.

### 5. **Playlist Building**
After a drive where you discovered great albums, note which ones you played and create a Plex playlist for later.

## Troubleshooting

### "Custom actions don't appear"
- Make sure you're in shuffle mode (not album mode)
- Some Android Auto head units hide custom actions in a menu
- Try tapping the "more" or "⋮" button

### "Wrong album plays"
- Check that your Plex metadata is correct
- Ensure tracks are properly linked to albums
- Verify album artwork and track numbers are set

### "Shuffle repeats songs too quickly"
- This happens with small libraries
- Consider adding more music to your Plex server
- Check that all music libraries are scanned

### "Album plays in wrong order"
- Verify track numbers in Plex
- Re-scan your music library in Plex
- Check for duplicate tracks

## Advanced Usage

### Strategy: Genre Discovery
1. Start shuffle and hit next until you land on a genre you want to explore
2. Switch to album mode to dive deeper into that genre
3. Repeat for different genres

### Strategy: Decade Exploration
1. Use shuffle to randomly encounter different eras
2. When you hit a 70s track you like, play the full 70s album
3. Great for mood-based listening

### Strategy: Artist Deep Dive
1. Shuffle until you hear an artist you haven't listened to in a while
2. Switch to album mode
3. After the album, you could manually search for more albums by that artist

## Future Features (Roadmap)

Planned improvements:
- [ ] "Return to Shuffle" button in album mode
- [ ] Queue management (see upcoming tracks)
- [ ] "Play Artist's Other Albums" action
- [ ] Recently played history
- [ ] Favorites/bookmarking system
- [ ] Smart shuffle (genre/mood-based)
- [ ] Skip count tracking
- [ ] Album completion tracking

---

## FAQ

**Q: Can I start with a specific genre?**
A: Not yet. Currently, shuffle includes your entire library. Genre filtering is planned.

**Q: Can I queue multiple albums?**
A: No, each album mode replaces the queue. Consider using Plex playlists instead.

**Q: Does this work with playlists?**
A: Currently, no. The app works with your full library and albums only.

**Q: Can I use this without Android Auto?**
A: The app is designed for Android Auto, but it may work with regular Android media controls.

**Q: What about podcasts or audiobooks?**
A: This app is designed specifically for music. Use Plex's official app for other media.

**Q: How much data does this use?**
A: Depends on your setup. If on local WiFi: minimal. If streaming remotely: about 1-5 MB per song (varies by bitrate).

## Support

For issues or questions:
1. Check the [README.md](README.md) for setup instructions
2. Review [CONFIGURATION.md](CONFIGURATION.md) for detailed configuration help
3. Check Plex server logs for connection issues
4. Use Android Studio Logcat for debugging

Enjoy your music discovery journey! 🎵🚗
