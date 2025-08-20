package eif.viko.lt.minigameapp.root.auth.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserResponse(
    @param:Json(name = "user")
    val user: UserDto,
    @param:Json(name = "auth_provider")
    val authProvider: String = "email",
    @param:Json(name = "has_battlenet")
    val hasBattleNet: Boolean = false,
    @param:Json(name = "oauth_providers")
    val oauthProviders: List<String> = emptyList()
)