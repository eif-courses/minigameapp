package eif.viko.lt.minigameapp.root.navigation.presentation.bottom_nav

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import eif.viko.lt.minigameapp.root.auth.domain.models.User
import eif.viko.lt.minigameapp.root.auth.domain.models.getAuthProvider
import eif.viko.lt.minigameapp.root.auth.domain.models.hasBattleNetAccess
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NestedGraph(
    navigateToSettings: () -> Unit,
    onSignOut: () -> Unit,
    user: User? = null // Add this parameter
) {
    val backStack = rememberNavBackStack<BottomBarScreen>(BottomBarScreen.Home)

    var currentBottomBarScreen: BottomBarScreen by rememberSaveable(
        stateSaver = BottomBarScreenSaver
    ) { mutableStateOf(BottomBarScreen.Home) }

    val stateHolder = rememberSaveableStateHolder()

    // Filter bottom bar items based on user authentication
    val availableItems = if (user?.hasBattleNetAccess() == true) {
        bottomBarItems // Show all items for Battle.net users
    } else {
        bottomBarItems.filter { item ->
            // Only show Home and Profile for email users (hide Search/Diablo features)
            item == BottomBarScreen.Home || item == BottomBarScreen.Profile
        }
    }


    // Add debouncing state
    var lastSignOutTime by remember { mutableStateOf(0L) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Minigame App ${if (user?.hasBattleNetAccess() == true) "⚔️" else "📧"}")
                },
                actions = {
                    IconButton(onClick = navigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings icon"
                        )
                    }
                    IconButton(
                        onClick = {
                            // Add debouncing to prevent multiple rapid calls
                            val currentTime = System.currentTimeMillis()
                            if (currentTime - lastSignOutTime > 1000) { // 1 second debounce
                                lastSignOutTime = currentTime
                                println("🚪 TopAppBar sign out clicked")
                                onSignOut()
                            } else {
                                println("🚫 Sign out debounced")
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Sign out"
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                availableItems.forEach { destination -> // Use filtered items
                    NavigationBarItem(
                        selected = currentBottomBarScreen == destination,
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = "$destination icon"
                            )
                        },
                        onClick = {
                            if (backStack.lastOrNull() != destination) {
                                if (backStack.lastOrNull() in availableItems) {
                                    backStack.removeAt(backStack.lastIndex)
                                }
                                backStack.add(destination)
                                currentBottomBarScreen = destination
                            }
                        }
                    )
                }
            }
        }
    ) {
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryDecorators = listOf(
                rememberSavedStateNavEntryDecorator(),
                // TODO COMMENT THIS
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<BottomBarScreen.Home> {
                    HomeScreen(user = user) // Pass user to home screen
                }
                entry<BottomBarScreen.Search> {
                    if (user?.hasBattleNetAccess() == true) {
                        // Show Diablo content for Battle.net users
                        DiabloScreen(user = user)
                    } else {
                        // Show upgrade prompt for email users
                        BattleNetRequiredScreen(onSignOut = onSignOut)
                    }
                }
                entry<BottomBarScreen.Profile> {
                    ProfileScreen(user = user, onSignOut = onSignOut)
                }
            }
        )
    }
}

@Composable
fun HomeScreen(user: User?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome, ${user?.firstName ?: "User"}!",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (user?.hasBattleNetAccess() == true) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0084FF).copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🎮 Battle.net Connected",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF0084FF)
                    )
                    Text(
                        text = "You have access to all Diablo 3 features!",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📧 Email Account",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Sign in with Battle.net to unlock Diablo 3 features",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun DiabloScreen(user: User?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "⚔️ Diablo 3 Features",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0084FF).copy(alpha = 0.1f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🎮 Character: ${user?.firstName}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF0084FF)
                )
                Text(
                    text = "Battle.net ID: ${user?.email}",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• View Character Profile\n• Browse Item Database\n• Track Seasonal Progress",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun ProfileScreen(user: User?, onSignOut: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Name: ${user?.firstName} ${user?.lastName}")
                Text("Email: ${user?.email}")
                Text("Provider: ${user?.getAuthProvider()?.uppercase()}")
                if (user?.hasBattleNetAccess() == true) {
                    Text("🎮 Battle.net Access: Enabled", color = Color(0xFF0084FF))
                } else {
                    Text("📧 Battle.net Access: Not Connected")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSignOut,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Sign Out")
        }
    }
}

@Composable
fun BattleNetRequiredScreen(onSignOut: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "⚔️",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Battle.net Required",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF0084FF)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "This feature requires a Battle.net account to access Diablo 3 game data.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSignOut,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0084FF))
        ) {
            Text("Sign in with Battle.net")
        }
    }
}