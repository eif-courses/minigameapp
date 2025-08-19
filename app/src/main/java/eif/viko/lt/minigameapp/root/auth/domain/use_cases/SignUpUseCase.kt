package eif.viko.lt.minigameapp.root.auth.domain.use_cases

import eif.viko.lt.minigameapp.root.auth.domain.models.AuthResult
import eif.viko.lt.minigameapp.root.auth.domain.models.User
import eif.viko.lt.minigameapp.root.auth.domain.repository.AuthRepository

class SignUpUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ): AuthResult<User> {
        // Validation
        if (email.isBlank()) {
            return AuthResult.Error("Email cannot be empty")
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return AuthResult.Error("Please enter a valid email address")
        }
        if (password.isBlank()) {
            return AuthResult.Error("Password cannot be empty")
        }
        if (password.length < 6) {
            return AuthResult.Error("Password must be at least 6 characters")
        }
        if (firstName.isBlank()) {
            return AuthResult.Error("First name cannot be empty")
        }
        if (lastName.isBlank()) {
            return AuthResult.Error("Last name cannot be empty")
        }

        return repository.signUp(email, password, firstName, lastName)
    }
}