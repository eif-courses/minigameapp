package eif.viko.lt.minigameapp.root.auth.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import eif.viko.lt.minigameapp.root.auth.presentation.viewmodel.SignUpViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignUpScreen(
    onNavigateToSignIn: () -> Unit,
    viewModel: SignUpViewModel = koinViewModel()
) {
    val uiState = viewModel.uiState
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    LaunchedEffect(uiState.isSignedUp) {
        if (uiState.isSignedUp) {
            kotlinx.coroutines.delay(2000)
            onNavigateToSignIn()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = firstName,
            onValueChange = {
                firstName = it
                if (uiState.error != null) viewModel.clearError()
            },
            label = { Text("First Name") },
            enabled = !uiState.isLoading,
            isError = uiState.error != null,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = lastName,
            onValueChange = {
                lastName = it
                if (uiState.error != null) viewModel.clearError()
            },
            label = { Text("Last Name") },
            enabled = !uiState.isLoading,
            isError = uiState.error != null,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                if (uiState.error != null) viewModel.clearError()
            },
            label = { Text("Email") },
            enabled = !uiState.isLoading,
            isError = uiState.error != null,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                if (uiState.error != null) viewModel.clearError()
            },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            enabled = !uiState.isLoading,
            isError = uiState.error != null,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.signUp(email, password, firstName, lastName)
            },
            enabled = !uiState.isLoading &&
                    email.isNotBlank() &&
                    password.isNotBlank() &&
                    firstName.isNotBlank() &&
                    lastName.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Sign Up")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onNavigateToSignIn,
            enabled = !uiState.isLoading
        ) {
            Text("Already have an account? Sign In")
        }

        uiState.error?.let { error ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}