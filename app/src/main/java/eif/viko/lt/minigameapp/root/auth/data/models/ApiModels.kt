package eif.viko.lt.minigameapp.root.auth.data.models

data class BattleNetTokenRequest(
    val authorization_code: String
)

data class AuthResponse(
    val success: Boolean,
    val access_token: String,
    val token_type: String,
    val expires_in: Int,
    val user: User,
    val provider: String
)

data class User(
    val id: String,
    val email: String,
    val first_name: String,
    val last_name: String
)

data class ErrorResponse(
    val error: Boolean,
    val message: String,
    val status: Int
)

// Diablo 3 API responses
data class ProfileResponse(
    val battleTag: String,
    val paragonLevel: Int,
    val paragonLevelHardcore: Int,
    val guildName: String?,
    val heroes: List<Hero>,
    val lastHeroPlayed: Long,
    val lastUpdated: Long
)

data class Hero(
    val id: Long,
    val name: String,
    val `class`: String,
    val gender: Int,
    val level: Int,
    val paragonLevel: Int,
    val hardcore: Boolean,
    val seasonal: Boolean,
    val dead: Boolean,
    val lastUpdated: Long
)

data class ActsResponse(
    val acts: List<Act>
)

data class Act(
    val slug: String,
    val number: Int,
    val name: String
)

data class ItemResponse(
    val item: Item,
    val iconURL: String?,
    val iconURLs: Map<String, String>?,
    val parsedStats: ParsedStats,
    val message: String
)

data class Item(
    val id: String,
    val slug: String,
    val name: String,
    val icon: String,
    val requiredLevel: Int,
    val accountBound: Boolean,
    val flavorText: String,
    val typeName: String,
    val damage: String?,
    val dps: String?,
    val color: String
)

data class ParsedStats(
    val damageRange: String?,
    val attackSpeed: String?,
    val dps: String?,
    val weaponType: String?,
    val isTwoHanded: Boolean,
    val primaryStats: List<String>?,
    val secondaryStats: List<String>?
)