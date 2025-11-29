package com.example.magnus.data.repository

import android.content.Context
import com.example.magnus.data.DataStoreManager
import com.example.magnus.data.model.User
import com.example.magnus.data.remote.RetrofitClient
import com.example.magnus.data.remote.dto.LoginRequest
import com.example.magnus.data.remote.dto.RegisterRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val context: Context) {
    private val apiService = RetrofitClient.getApiService(context)
    private val dataStore = DataStoreManager(context)
    
    suspend fun getCurrentUser(): User? = withContext(Dispatchers.IO) {
        return@withContext dataStore.getUserData()
    }
    
    suspend fun signIn(email: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        return@withContext try {
            val request = LoginRequest(email, password)
            val response = apiService.login(request)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val loginData = response.body()!!.data!!
                
                // Save token and user data
                dataStore.saveAuthToken(loginData.token)
                dataStore.saveTokenExpiry(loginData.expiresAtUtc)
                dataStore.saveCurrentUserId(loginData.user.id)
                
                // Convert UserDto to User model
                val user = User(
                    id = loginData.user.id,
                    email = loginData.user.email,
                    name = loginData.user.nombre,
                    profileImageUrl = "", // Backend doesn't have this yet
                    createdAt = parseIsoDate(loginData.user.createdAt)
                )
                
                dataStore.saveUserData(user)
                
                Result.success(user)
            } else {
                val errorMessage = response.body()?.message ?: "Error al iniciar sesión"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
    
    suspend fun signUp(email: String, password: String, name: String): Result<User> = withContext(Dispatchers.IO) {
        return@withContext try {
            val request = RegisterRequest(name, email, password)
            val response = apiService.register(request)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val userData = response.body()!!.data!!
                
                // After registration, automatically login
                signIn(email, password)
            } else {
                val errorMessage = response.body()?.message ?: "Error al registrarse"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
    
    suspend fun signOut(): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            dataStore.clearAll()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun isUserSignedIn(): Boolean = withContext(Dispatchers.IO) {
        val token = dataStore.getAuthToken()
        return@withContext !token.toString().isNullOrEmpty()
    }
    
    private fun parseIsoDate(isoDate: String): Long {
        return try {
            // Simple parsing - you might want to use a proper date library
            System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }
}
