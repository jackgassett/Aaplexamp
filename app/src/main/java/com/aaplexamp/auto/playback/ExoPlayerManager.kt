package com.aaplexamp.auto.playback

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource

class ExoPlayerManager(private val context: Context) {
    
    private var exoPlayer: ExoPlayer? = null
    private var playerListener: Player.Listener? = null
    
    fun initialize(listener: Player.Listener) {
        playerListener = listener
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            addListener(listener)
        }
    }
    
    fun play(streamUrl: String) {
        exoPlayer?.let { player ->
            val dataSourceFactory = DefaultHttpDataSource.Factory()
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(streamUrl)))
            
            player.setMediaSource(mediaSource)
            player.prepare()
            player.playWhenReady = true
        }
    }
    
    fun pause() {
        exoPlayer?.playWhenReady = false
    }
    
    fun resume() {
        exoPlayer?.playWhenReady = true
    }
    
    fun stop() {
        exoPlayer?.stop()
    }
    
    fun release() {
        playerListener?.let { exoPlayer?.removeListener(it) }
        exoPlayer?.release()
        exoPlayer = null
    }
    
    fun isPlaying(): Boolean = exoPlayer?.isPlaying ?: false
    
    fun getCurrentPosition(): Long = exoPlayer?.currentPosition ?: 0
    
    fun getDuration(): Long = exoPlayer?.duration ?: 0
    
    fun seekTo(positionMs: Long) {
        exoPlayer?.seekTo(positionMs)
    }
}
