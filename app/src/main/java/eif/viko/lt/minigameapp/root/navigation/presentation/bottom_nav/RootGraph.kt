package eif.viko.lt.minigameapp.root.navigation.presentation.bottom_nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import eif.viko.lt.minigameapp.root.auth.presentation.screens.SignInScreen
import eif.viko.lt.minigameapp.root.auth.presentation.screens.SignUpScreen
import eif.viko.lt.minigameapp.root.auth.presentation.viewmodel.AuthState
import eif.viko.lt.minigameapp.root.auth.presentation.viewmodel.AuthStateViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun RootGraph() {
    val authViewModel: AuthStateViewModel = koinViewModel()
    val backStack = rememberNavBackStack<Screen>(Screen.Auth)

    // Handle navigation based on auth state - FIXED VERSION
    LaunchedEffect(authViewModel.authState) {
        when (authViewModel.authState) {
            is AuthState.Authenticated -> {
                // Only navigate if we're not already there
                val currentScreen = backStack.lastOrNull()
                if (currentScreen != Screen.NestedGraph) {
                    backStack.clear()
                    backStack.add(Screen.NestedGraph)
                }
            }
            is AuthState.Unauthenticated -> {
                // Only navigate if we're not already there
                val currentScreen = backStack.lastOrNull()
                if (currentScreen != Screen.Auth && currentScreen != Screen.SignUp) {
                    backStack.clear()
                    backStack.add(Screen.Auth)
                }
            }
            is AuthState.Loading -> {
                // Don't change navigation during loading
            }
        }
    }

    // Show loading while checking auth status
    if (authViewModel.authState is AuthState.Loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
            Text(
                text = "Loading...",
                modifier = Modifier.padding(top = 16.dp)
            )
        }
        return
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Screen.Auth> {
                SignInScreen(
                    onNavigateToHome = {
                        println("🏠 RootGraph: onNavigateToHome called - notifying AuthStateViewModel")
                        //authViewModel.onSignInSuccess()
                    },
                    onNavigateToSignUp = {
                        backStack.add(Screen.SignUp)
                    }
                )
            }

            entry<Screen.SignUp> {
                SignUpScreen(
                    onNavigateToSignIn = {
                        backStack.removeLastOrNull()
                    }
                )
            }

            entry<Screen.Settings> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Settings")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                authViewModel.signOut()
                            }
                        ) {
                            Text(text = "Sign Out")
                        }
                    }
                }
            }

            entry<Screen.NestedGraph> {
                // Only render if we have a user (prevents blank screens)
                if (authViewModel.currentUser != null) {
                    NestedGraph(
                        navigateToSettings = {
                            backStack.add(Screen.Settings)
                        },
                        onSignOut = {
                            authViewModel.signOut()
                        },
                        user = authViewModel.currentUser
                    )
                } else {
                    // Show loading while user data is being fetched
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = "Loading user data...",
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
            }
        }
    )
}