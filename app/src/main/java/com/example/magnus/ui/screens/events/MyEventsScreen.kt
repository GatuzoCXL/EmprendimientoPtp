package com.example.magnus.ui.screens.events

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.magnus.data.model.Event
import com.example.magnus.ui.components.*
import com.example.magnus.ui.theme.*
import com.example.magnus.utils.canBeDeleted
import com.example.magnus.utils.getStatus
import com.example.magnus.viewmodel.AuthViewModel
import com.example.magnus.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyEventsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEventDetail: (String) -> Unit,
    onNavigateToCreateEvent: () -> Unit,
    authViewModel: AuthViewModel = viewModel(),
    eventViewModel: EventViewModel = viewModel()
) {
    val authState by authViewModel.uiState.collectAsState()
    val eventState by eventViewModel.uiState.collectAsState()
    var eventToDelete by remember { mutableStateOf<Event?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Load events when screen opens
    LaunchedEffect(authState.userData) {
        authState.userData?.let { user ->
            eventViewModel.loadEventsByCreator(user.id)
        }
    }
    
    // Mostrar snackbar cuando hay error
    LaunchedEffect(eventState.error) {
        eventState.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Short
            )
            eventViewModel.clearError()
        }
    }
    
    // Diálogo de confirmación de eliminación
    if (showDeleteDialog && eventToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = EventError,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    "¿Eliminar evento?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "¿Estás seguro de que deseas eliminar \"${eventToDelete!!.name}\"? Esta acción no se puede deshacer.",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        eventToDelete?.let { event ->
                            eventViewModel.deleteEvent(
                                eventId = event.id,
                                eventDate = event.date,
                                onSuccess = {
                                    showDeleteDialog = false
                                    eventToDelete = null
                                }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EventError
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        eventToDelete = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Eventos") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            GradientFloatingActionButton(
                onClick = onNavigateToCreateEvent,
                icon = Icons.Default.Add,
                contentDescription = "Crear evento",
                modifier = Modifier.pulseEffect()
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (eventState.isLoading) {
                LoadingScreen(message = "Cargando tus eventos...")
            } else if (eventState.events.isEmpty()) {
                EmptyStateScreen(
                    title = "No tienes eventos aún",
                    message = "Crea tu primer evento y comienza a organizar",
                    actionText = "Crear Evento",
                    onActionClick = onNavigateToCreateEvent
                )
            } else {
                // Events list con animaciones stagger
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(eventState.events) { index, event ->
                        AnimatedListItem(index = index) {
                            ModernEventCard(
                                event = event,
                                onClick = { onNavigateToEventDetail(event.id) },
                                onDelete = { selectedEvent ->
                                    eventToDelete = selectedEvent
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            }
            
            // Error handling con animación
            eventState.error?.let { errorMessage ->
                AnimatedVisibilityFade(visible = true) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "⚠️", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernEventCard(
    event: Event,
    onClick: () -> Unit,
    onDelete: (Event) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    
    // Usar función helper para determinar el estado
    val eventStatus = event.getStatus()
    val canDelete = event.canBeDeleted()
    val dateFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Header con estado y menú
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = event.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StatusBadge(status = eventStatus)
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // Menú de opciones
                        Box {
                            IconButton(
                                onClick = { showMenu = true },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.MoreVert,
                                    contentDescription = "Opciones",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { 
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = null,
                                                    tint = if (canDelete) EventError else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    "Eliminar",
                                                    color = if (canDelete) EventError else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            if (!canDelete) {
                                                Text(
                                                    "Solo se pueden eliminar eventos próximos",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                                    modifier = Modifier.padding(start = 32.dp, top = 4.dp)
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        showMenu = false
                                        if (canDelete) {
                                            onDelete(event)
                                        }
                                    },
                                    enabled = canDelete
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Información del evento
                EventInfoRow(
                    icon = Icons.Default.DateRange,
                    text = dateFormatter.format(Date(event.date)),
                    iconTint = EventPrimary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                EventInfoRow(
                    icon = Icons.Default.LocationOn,
                    text = event.location,
                    iconTint = EventSecondary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                EventInfoRow(
                    icon = Icons.Default.Person,
                    text = "${event.currentGuests} asistentes",
                    iconTint = EventTertiary
                )
            }
        }
    }
}
