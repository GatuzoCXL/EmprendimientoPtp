package com.example.magnus.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.magnus.data.model.Event
import com.example.magnus.data.model.Guest
import com.example.magnus.data.model.GuestStatus
import com.example.magnus.data.repository.EventRepository
import com.example.magnus.data.repository.EventStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EventUiState(
    val isLoading: Boolean = false,
    val events: List<Event> = emptyList(),
    val currentEvent: Event? = null,
    val guests: List<Guest> = emptyList(),
    val error: String? = null,
    val isEventCreated: Boolean = false
)

class EventViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = EventRepository(application)
    
    private val _uiState = MutableStateFlow(EventUiState())
    val uiState: StateFlow<EventUiState> = _uiState.asStateFlow()
    
    fun createEvent(
        name: String,
        description: String,
        date: Long,
        location: String,
        maxGuests: Int,
        creatorId: String,
        creatorName: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val event = Event(
                    id = "event_${System.currentTimeMillis()}",
                    name = name,
                    description = description,
                    date = date,
                    location = location,
                    maxGuests = maxGuests,
                    creatorId = creatorId,
                    creatorName = creatorName,
                    currentGuests = 0
                )
                
                val result = repository.createEvent(event)
                
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isEventCreated = true
                    )
                    // Recargar eventos del usuario
                    loadEventsByCreator(creatorId)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Error al crear evento"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }
    
    fun loadEventsByCreator(creatorId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val result = repository.getEventsByCreator(creatorId)
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        events = result.getOrNull() ?: emptyList()
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Error al cargar eventos"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al cargar eventos"
                )
            }
        }
    }
    
    fun loadEventById(eventId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val result = repository.getEventById(eventId)
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentEvent = result.getOrNull()
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Error al cargar evento"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al cargar evento"
                )
            }
        }
    }
    
    fun addGuest(
        eventId: String,
        name: String,
        email: String,
        phone: String = ""
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val guest = Guest(
                    id = "guest_${System.currentTimeMillis()}",
                    eventId = eventId,
                    name = name,
                    email = email,
                    phone = phone,
                    status = GuestStatus.PENDING
                )
                
                val result = repository.addGuest(guest)
                if (result.isSuccess) {
                    loadGuestsByEvent(eventId)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Error al añadir invitado"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al añadir invitado"
                )
            }
        }
    }
    
    fun loadGuestsByEvent(eventId: String) {
        viewModelScope.launch {
            try {
                val result = repository.getGuestsByEvent(eventId)
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        guests = result.getOrNull() ?: emptyList()
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Error al cargar invitados"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al cargar invitados"
                )
            }
        }
    }
    
    fun updateGuestStatus(guestId: String, status: GuestStatus) {
        viewModelScope.launch {
            try {
                val result = repository.updateGuestStatus(guestId, status)
                if (result.isSuccess) {
                    // Recargar la lista de invitados para reflejar el cambio
                    val currentGuest = _uiState.value.guests.find { it.id == guestId }
                    currentGuest?.let { guest ->
                        loadGuestsByEvent(guest.eventId)
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = result.exceptionOrNull()?.message ?: "Error al actualizar estado"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error al actualizar estado"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun clearEventCreated() {
        _uiState.value = _uiState.value.copy(isEventCreated = false)
    }
    
    // Función para obtener estadísticas
    fun getEventStats(creatorId: String, callback: (EventStats) -> Unit) {
        viewModelScope.launch {
            val stats = repository.getEventStats(creatorId)
            callback(stats)
        }
    }
}
