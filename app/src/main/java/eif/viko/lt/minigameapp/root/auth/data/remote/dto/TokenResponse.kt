package eif.viko.lt.minigameapp.root.auth.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import eif.viko.lt.minigameapp.root.auth.domain.models.TokenData


@JsonClass(generateAdapter = true)
data class TokenResponse(
    @param:Json(name = "access_token")
    val accessToken: String,
    @param:Json(name = "user")
    val user: UserDto,
    @param:Json(name = "message")
    val message: String? = null, // Make this optional with default value
    @param:Json(name = "success")
    val success: Boolean? = null, // Make this optional too
    @param:Json(name = "token_type")
    val tokenType: String? = null, // Make this optional
    @param:Json(name = "expires_in")
    val expiresIn: Int? = null, // Make this optional
    @param:Json(name = "provider")
    val provider: String? = null // Make this optional
)
fun TokenResponse.toDomainModel(authProvider: String = "email", hasBattleNet: Boolean = false): TokenData {
    return TokenData(
        user = user.toDomainModel(authProvider, hasBattleNet), // Pass the auth provider info
        accessToken = accessToken
    )
}
