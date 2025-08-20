package eif.viko.lt.minigameapp.root.auth.presentation.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eif.viko.lt.minigameapp.root.auth.domain.use_cases.CheckAuthStatusUseCase
import eif.viko.lt.minigameapp.root.auth.domain.use_cases.SignOutUseCase
import kotlinx.coroutines.launch

class AuthStateViewModel(
    private val checkAuthStatusUseCase: CheckAuthStatusUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val context: Application
) : ViewModel() {

    var authState by mutableStateOf<AuthState>(AuthState.Loading)
        private set

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            authState = if (checkAuthStatusUseCase()) {
                AuthState.Authenticated
            } else {
                AuthState.Unauthenticated
            }
        }
    }
    fun signOut(){
        viewModelScope.launch {

            try {
                // Clear any stored auth codes that might cause auto-login
                signOutUseCase()
                clearStoredOAuthCodes()

                // Update state
                authState = AuthState.Unauthenticated
            } catch (e: Exception) {
                // Even if sign out fails, set to unauthenticated
                authState = AuthState.Unauthenticated
            }

        }
    }
    fun onSignInSuccess(){
        authState = AuthState.Authenticated
    }
    fun refreshAuthStatus(){
        checkAuthStatus()
    }

    private fun clearStoredOAuthCodes() {
        try {
            // Clear SharedPreferences oauth codes
            // This prevents the automatic login issue you mentioned earlier
            val prefs = context.getSharedPreferences("oauth_temp", Context.MODE_PRIVATE)
            prefs.edit().clear().apply()
        } catch (e: Exception) {
            // Ignore errors
        }
    }


}

sealed class AuthState {
    object Loading : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
}