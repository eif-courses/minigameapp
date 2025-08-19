package eif.viko.lt.minigameapp.root.auth.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SignUpResponse(
    @param:Json(name = "user")
    val user: UserDto,
    @param:Json(name = "message")
    val message: String
)
