package eif.viko.lt.minigameapp.root.auth.presentation.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eif.viko.lt.minigameapp.root.auth.domain.models.AuthResult
import eif.viko.lt.minigameapp.root.auth.domain.models.User
import eif.viko.lt.minigameapp.root.auth.domain.repository.AuthRepository
import eif.viko.lt.minigameapp.root.auth.domain.use_cases.SignInUseCase
import eif.viko.lt.minigameapp.root.auth.domain.use_cases.SignInWithBattleNetUseCase
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SignInViewModel(
    private val signInUseCase: SignInUseCase,
    private val signInWithBattleNetUseCase: SignInWithBattleNetUseCase,
    private val authRepository: AuthRepository
): ViewModel(), KoinComponent {

    private val context: Application by inject()

    var uiState by mutableStateOf(SignInUiState())

    init {
        checkAuthStatus()
        checkForOAuthCode()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            val isLoggedIn = authRepository.isUserIsSignedIn()
            uiState = uiState.copy(isSignedIn = isLoggedIn)
        }
    }

    private fun checkForOAuthCode() {
        viewModelScope.launch {
            val prefs = context.getSharedPreferences("oauth_temp", Context.MODE_PRIVATE)
            val authCode = prefs.getString("auth_code", null)
            val authError = prefs.getString("auth_error", null)

            when {
                authCode != null -> {
                    // Clear the stored code
                    prefs.edit().remove("auth_code").apply()
                    // Process the auth code
                    signInWithBattleNet(authCode)
                }
                authError != null -> {
                    // Clear the stored error
                    prefs.edit().remove("auth_error").apply()
                    // Show error
                    uiState = uiState.copy(
                        isLoading = false,
                        error = "Battle.net authentication failed: $authError"
                    )
                }
            }
        }
    }

    fun signIn(email: String, password: String) {
        if (uiState.isLoading) return

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)

            when(val result = signInUseCase(email, password)) {
                is AuthResult.Success -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        isSignedIn = true,
                        user = result.data.user
                    )
                }
                is AuthResult.Error -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        error = result.message ?: "An unexpected error occurred"
                    )
                }
                is AuthResult.Unauthorized -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        error = "Invalid email or password. Please try again."
                    )
                }
                is AuthResult.Loading -> {}
            }
        }
    }

    fun signInWithBattleNet(authorizationCode: String) {
        if (uiState.isLoading) return

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)

            when(val result = signInWithBattleNetUseCase(authorizationCode)) {
                is AuthResult.Success -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        isSignedIn = true,
                        user = result.data.user
                    )
                }
                is AuthResult.Error -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        error = result.message ?: "Battle.net authentication failed"
                    )
                }
                is AuthResult.Unauthorized -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        error = "Battle.net authentication failed. Please try again."
                    )
                }
                is AuthResult.Loading -> {}
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            uiState = SignInUiState()
        }
    }

    fun clearError() {
        uiState = uiState.copy(error = null)
    }
}

data class SignInUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSignedIn: Boolean = false,
    val user: User? = null
)