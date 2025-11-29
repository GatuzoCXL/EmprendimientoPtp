package com.example.magnus.data.repository

import android.content.Context
import com.example.magnus.data.DataStoreManager
import com.example.magnus.data.model.Event
import com.example.magnus.data.model.Guest
import com.example.magnus.data.model.GuestStatus
import kotlinx.coroutines.delay

// Repository con persistencia local usando DataStore
class MockEventRepository(context: Context) {
    private val dataStore = DataStoreManager(context)
    
    suspend fun createEvent(event: Event): Result<String> {
        return try {
            delay(1000) // Simular delay de red
            val events = dataStore.getEvents().toMutableList()
            events.add(event)
            dataStore.saveEvents(events)
            Result.success(event.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getEventsByCreator(creatorId: String): Result<List<Event>> {
        return try {
            delay(500)
            val events = dataStore.getEvents()
            val userEvents = events.filter { it.creatorId == creatorId }
            Result.success(userEvents)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getEventById(eventId: String): Result<Event?> {
        return try {
            delay(300)
            val events = dataStore.getEvents()
            val event = events.find { it.id == eventId }
            Result.success(event)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateEvent(event: Event): Result<Unit> {
        return try {
            delay(500)
            val events = dataStore.getEvents().toMutableList()
            val index = events.indexOfFirst { it.id == event.id }
            if (index != -1) {
                events[index] = event.copy(updatedAt = System.currentTimeMillis())
                dataStore.saveEvents(events)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteEvent(eventId: String): Result<Unit> {
        return try {
            delay(300)
            val events = dataStore.getEvents().toMutableList()
            events.removeAll { it.id == eventId }
            dataStore.saveEvents(events)
            
            val guests = dataStore.getGuests().toMutableList()
            guests.removeAll { it.eventId == eventId }
            dataStore.saveGuests(guests)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Guest management
    suspend fun addGuest(guest: Guest): Result<String> {
        return try {
            delay(500)
            val guests = dataStore.getGuests().toMutableList()
            guests.add(guest)
            dataStore.saveGuests(guests)
            
            // Actualizar contador de invitados en el evento
            val events = dataStore.getEvents().toMutableList()
            val eventIndex = events.indexOfFirst { it.id == guest.eventId }
            if (eventIndex != -1) {
                events[eventIndex] = events[eventIndex].copy(
                    currentGuests = events[eventIndex].currentGuests + 1
                )
                dataStore.saveEvents(events)
            }
            
            Result.success(guest.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getGuestsByEvent(eventId: String): Result<List<Guest>> {
        return try {
            delay(300)
            val guests = dataStore.getGuests()
            val eventGuests = guests.filter { it.eventId == eventId }
            Result.success(eventGuests)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateGuestStatus(guestId: String, status: GuestStatus): Result<Unit> {
        return try {
            delay(300)
            val guests = dataStore.getGuests().toMutableList()
            val guestIndex = guests.indexOfFirst { it.id == guestId }
            if (guestIndex != -1) {
                guests[guestIndex] = guests[guestIndex].copy(
                    status = status,
                    respondedAt = System.currentTimeMillis()
                )
                dataStore.saveGuests(guests)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Función para obtener estadísticas
    suspend fun getEventStats(creatorId: String): EventStats {
        val events = dataStore.getEvents()
        val guests = dataStore.getGuests()
        
        val userEvents = events.filter { it.creatorId == creatorId }
        val totalGuests = userEvents.sumOf { event ->
            guests.count { it.eventId == event.id }
        }
        val confirmedGuests = userEvents.sumOf { event ->
            guests.count { it.eventId == event.id && it.status == GuestStatus.CONFIRMED }
        }
        
        return EventStats(
            totalEvents = userEvents.size,
            totalGuests = totalGuests,
            confirmedGuests = confirmedGuests,
            pendingGuests = totalGuests - confirmedGuests
        )
    }
    
    // Función para limpiar datos (útil para testing)
    suspend fun clearAllData() {
        dataStore.clearAll()
    }
}

data class EventStats(
    val totalEvents: Int,
    val totalGuests: Int,
    val confirmedGuests: Int,
    val pendingGuests: Int
)
