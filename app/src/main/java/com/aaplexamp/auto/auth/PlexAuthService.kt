package com.aaplexamp.auto.auth

import retrofit2.http.*

interface PlexAuthService {
    
    @Headers("Accept: application/json")
    @POST("pins")
    suspend fun createAuthPin(
        @Header("X-Plex-Client-Identifier") clientId: String,
        @Header("X-Plex-Product") product: String = "Aaplexamp",
        @Header("X-Plex-Version") version: String = "1.0",
        @Header("X-Plex-Device") device: String = "Android",
        @Header("X-Plex-Device-Name") deviceName: String = "Android Phone",
        @Query("strong") strong: Boolean = true
    ): PlexAuthPinResponse
    
    @Headers("Accept: application/json")
    @GET("pins/{id}")
    suspend fun checkAuthPin(
        @Path("id") pinId: Int,
        @Header("X-Plex-Client-Identifier") clientId: String,
        @Query("code") code: String
    ): PlexAuthPin
    
    @Headers("Accept: application/json")
    @GET("users/account")
    suspend fun getUserInfo(
        @Header("X-Plex-Token") token: String
    ): PlexUserResponse
    
    @Headers("Accept: application/json")
    @GET("resources")
    suspend fun getServers(
        @Header("X-Plex-Token") token: String,
        @Header("X-Plex-Client-Identifier") clientId: String,
        @Query("includeHttps") includeHttps: Int = 1,
        @Query("includeRelay") includeRelay: Int = 1
    ): List<PlexServer>
}
