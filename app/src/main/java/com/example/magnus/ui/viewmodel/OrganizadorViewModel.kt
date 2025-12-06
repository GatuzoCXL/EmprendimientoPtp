package com.example.magnus.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.magnus.data.model.CreateOrganizadorData
import com.example.magnus.data.model.Organizador
import com.example.magnus.data.repository.OrganizadorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrganizadorViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = OrganizadorRepository(application)

    private val _organizadores = MutableStateFlow<List<Organizador>>(emptyList())
    val organizadores: StateFlow<List<Organizador>> = _organizadores.asStateFlow()

    private val _selectedOrganizador = MutableStateFlow<Organizador?>(null)
    val selectedOrganizador: StateFlow<Organizador?> = _selectedOrganizador.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _createSuccess = MutableStateFlow(false)
    val createSuccess: StateFlow<Boolean> = _createSuccess.asStateFlow()

    private val _existingOrganizador = MutableStateFlow<Organizador?>(null)
    val existingOrganizador: StateFlow<Organizador?> = _existingOrganizador.asStateFlow()

    fun loadOrganizadores() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.getAllOrganizadores()
                .onSuccess { _organizadores.value = it }
                .onFailure { _error.value = it.message ?: "Error desconocido" }
            _isLoading.value = false
        }
    }

    fun loadOrganizadorById(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.getOrganizadorById(id)
                .onSuccess { _selectedOrganizador.value = it }
                .onFailure { _error.value = it.message ?: "Error desconocido" }
            _isLoading.value = false
        }
    }

    fun checkIfUserIsOrganizador(usuarioId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.getOrganizadorByUsuarioId(usuarioId)
                .onSuccess { _existingOrganizador.value = it }
                .onFailure { _error.value = it.message ?: "Error al verificar organizador" }
            _isLoading.value = false
        }
    }

    fun createOrganizador(data: CreateOrganizadorData) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _createSuccess.value = false
            repository.createOrganizador(data)
                .onSuccess { 
                    _createSuccess.value = true
                    loadOrganizadores()
                }
                .onFailure { _error.value = it.message ?: "Error al crear organizador" }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearCreateSuccess() {
        _createSuccess.value = false
    }
}
