package eif.viko.lt.minigameapp.root.auth.data.remote

import eif.viko.lt.minigameapp.root.auth.data.models.Act
import eif.viko.lt.minigameapp.root.auth.data.models.ActsResponse
import eif.viko.lt.minigameapp.root.auth.data.models.AuthResponse
import eif.viko.lt.minigameapp.root.auth.data.models.BattleNetTokenRequest
import eif.viko.lt.minigameapp.root.auth.data.models.ItemResponse
import eif.viko.lt.minigameapp.root.auth.data.models.ProfileResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.moshi.MoshiConverterFactory

interface DiabloApiService {

    @POST("auth/battlenet/token")
    suspend fun loginWithBattleNet(
        @Body request: BattleNetTokenRequest
    ): Response<AuthResponse>

    @GET("profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<ProfileResponse>

    @GET("d3/profile")
    suspend fun getD3Profile(
        @Header("Authorization") token: String
    ): Response<ProfileResponse>

    @GET("d3/profile/{battleTag}")
    suspend fun getD3ProfileByBattleTag(
        @Header("Authorization") token: String,
        @Path("battleTag") battleTag: String
    ): Response<ProfileResponse>

    @GET("d3/acts")
    suspend fun getD3Acts(
        @Header("Authorization") token: String
    ): Response<ActsResponse>

    @GET("d3/act/{actId}")
    suspend fun getD3Act(
        @Header("Authorization") token: String,
        @Path("actId") actId: Int
    ): Response<Act>

    @GET("d3/item/{itemSlugAndId}")
    suspend fun getD3Item(
        @Header("Authorization") token: String,
        @Path("itemSlugAndId") itemSlugAndId: String
    ): Response<ItemResponse>

    @GET("hello")
    suspend fun testAuth(
        @Header("Authorization") token: String
    ): Response<Map<String, Any>>

    companion object {
        private const val BASE_URL = "https://minigameapi-production.up.railway.app/api/v1/"

        fun create(): DiabloApiService {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

            return retrofit.create(DiabloApiService::class.java)
        }
    }
}