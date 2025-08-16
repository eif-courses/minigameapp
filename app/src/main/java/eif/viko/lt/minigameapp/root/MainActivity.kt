package eif.viko.lt.minigameapp.root

import android.content.Intent
import android.net.Uri
import android.os.Bundle
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

    // Alternative: Handle new intents differently for newer Android versions
    override fun onResume() {
        super.onResume()
        // Re-handle the intent in case it changed
        handleOAuthIntent(intent)
    }

    private fun handleOAuthIntent(intent: Intent?) {
        val data: Uri? = intent?.data
        if (data != null) {
            val code = data.getQueryParameter("code")
            val state = data.getQueryParameter("state")

            if (code != null && state != null && pendingAuthCode == null) {
                pendingAuthCode = code
                pendingAuthState = state
            }
        }
    }
}