package com.example.magnus.data.repository

import android.content.Context
import com.example.magnus.data.DataStoreManager
import com.example.magnus.data.model.User
import kotlinx.coroutines.delay

// Mock repository para desarrollo sin Firebase con persistencia local
class MockAuthRepository(context: Context) {
    private val dataStore = DataStoreManager(context)
    
    suspend fun getCurrentUser(): MockUser? {
        val userId = dataStore.getCurrentUserId()
        return userId?.let { MockUser(it) }
    }
    
    suspend fun signUp(email: String, password: String, name: String): Result<MockUser> {
        return try {
            delay(1000) // Simular delay de red
            
            // Cargar usuarios existentes
            val users = dataStore.getUsers().toMutableMap()
            
            // Verificar si el email ya existe
            if (users.values.any { it.email == email }) {
                throw Exception("El email ya está registrado")
            }
            
            val userId = "user_${System.currentTimeMillis()}"
            val user = User(
                id = userId,
                email = email,
                name = name
            )
            
            // Guardar nuevo usuario
            users[userId] = user
            dataStore.saveUsers(users)
            dataStore.saveCurrentUserId(userId)
            
            Result.success(MockUser(userId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signIn(email: String, password: String): Result<MockUser> {
        return try {
            delay(1000) // Simular delay de red
            
            // Cargar usuarios existentes
            val users = dataStore.getUsers()
            
            val user = users.values.find { it.email == email }
            if (user != null) {
                dataStore.saveCurrentUserId(user.id)
                Result.success(MockUser(user.id))
            } else {
                throw Exception("Email o contraseña incorrectos")
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signOut() {
        dataStore.saveCurrentUserId(null)
    }
    
    suspend fun getCurrentUserData(): Result<User?> {
        return try {
            delay(500)
            val userId = dataStore.getCurrentUserId()
            val users = dataStore.getUsers()
            val user = userId?.let { users[it] }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Mock user class
data class MockUser(val uid: String)
