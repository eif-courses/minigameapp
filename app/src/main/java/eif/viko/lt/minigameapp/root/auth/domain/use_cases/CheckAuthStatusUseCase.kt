package eif.viko.lt.minigameapp.root.auth.domain.use_cases

import eif.viko.lt.minigameapp.root.auth.domain.repository.AuthRepository

class CheckAuthStatusUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Boolean{
        return repository.isUserIsSignedIn()
    }
}