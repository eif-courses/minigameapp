package eif.viko.lt.minigameapp.root.auth.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import eif.viko.lt.minigameapp.root.auth.domain.models.User

@JsonClass(generateAdapter = true)
data class UserDto(
    @param:Json(name = "id")
    val id: String,
    @param:Json(name = "email")
    val email: String,
    @param:Json(name = "first_name")
    val firstName: String,
    @param:Json(name = "last_name")
    val lastName: String,
    @param:Json(name = "role")
    val role: String,
    @param:Json(name = "is_active")
    val isActive: Boolean,
    @param:Json(name = "created_at")
    val createdAt: String? = null
)

// Update extension function
fun UserDto.toDomainModel(authProvider: String, hasBattleNet: Boolean): User {
    return User(
        id = id,
        email = email,
        firstName = firstName,
        lastName = lastName,
        role = role,
        isActive = isActive,
        createdAt = createdAt,
        authProvider = authProvider,     // Add this
        hasBattleNet = hasBattleNet      // Add this
    )
}
