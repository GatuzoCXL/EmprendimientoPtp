package com.example.magnus.ui.screens.guests

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.magnus.data.model.Guest
import com.example.magnus.data.model.GuestStatus
import com.example.magnus.viewmodel.EventViewModel
import androidx.compose.foundation.text.KeyboardOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestManagementScreen(
    eventId: String,
    onNavigateBack: () -> Unit,
    eventViewModel: EventViewModel = viewModel()
) {
    var showAddGuestDialog by remember { mutableStateOf(false) }
    
    val eventState by eventViewModel.uiState.collectAsState()
    
    // Load guests when screen opens
    LaunchedEffect(eventId) {
        eventViewModel.loadGuestsByEvent(eventId)
        eventViewModel.loadEventById(eventId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Invitados") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddGuestDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir invitado")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Event info and stats
            eventState.currentEvent?.let { event ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = event.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Stats
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
                                value = eventState.guests.count { it.status == GuestStatus.CONFIRMED }.toString()
                            )
                            StatItem(
                                label = "Pendientes",
                                value = eventState.guests.count { it.status == GuestStatus.PENDING }.toString()
                            )
                        }
                    }
                }
            }
            
            // Guests list
            if (eventState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (eventState.guests.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "👥",
                        fontSize = 64.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No hay invitados aún",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Añade invitados para comenzar a gestionar tu evento",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(eventState.guests) { guest ->
                        GuestCard(
                            guest = guest,
                            onStatusChange = { newStatus ->
                                eventViewModel.updateGuestStatus(guest.id, newStatus)
                            }
                        )
                    }
                }
            }
        }
    }
    
    // Add Guest Dialog
    if (showAddGuestDialog) {
        AddGuestDialog(
            onDismiss = { showAddGuestDialog = false },
            onAddGuest = { name, email, phone ->
                eventViewModel.addGuest(eventId, name, email, phone)
                showAddGuestDialog = false
            }
        )
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
            fontSize = 24.sp,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GuestCard(
    guest: Guest,
    onStatusChange: (GuestStatus) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = guest.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (guest.email.isNotEmpty()) {
                        Text(
                            text = guest.email,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (guest.phone.isNotEmpty()) {
                        Text(
                            text = guest.phone,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Status chip
                FilterChip(
                    onClick = {
                        val newStatus = when (guest.status) {
                            GuestStatus.PENDING -> GuestStatus.CONFIRMED
                            GuestStatus.CONFIRMED -> GuestStatus.DECLINED
                            GuestStatus.DECLINED -> GuestStatus.PENDING
                        }
                        onStatusChange(newStatus)
                    },
                    label = {
                        Text(
                            text = when (guest.status) {
                                GuestStatus.PENDING -> "Pendiente"
                                GuestStatus.CONFIRMED -> "Confirmado"
                                GuestStatus.DECLINED -> "Rechazado"
                            }
                        )
                    },
                    selected = guest.status == GuestStatus.CONFIRMED,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = when (guest.status) {
                            GuestStatus.CONFIRMED -> MaterialTheme.colorScheme.primaryContainer
                            GuestStatus.DECLINED -> MaterialTheme.colorScheme.errorContainer
                            GuestStatus.PENDING -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddGuestDialog(
    onDismiss: () -> Unit,
    onAddGuest: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir Invitado") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre completo") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Teléfono (opcional)") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank() && email.isNotBlank()) {
                        onAddGuest(name.trim(), email.trim(), phone.trim())
                    }
                },
                enabled = name.isNotBlank() && email.isNotBlank()
            ) {
                Text("Añadir")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
