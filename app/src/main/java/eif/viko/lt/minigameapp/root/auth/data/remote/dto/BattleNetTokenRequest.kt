package eif.viko.lt.minigameapp.root.auth.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BattleNetTokenRequest(
    @param:Json(name = "authorization_code")
    val authorizationCode: String,
    @param:Json(name = "redirect_uri")
    val redirectURI: String = "https://minigameapi-production.up.railway.app/oauth/mobile"
)