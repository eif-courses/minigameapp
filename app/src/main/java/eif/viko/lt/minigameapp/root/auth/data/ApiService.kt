package eif.viko.lt.minigameapp.root.auth.data

import retrofit2.http.*

interface ApiService {
    @GET("users")
    suspend fun getUsers(): ApiResponse<List<User>>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Int): ApiResponse<User>

    @POST("users")
    suspend fun createUser(@Body user: User): ApiResponse<User>

    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body user: User): ApiResponse<User>

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): ApiResponse<Unit>
}