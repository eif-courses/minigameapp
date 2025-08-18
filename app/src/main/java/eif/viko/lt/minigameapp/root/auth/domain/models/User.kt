package eif.viko.lt.minigameapp.root.auth.domain.models

data class User(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: String,
    val isActive: Boolean,
    val createdAt: String? = null,  // Optional since you might not always need it
    val fullName: String = "$firstName $lastName"
)