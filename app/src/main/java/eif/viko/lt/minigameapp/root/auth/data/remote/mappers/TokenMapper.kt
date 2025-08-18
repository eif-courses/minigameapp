package eif.viko.lt.minigameapp.root.auth.data.remote.mappers

import eif.viko.lt.minigameapp.root.auth.data.remote.dto.TokenResponse
import eif.viko.lt.minigameapp.root.auth.domain.models.TokenData

fun TokenResponse.toDomainModel(): TokenData {
    return TokenData(
        user = user.toDomainModel(),
        accessToken = access_token
    )
}