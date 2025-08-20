package eif.viko.lt.minigameapp.root.auth.domain.use_cases

import eif.viko.lt.minigameapp.root.auth.domain.models.AuthResult
import eif.viko.lt.minigameapp.root.auth.domain.models.TokenData
import eif.viko.lt.minigameapp.root.auth.domain.repository.AuthRepository

class SignInUseCase(
    private val repository: AuthRepository
){
    suspend operator fun invoke(email: String, password: String): AuthResult<TokenData> {
        // Input validation
        if (email.isBlank()) {
            return AuthResult.Error("Email cannot be empty")
        }

        if (password.isBlank()) {
            return AuthResult.Error("Password cannot be empty")
        }

        if (!isValidEmail(email.trim())) {
            return AuthResult.Error("Please enter a valid email address")
        }

        if (password.length < 6) {
            return AuthResult.Error("Password must be at least 6 characters")
        }

        // Delegate to repository
        return repository.signIn(email.trim().lowercase(), password)
    }
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}