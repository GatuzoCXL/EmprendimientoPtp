package com.example.magnus.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.magnus.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    val authState by authViewModel.uiState.collectAsState()
    
    // Navigate to dashboard when signed in
    LaunchedEffect(authState.isSignedIn) {
        if (authState.isSignedIn) {
            onNavigateToDashboard()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Title
        Text(
            text = "Magnus",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Gestiona tus eventos de manera inteligente",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 48.dp)
        )
        
        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )
        
        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null)
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Text(
                        text = if (passwordVisible) "👁️" else "🙈",
                        fontSize = 16.sp
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            singleLine = true
        )
        
        // Error Message
        authState.error?.let { errorMessage ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        
        // Login Button
        Button(
            onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    authViewModel.signIn(email.trim(), password)
                }
            },
            enabled = !authState.isLoading && email.isNotBlank() && password.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            if (authState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Iniciar Sesión", fontSize = 16.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Register Link
        TextButton(
            onClick = onNavigateToRegister,
            enabled = !authState.isLoading
        ) {
            Text("¿No tienes cuenta? Regístrate aquí")
        }
    }
}
