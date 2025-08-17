// In your MainActivity.kt
package eif.viko.lt.minigameapp.root

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import eif.viko.lt.minigameapp.root.auth.presentation.login.LoginScreen
import eif.viko.lt.minigameapp.root.auth.presentation.login.LoginViewModel

import eif.viko.lt.minigameapp.root.ui.theme.MinigameappTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    private var pendingAuthCode: String? = null
    private var pendingAuthState: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Handle OAuth redirect from intent
        handleOAuthIntent(intent)

        setContent {
            MinigameappTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewModel: LoginViewModel = koinViewModel()

                    // Handle pending OAuth redirect
                    LaunchedEffect(pendingAuthCode, pendingAuthState) {
                        if (pendingAuthCode != null && pendingAuthState != null) {
                            Log.d("OAuth", "Processing auth code: $pendingAuthCode")
                            viewModel.handleAuthorizationCode(pendingAuthCode!!, pendingAuthState!!)
                            pendingAuthCode = null
                            pendingAuthState = null
                        }
                    }

                    LoginScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleOAuthIntent(intent)
    }

    private fun handleOAuthIntent(intent: Intent?) {
        val data: Uri? = intent?.data
        if (data != null) {
            Log.d("OAuth", "Received intent: ${data.toString()}")

            val scheme = data.scheme
            val host = data.host

            when {
                // Handle custom scheme: eif.viko.lt.minigameapp.root://oauth?code=...&state=...&success=true
                scheme == "minigameapp" && host == "oauth" -> {
                    val success = data.getBooleanQueryParameter("success", false)
                    val error = data.getQueryParameter("error")
                    val code = data.getQueryParameter("code")
                    val state = data.getQueryParameter("state")

                    Log.d("OAuth", "Custom scheme callback - success: $success, code: $code, error: $error")

                    when {
                        success && code != null && state != null -> {
                            Log.d("OAuth", "Setting pending auth code")
                            pendingAuthCode = code
                            pendingAuthState = state
                        }
                        error != null -> {
                            Log.e("OAuth", "OAuth error received: $error")
                            // You could show an error message in your UI here
                        }
                    }
                }
                // Fallback: Handle regular OAuth callback URLs
                data.toString().contains("code=") -> {
                    val code = data.getQueryParameter("code")
                    val state = data.getQueryParameter("state")

                    Log.d("OAuth", "Regular callback - code: $code, state: $state")

                    if (code != null && state != null) {
                        pendingAuthCode = code
                        pendingAuthState = state
                    }
                }
            }
        }
    }
}