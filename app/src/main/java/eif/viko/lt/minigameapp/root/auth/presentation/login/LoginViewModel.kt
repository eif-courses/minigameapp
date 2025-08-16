package eif.viko.lt.minigameapp.root.auth.presentation.login

// presentation/login/LoginViewModel.kt (create this file)


// presentation/login/LoginViewModel.kt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eif.viko.lt.minigameapp.root.auth.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        _state.value = _state.value.copy(
            isLoggedIn = userRepository.isLoggedIn(),
            userInfo = userRepository.getUserInfo()
        )
    }

    fun handleAuthorizationCode(code: String, state: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val result = userRepository.handleAuthorizationCode(code, state)

            result.onSuccess { authResponse ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    isLoggedIn = true,
                    userInfo = mapOf(
                        "first_name" to authResponse.user.first_name,
                        "email" to authResponse.user.email
                    ),
                    message = "Login successful!"
                )
            }.onFailure { error ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = error.message
                )
            }
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val result = userRepository.getProfile()

            result.onSuccess { profile ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    profile = profile
                )
            }.onFailure { error ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = error.message
                )
            }
        }
    }

    fun loadActs() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val result = userRepository.getActs()

            result.onSuccess { acts ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    acts = acts.acts
                )
            }.onFailure { error ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = error.message
                )
            }
        }
    }

    fun startLogin() {
        userRepository.startLogin()
    }

    fun logout() {
        userRepository.logout()
        _state.value = LoginState()
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun clearMessage() {
        _state.value = _state.value.copy(message = null)
    }
}