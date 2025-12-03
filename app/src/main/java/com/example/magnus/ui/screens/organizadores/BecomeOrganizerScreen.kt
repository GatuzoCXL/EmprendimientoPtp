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
    userId: String,
    viewModel: OrganizadorViewModel = viewModel()
) {
    var nombreEmpresa by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var precioPorEvento by remember { mutableStateOf("") }
    var añosExperiencia by remember { mutableStateOf("") }
    var especialidad by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val createSuccess by viewModel.createSuccess.collectAsState()

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
                label = { Text("Dirección") },
                placeholder = { Text("Ciudad, Estado") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
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
                    placeholder = { Text("5000") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    prefix = { Text("$") }
                )

                OutlinedTextField(
                    value = añosExperiencia,
                    onValueChange = { añosExperiencia = it },
                    label = { Text("Años de Experiencia *") },
                    placeholder = { Text("5") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = especialidad,
                onValueChange = { especialidad = it },
                label = { Text("Especialidad") },
                placeholder = { Text("Bodas elegantes, eventos al aire libre, etc.") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción del Servicio") },
                placeholder = { Text("Cuéntanos sobre tus servicios, experiencia, y qué te hace único...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 6
            )

            Spacer(modifier = Modifier.height(24.dp))

            error?.let {
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
                        val precio = precioPorEvento.toDoubleOrNull()
                        val experiencia = añosExperiencia.toIntOrNull()

                        if (nombreEmpresa.isBlank() || telefono.isBlank() || precio == null || experiencia == null) {
                            // Validación básica
                            return@Button
                        }

                        val data = CreateOrganizadorData(
                            nombreEmpresa = nombreEmpresa,
                            descripcion = descripcion.ifBlank { null },
                            telefono = telefono,
                            direccion = direccion.ifBlank { null },
                            precioPorEvento = precio,
                            añosExperiencia = experiencia,
                            especialidad = especialidad.ifBlank { null },
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
