package eif.viko.lt.minigameapp.root.auth.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDto(
    val id: String,
    val email: String,
    val first_name: String,
    val last_name: String,
    val role: String,  // Always present (defaults to "user")
    val is_active: Boolean,  // Always present as Bool
    val created_at: String  // Timestamp as string
    // Note: No updated_at, last_login_at, or password_hash (sanitized out)
)