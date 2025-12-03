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
        endDate: Long,
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
                    endDate = endDate,
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
    
    // TODO: Backend no tiene endpoints de Guests aún
    // Implementar cuando el backend tenga estos endpoints
    fun addGuest(
        eventId: String,
        name: String,
        email: String,
        phone: String = ""
    ) {
        _uiState.value = _uiState.value.copy(
            error = "Funcionalidad de invitados próximamente"
        )
    }
    
    fun loadGuestsByEvent(eventId: String) {
        _uiState.value = _uiState.value.copy(
            guests = emptyList()
        )
    }
    
    fun updateGuestStatus(guestId: String, status: GuestStatus) {
        _uiState.value = _uiState.value.copy(
            error = "Funcionalidad de invitados próximamente"
        )
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun clearEventCreated() {
        _uiState.value = _uiState.value.copy(isEventCreated = false)
    }
    
    fun deleteEvent(eventId: String, eventDate: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            // Validar que el evento pueda eliminarse
            val now = System.currentTimeMillis()
            val isUpcoming = eventDate > now
            
            if (!isUpcoming) {
                _uiState.value = _uiState.value.copy(
                    error = "No puedes eliminar un evento que ya comenzó o finalizó"
                )
                return@launch
            }
            
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val result = repository.deleteEvent(eventId)
                
                if (result.isSuccess) {
                    // Actualizar lista de eventos (remover el eliminado)
                    val updatedEvents = _uiState.value.events.filter { it.id != eventId }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        events = updatedEvents
                    )
                    onSuccess()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Error al eliminar evento"
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
    
    // Función para obtener estadísticas
    fun getEventStats(creatorId: String, callback: (EventStats) -> Unit) {
        viewModelScope.launch {
            val result = repository.getEventsByCreator(creatorId)
            val events = result.getOrNull() ?: emptyList()
            
            val stats = EventStats(
                totalEvents = events.size,
                totalGuests = events.sumOf { it.currentGuests },
                upcomingEvents = events.count { it.date > System.currentTimeMillis() }
            )
            callback(stats)
        }
    }
}
