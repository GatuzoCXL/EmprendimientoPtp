package com.example.magnus.data.repository

import android.content.Context
import com.example.magnus.data.DataStoreManager
import com.example.magnus.data.model.Event
import com.example.magnus.data.remote.RetrofitClient
import com.example.magnus.data.remote.dto.CreateEventRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class EventRepository(context: Context) {
    private val appContext = context.applicationContext
    private val apiService = RetrofitClient.getApiService(appContext)
    private val dataStore = DataStoreManager(appContext)
    
    suspend fun getAllEvents(): Result<List<Event>> = withContext(Dispatchers.IO) {
        return@withContext try {
            // El backend no tiene endpoint para todos los eventos
            // Cargar desde cache local
            val cachedEvents = dataStore.getEvents()
            if (cachedEvents.isNotEmpty()) {
                Result.success(cachedEvents)
            } else {
                Result.failure(Exception("No hay eventos disponibles. Use getEventsByCreator() en su lugar."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error al cargar eventos: ${e.message}"))
        }
    }
    
    suspend fun getEventById(id: String): Result<Event> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.getEvento(id)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val dto = response.body()!!.data!!
                
                val event = Event(
                    id = dto.id,
                    name = dto.titulo,
                    description = dto.descripcion ?: "",
                    date = parseIsoToMillis(dto.fechaInicio),
                    endDate = parseIsoToMillis(dto.fechaFin),
                    location = dto.lugar,
                    latitude = 0.0,
                    longitude = 0.0,
                    creatorId = dto.organizadorId,
                    creatorName = dto.organizador?.nombreEmpresa ?: "Desconocido",
                    imageUrl = "",
                    maxGuests = dto.capacidad,
                    currentGuests = 0,
                    isPublic = true,
                    createdAt = parseIsoToMillis(dto.createdAt),
                    updatedAt = parseIsoToMillis(dto.createdAt)
                )
                
                Result.success(event)
            } else {
                val errorMessage = response.body()?.message ?: "Evento no encontrado"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
    
    suspend fun createEvent(event: Event): Result<Event> = withContext(Dispatchers.IO) {
        return@withContext try {
            val userId = dataStore.getCurrentUserId() 
                ?: return@withContext Result.failure(Exception("Usuario no autenticado"))
            
            val request = CreateEventRequest(
                titulo = event.name,
                descripcion = event.description,
                fechaInicio = formatMillisToIso(event.date),
                fechaFin = formatMillisToIso(event.endDate),
                lugar = event.location,
                capacidad = event.maxGuests,
                organizadorId = userId
            )
            
            val response = apiService.createEvento(request)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val dto = response.body()!!.data!!
                
                val createdEvent = Event(
                    id = dto.id,
                    name = dto.titulo,
                    description = dto.descripcion ?: "",
                    date = parseIsoToMillis(dto.fechaInicio),
                    endDate = parseIsoToMillis(dto.fechaFin),
                    location = dto.lugar,
                    latitude = event.latitude,
                    longitude = event.longitude,
                    creatorId = dto.organizadorId,
                    creatorName = dto.organizador?.nombreEmpresa ?: "Desconocido",
                    imageUrl = event.imageUrl,
                    maxGuests = dto.capacidad,
                    currentGuests = 0,
                    isPublic = event.isPublic,
                    createdAt = parseIsoToMillis(dto.createdAt),
                    updatedAt = parseIsoToMillis(dto.createdAt)
                )
                
                Result.success(createdEvent)
            } else {
                val errorMessage = response.body()?.message ?: "Error al crear evento"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
    
    suspend fun updateEvent(event: Event): Result<Event> = withContext(Dispatchers.IO) {
        return@withContext try {
            val userId = dataStore.getCurrentUserId() 
                ?: return@withContext Result.failure(Exception("Usuario no autenticado"))
            
            val request = CreateEventRequest(
                titulo = event.name,
                descripcion = event.description,
                fechaInicio = formatMillisToIso(event.date),
                fechaFin = formatMillisToIso(event.endDate),
                lugar = event.location,
                capacidad = event.maxGuests,
                organizadorId = userId
            )
            
            val response = apiService.updateEvento(event.id, request)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val dto = response.body()!!.data!!
                
                val updatedEvent = Event(
                    id = dto.id,
                    name = dto.titulo,
                    description = dto.descripcion ?: "",
                    date = parseIsoToMillis(dto.fechaInicio),
                    endDate = parseIsoToMillis(dto.fechaFin),
                    location = dto.lugar,
                    latitude = event.latitude,
                    longitude = event.longitude,
                    creatorId = dto.organizadorId,
                    creatorName = dto.organizador?.nombreEmpresa ?: "Desconocido",
                    imageUrl = event.imageUrl,
                    maxGuests = dto.capacidad,
                    currentGuests = event.currentGuests,
                    isPublic = event.isPublic,
                    createdAt = event.createdAt,
                    updatedAt = System.currentTimeMillis()
                )
                
                Result.success(updatedEvent)
            } else {
                val errorMessage = response.body()?.message ?: "Error al actualizar evento"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
    
    suspend fun deleteEvent(eventId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.deleteEvento(eventId)
            
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                val errorMessage = response.body()?.message ?: "Error al eliminar evento"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
    
    suspend fun getEventsByCreator(creatorId: String): Result<List<Event>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.getEventosPorOrganizador(creatorId)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val eventosDto = response.body()!!.data ?: emptyList()
                
                val events = eventosDto.map { dto ->
                    Event(
                        id = dto.id,
                        name = dto.titulo,
                        description = dto.descripcion ?: "",
                        date = parseIsoToMillis(dto.fechaInicio),
                        endDate = parseIsoToMillis(dto.fechaFin),
                        location = dto.lugar,
                        latitude = 0.0,
                        longitude = 0.0,
                        creatorId = dto.organizadorId,
                        creatorName = dto.organizador?.nombreEmpresa ?: "Desconocido",
                        imageUrl = "",
                        maxGuests = dto.capacidad,
                        currentGuests = 0,
                        isPublic = true,
                        createdAt = parseIsoToMillis(dto.createdAt),
                        updatedAt = parseIsoToMillis(dto.createdAt)
                    )
                }
                
                // Cache events locally
                dataStore.saveEvents(events)
                
                Result.success(events)
            } else {
                val errorMessage = response.body()?.message ?: "Error al cargar eventos"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            // Try to load from cache if network fails
            try {
                val cachedEvents = dataStore.getEvents()
                    .filter { it.creatorId == creatorId }
                if (cachedEvents.isNotEmpty()) {
                    Result.success(cachedEvents)
                } else {
                    Result.failure(Exception("Error de conexión: ${e.message}"))
                }
            } catch (cacheError: Exception) {
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }
    }
    
    // Helper functions for date conversion
    private fun parseIsoToMillis(isoDate: String): Long {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            format.parse(isoDate)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }
    
    private fun formatMillisToIso(millis: Long): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return format.format(Date(millis))
    }
}
