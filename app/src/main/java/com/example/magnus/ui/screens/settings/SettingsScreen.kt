package com.example.magnus.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.magnus.ui.components.AnimatedListItem
import com.example.magnus.ui.components.SectionHeader
import com.example.magnus.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onSignOut: () -> Unit,
    authViewModel: AuthViewModel
) {
    val authState by authViewModel.uiState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro de que deseas cerrar sesión?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        authViewModel.signOut()
                        showLogoutDialog = false
                        onSignOut()
                    }
                ) {
                    Text("Sí, cerrar sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("Acerca de Magnus") },
            text = { 
                Column {
                    Text("Magnus - App de Gestión de Eventos")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Versión: 1.0.0 (Prototipo)")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Desarrollado con Kotlin y Jetpack Compose")
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Aceptar")
                }
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Account Section
            AnimatedListItem(index = 0) {
                Column {
                    SectionHeader(title = "Cuenta", modifier = Modifier.padding(horizontal = 16.dp))
                    
                    SettingsItem(
                        icon = Icons.Default.Person,
                        title = "Perfil",
                        subtitle = authState.userData?.email ?: "No disponible",
                        onClick = { /* Ya existe la pantalla de perfil */ }
                    )
                    
                    HorizontalDivider()
                }
            }
            
            // App Settings Section
            AnimatedListItem(index = 1) {
                Column {
                    SectionHeader(title = "Aplicación", modifier = Modifier.padding(horizontal = 16.dp))
                    
                    SettingsItem(
                        icon = Icons.Default.Notifications,
                        title = "Notificaciones",
                        subtitle = "Gestionar notificaciones",
                        onClick = { /* Funcionalidad futura */ }
                    )
                    
                    HorizontalDivider()
                    
                    SettingsItem(
                        icon = Icons.Default.Settings,
                        title = "Idioma",
                        subtitle = "Español",
                        onClick = { /* Funcionalidad futura */ }
                    )
                    
                    HorizontalDivider()
                    
                    SettingsItem(
                        icon = Icons.Default.Settings,
                        title = "Tema",
                        subtitle = "Sistema",
                        onClick = { /* Funcionalidad futura */ }
                    )
                    
                    HorizontalDivider()
                }
            }
            
            // Data Section
            AnimatedListItem(index = 2) {
                Column {
                    SectionHeader(title = "Datos", modifier = Modifier.padding(horizontal = 16.dp))
                    
                    SettingsItem(
                        icon = Icons.Default.Settings,
                        title = "Almacenamiento",
                        subtitle = "Gestionar datos locales",
                        onClick = { /* Funcionalidad futura */ }
                    )
                    
                    HorizontalDivider()
                    
                    SettingsItem(
                        icon = Icons.Default.Settings,
                        title = "Sincronización",
                        subtitle = "Modo sin conexión activado",
                        onClick = { /* Funcionalidad futura */ }
                    )
                    
                    HorizontalDivider()
                }
            }
            
            // About Section
            AnimatedListItem(index = 3) {
                Column {
                    SectionHeader(title = "Información", modifier = Modifier.padding(horizontal = 16.dp))
                    
                    SettingsItem(
                        icon = Icons.Default.Info,
                        title = "Acerca de",
                        subtitle = "Magnus v1.0.0",
                        onClick = { showAboutDialog = true }
                    )
                    
                    HorizontalDivider()
                    
                    SettingsItem(
                        icon = Icons.Default.Info,
                        title = "Ayuda y Soporte",
                        subtitle = "Centro de ayuda",
                        onClick = { /* Funcionalidad futura */ }
                    )
                    
                    HorizontalDivider()
                    
                    SettingsItem(
                        icon = Icons.Default.Info,
                        title = "Términos y Condiciones",
                        subtitle = "Políticas de uso",
                        onClick = { /* Funcionalidad futura */ }
                    )
                    
                    HorizontalDivider()
                }
            }
            
            // Logout
            AnimatedListItem(index = 4) {
                Column {
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = { showLogoutDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cerrar Sesión", fontSize = 16.sp)
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
