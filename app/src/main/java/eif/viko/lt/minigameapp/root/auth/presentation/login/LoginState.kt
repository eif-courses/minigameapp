package eif.viko.lt.minigameapp.root.auth.presentation.login

import eif.viko.lt.minigameapp.root.auth.data.models.Act
import eif.viko.lt.minigameapp.root.auth.data.models.ProfileResponse

data class LoginState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val userInfo: Map<String, String>? = null,
    val profile: ProfileResponse? = null,
    val acts: List<Act> = emptyList(),
    val error: String? = null,
    val message: String? = null
)