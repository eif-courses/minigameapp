package eif.viko.lt.minigameapp.root.auth.domain.models

data class TokenData(
    val user: User,
    val accessToken: String
)