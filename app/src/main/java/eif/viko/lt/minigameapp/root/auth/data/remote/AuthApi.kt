package eif.viko.lt.minigameapp.root.auth.data.remote

import eif.viko.lt.minigameapp.root.auth.data.remote.dto.BattleNetTokenRequest
import eif.viko.lt.minigameapp.root.auth.data.remote.dto.SignInRequest
import eif.viko.lt.minigameapp.root.auth.data.remote.dto.SignUpRequest
import eif.viko.lt.minigameapp.root.auth.data.remote.dto.SignUpResponse
import eif.viko.lt.minigameapp.root.auth.data.remote.dto.TokenResponse
import eif.viko.lt.minigameapp.root.auth.data.remote.dto.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {
    @POST("api/v1/auth/login")
    suspend fun signIn(@Body request: SignInRequest): TokenResponse
    @POST("api/v1/auth/register")
    suspend fun signUp(@Body request: SignUpRequest): SignUpResponse

    @POST("api/v1/auth/battlenet/token")
    suspend fun signInWithBattleNet(@Body request: BattleNetTokenRequest): TokenResponse

    @GET("api/v1/auth/profile")
    suspend fun getCurrentUser(@Header("Authorization") authHeader: String): UserResponse

}