package eif.viko.lt.minigameapp.root.auth.presentation.login

// presentation/login/components/LoginScreen.kt

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = koinViewModel(),
    modifier: Modifier = Modifier  // Add this parameter
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = modifier  // Use the passed modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Diablo 3 API App",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        if (!state.isLoggedIn) {
            LoginSection(
                onLoginClick = { viewModel.startLogin() },
                isLoading = state.isLoading
            )
        } else {
            LoggedInSection(
                state = state,
                onLogoutClick = { viewModel.logout() },
                onLoadProfileClick = { viewModel.loadProfile() },
                onLoadActsClick = { viewModel.loadActs() }
            )
        }

        // Show loading indicator
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.padding(16.dp)
            )
        }

        // Show error messages
        state.error?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = "Error: $error",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }

            LaunchedEffect(error) {
                kotlinx.coroutines.delay(5000)
                viewModel.clearError()
            }
        }

        // Show success messages
        state.message?.let { message ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            LaunchedEffect(message) {
                kotlinx.coroutines.delay(3000)
                viewModel.clearMessage()
            }
        }
    }
}

@Composable
private fun LoginSection(
    onLoginClick: () -> Unit,
    isLoading: Boolean
) {
    Button(
        onClick = onLoginClick,
        enabled = !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text("Login with Battle.net")
        }
    }
}

@Composable
private fun LoggedInSection(
    state: LoginState,
    onLogoutClick: () -> Unit,
    onLoadProfileClick: () -> Unit,
    onLoadActsClick: () -> Unit
) {
    // User info
    state.userInfo?.let { userInfo ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Welcome ${userInfo["first_name"]}!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Email: ${userInfo["email"]}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    // Action buttons
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onLoadProfileClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            Text("Load D3 Profile")
        }

        Button(
            onClick = onLoadActsClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            Text("Load D3 Acts")
        }

        OutlinedButton(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }

    // Profile data
    state.profile?.let { profile ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Diablo 3 Profile",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text("BattleTag: ${profile.battleTag}")
                Text("Paragon Level: ${profile.paragonLevel}")
                if (profile.heroes.isNotEmpty()) {
                    Text("Heroes: ${profile.heroes.size}")
                }
            }
        }
    }

    // Acts data
    if (state.acts.isNotEmpty()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Diablo 3 Acts",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                state.acts.forEach { act ->
                    Text("${act.number}. ${act.name}")
                }
            }
        }
    }
}