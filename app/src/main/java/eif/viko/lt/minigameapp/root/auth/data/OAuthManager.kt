package eif.viko.lt.minigameapp.root.auth.data

import eif.viko.lt.minigameapp.root.auth.data.models.AuthResponse
import eif.viko.lt.minigameapp.root.auth.data.models.BattleNetTokenRequest
import eif.viko.lt.minigameapp.root.auth.data.remote.DiabloApiService
import androidx.core.net.toUri
import androidx.core.content.edit

// data/remote/OAuthManager.kt

import android.content.Context
import android.content.Intent
import android.net.Uri

import java.net.URLEncoder
import java.util.*

class OAuthManager(private val context: Context) {

    private val apiService = DiabloApiService.create()
    private val prefs = context.getSharedPreferences("diablo_auth", Context.MODE_PRIVATE)

    companion object {
        private const val BATTLE_NET_CLIENT_ID = "ee69f44ad0ba4e61967a260098cc77d8" // Replace with your actual client ID
        private const val REDIRECT_URI = "https://minigameapi-production.up.railway.app/oauth/mobile"
        private const val OAUTH_STATE_KEY = "oauth_state"
    }

    fun startBattleNetLogin(): Result<Unit> {
        val state = generateRandomState()
        saveOAuthState(state)

        val authUrl = buildBattleNetAuthUrl(state)

        return try {
            // Create intent with proper flags
            val intent = Intent(Intent.ACTION_VIEW, authUrl.toUri()).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addCategory(Intent.CATEGORY_BROWSABLE)
            }

            // Check if intent can be resolved
            val packageManager = context.packageManager
            if (intent.resolveActivity(packageManager) != null) {
                context.startActivity(intent)
                Result.success(Unit)
            } else {
                // Try to explicitly target Chrome if available
                tryLaunchWithChrome(authUrl)
            }
        } catch (e: Exception) {
            Result.failure(Exception("Failed to open browser: ${e.message}"))
        }
    }

    private fun tryLaunchWithChrome(url: String): Result<Unit> {
        return try {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                setPackage("com.android.chrome") // Explicitly target Chrome
            }

            context.startActivity(intent)
            Result.success(Unit)
        } catch (e: Exception) {
            // If Chrome-specific intent fails, try other browsers
            tryLaunchWithAnyBrowser(url)
        }
    }

    private fun tryLaunchWithAnyBrowser(url: String): Result<Unit> {
        val browserPackages = listOf(
            "com.android.chrome",
            "org.mozilla.firefox",
            "com.microsoft.emmx",
            "com.opera.browser",
            "com.android.browser"
        )

        for (packageName in browserPackages) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    setPackage(packageName)
                }

                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                    return Result.success(Unit)
                }
            } catch (e: Exception) {
                continue // Try next browser
            }
        }

        return Result.failure(Exception("No compatible browser found. Please install a web browser."))
    }

    private fun buildBattleNetAuthUrl(state: String): String {
        val baseUrl = "https://oauth.battle.net/authorize"
        val params = mapOf(
            "client_id" to BATTLE_NET_CLIENT_ID,
            "redirect_uri" to REDIRECT_URI,
            "response_type" to "code",
            "scope" to "wow.profile",
            "state" to state
        )

        val queryString = params.entries.joinToString("&") { (key, value) ->
            "$key=${URLEncoder.encode(value, "UTF-8")}"
        }

        return "$baseUrl?$queryString"
    }

    suspend fun handleAuthorizationCode(code: String, state: String): Result<AuthResponse> {
        if (!verifyOAuthState(state)) {
            return Result.failure(Exception("Invalid OAuth state"))
        }

        return try {
            val request = BattleNetTokenRequest(authorization_code = code)
            val response = apiService.loginWithBattleNet(request)

            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                saveAuthData(authResponse)
                clearOAuthState()
                Result.success(authResponse)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Login failed: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun saveAuthData(authResponse: AuthResponse) {
        prefs.edit {
            putString("access_token", authResponse.access_token)
                .putString("token_type", authResponse.token_type)
                .putString("user_id", authResponse.user.id)
                .putString("user_email", authResponse.user.email)
                .putString("user_first_name", authResponse.user.first_name)
                .putString("user_last_name", authResponse.user.last_name)
                .putString("provider", authResponse.provider)
                .putLong(
                    "expires_at",
                    System.currentTimeMillis() + (authResponse.expires_in * 1000)
                )
                .putLong("login_time", System.currentTimeMillis())
        }
    }

    fun getAuthToken(): String? {
        val token = prefs.getString("access_token", null)
        val tokenType = prefs.getString("token_type", "Bearer")
        val expiresAt = prefs.getLong("expires_at", 0)

        return if (token != null && System.currentTimeMillis() < expiresAt) {
            "$tokenType $token"
        } else {
            null
        }
    }

    fun isLoggedIn(): Boolean = getAuthToken() != null

    fun getUserInfo(): Map<String, String>? {
        return if (isLoggedIn()) {
            mapOf(
                "id" to (prefs.getString("user_id", "") ?: ""),
                "email" to (prefs.getString("user_email", "") ?: ""),
                "first_name" to (prefs.getString("user_first_name", "") ?: ""),
                "last_name" to (prefs.getString("user_last_name", "") ?: ""),
                "provider" to (prefs.getString("provider", "") ?: "")
            )
        } else null
    }

    fun logout() {
        prefs.edit { clear() }
    }

    private fun generateRandomState(): String = UUID.randomUUID().toString()

    private fun saveOAuthState(state: String) {
        prefs.edit { putString(OAUTH_STATE_KEY, state) }
    }

    private fun verifyOAuthState(state: String): Boolean {
        val savedState = prefs.getString(OAUTH_STATE_KEY, null)
        return savedState == state
    }

    private fun clearOAuthState() {
        prefs.edit { remove(OAUTH_STATE_KEY) }
    }
}
