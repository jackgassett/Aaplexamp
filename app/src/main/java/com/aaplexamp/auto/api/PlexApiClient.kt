package com.aaplexamp.auto.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class PlexApiClient(
    private val serverUrl: String,
    private val token: String
) {
    private val plexService: PlexService
    
    init {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .header("Accept", "application/json")
                    .build()
                chain.proceed(request)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
        
        val retrofit = Retrofit.Builder()
            .baseUrl(serverUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        plexService = retrofit.create(PlexService::class.java)
    }
    
    suspend fun getAllTracks(sectionId: String) = 
        plexService.getAllTracks(sectionId, token = token)
    
    suspend fun getAlbumTracks(albumId: String) = 
        plexService.getAlbumTracks(albumId, token = token)
    
    suspend fun getAlbum(albumId: String) = 
        plexService.getAlbum(albumId, token = token)
    
    suspend fun getMusicLibrarySections() = 
        plexService.getLibrarySections(token = token)
    
    fun getTrackUrl(trackKey: String): String {
        return "$serverUrl$trackKey?X-Plex-Token=$token"
    }
    
    fun getImageUrl(thumbPath: String?): String? {
        if (thumbPath == null) return null
        return "$serverUrl$thumbPath?X-Plex-Token=$token"
    }
}
