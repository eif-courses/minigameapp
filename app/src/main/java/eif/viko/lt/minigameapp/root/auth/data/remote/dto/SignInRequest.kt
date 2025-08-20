package eif.viko.lt.minigameapp.root.auth.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SignInRequest(
    val email: String,
    val password: String
)