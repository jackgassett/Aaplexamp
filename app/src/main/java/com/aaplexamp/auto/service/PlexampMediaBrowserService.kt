package com.aaplexamp.auto.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.aaplexamp.auto.R
import com.aaplexamp.auto.api.PlexApiClient
import com.aaplexamp.auto.model.PlexTrack
import com.aaplexamp.auto.playback.ExoPlayerManager
import com.aaplexamp.auto.playback.PlaylistManager
import com.aaplexamp.auto.playback.PlaylistMode
import com.google.android.exoplayer2.Player
import kotlinx.coroutines.*

class PlexampMediaBrowserService : MediaBrowserServiceCompat() {
    
    companion object {
        private const val CHANNEL_ID = "plexamp_playback"
        private const val NOTIFICATION_ID = 1
        
        // Custom actions for Android Auto
        const val ACTION_PLAY_ALBUM_FROM_CURRENT = "com.aaplexamp.auto.PLAY_ALBUM_FROM_CURRENT"
        const val ACTION_PLAY_ALBUM_FROM_START = "com.aaplexamp.auto.PLAY_ALBUM_FROM_START"
        const val ACTION_BACK_TO_SHUFFLE = "com.aaplexamp.auto.BACK_TO_SHUFFLE"
        
        private const val MEDIA_ROOT_ID = "root"
        private const val MEDIA_SHUFFLE_ID = "shuffle"
    }
    
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var stateBuilder: PlaybackStateCompat.Builder
    private lateinit var playlistManager: PlaylistManager
    private lateinit var exoPlayerManager: ExoPlayerManager
    private lateinit var plexApiClient: PlexApiClient
    
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    
    private var audioFocusRequest: AudioFocusRequest? = null
    private val audioManager by lazy { getSystemService(AUDIO_SERVICE) as AudioManager }
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize managers
        playlistManager = PlaylistManager()
        exoPlayerManager = ExoPlayerManager(this)
        
        // Load configuration from SharedPreferences
        val prefs = getSharedPreferences("plexamp_prefs", MODE_PRIVATE)
        val serverUrl = prefs.getString("server_url", "") ?: ""
        val serverToken = prefs.getString("server_token", "") ?: ""
        val accountToken = prefs.getString("auth_token", "") ?: ""
        
        // Use account token as fallback if server token doesn't work
        val token = if (serverToken.isNotEmpty()) serverToken else accountToken
        
        if (serverUrl.isEmpty() || token.isEmpty()) {
            // Configuration not set, service cannot function
            android.util.Log.e("PlexampService", "Server URL or token not configured")
            stopSelf()
            return
        }
        
        android.util.Log.d("PlexampService", "Initializing with server: $serverUrl")
        android.util.Log.d("PlexampService", "Using token: ${token.take(10)}...")
        plexApiClient = PlexApiClient(serverUrl, token)
        
        // Initialize ExoPlayer
        exoPlayerManager.initialize(playerListener)
        
        // Create notification channel
        createNotificationChannel()
        
        // Initialize MediaSession
        mediaSession = MediaSessionCompat(this, "PlexampSession").apply {
            setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )
            
            stateBuilder = PlaybackStateCompat.Builder()
                .setActions(getAvailableActions())
            setPlaybackState(stateBuilder.build())
            
