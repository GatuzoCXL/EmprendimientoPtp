package com.example.magnus.ui.screens.dashboard

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.magnus.data.model.Event
import com.example.magnus.ui.components.*
import com.example.magnus.ui.theme.*
import com.example.magnus.viewmodel.AuthViewModel
import com.example.magnus.viewmodel.EventViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToCreateEvent: () -> Unit,
    onNavigateToMyEvents: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onSignOut: () -> Unit,
    authViewModel: AuthViewModel,
    eventViewModel: EventViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Magnus",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Configuración")
                    }
                }
            )
        },
        floatingActionButton = {
            GradientFloatingActionButton(
                onClick = onNavigateToCreateEvent,
                icon = Icons.Default.Add,
                contentDescription = "Crear evento",
                modifier = Modifier.pulseEffect()
            )
        }
    ) { paddingValues ->
        val authState by authViewModel.uiState.collectAsState()
        val eventState by eventViewModel.uiState.collectAsState()
        
        // Load user's events for statistics
        LaunchedEffect(authState.userData) {
            authState.userData?.let { user ->
                eventViewModel.loadEventsByCreator(user.id)
            }
        }
        
        var stats by remember { mutableStateOf<com.example.magnus.data.repository.EventStats?>(null) }
        
        LaunchedEffect(authState.userData, eventState.events) {
            authState.userData?.let { user ->
                eventViewModel.getEventStats(user.id) { newStats ->
                    stats = newStats
                }
            }
        }
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Welcome Section con gradiente y animación
                AnimatedListItem(index = 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        EventPrimary,
                                        EventSecondary
                                    )
                                )
                            )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Text(
                                text = "¡Hola ${authState.userData?.name ?: "Usuario"}!",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = androidx.compose.ui.graphics.Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Gestiona tus eventos de manera inteligente y eficiente",
                                fontSize = 16.sp,
                                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }
            
            item {
                // Quick Stats con animación stagger
                AnimatedListItem(index = 1) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Eventos",
                            value = "${stats?.totalEvents ?: 0}",
                            icon = Icons.Default.DateRange,
                            color = EventPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Invitados",
                            value = "${stats?.totalGuests ?: 0}",
                            icon = Icons.Default.Person,
                            color = EventSecondary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            
            item {
                // Quick Actions con animación
                AnimatedListItem(index = 2) {
                    SectionHeader(title = "Acciones Rápidas")
                }
            }
            
            item {
                AnimatedListItem(index = 3) {
                    ActionCard(
                        title = "Crear Nuevo Evento",
                        description = "Organiza un nuevo evento y gestiona invitados",
                        icon = Icons.Default.Add,
                        color = EventPrimary,
                        onClick = onNavigateToCreateEvent
                    )
                }
            }
            
            item {
                AnimatedListItem(index = 4) {
                    ActionCard(
                        title = "Mis Eventos",
                        description = "Ver y gestionar todos tus eventos",
                        icon = Icons.Default.DateRange,
                        color = EventSecondary,
                        onClick = onNavigateToMyEvents
                    )
                }
            }
            
            item {
                // Recent Events Section (placeholder)
                SectionHeader(title = "Eventos Recientes")
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No tienes eventos recientes",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
