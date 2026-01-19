package com.aaplexamp.auto.playback

import com.aaplexamp.auto.model.PlexTrack

sealed class PlaylistMode {
    object Shuffle : PlaylistMode()
    data class Album(val albumId: String) : PlaylistMode()
}

class PlaylistManager {
    private var currentMode: PlaylistMode = PlaylistMode.Shuffle
    private var shuffledLibrary: MutableList<PlexTrack> = mutableListOf()
    private var albumPlaylist: MutableList<PlexTrack> = mutableListOf()
    private var currentIndex: Int = 0
    
    fun setShuffleMode(tracks: List<PlexTrack>) {
        currentMode = PlaylistMode.Shuffle
        shuffledLibrary = tracks.shuffled().toMutableList()
        currentIndex = 0
    }
    
    fun setAlbumMode(tracks: List<PlexTrack>, albumId: String, startFromTrack: PlexTrack? = null) {
        currentMode = PlaylistMode.Album(albumId)
        albumPlaylist = tracks.sortedBy { it.trackNumber }.toMutableList()
        
        currentIndex = if (startFromTrack != null) {
            albumPlaylist.indexOfFirst { it.ratingKey == startFromTrack.ratingKey }.coerceAtLeast(0)
        } else {
            0
        }
    }
    
    fun getCurrentTrack(): PlexTrack? {
        return when (currentMode) {
            is PlaylistMode.Shuffle -> shuffledLibrary.getOrNull(currentIndex)
            is PlaylistMode.Album -> albumPlaylist.getOrNull(currentIndex)
        }
    }
    
    fun getNextTrack(): PlexTrack? {
        val playlist = when (currentMode) {
            is PlaylistMode.Shuffle -> shuffledLibrary
            is PlaylistMode.Album -> albumPlaylist
        }
        
        return if (currentIndex + 1 < playlist.size) {
            currentIndex++
            playlist[currentIndex]
        } else {
            null
        }
    }
    
    fun getPreviousTrack(): PlexTrack? {
        val playlist = when (currentMode) {
            is PlaylistMode.Shuffle -> shuffledLibrary
            is PlaylistMode.Album -> albumPlaylist
        }
        
        return if (currentIndex - 1 >= 0) {
            currentIndex--
            playlist[currentIndex]
        } else {
            null
        }
    }
    
    fun hasNext(): Boolean {
        val playlist = when (currentMode) {
            is PlaylistMode.Shuffle -> shuffledLibrary
            is PlaylistMode.Album -> albumPlaylist
        }
        return currentIndex + 1 < playlist.size
    }
    
    fun hasPrevious(): Boolean {
        return currentIndex > 0
    }
    
    fun getCurrentMode(): PlaylistMode = currentMode
    
    fun getCurrentPlaylist(): List<PlexTrack> {
        return when (currentMode) {
            is PlaylistMode.Shuffle -> shuffledLibrary
            is PlaylistMode.Album -> albumPlaylist
        }
    }
    
    fun getQueueSize(): Int {
        return when (currentMode) {
            is PlaylistMode.Shuffle -> shuffledLibrary.size
            is PlaylistMode.Album -> albumPlaylist.size
        }
    }
    
    fun getCurrentPosition(): Int = currentIndex
}
