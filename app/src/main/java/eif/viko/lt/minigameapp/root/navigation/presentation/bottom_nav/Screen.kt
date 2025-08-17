package eif.viko.lt.minigameapp.root.navigation.presentation.bottom_nav

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen : NavKey {

    @Serializable
    data object Auth : Screen()

    @Serializable
    data object NestedGraph: Screen()

    @Serializable
    data object Settings: Screen()
}