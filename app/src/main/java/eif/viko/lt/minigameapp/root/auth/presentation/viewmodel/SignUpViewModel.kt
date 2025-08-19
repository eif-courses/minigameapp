package eif.viko.lt.minigameapp.root.auth.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eif.viko.lt.minigameapp.root.auth.domain.models.AuthResult
import eif.viko.lt.minigameapp.root.auth.domain.models.User
import eif.viko.lt.minigameapp.root.auth.domain.use_cases.SignUpUseCase
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val signUpUseCase: SignUpUseCase
): ViewModel() {

    var uiState by mutableStateOf(SignUpUiState())

    fun signUp(email: String, password: String, firstName: String, lastName: String) {
        if (uiState.isLoading) return

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)

            when(val result = signUpUseCase(email, password, firstName, lastName)) {
                is AuthResult.Success -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        isSignedUp = true,
                        user = result.data,
                        successMessage = "Registration successful. Please login."
                    )
                }
                is AuthResult.Error -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        error = result.message ?: "Registration failed"
                    )
                }
                is AuthResult.Unauthorized -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        error = "Registration failed. Please try again."
                    )
                }
                is AuthResult.Loading -> {
                    uiState = uiState.copy(isLoading = true)
                }
            }
        }
    }

    fun clearError() {
        uiState = uiState.copy(error = null)
    }
}

data class SignUpUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSignedUp: Boolean = false,
    val user: User? = null,
    val successMessage: String? = null
)