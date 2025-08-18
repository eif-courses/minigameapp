package eif.viko.lt.minigameapp.root.auth.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eif.viko.lt.minigameapp.root.auth.domain.models.AuthResult
import eif.viko.lt.minigameapp.root.auth.domain.models.User
import eif.viko.lt.minigameapp.root.auth.domain.use_cases.SignInUseCase
import kotlinx.coroutines.launch

class SignInViewModel (
    private val signInUseCase: SignInUseCase
): ViewModel(){

    var uiState by mutableStateOf(SignInUiState())

    fun signIn(email: String, password: String){
        if (uiState.isLoading) return

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)

            when(val result = signInUseCase(email, password)){
                is AuthResult.Success ->{
                    uiState = uiState.copy(
                        isLoading = false,
                        isSignedIn = true,
                        user = result.data.user,
                        accessToken = result.data.accessToken
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
                is AuthResult.Loading -> {

                }
            }
        }
    }




    fun clearError() {
        uiState = uiState.copy(error = null)
    }

    fun signOut() {
        uiState = SignInUiState() // Reset to initial state
    }
}


data class SignInUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSignedIn: Boolean = false,
    val user: User? = null,
    val accessToken: String? = null
)