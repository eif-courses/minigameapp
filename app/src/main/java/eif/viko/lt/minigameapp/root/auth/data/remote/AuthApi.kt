package eif.viko.lt.minigameapp.root.auth.data.remote

import eif.viko.lt.minigameapp.root.auth.data.remote.dto.SignInRequest
import eif.viko.lt.minigameapp.root.auth.data.remote.dto.TokenResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/v1/auth/login")
    suspend fun signIn(@Body request: SignInRequest): TokenResponse
}