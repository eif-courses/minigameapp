package eif.viko.lt.minigameapp.root.auth.presentation.viewmodel

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
    private val signOutUseCase: SignOutUseCase
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
            signOutUseCase()
            authState = AuthState.Unauthenticated
        }
    }
    fun onSignInSuccess(){
        authState = AuthState.Authenticated
    }
    fun refreshAuthStatus(){
        checkAuthStatus()
    }


}

sealed class AuthState {
    object Loading : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
}