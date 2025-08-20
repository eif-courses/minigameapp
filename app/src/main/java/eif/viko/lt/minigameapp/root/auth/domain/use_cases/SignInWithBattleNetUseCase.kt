package eif.viko.lt.minigameapp.root.auth.domain.use_cases

import eif.viko.lt.minigameapp.root.auth.domain.models.AuthResult
import eif.viko.lt.minigameapp.root.auth.domain.models.TokenData
import eif.viko.lt.minigameapp.root.auth.domain.repository.AuthRepository

class SignInWithBattleNetUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(authorizationCode: String): AuthResult<TokenData> {
        if (authorizationCode.isBlank()) {
            return AuthResult.Error("Authorization code cannot be empty")
        }

        return repository.signInWithBattleNet(authorizationCode)
    }
}