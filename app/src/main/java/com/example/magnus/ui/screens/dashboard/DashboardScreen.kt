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
import com.example.magnus.data.model.OrganizadorStats
import com.example.magnus.data.repository.OrganizadorRepository
import com.example.magnus.ui.components.*
import com.example.magnus.ui.theme.*
import com.example.magnus.viewmodel.AuthViewModel
import com.example.magnus.viewmodel.EventViewModel
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToCreateEvent: () -> Unit,
    onNavigateToMyEvents: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToOrganizadores: () -> Unit = {},
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
        val context = LocalContext.current
        val organizadorRepository = remember { OrganizadorRepository(context) }
        
        // Debug: Check organizador state
        LaunchedEffect(authState.organizador) {
            android.util.Log.d("DashboardScreen", "Organizador state: ${authState.organizador}")
            android.util.Log.d("DashboardScreen", "Is Organizador: ${authState.organizador != null}")
        }
        
        // Load user's events for statistics
        LaunchedEffect(authState.userData) {
            authState.userData?.let { user ->
                eventViewModel.loadEventsByCreator(user.id)
            }
        }
        
        var stats by remember { mutableStateOf<com.example.magnus.data.repository.EventStats?>(null) }
        var organizadorStats by remember { mutableStateOf<OrganizadorStats?>(null) }
        
        LaunchedEffect(authState.userData, eventState.events) {
            authState.userData?.let { user ->
                eventViewModel.getEventStats(user.id) { newStats ->
                    stats = newStats
                }
            }
        }
        
        // Load organizador stats if user is an organizador
        LaunchedEffect(authState.organizador) {
            authState.organizador?.let { organizador ->
                val result = organizadorRepository.getOrganizadorStats(organizador.id)
                if (result.isSuccess) {
                    organizadorStats = result.getOrNull()
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
                    if (authState.organizador != null) {
                        // Professional Organizador Banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            androidx.compose.ui.graphics.Color(0xFF4F46E5),
                                            androidx.compose.ui.graphics.Color(0xFF9333EA)
                                        )
                                    )
                                )
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = authState.organizador!!.nombreEmpresa,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = androidx.compose.ui.graphics.Color.White
                                    )
                                    Surface(
                                        color = androidx.compose.ui.graphics.Color(0xFFFBBF24),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "⭐",
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                text = "Verificado",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = androidx.compose.ui.graphics.Color(0xFF78350F)
                                            )
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "⭐",
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "${String.format("%.1f", authState.organizador!!.rating)} (${authState.organizador!!.cantidadReseñas} reseñas)",
                                            fontSize = 14.sp,
                                            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.9f)
                                        )
                                    }
                                    
                                    if (!authState.organizador!!.especialidad.isNullOrEmpty()) {
                                        Text(
                                            text = "• ${authState.organizador!!.especialidad}",
                                            fontSize = 14.sp,
                                            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.9f)
                                        )
                                    }
                                    
                                    Text(
                                        text = "• ${authState.organizador!!.añosExperiencia} años",
                                        fontSize = 14.sp,
                                        color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.9f)
                                    )
                                }
                            }
                        }
                    } else {
                        // Normal User Welcome Banner
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
            }
            
            item {
                // Quick Stats con animación stagger
                AnimatedListItem(index = 1) {
                    if (authState.organizador != null && organizadorStats != null) {
                        // Professional Organizador Stats
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                StatCard(
                                    title = "Eventos Organizados",
                                    value = "${organizadorStats!!.eventosOrganizados}",
                                    icon = Icons.Default.DateRange,
                                    color = androidx.compose.ui.graphics.Color(0xFF4F46E5),
                                    modifier = Modifier.weight(1f)
                                )
                                StatCard(
                                    title = "Ingresos",
                                    value = "$${organizadorStats!!.ingresosTotales.toInt()}",
                                    icon = Icons.Default.ExitToApp,
                                    color = androidx.compose.ui.graphics.Color(0xFF10B981),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                StatCard(
                                    title = "Rating",
                                    value = String.format("%.1f ⭐", organizadorStats!!.ratingPromedio),
                                    icon = Icons.Default.Person,
                                    color = androidx.compose.ui.graphics.Color(0xFFF59E0B),
                                    modifier = Modifier.weight(1f)
                                )
                                StatCard(
                                    title = "Clientes",
                                    value = "${organizadorStats!!.clientesSatisfechos}",
                                    icon = Icons.Default.Person,
                                    color = androidx.compose.ui.graphics.Color(0xFF3B82F6),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    } else {
                        // Normal User Stats
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
            }
            
            item {
                // Quick Actions con animación
                AnimatedListItem(index = 2) {
                    SectionHeader(title = "Acciones Rápidas")
                }
            }
            
            if (authState.organizador != null) {
                // Organizador Quick Actions
                item {
                    AnimatedListItem(index = 3) {
                        ActionCard(
                            title = "Ver Mis Eventos",
                            description = "Gestiona tus eventos personales",
                            icon = Icons.Default.DateRange,
                            color = EventPrimary,
                            onClick = onNavigateToMyEvents
                        )
                    }
                }
                
                item {
                    AnimatedListItem(index = 4) {
                        ActionCard(
                            title = "Editar Mi Perfil",
                            description = "Actualiza tu información profesional",
                            icon = Icons.Default.Settings,
                            color = androidx.compose.ui.graphics.Color(0xFF4F46E5),
                            onClick = onNavigateToProfile
                        )
                    }
                }
                
                item {
                    AnimatedListItem(index = 5) {
                        ActionCard(
                            title = "Ver Estadísticas",
                            description = "Revisa tus métricas de desempeño",
                            icon = Icons.Default.ExitToApp,
                            color = EventSecondary,
                            onClick = onNavigateToProfile
                        )
                    }
                }
            } else {
                // Normal User Quick Actions
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
                    AnimatedListItem(index = 5) {
                        ActionCard(
                            title = "Organizadores de Bodas",
                            description = "Encuentra el planificador perfecto para tu evento",
                            icon = Icons.Default.Person,
                            color = EventTertiary,
                            onClick = onNavigateToOrganizadores
                        )
                    }
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
