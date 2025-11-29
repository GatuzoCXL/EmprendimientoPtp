package com.example.magnus.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.magnus.data.model.Event
import com.example.magnus.data.model.Guest
import com.example.magnus.data.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// Extension property para crear el DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "magnus_data")

class DataStoreManager(private val context: Context) {
    private val gson = Gson()
    
    companion object {
        private val CURRENT_USER_ID = stringPreferencesKey("current_user_id")
        private val USERS_JSON = stringPreferencesKey("users_json")
        private val EVENTS_JSON = stringPreferencesKey("events_json")
        private val GUESTS_JSON = stringPreferencesKey("guests_json")
    }
    
    // Guardar ID del usuario actual
    suspend fun saveCurrentUserId(userId: String?) {
        context.dataStore.edit { preferences ->
            if (userId != null) {
                preferences[CURRENT_USER_ID] = userId
            } else {
                preferences.remove(CURRENT_USER_ID)
            }
        }
    }
    
    // Obtener ID del usuario actual
    suspend fun getCurrentUserId(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[CURRENT_USER_ID]
        }.first()
    }
    
    // Guardar usuarios
    suspend fun saveUsers(users: Map<String, User>) {
        context.dataStore.edit { preferences ->
            val json = gson.toJson(users)
            preferences[USERS_JSON] = json
        }
    }
    
    // Obtener usuarios
    suspend fun getUsers(): Map<String, User> {
        val json = context.dataStore.data.map { preferences ->
            preferences[USERS_JSON]
        }.first()
        
        return if (json != null) {
            val type = object : TypeToken<Map<String, User>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyMap()
        }
    }
    
    // Guardar eventos
    suspend fun saveEvents(events: List<Event>) {
        context.dataStore.edit { preferences ->
            val json = gson.toJson(events)
            preferences[EVENTS_JSON] = json
        }
    }
    
    // Obtener eventos
    suspend fun getEvents(): List<Event> {
        val json = context.dataStore.data.map { preferences ->
            preferences[EVENTS_JSON]
        }.first()
        
        return if (json != null) {
            val type = object : TypeToken<List<Event>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }
    
    // Guardar invitados
    suspend fun saveGuests(guests: List<Guest>) {
        context.dataStore.edit { preferences ->
            val json = gson.toJson(guests)
            preferences[GUESTS_JSON] = json
        }
    }
    
    // Obtener invitados
    suspend fun getGuests(): List<Guest> {
        val json = context.dataStore.data.map { preferences ->
            preferences[GUESTS_JSON]
        }.first()
        
        return if (json != null) {
            val type = object : TypeToken<List<Guest>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }
    
    // Limpiar todos los datos
    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
