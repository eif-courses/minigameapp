package eif.viko.lt.minigameapp.root.auth.presentation.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.core.graphics.toColorInt



@Composable
fun BattleNetSignInButton(
    onAuthCode: (String) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var hasStartedOAuth by remember { mutableStateOf(false) }

    // Periodically check for OAuth code if OAuth flow was started
    LaunchedEffect(hasStartedOAuth) {
        if (hasStartedOAuth) {
            while (hasStartedOAuth) {
                kotlinx.coroutines.delay(1000) // Check every second

                val prefs = context.getSharedPreferences("oauth_temp", Context.MODE_PRIVATE)
                val authCode = prefs.getString("auth_code", null)

                if (authCode != null) {
                    prefs.edit().remove("auth_code").apply()
                    hasStartedOAuth = false
                    onAuthCode(authCode)
                    break
                }
            }
        }
    }

    Button(
        onClick = {
            val prefs = context.getSharedPreferences("oauth_temp", Context.MODE_PRIVATE)
            val existingCode = prefs.getString("auth_code", null)

            if (existingCode != null) {
                prefs.edit().remove("auth_code").apply()
                onAuthCode(existingCode)
            } else {
                prefs.edit().clear().apply()
                hasStartedOAuth = true // Start monitoring for OAuth code

                val authUrl = buildBattleNetAuthUrl()
                val customTabsIntent = CustomTabsIntent.Builder()
                    .setToolbarColor("#0084FF".toColorInt())
                    .setShowTitle(true)
                    .build()

                try {
                    customTabsIntent.launchUrl(context, authUrl.toUri())
                } catch (e: Exception) {
                    val intent = Intent(Intent.ACTION_VIEW, authUrl.toUri())
                    context.startActivity(intent)
                }
            }
        },
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF0084FF)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Face,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign in with Battle.net", color = Color.White)
        }
    }
}

private fun buildBattleNetAuthUrl(): String {
    val clientId = "ee69f44ad0ba4e61967a260098cc77d8"
    val redirectUri = "https://minigameapi-production.up.railway.app/oauth/mobile"
    val scope = "d3.profile"
    val state = java.util.UUID.randomUUID().toString()

    return "https://oauth.battle.net/authorize" +
            "?client_id=$clientId" +
            "&redirect_uri=$redirectUri" +
            "&response_type=code" +
            "&scope=$scope" +
            "&state=$state"
}