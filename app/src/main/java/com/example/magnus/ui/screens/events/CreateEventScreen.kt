package com.example.magnus.ui.screens.events

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.magnus.viewmodel.AuthViewModel
import com.example.magnus.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    onNavigateBack: () -> Unit,
    authViewModel: AuthViewModel = viewModel(),
    eventViewModel: EventViewModel = viewModel()
) {
    var eventName by remember { mutableStateOf("") }
    var eventDescription by remember { mutableStateOf("") }
    var eventLocation by remember { mutableStateOf("") }
    var maxGuests by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    
    val authState by authViewModel.uiState.collectAsState()
    val eventState by eventViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Navigate back when event is created
    LaunchedEffect(eventState.isEventCreated) {
        if (eventState.isEventCreated) {
            showSuccessDialog = true
            eventViewModel.clearEventCreated()
        }
    }
    
    // Show success dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("¡Evento Creado!") },
            text = { Text("Tu evento ha sido creado exitosamente.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSuccessDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }
    
    val isFormValid = eventName.isNotBlank() && 
                     eventDescription.isNotBlank() && 
                     eventLocation.isNotBlank() && 
                     maxGuests.isNotBlank() && 
                     maxGuests.toIntOrNull() != null &&
                     selectedDate != null
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Evento") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Event Name
            OutlinedTextField(
                value = eventName,
                onValueChange = { eventName = it },
                label = { Text("Nombre del evento") },
                placeholder = { Text("Ej: Fiesta de cumpleaños") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Event Description
            OutlinedTextField(
                value = eventDescription,
                onValueChange = { eventDescription = it },
                label = { Text("Descripción") },
                placeholder = { Text("Describe tu evento...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 4
            )
            
            // Event Location
            OutlinedTextField(
                value = eventLocation,
                onValueChange = { eventLocation = it },
                label = { Text("Ubicación") },
                placeholder = { Text("Ej: Salón de eventos Plaza") },
                leadingIcon = {
                    Icon(Icons.Default.LocationOn, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Date Selection
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    // TODO: Implementar date picker
                    selectedDate = System.currentTimeMillis() + (24 * 60 * 60 * 1000) // Mañana por defecto
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
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
                            text = "Fecha del evento",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (selectedDate != null) {
                                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                    .format(Date(selectedDate!!))
                            } else {
                                "Seleccionar fecha"
                            },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            // Max Guests
            OutlinedTextField(
                value = maxGuests,
                onValueChange = { 
                    if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                        maxGuests = it
                    }
                },
                label = { Text("Número máximo de invitados") },
                placeholder = { Text("Ej: 50") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Error Message
            eventState.error?.let { errorMessage ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Create Button
            Button(
                onClick = {
                    if (authState.userData == null) {
                        // Usuario no logueado
                        return@Button
                    }
                    
                    authState.userData?.let { userData ->
                        if (isFormValid) {
                            eventViewModel.createEvent(
                                name = eventName.trim(),
                                description = eventDescription.trim(),
                                date = selectedDate!!,
                                location = eventLocation.trim(),
                                maxGuests = maxGuests.toInt(),
                                creatorId = userData.id,
                                creatorName = userData.name
                            )
                        }
                    }
                },
                enabled = !eventState.isLoading && isFormValid && authState.userData != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (eventState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Crear Evento", fontSize = 16.sp)
                }
            }
            
            // Debug info (solo para desarrollo)
            if (authState.userData == null) {
                Text(
                    text = "⚠️ Usuario no detectado. Por favor, vuelve a iniciar sesión.",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
