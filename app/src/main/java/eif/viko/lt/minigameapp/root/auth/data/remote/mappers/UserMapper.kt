package eif.viko.lt.minigameapp.root.auth.data.remote.mappers

import eif.viko.lt.minigameapp.root.auth.data.remote.dto.UserDto
import eif.viko.lt.minigameapp.root.auth.domain.models.User

fun UserDto.toDomainModel(): User {
    return User(
        id = id,
        email = email,
        firstName = first_name,  // Convert snake_case to camelCase
        lastName = last_name,
        role = role,
        isActive = is_active,
        createdAt = created_at
    )
}