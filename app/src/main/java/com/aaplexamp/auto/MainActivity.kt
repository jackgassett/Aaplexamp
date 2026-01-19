package com.aaplexamp.auto

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aaplexamp.auto.auth.PlexAuthManager
import com.aaplexamp.auto.auth.PlexAuthResult
import com.aaplexamp.auto.auth.PlexServer
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private lateinit var authManager: PlexAuthManager
    private lateinit var loginButton: Button
    private lateinit var statusText: TextView
    private lateinit var serverText: TextView
    private lateinit var progressBar: ProgressBar
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        authManager = PlexAuthManager(this)
        
        loginButton = findViewById(R.id.loginButton)
        statusText = findViewById(R.id.statusText)
        serverText = findViewById(R.id.serverText)
        progressBar = findViewById(R.id.progressBar)
        
        updateUI()
        
        loginButton.setOnClickListener {
            startPlexLogin()
        }
    }
    
    private fun updateUI() {
        val authToken = authManager.getAuthToken()
        val serverUrl = authManager.getSelectedServerUrl()
        val serverName = getSharedPreferences("plexamp_prefs", MODE_PRIVATE)
            .getString("server_name", null)
        
        if (authToken != null && serverUrl != null) {
            statusText.text = "✓ Signed in to Plex"
            serverText.text = "Server: $serverName\n$serverUrl"
            serverText.visibility = View.VISIBLE
            loginButton.text = "Change Server"
        } else if (authToken != null) {
            statusText.text = "Signed in - Select a server"
            serverText.visibility = View.GONE
            loginButton.text = "Select Server"
            showServerSelection(authToken)
        } else {
            statusText.text = "Not signed in"
            serverText.visibility = View.GONE
            loginButton.text = "Sign in with Plex"
        }
    }
    
    private fun startPlexLogin() {
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            loginButton.isEnabled = false
            statusText.text = "Connecting to Plex..."
            
            when (val result = authManager.startAuth()) {
                is PlexAuthResult.PinCreated -> {
                    // Open browser for authentication
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(result.authUrl))
                    startActivity(intent)
                    
                    statusText.text = "Complete sign-in in browser..."
                    
                    // Poll for authentication
                    when (val authResult = authManager.pollForAuth(result.pinId, result.code)) {
                        is PlexAuthResult.Success -> {
                            statusText.text = "Signed in! Loading servers..."
                            showServerSelection(authResult.authToken)
                        }
                        is PlexAuthResult.Error -> {
                            statusText.text = "Sign-in failed: ${authResult.message}"
                            Toast.makeText(this@MainActivity, authResult.message, Toast.LENGTH_LONG).show()
                        }
                        else -> {}
                    }
                }
                is PlexAuthResult.Error -> {
                    statusText.text = "Error: ${result.message}"
                    Toast.makeText(this@MainActivity, result.message, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
            
            progressBar.visibility = View.GONE
            loginButton.isEnabled = true
        }
    }
    
    private fun showServerSelection(authToken: String) {
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            statusText.text = "Loading your servers..."
            
            val servers = authManager.getServers(authToken)
            
            progressBar.visibility = View.GONE
            
            if (servers.isEmpty()) {
                val message = "No Plex Media Servers found. Make sure you have a Plex server set up and it's accessible."
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
                statusText.text = "No servers found"
                loginButton.text = "Try Again"
                return@launch
            }
            
            val serverNames = servers.map { 
                "${it.name}${if (it.owned) " (Mine)" else ""}"
            }.toTypedArray()
            
            AlertDialog.Builder(this@MainActivity)
                .setTitle("Select Plex Server")
                .setItems(serverNames) { _, which ->
                    val selectedServer = servers[which]
                    authManager.saveSelectedServer(selectedServer)
                    updateUI()
                    Toast.makeText(
                        this@MainActivity,
                        "Connected to ${selectedServer.name}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}
