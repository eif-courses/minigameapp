package eif.viko.lt.minigameapp.root.navigation.presentation.bottom_nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    // Handle navigation based on auth state
    LaunchedEffect(authViewModel.authState) {
        when (authViewModel.authState) {
            is AuthState.Authenticated -> {
                // User is logged in, go to main app
                if (backStack.lastOrNull() != Screen.NestedGraph) {
                    backStack.clear()
                    backStack.add(Screen.NestedGraph)
                }
            }
            is AuthState.Unauthenticated -> {
                // User is not logged in, show auth screen
                if (backStack.lastOrNull() != Screen.Auth) {
                    backStack.clear()
                    backStack.add(Screen.Auth)
                }
            }
            is AuthState.Loading -> {
                // Do nothing, show loading
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
                        authViewModel.onSignInSuccess()
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
                    Button(
                        onClick = {
                            authViewModel.signOut()
                        }
                    ) {
                        Text(text = "Sign Out")
                    }
                }
            }

            entry<Screen.NestedGraph> {
                NestedGraph(
                    navigateToSettings = {
                        backStack.add(Screen.Settings)
                    },
                    onSignOut = {
                        // FIXED: Call authViewModel.signOut() instead of direct navigation
                        authViewModel.signOut()
                    }
                )
            }
        }
    )
}