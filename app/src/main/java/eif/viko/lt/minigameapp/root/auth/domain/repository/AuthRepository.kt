package eif.viko.lt.minigameapp.root.auth.domain.repository

import eif.viko.lt.minigameapp.root.auth.domain.models.AuthResult
import eif.viko.lt.minigameapp.root.auth.domain.models.TokenData
import eif.viko.lt.minigameapp.root.auth.domain.models.User

interface AuthRepository {
    suspend fun signIn(email: String, password: String): AuthResult<TokenData>
    suspend fun signUp(email: String, password: String, firstName: String, lastName: String): AuthResult<User>
    suspend fun signOut()
    suspend fun isUserIsSignedIn(): Boolean
    suspend fun getCurrentToken(): String?

    //OAuth
    suspend fun signInWithBattleNet(authorizationCode: String): AuthResult<TokenData>
    suspend fun clearOAuthCodes()


}