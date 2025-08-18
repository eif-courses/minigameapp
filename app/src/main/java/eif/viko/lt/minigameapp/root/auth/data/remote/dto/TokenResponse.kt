package eif.viko.lt.minigameapp.root.auth.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TokenResponse(
    val user: UserDto,
    val access_token: String,
    val message: String,
    val auth_method: String
)
