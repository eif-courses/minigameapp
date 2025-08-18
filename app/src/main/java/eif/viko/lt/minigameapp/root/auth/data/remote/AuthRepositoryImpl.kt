package eif.viko.lt.minigameapp.root.auth.data.remote

import eif.viko.lt.minigameapp.root.auth.data.remote.dto.SignInRequest
import eif.viko.lt.minigameapp.root.auth.data.remote.mappers.toDomainModel
import eif.viko.lt.minigameapp.root.auth.domain.models.AuthResult
import eif.viko.lt.minigameapp.root.auth.domain.models.TokenData
import eif.viko.lt.minigameapp.root.auth.domain.repository.AuthRepository
import okio.IOException
import retrofit2.HttpException

class AuthRepositoryImpl(
    private val api: AuthApi
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): AuthResult<TokenData> {
        return try {
            val request = SignInRequest(email = email, password = password)
            val response = api.signIn(request)
            AuthResult.Success(response.toDomainModel())
        } catch (e: HttpException) {
            when (e.code()) {
                400 -> AuthResult.Error("Invalid request. Please check your input.")
                401 -> AuthResult.Unauthorized
                500 -> AuthResult.Error("Server error. Please try again later.")
                else -> AuthResult.Error("Network error: ${e.message()}")
            }
        } catch (e: IOException) {
            AuthResult.Error("Network connection error. Please check your internet. ${e.message}")
        } catch (e: Exception) {
            AuthResult.Error("Unexpected error: ${e.message}")
        }
    }

}