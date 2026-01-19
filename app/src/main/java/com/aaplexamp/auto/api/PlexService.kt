package com.aaplexamp.auto.api

import com.aaplexamp.auto.model.*
import retrofit2.http.*

interface PlexService {
    
    @GET("library/sections/{sectionId}/all")
    suspend fun getAllTracks(
        @Path("sectionId") sectionId: String,
        @Query("type") type: Int = 10, // 10 = Track
        @Header("X-Plex-Token") token: String
    ): PlexContainer<PlexTrack>
    
    @GET("library/metadata/{albumId}/children")
    suspend fun getAlbumTracks(
        @Path("albumId") albumId: String,
        @Header("X-Plex-Token") token: String
    ): PlexContainer<PlexTrack>
    
    @GET("library/metadata/{albumId}")
    suspend fun getAlbum(
        @Path("albumId") albumId: String,
        @Header("X-Plex-Token") token: String
    ): PlexContainer<PlexAlbum>
    
    @GET("library/sections")
    suspend fun getLibrarySections(
        @Header("X-Plex-Token") token: String
    ): PlexContainer<PlexLibrarySection>
}

data class PlexLibrarySection(
    val key: String,
    val type: String,
    val title: String
)
