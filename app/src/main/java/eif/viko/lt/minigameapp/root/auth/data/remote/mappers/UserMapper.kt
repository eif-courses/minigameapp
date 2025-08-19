package eif.viko.lt.minigameapp.root.auth.data.remote.mappers

import eif.viko.lt.minigameapp.root.auth.data.remote.dto.UserDto
import eif.viko.lt.minigameapp.root.auth.domain.models.User

fun UserDto.toDomainModel(): User {
    return User(
        id = id,
        email = email,
        firstName = firstName,
        lastName = lastName,
        role = role,
        isActive = isActive,
        createdAt = createdAt
    )
}