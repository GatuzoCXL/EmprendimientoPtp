package com.example.magnus.ui.screens.organizadores

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.magnus.data.model.CreateOrganizadorData
import com.example.magnus.ui.viewmodel.OrganizadorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BecomeOrganizerScreen(
    navController: NavController,
    authViewModel: com.example.magnus.viewmodel.AuthViewModel,
    viewModel: OrganizadorViewModel = viewModel()
) {
    val authState by authViewModel.uiState.collectAsState()
    val userId = authState.userData?.id ?: ""
    var nombreEmpresa by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var precioPorEvento by remember { mutableStateOf("") }
    var añosExperiencia by remember { mutableStateOf("") }
    var especialidad by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    var validationError by remember { mutableStateOf<String?>(null) }

    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val createSuccess by viewModel.createSuccess.collectAsState()
    val existingOrganizador by viewModel.existingOrganizador.collectAsState()

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            viewModel.checkIfUserIsOrganizador(userId)
        }
    }

    LaunchedEffect(existingOrganizador) {
        existingOrganizador?.let { organizador ->
            navController.navigate("organizador_detail/${organizador.id}") {
                popUpTo("become_organizer") { inclusive = true }
            }
        }
    }

    LaunchedEffect(createSuccess) {
        if (createSuccess) {
            navController.navigateUp()
            viewModel.clearCreateSuccess()
        }
    }

    LaunchedEffect(error) {
        error?.let {
            // El error se mostrará en un Snackbar
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Conviértete en Organizador") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
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
                .padding(16.dp)
        ) {
            Text(
                text = "Completa tu perfil profesional para ofrecer tus servicios",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = nombreEmpresa,
                onValueChange = { nombreEmpresa = it },
                label = { Text("Nombre de la Empresa *") },
                placeholder = { Text("Bodas Mágicas") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono de Contacto *") },
                placeholder = { Text("+52 123 456 7890") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección *") },
                placeholder = { Text("Guadalajara, Jalisco") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = { Text("Mínimo 5 caracteres") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = precioPorEvento,
                    onValueChange = { precioPorEvento = it },
                    label = { Text("Precio por Evento *") },
                    placeholder = { Text("15000") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    prefix = { Text("$") },
                    supportingText = { Text("$1K - $500K", style = MaterialTheme.typography.labelSmall) }
                )

                OutlinedTextField(
                    value = añosExperiencia,
                    onValueChange = { añosExperiencia = it },
                    label = { Text("Años de Experiencia *") },
                    placeholder = { Text("5") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    supportingText = { Text("1 - 50 años", style = MaterialTheme.typography.labelSmall) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = especialidad,
                onValueChange = { especialidad = it },
                label = { Text("Especialidad *") },
                placeholder = { Text("Bodas elegantes, eventos corporativos, XV años...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = { Text("Mínimo 5 caracteres") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción del Servicio *") },
                placeholder = { Text("Describe tus servicios, experiencia, logros y qué te hace único...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 6,
                supportingText = { Text("${descripcion.length} / 50 caracteres mínimo") }
            )   maxLines = 6
            )

            Spacer(modifier = Modifier.height(24.dp))

            (validationError ?: error)?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = it,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = {
                        validationError = null
                        
                        // Validaciones
                        if (nombreEmpresa.length < 3) {
                            validationError = "El nombre de la empresa debe tener al menos 3 caracteres"
                            return@Button
                        }
                        
                        if (telefono.length < 10) {
                            validationError = "El teléfono debe tener al menos 10 dígitos"
                            return@Button
                        }
                        
                        if (direccion.length < 5) {
                            validationError = "La dirección debe tener al menos 5 caracteres"
                            return@Button
                        }
                        
                        val precio = precioPorEvento.toDoubleOrNull()
                        if (precio == null || precio < 1000 || precio > 500000) {
                            validationError = "El precio debe estar entre $1,000 y $500,000"
                            return@Button
                        }
                        
                        val experiencia = añosExperiencia.toIntOrNull()
                        if (experiencia == null || experiencia < 1 || experiencia > 50) {
                            validationError = "Los años de experiencia deben estar entre 1 y 50"
                            return@Button
                        }
                        
                        if (especialidad.length < 5) {
                            validationError = "La especialidad debe tener al menos 5 caracteres"
                            return@Button
                        }
                        
                        if (descripcion.length < 50) {
                            validationError = "La descripción debe tener al menos 50 caracteres"
                            return@Button
                        }

                        val data = CreateOrganizadorData(
                            nombreEmpresa = nombreEmpresa.trim(),
                            descripcion = descripcion.trim(),
                            telefono = telefono.trim(),
                            direccion = direccion.trim(),
                            precioPorEvento = precio,
                            añosExperiencia = experiencia,
                            especialidad = especialidad.trim(),
                            usuarioId = userId
                        )
                        viewModel.createOrganizador(data)
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading && nombreEmpresa.isNotBlank() && telefono.isNotBlank() && 
                              precioPorEvento.isNotBlank() && añosExperiencia.isNotBlank()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Crear Perfil")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "* Campos requeridos",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
