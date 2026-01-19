package com.aaplexamp.auto.auth

import android.content.Context
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID

class PlexAuthManager(private val context: Context) {
    
    companion object {
        private const val PLEX_AUTH_URL = "https://plex.tv/api/v2/"
        private const val PLEX_WEB_AUTH_URL = "https://app.plex.tv/auth#?"
        private const val PREFS_NAME = "plexamp_prefs"
    }
    
    private val clientId: String by lazy {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        var id = prefs.getString("client_id", null)
        if (id == null) {
            id = UUID.randomUUID().toString()
            prefs.edit().putString("client_id", id).apply()
        }
        id
    }
    
    private val authService: PlexAuthService
    
    init {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        
        val retrofit = Retrofit.Builder()
            .baseUrl(PLEX_AUTH_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        authService = retrofit.create(PlexAuthService::class.java)
    }
    
    suspend fun startAuth(): PlexAuthResult {
        try {
            val pinResponse = authService.createAuthPin(clientId)
            val authUrl = "$PLEX_WEB_AUTH_URL" +
                    "clientID=$clientId&" +
                    "code=${pinResponse.code}&" +
                    "context%5Bdevice%5D%5Bproduct%5D=Aaplexamp"
            
            return PlexAuthResult.PinCreated(
                pinId = pinResponse.id,
                code = pinResponse.code,
                authUrl = authUrl
            )
        } catch (e: Exception) {
            return PlexAuthResult.Error(e.message ?: "Failed to create auth pin")
        }
    }
    
    suspend fun pollForAuth(pinId: Int, code: String, maxAttempts: Int = 60): PlexAuthResult {
        repeat(maxAttempts) {
            try {
                val pin = authService.checkAuthPin(pinId, clientId, code)
                if (pin.authToken != null) {
                    saveAuthToken(pin.authToken)
                    return PlexAuthResult.Success(pin.authToken)
                }
            } catch (e: Exception) {
                // Continue polling
            }
            delay(2000) // Wait 2 seconds between checks
        }
        return PlexAuthResult.Error("Authentication timeout")
    }
    
    suspend fun getServers(authToken: String): List<PlexServer> {
        return try {
            android.util.Log.d("PlexAuth", "Fetching servers with token: ${authToken.take(10)}...")
            val servers = authService.getServers(authToken, clientId)
            android.util.Log.d("PlexAuth", "Found ${servers.size} servers")
            servers.forEach { server ->
                android.util.Log.d("PlexAuth", "Server: ${server.name}, owned=${server.owned}, provides=${server.provides}")
                server.connections?.forEach { conn ->
                    android.util.Log.d("PlexAuth", "  Connection: ${conn.uri} (local=${conn.local}, relay=${conn.relay})")
                }
            }
            // Filter to only media servers (not clients)
            servers.filter { it.provides?.contains("server") == true }
        } catch (e: Exception) {
            android.util.Log.e("PlexAuth", "Error fetching servers", e)
            emptyList()
        }
    }
    
    fun saveAuthToken(token: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString("auth_token", token)
            .apply()
    }
    
    fun getAuthToken(): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString("auth_token", null)
    }
    
    fun saveSelectedServer(server: PlexServer) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        
        // Use the connections list if available, prioritizing HTTPS then remote HTTP, then local
        val serverUrl = if (server.connections != null && server.connections.isNotEmpty()) {
            val httpsRemote = server.connections.firstOrNull { it.protocol == "https" && !it.local }
            val httpRemote = server.connections.firstOrNull { it.protocol == "http" && !it.local }
            val httpsLocal = server.connections.firstOrNull { it.protocol == "https" && it.local }
            val httpLocal = server.connections.firstOrNull { it.protocol == "http" && it.local }
            
            val connection = httpsRemote ?: httpRemote ?: httpsLocal ?: httpLocal ?: server.connections.first()
            connection.uri
        } else {
            // Fallback to old logic
            val addresses = server.localAddresses?.split(",") ?: emptyList()
            val port = server.port ?: 32400
            val useHttps = server.httpsRequired == true || server.publicAddress != null
            
            when {
                server.publicAddress != null -> "${if (useHttps) "https" else "http"}://${server.publicAddress}:$port"
                addresses.isNotEmpty() -> "http://${addresses.first()}:$port"
                server.host != null -> "${if (useHttps) "https" else "http"}://${server.host}:$port"
                else -> "http://localhost:$port"
            }
        }
        
        android.util.Log.d("PlexAuth", "Saving server: $serverUrl")
        
        prefs.edit()
            .putString("server_url", serverUrl)
            .putString("server_token", server.accessToken)
            .putString("server_name", server.name)
            .apply()
    }
    
    fun getSelectedServerUrl(): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString("server_url", null)
    }
    
    fun getSelectedServerToken(): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString("server_token", null)
    }
}

sealed class PlexAuthResult {
    data class PinCreated(val pinId: Int, val code: String, val authUrl: String) : PlexAuthResult()
    data class Success(val authToken: String) : PlexAuthResult()
    data class Error(val message: String) : PlexAuthResult()
}
