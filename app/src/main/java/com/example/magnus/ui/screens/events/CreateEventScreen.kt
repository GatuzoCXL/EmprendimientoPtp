package com.example.magnus.ui.screens.events

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import java.util.Calendar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.magnus.ui.components.*
import com.example.magnus.ui.theme.*
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
    
    // Date and Time states
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var startHour by remember { mutableStateOf(10) }
    var startMinute by remember { mutableStateOf(0) }
    var endHour by remember { mutableStateOf(18) }
    var endMinute by remember { mutableStateOf(0) }
    
    // Dialog states
    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    
    val datePickerState = rememberDatePickerState()
    val startTimePickerState = rememberTimePickerState(
        initialHour = startHour,
        initialMinute = startMinute
    )
    val endTimePickerState = rememberTimePickerState(
        initialHour = endHour,
        initialMinute = endMinute
    )
    
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
    
    // Show success dialog con animación
    AnimatedVisibilityFade(visible = showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            icon = {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = EventSuccess,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    "¡Evento Creado!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Tu evento ha sido creado exitosamente.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Ya puedes verlo en 'Mis Eventos'",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onNavigateBack()
                    },
                    modifier = Modifier.bounceClick()
                ) {
                    Text("Ver Mis Eventos")
                }
            }
        )
    }
    
    val isFormValid = eventName.isNotBlank() && 
                     eventDescription.isNotBlank() && 
                     eventLocation.isNotBlank() && 
                     maxGuests.isNotBlank() && 
                     maxGuests.toIntOrNull() != null &&
                     selectedDateMillis != null
    
    // DatePicker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedDateMillis = datePickerState.selectedDateMillis
                        showDatePicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    // Start Time Picker Dialog
    if (showStartTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showStartTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        startHour = startTimePickerState.hour
                        startMinute = startTimePickerState.minute
                        showStartTimePicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartTimePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            TimePicker(state = startTimePickerState)
        }
    }
    
    // End Time Picker Dialog
    if (showEndTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showEndTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        endHour = endTimePickerState.hour
                        endMinute = endTimePickerState.minute
                        showEndTimePicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndTimePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            TimePicker(state = endTimePickerState)
        }
    }
    
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
            // Event Name con animación
            AnimatedListItem(index = 0) {
                OutlinedTextField(
                value = eventName,
                onValueChange = { eventName = it },
                label = { Text("Nombre del evento") },
                placeholder = { Text("Ej: Fiesta de cumpleaños") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            
            // Event Description con animación
            AnimatedListItem(index = 1) {
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
            }
            
            // Event Location con animación
            AnimatedListItem(index = 2) {
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
            }
            
            // Date Selection con animación
            AnimatedListItem(index = 3) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showDatePicker = true }
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
                            tint = EventPrimary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Fecha del evento",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (selectedDateMillis != null) {
                                    SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("es", "ES"))
                                        .format(Date(selectedDateMillis!!))
                                } else {
                                    "Seleccionar fecha"
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
            
            // Start Time Selection
            AnimatedListItem(index = 4) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        onClick = { showStartTimePicker = true }
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
                                tint = EventSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Hora inicio",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = String.format("%02d:%02d", startHour, startMinute),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    
                    Card(
                        modifier = Modifier.weight(1f),
                        onClick = { showEndTimePicker = true }
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
                                tint = EventTertiary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Hora fin",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = String.format("%02d:%02d", endHour, endMinute),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
            
            // Max Guests con animación
            AnimatedListItem(index = 5) {
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
            }
            
            // Error Message con animación
            AnimatedVisibilityFade(visible = eventState.error != null) {
                eventState.error?.let { errorMessage ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
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
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Create Button con animación
            AnimatedListItem(index = 6) {
                Button(
                    onClick = {
                        if (authState.userData == null) {
                            // Usuario no logueado
                            return@Button
                        }
                        
                        authState.userData?.let { userData ->
                            if (isFormValid) {
                                // Combinar fecha seleccionada con horas
                                val calendar = Calendar.getInstance()
                                calendar.timeInMillis = selectedDateMillis!!
                                
                                // Fecha de inicio con hora seleccionada
                                calendar.set(Calendar.HOUR_OF_DAY, startHour)
                                calendar.set(Calendar.MINUTE, startMinute)
                                calendar.set(Calendar.SECOND, 0)
                                val startDateTime = calendar.timeInMillis
                                
                                // Fecha de fin con hora seleccionada (mismo día)
                                calendar.set(Calendar.HOUR_OF_DAY, endHour)
                                calendar.set(Calendar.MINUTE, endMinute)
                                val endDateTime = calendar.timeInMillis
                                
                                eventViewModel.createEvent(
                                    name = eventName.trim(),
                                    description = eventDescription.trim(),
                                    date = startDateTime,
                                    endDate = endDateTime,
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
                        .bounceClick(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EventPrimary
                    )
                ) {
                    if (eventState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = androidx.compose.ui.graphics.Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Creando...", fontSize = 16.sp)
                    } else {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Crear Evento", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            // Debug info (solo para desarrollo)
            AnimatedVisibilityFade(visible = authState.userData == null) {
                if (authState.userData == null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "⚠️", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Usuario no detectado. Por favor, vuelve a iniciar sesión.",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
