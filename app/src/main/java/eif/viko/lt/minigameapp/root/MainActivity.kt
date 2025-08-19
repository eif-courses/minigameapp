// In your MainActivity.kt
package eif.viko.lt.minigameapp.root

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import eif.viko.lt.minigameapp.root.navigation.presentation.bottom_nav.RootGraph


import eif.viko.lt.minigameapp.root.ui.theme.MinigameappTheme
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        // Handle OAuth callback
        handleOAuthCallback(intent)


        setContent {
            MinigameappTheme {
                RootGraph()
            }
        }
    }


    override fun onNewIntent(intent: Intent) { // Changed Intent? to Intent
        super.onNewIntent(intent)
        handleOAuthCallback(intent) // No need for intent?.let anymore if intent is non-nullable
    }

    private fun handleOAuthCallback(intent: Intent) {
        val data: Uri? = intent.data
        if (data != null && data.scheme == "minigameapp" && data.host == "oauth") {
            val authCode = data.getQueryParameter("code")
            val error = data.getQueryParameter("error")

            when {
                authCode != null -> {
                    handleAuthSuccess(authCode)
                }
                error != null -> {
                    handleAuthError(error)
                }
            }
        }
    }

    private fun handleAuthSuccess(authCode: String) {
        // Store the auth code temporarily so your ViewModel can pick it up
        val prefs = getSharedPreferences("oauth_temp", MODE_PRIVATE)
        prefs.edit().putString("auth_code", authCode).apply()
    }

    private fun handleAuthError(error: String) {
        // Handle error - you can show a toast or log it
        val prefs = getSharedPreferences("oauth_temp", MODE_PRIVATE)
        prefs.edit().putString("auth_error", error).apply()
    }


}
