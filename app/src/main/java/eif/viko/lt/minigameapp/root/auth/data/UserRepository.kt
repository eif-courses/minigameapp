package eif.viko.lt.minigameapp.root.auth.data

import eif.viko.lt.minigameapp.root.auth.data.models.ActsResponse
import eif.viko.lt.minigameapp.root.auth.data.models.AuthResponse
import eif.viko.lt.minigameapp.root.auth.data.models.ItemResponse
import eif.viko.lt.minigameapp.root.auth.data.models.ProfileResponse
import eif.viko.lt.minigameapp.root.auth.data.remote.DiabloApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.dsl.module

class UserRepository(
    private val apiService: DiabloApiService,
    private val oauthManager: OAuthManager
) {

    suspend fun getProfile(): Result<ProfileResponse> {
        val token = oauthManager.getAuthToken() ?: return Result.failure(Exception("Not logged in"))

        return try {
            val response = apiService.getD3Profile(token)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get profile: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getActs(): Result<ActsResponse> {
        val token = oauthManager.getAuthToken() ?: return Result.failure(Exception("Not logged in"))

        return try {
            val response = apiService.getD3Acts(token)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get acts: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getItem(itemSlugAndId: String): Result<ItemResponse> {
        val token = oauthManager.getAuthToken() ?: return Result.failure(Exception("Not logged in"))

        return try {
            val response = apiService.getD3Item(token, itemSlugAndId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get item: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun testAuth(): Result<Boolean> {
        val token = oauthManager.getAuthToken() ?: return Result.failure(Exception("Not logged in"))

        return try {
            val response = apiService.testAuth(token)
            Result.success(response.isSuccessful)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun isLoggedIn(): Boolean = oauthManager.isLoggedIn()

    fun getUserInfo(): Map<String, String>? = oauthManager.getUserInfo()

    fun logout() = oauthManager.logout()

    suspend fun handleAuthorizationCode(code: String, state: String): Result<AuthResponse> {
        return oauthManager.handleAuthorizationCode(code, state)
    }

    fun startLogin() = oauthManager.startBattleNetLogin()
}
// Add to Koin module
val repositoryModule = module {
 //   single { UserRepository(get()) }
}