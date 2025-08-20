package eif.viko.lt.minigameapp.root.auth.domain.use_cases

import eif.viko.lt.minigameapp.root.auth.domain.repository.AuthRepository

class SignOutUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() {
        repository.signOut()
    }
}