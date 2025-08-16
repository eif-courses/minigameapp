package eif.viko.lt.minigameapp.root.auth.data


import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    val data: T,
    val status: String,
    val message: String? = null
)
