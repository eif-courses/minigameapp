package eif.viko.lt.minigameapp.root.auth.domain.models

data class User(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: String,
    val isActive: Boolean,
    val createdAt: String? = null,
    val fullName: String = "$firstName $lastName",
    val authProvider: String = "email",    // Add this
    val hasBattleNet: Boolean = false      // Add this
)

// Update extensions to use the fields
fun User.hasBattleNetAccess(): Boolean {
    return hasBattleNet // Use the explicit field
}

fun User.getAuthProvider(): String {
    return authProvider // Use the explicit field
}