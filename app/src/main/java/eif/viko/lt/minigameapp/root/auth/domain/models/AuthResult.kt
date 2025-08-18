package eif.viko.lt.minigameapp.root.auth.domain.models

sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val message: String? = null) : AuthResult<Nothing>()
    object Unauthorized : AuthResult<Nothing>()
    object Loading : AuthResult<Nothing>()
}