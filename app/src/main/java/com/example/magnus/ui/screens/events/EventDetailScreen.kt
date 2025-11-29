package com.example.magnus.ui.screens.events

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.magnus.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    eventId: String,
    onNavigateBack: () -> Unit,
    onNavigateToGuestManagement: (String) -> Unit,
    eventViewModel: EventViewModel = viewModel()
) {
    val eventState by eventViewModel.uiState.collectAsState()
    
    // Load event details and guests
    LaunchedEffect(eventId) {
        eventViewModel.loadEventById(eventId)
        eventViewModel.loadGuestsByEvent(eventId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Evento") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (eventState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            eventState.currentEvent?.let { event ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Event Header
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = event.name,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Creado por ${event.creatorName}",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    
                    // Event Details
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Date
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.DateRange,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Fecha",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = SimpleDateFormat("dd/MM/yyyy 'a las' HH:mm", Locale.getDefault())
                                            .format(Date(event.date)),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            
                            HorizontalDivider()
                            
                            // Location
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Ubicación",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = event.location,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            
                            HorizontalDivider()
                            
                            // Capacity
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Capacidad",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${event.currentGuests} / ${event.maxGuests} invitados",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                    
                    // Description
                    if (event.description.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Descripción",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = event.description,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    
                    // Guest Statistics
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Invitados",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                TextButton(
                                    onClick = { onNavigateToGuestManagement(eventId) }
                                ) {
                                    Icon(
                                        Icons.Default.Settings,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Gestionar")
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatItem(
                                    label = "Total",
                                    value = eventState.guests.size.toString()
                                )
                                StatItem(
                                    label = "Confirmados",
                                    value = eventState.guests.count { 
                                        it.status == com.example.magnus.data.model.GuestStatus.CONFIRMED 
                                    }.toString()
                                )
                                StatItem(
                                    label = "Pendientes",
                                    value = eventState.guests.count { 
                                        it.status == com.example.magnus.data.model.GuestStatus.PENDING 
                                    }.toString()
                                )
                            }
                        }
                    }
                    
                    // Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { onNavigateToGuestManagement(eventId) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Gestionar Invitados")
                        }
                        
                        OutlinedButton(
                            onClick = { /* TODO: Implementar compartir */ },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Compartir")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
