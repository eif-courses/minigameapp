package eif.viko.lt.minigameapp.root.auth.data.remote

import eif.viko.lt.minigameapp.root.auth.data.remote.dto.SignInRequest
import eif.viko.lt.minigameapp.root.auth.data.remote.dto.SignUpRequest
import eif.viko.lt.minigameapp.root.auth.data.remote.mappers.toDomainModel
import eif.viko.lt.minigameapp.root.auth.domain.models.AuthResult
import eif.viko.lt.minigameapp.root.auth.domain.models.TokenData
import eif.viko.lt.minigameapp.root.auth.domain.models.User
import eif.viko.lt.minigameapp.root.auth.domain.repository.AuthRepository
import eif.viko.lt.minigameapp.root.auth.domain.utils.TokenStorage
import okio.IOException
import retrofit2.HttpException

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val tokenStorage: TokenStorage
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): AuthResult<TokenData> {
        return try {
            val request = SignInRequest(email = email, password = password)
            val response = api.signIn(request)
            val domainModel = response.toDomainModel()

            // Issaugom tokena po prisijungimo
            tokenStorage.saveToken(domainModel.accessToken)
            AuthResult.Success(domainModel)
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
    override suspend fun signUp(email: String, password: String, firstName: String, lastName: String): AuthResult<User> {
        return try {
            val request = SignUpRequest(
                email = email,
                password = password,
                firstName = firstName,
                lastName = lastName
            )
            val response = api.signUp(request)

            // Don't save token since registration doesn't return one
            AuthResult.Success(response.user.toDomainModel())
        } catch (e: HttpException) {
            when (e.code()) {
                400 -> AuthResult.Error("All fields are required")
                409 -> AuthResult.Error("Email already registered")
                else -> AuthResult.Error("Registration failed: ${e.message()}")
            }
        } catch (e: IOException) {
            AuthResult.Error("Network connection error. Please check your internet.")
        } catch (e: Exception) {
            AuthResult.Error("Unexpected error: ${e.message}")
        }
    }

    override suspend fun signOut() {
        tokenStorage.clearToken()
    }

    override suspend fun isUserIsSignedIn(): Boolean {
        return tokenStorage.hasValidToken()
    }

    override suspend fun getCurrentToken(): String? {
        return tokenStorage.getToken()
    }

}