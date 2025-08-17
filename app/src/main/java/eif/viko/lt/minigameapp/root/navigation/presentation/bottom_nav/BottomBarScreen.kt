package eif.viko.lt.minigameapp.root.navigation.presentation.bottom_nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable


val bottomBarItems = listOf<BottomBarScreen>(
    BottomBarScreen.Home,
    BottomBarScreen.Search,
    BottomBarScreen.Profile
)

@Serializable
sealed class BottomBarScreen(
    @Contextual val icon: ImageVector,
    val title: String
): NavKey {

    @Serializable
    data object Home: BottomBarScreen(
        icon = Icons.Default.Home,
        title = "Home"
    )

    @Serializable
    data object Search: BottomBarScreen(
        icon = Icons.Default.Search,
        title="search"
    )

    @Serializable
    data object Profile: BottomBarScreen(
        icon = Icons.Default.Person,
        title = "Profile"
    )
}
val BottomBarScreenSaver = Saver<BottomBarScreen, String>(
    save = {it::class.simpleName ?: "Unknown"},
    restore = {
        when (it) {
            BottomBarScreen.Home::class.simpleName -> BottomBarScreen.Home
            BottomBarScreen.Search::class.simpleName -> BottomBarScreen.Search
            BottomBarScreen.Profile::class.simpleName -> BottomBarScreen.Profile
            else -> BottomBarScreen.Home
        }
    }

)
