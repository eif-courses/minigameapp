package eif.viko.lt.minigameapp.root.auth.presentation.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eif.viko.lt.minigameapp.root.auth.domain.models.User // Add this import
import eif.viko.lt.minigameapp.root.auth.domain.repository.AuthRepository // Add this import
import eif.viko.lt.minigameapp.root.auth.domain.use_cases.CheckAuthStatusUseCase
import eif.viko.lt.minigameapp.root.auth.domain.use_cases.SignOutUseCase
import kotlinx.coroutines.launch

class AuthStateViewModel(
    private val checkAuthStatusUseCase: CheckAuthStatusUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val authRepository: AuthRepository,
    private val context: Application
) : ViewModel() {

    var authState by mutableStateOf<AuthState>(AuthState.Loading)
        private set

    var currentUser by mutableStateOf<User?>(null)
        private set

    init {
        checkAuthStatus()
        // Start monitoring for auth changes
        startAuthMonitoring()
    }

    private fun startAuthMonitoring() {
        viewModelScope.launch {
            // Check auth status periodically to detect sign-ins
            while (true) {
                kotlinx.coroutines.delay(2000) // Check every 2 seconds

                // Only check if currently unauthenticated
                if (authState == AuthState.Unauthenticated) {
                    val isLoggedIn = checkAuthStatusUseCase()
                    if (isLoggedIn) {
                        println("🔍 Auto-detected sign-in, updating auth state")
                        checkAuthStatus()
                    }
                }
            }
        }
    }
    private fun checkAuthStatus() {
        viewModelScope.launch {
            println("🔍 Checking auth status...")
            authState = AuthState.Loading

            try {
                val isLoggedIn = checkAuthStatusUseCase()
                println("🔍 Is logged in: $isLoggedIn")

                if (isLoggedIn) {
                    currentUser = authRepository.getCurrentUser()
                    println("👤 Current user: ${currentUser?.email}")
                    authState = AuthState.Authenticated
                } else {
                    currentUser = null
                    authState = AuthState.Unauthenticated
                }
            } catch (e: Exception) {
                println("❌ Auth check failed: ${e.message}")
                currentUser = null
                authState = AuthState.Unauthenticated
            }
        }
    }

    fun signOut(){
        viewModelScope.launch {
            println("🚪 Signing out...")
            try {
                signOutUseCase()
                clearStoredOAuthCodes()
                currentUser = null
                authState = AuthState.Unauthenticated
                println("✅ Sign out successful")
            } catch (e: Exception) {
                println("❌ Sign out error: ${e.message}")
                currentUser = null
                authState = AuthState.Unauthenticated
            }
        }
    }

    fun onSignInSuccess(){
        println("🎉 Sign in success - refreshing auth status")
        checkAuthStatus()
    }

    fun refreshAuthStatus(){
        println("🔄 Refreshing auth status")
        checkAuthStatus()
    }

    private fun clearStoredOAuthCodes() {
        try {
            val prefs = context.getSharedPreferences("oauth_temp", Context.MODE_PRIVATE)
            prefs.edit().clear().apply()
            println("🧹 OAuth codes cleared")
        } catch (e: Exception) {
            println("❌ Failed to clear OAuth codes: ${e.message}")
        }
    }
}

sealed class AuthState {
    object Loading : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
}