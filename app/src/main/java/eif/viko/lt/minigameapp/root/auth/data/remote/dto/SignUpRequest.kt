package eif.viko.lt.minigameapp.root.auth.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SignUpRequest(
    @param:Json(name = "email")
    val email: String,
    @param:Json(name = "password")
    val password: String,
    @param:Json(name = "first_name")
    val firstName: String,
    @param:Json(name = "last_name")
    val lastName: String
)