            setCallback(mediaSessionCallback)
            setSessionToken(sessionToken)
        }
        
        // Start in shuffle mode
        serviceScope.launch {
            initializeShuffleMode()
        }
    }
    
    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_ENDED -> {
                    // Auto-advance to next track
                    mediaSessionCallback.onSkipToNext()
                }
            }
        }
    }
    
    private suspend fun initializeShuffleMode() {
        try {
            android.util.Log.d("PlexampService", "Initializing shuffle mode...")
            val sections = plexApiClient.getMusicLibrarySections()
            android.util.Log.d("PlexampService", "Got ${sections.mediaContainer.directory?.size ?: 0} library sections")
            
            val musicSection = sections.mediaContainer.directory
                ?.firstOrNull { it.type == "artist" }
            
            if (musicSection != null) {
                android.util.Log.d("PlexampService", "Found music library: ${musicSection.title}, section ID: ${musicSection.key}")
                android.util.Log.d("PlexampService", "Loading all tracks...")
                val allTracks = plexApiClient.getAllTracks(musicSection.key)
                val trackCount = allTracks.mediaContainer.metadata?.size ?: 0
                android.util.Log.d("PlexampService", "Loaded $trackCount tracks")
                
                allTracks.mediaContainer.metadata?.let { tracks ->
                    if (tracks.isEmpty()) {
                        android.util.Log.e("PlexampService", "No tracks found in music library!")
                        return
                    }
                    
                    android.util.Log.d("PlexampService", "Setting up shuffle with ${tracks.size} tracks")
                    playlistManager.setShuffleMode(tracks)
                    
                    // Auto-play first track
                    playlistManager.getCurrentTrack()?.let { track ->
                        android.util.Log.d("PlexampService", "Auto-playing: ${track.title}")
                        playTrack(track)
                    }
                }
            } else {
                android.util.Log.e("PlexampService", "No music library found in sections")
            }
        } catch (e: Exception) {
            android.util.Log.e("PlexampService", "Error initializing shuffle mode", e)
            e.printStackTrace()
        }
    }
    
    private val mediaSessionCallback = object : MediaSessionCompat.Callback() {
        
        override fun onPlay() {
            if (!requestAudioFocus()) return
            
            startService(Intent(applicationContext, PlexampMediaBrowserService::class.java))
            mediaSession.isActive = true
            
            val currentTrack = playlistManager.getCurrentTrack()
            if (currentTrack != null) {
                exoPlayerManager.resume()
                updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)
                showNotification()
            }
        }
        
        override fun onPause() {
            exoPlayerManager.pause()
            updatePlaybackState(PlaybackStateCompat.STATE_PAUSED)
            showNotification()
        }
        
        override fun onStop() {
            abandonAudioFocus()
            exoPlayerManager.stop()
            updatePlaybackState(PlaybackStateCompat.STATE_STOPPED)
            mediaSession.isActive = false
            stopForeground(true)
            stopSelf()
        }
        
        override fun onSkipToNext() {
            playlistManager.getNextTrack()?.let { track ->
                playTrack(track)
            }
        }
        
        override fun onSkipToPrevious() {
            playlistManager.getPreviousTrack()?.let { track ->
                playTrack(track)
            }
        }
        
        override fun onCustomAction(action: String, extras: Bundle?) {
            when (action) {
                ACTION_PLAY_ALBUM_FROM_CURRENT -> {
                    handlePlayAlbumFromCurrent()
                }
                ACTION_PLAY_ALBUM_FROM_START -> {
                    handlePlayAlbumFromStart()
                }
                ACTION_BACK_TO_SHUFFLE -> {
                    handleBackToShuffle()
                }
            }
        }
        
        override fun onSeekTo(pos: Long) {
            exoPlayerManager.seekTo(pos)
            updatePlaybackState(
                if (exoPlayerManager.isPlaying()) 
                    PlaybackStateCompat.STATE_PLAYING 
                else 
                    PlaybackStateCompat.STATE_PAUSED
            )
        }
    }
    
    private fun handlePlayAlbumFromCurrent() {
        val currentTrack = playlistManager.getCurrentTrack() ?: return
        
        serviceScope.launch {
            try {
                val albumTracks = plexApiClient.getAlbumTracks(currentTrack.parentRatingKey)
                albumTracks.mediaContainer.metadata?.let { tracks ->
                    playlistManager.setAlbumMode(
                        tracks, 
                        currentTrack.parentRatingKey,
                        startFromTrack = currentTrack
                    )
                    
                    // Continue playing current track in album context
                    updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)
                    updateMetadata(currentTrack)
                    showNotification()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    private fun handlePlayAlbumFromStart() {
        val currentTrack = playlistManager.getCurrentTrack() ?: return
        
        serviceScope.launch {
            try {
                val albumTracks = plexApiClient.getAlbumTracks(currentTrack.parentRatingKey)
                albumTracks.mediaContainer.metadata?.let { tracks ->
                    playlistManager.setAlbumMode(
                        tracks,
                        currentTrack.parentRatingKey,
                        startFromTrack = null
                    )
                    
                    // Play first track of album
                    playlistManager.getCurrentTrack()?.let { firstTrack ->
                        playTrack(firstTrack)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    private fun handleBackToShuffle() {
        serviceScope.launch {
            try {
                android.util.Log.d("PlexampService", "Returning to shuffle mode...")
                
                // Reload all tracks and return to shuffle mode
                val sections = plexApiClient.getMusicLibrarySections()
                val musicSection = sections.mediaContainer.directory
                    ?.firstOrNull { it.type == "artist" }
                
                if (musicSection != null) {
                    val allTracks = plexApiClient.getAllTracks(musicSection.key)
                    allTracks.mediaContainer.metadata?.let { tracks ->
                        playlistManager.setShuffleMode(tracks)
                        
                        // Play first shuffled track
                        playlistManager.getCurrentTrack()?.let { track ->
                            playTrack(track)
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("PlexampService", "Error returning to shuffle", e)
                e.printStackTrace()
            }
        }
    }
    
    private fun playTrack(track: PlexTrack) {
        val streamUrl = track.media?.firstOrNull()?.part?.firstOrNull()?.key?.let {
            plexApiClient.getTrackUrl(it)
        } ?: return
        
        exoPlayerManager.play(streamUrl)
        updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)
        updateMetadata(track)
        showNotification()
    }
    
    private fun updateMetadata(track: PlexTrack) {
        val metadata = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, track.title)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, track.artistName ?: "Unknown Artist")
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, track.albumTitle ?: "Unknown Album")
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, track.duration)
            .putString(
                MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
                plexApiClient.getImageUrl(track.albumThumb)
            )
            .build()
        
        mediaSession.setMetadata(metadata)
    }
    
    private fun updatePlaybackState(state: Int) {
        val actions = getAvailableActions()
        
        // Add custom actions based on mode
        val stateBuilder = PlaybackStateCompat.Builder()
            .setState(state, exoPlayerManager.getCurrentPosition(), 1.0f)
            .setActions(actions)
        
        if (playlistManager.getCurrentMode() is PlaylistMode.Shuffle) {
            // In shuffle mode, offer album options
            stateBuilder.addCustomAction(
                PlaybackStateCompat.CustomAction.Builder(
                    ACTION_PLAY_ALBUM_FROM_CURRENT,
                    "Play Album from Current",
                    R.drawable.ic_album // You'll need to add this icon
                ).build()
            )
            
            stateBuilder.addCustomAction(
                PlaybackStateCompat.CustomAction.Builder(
                    ACTION_PLAY_ALBUM_FROM_START,
                    "Play Album from Start",
                    R.drawable.ic_album_start // You'll need to add this icon
                ).build()
            )
        } else if (playlistManager.getCurrentMode() is PlaylistMode.Album) {
            // In album mode, offer option to return to shuffle
            stateBuilder.addCustomAction(
                PlaybackStateCompat.CustomAction.Builder(
                    ACTION_BACK_TO_SHUFFLE,
                    "Back to Shuffle",
                    android.R.drawable.ic_media_rew
                ).build()
            )
        }
        
        mediaSession.setPlaybackState(stateBuilder.build())
    }
    
    private fun getAvailableActions(): Long {
        var actions = PlaybackStateCompat.ACTION_PLAY_PAUSE or
                     PlaybackStateCompat.ACTION_PLAY or
                     PlaybackStateCompat.ACTION_PAUSE or
                     PlaybackStateCompat.ACTION_STOP or
                     PlaybackStateCompat.ACTION_SEEK_TO
        
        if (playlistManager.hasNext()) {
            actions = actions or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
        }
        
        if (playlistManager.hasPrevious()) {
            actions = actions or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
        }
        
        return actions
    }
    
    private fun showNotification() {
        val controller = mediaSession.controller
        val mediaMetadata = controller.metadata
        val description = mediaMetadata?.description
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(description?.title)
            .setContentText(description?.subtitle)
            .setSubText(description?.description)
            .setSmallIcon(R.drawable.ic_music_note) // You'll need to add this icon
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .addAction(
                R.drawable.ic_skip_previous,
                "Previous",
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this,
                    PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                )
            )
            .addAction(
                if (controller.playbackState?.state == PlaybackStateCompat.STATE_PLAYING)
                    R.drawable.ic_pause
                else
                    R.drawable.ic_play_arrow,
                if (controller.playbackState?.state == PlaybackStateCompat.STATE_PLAYING)
                    "Pause"
                else
                    "Play",
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this,
                    PlaybackStateCompat.ACTION_PLAY_PAUSE
                )
            )
            .addAction(
                R.drawable.ic_skip_next,
                "Next",
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this,
                    PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                )
            )
            .build()
        
        startForeground(NOTIFICATION_ID, notification)
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Plexamp Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Plexamp music playback controls"
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun requestAudioFocus(): Boolean {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        
        audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(audioAttributes)
            .setAcceptsDelayedFocusGain(true)
            .setOnAudioFocusChangeListener { focusChange ->
                when (focusChange) {
                    AudioManager.AUDIOFOCUS_GAIN -> exoPlayerManager.resume()
                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> exoPlayerManager.pause()
                    AudioManager.AUDIOFOCUS_LOSS -> mediaSessionCallback.onStop()
                }
            }
            .build()
        
        return audioManager.requestAudioFocus(audioFocusRequest!!) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }
    
    private fun abandonAudioFocus() {
        audioFocusRequest?.let {
            audioManager.abandonAudioFocusRequest(it)
        }
    }
    
    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        return BrowserRoot(MEDIA_ROOT_ID, null)
    }
    
    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        val mediaItems = mutableListOf<MediaBrowserCompat.MediaItem>()
        
        when (parentId) {
            MEDIA_ROOT_ID -> {
                // Return shuffle mode as browseable item
                val shuffleDesc = MediaDescriptionCompat.Builder()
                    .setMediaId(MEDIA_SHUFFLE_ID)
                    .setTitle("Shuffle All")
                    .build()
                
                mediaItems.add(
                    MediaBrowserCompat.MediaItem(
                        shuffleDesc,
                        MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
                    )
                )
            }
        }
        
        result.sendResult(mediaItems)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        exoPlayerManager.release()
        mediaSession.release()
        serviceJob.cancel()
    }
}
