package eif.viko.lt.minigameapp.root.core.data.storage

interface TokenStorage {
    suspend fun saveToken(token: String)
    suspend fun getToken(): String?
    suspend fun clearToken()
    suspend fun hasValidToken(): Boolean
}
