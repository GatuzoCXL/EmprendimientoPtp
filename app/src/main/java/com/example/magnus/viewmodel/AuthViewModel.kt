package com.example.magnus.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.magnus.data.repository.MockAuthRepository
import com.example.magnus.data.repository.MockUser
import com.example.magnus.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val currentUser: MockUser? = null,
    val userData: User? = null,
    val error: String? = null,
    val isSignedIn: Boolean = false
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = MockAuthRepository(application)
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    init {
        checkAuthState()
    }
    
    private fun checkAuthState() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            _uiState.value = _uiState.value.copy(
                currentUser = currentUser,
                isSignedIn = currentUser != null
            )
            
            if (currentUser != null) {
                loadUserData()
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
                    currentUser = result.getOrNull(),
                    isSignedIn = true
                )
                loadUserData()
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
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    currentUser = result.getOrNull(),
                    isSignedIn = true
                )
                loadUserData()
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
    
    private fun loadUserData() {
        viewModelScope.launch {
            val result = authRepository.getCurrentUserData()
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(userData = result.getOrNull())
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
