package eif.viko.lt.minigameapp.root.auth.domain.repository

import eif.viko.lt.minigameapp.root.auth.domain.models.AuthResult
import eif.viko.lt.minigameapp.root.auth.domain.models.TokenData

interface AuthRepository {
    suspend fun signIn(email: String, password: String): AuthResult<TokenData>
    suspend fun signOut()
    suspend fun isUserIsSignedIn(): Boolean
    suspend fun getCurrentToken(): String?
}