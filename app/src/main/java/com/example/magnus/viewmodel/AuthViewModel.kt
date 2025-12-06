package com.example.magnus.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.magnus.data.repository.AuthRepository
import com.example.magnus.data.repository.OrganizadorRepository
import com.example.magnus.data.model.User
import com.example.magnus.data.model.Organizador
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val userData: User? = null,
    val organizador: Organizador? = null,
    val error: String? = null,
    val isSignedIn: Boolean = false
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(application)
    private val organizadorRepository = OrganizadorRepository(application)
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    init {
        checkAuthState()
    }
    
    private fun checkAuthState() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            _uiState.value = _uiState.value.copy(
                userData = currentUser,
                isSignedIn = currentUser != null
            )
            
            // Load organizador if user is signed in
            currentUser?.let { loadOrganizadorData(it.id) }
        }
    }
    
    fun loadOrganizadorData(usuarioId: String) {
        viewModelScope.launch {
            val result = organizadorRepository.getOrganizadorByUsuarioId(usuarioId)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(organizador = result.getOrNull())
            }
        }
    }
    
    fun signUp(email: String, password: String, name: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val result = authRepository.signUp(email, password, name)
            
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    userData = result.getOrNull(),
                    isSignedIn = true
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Error desconocido"
                )
            }
        }
    }
    
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val result = authRepository.signIn(email, password)
            
            if (result.isSuccess) {
                val user = result.getOrNull()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    userData = user,
                    isSignedIn = true
                )
                // Load organizador data after successful login
                user?.let { loadOrganizadorData(it.id) }
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Error desconocido"
                )
            }
        }
    }
    
    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _uiState.value = AuthUiState()
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
