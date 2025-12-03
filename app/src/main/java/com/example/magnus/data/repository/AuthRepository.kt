package com.example.magnus.data.repository

import android.content.Context
import com.example.magnus.data.DataStoreManager
import com.example.magnus.data.model.User
import com.example.magnus.data.remote.RetrofitClient
import com.example.magnus.data.remote.dto.LoginRequest
import com.example.magnus.data.remote.dto.RegisterRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(context: Context) {
    private val appContext = context.applicationContext
    private val apiService = RetrofitClient.getApiService(appContext)
    private val dataStore = DataStoreManager(appContext)
    
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
                    rol = com.example.magnus.data.model.UserRole.fromInt(loginData.user.rol),
                    profileImageUrl = "", // Backend doesn't have this yet
                    createdAt = parseIsoDate(loginData.user.createdAt)
                )
                
                dataStore.saveUserData(user)
                
                Result.success(user)
            } else {
                // Parse validation errors from backend
                val errorMessage = if (response.code() == 400) {
                    try {
                        val errorBody = response.errorBody()?.string()
                        parseValidationErrors(errorBody)
                    } catch (e: Exception) {
                        response.body()?.message ?: "Credenciales inválidas"
                    }
                } else {
                    response.body()?.message ?: "Error al iniciar sesión"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
    
    suspend fun signUp(email: String, password: String, name: String, rol: Int = 0): Result<User> = withContext(Dispatchers.IO) {
        return@withContext try {
            val request = RegisterRequest(name, email, password, rol)
            val response = apiService.register(request)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val userData = response.body()!!.data!!
                
                // After registration, automatically login
                signIn(email, password)
            } else {
                val errorBody = response.errorBody()?.string() ?: ""
                
                val errorMessage = if (errorBody.contains("\"errors\"")) {
                    parseApiErrors(errorBody)
                } else {
                    "Error al registrarse"
                }
                
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
    
    private fun parseValidationErrors(errorBody: String?): String {
        if (errorBody.isNullOrEmpty()) return "Error de validación"
        
        return try {
            val gson = com.google.gson.Gson()
            val errorResponse = gson.fromJson(errorBody, com.example.magnus.data.remote.dto.ValidationErrorResponse::class.java)
            
            // Extract all error messages from the errors map
            val allErrors = errorResponse.errors?.flatMap { (field, messages) ->
                messages.map { "$field: $it" }
            } ?: listOf(errorResponse.title ?: "Error de validación")
            
            // Return first 3 errors joined
            allErrors.take(3).joinToString("\n")
        } catch (e: Exception) {
            "Error de validación"
        }
    }
    
    private fun parseApiErrors(errorBody: String?): String {
        if (errorBody.isNullOrEmpty()) return "Error del servidor"
        
        return try {
            val gson = com.google.gson.Gson()
            val errorResponse = gson.fromJson(errorBody, com.example.magnus.data.remote.dto.ApiResponse::class.java)
            
            val errors = errorResponse.errors?.firstOrNull() 
                ?: errorResponse.message 
                ?: "Error del servidor"
            
            when {
                errors.contains("Ya existe un usuario", ignoreCase = true) ->
                    "Este correo electrónico ya está registrado. Intenta iniciar sesión."
                errors.contains("initialization string", ignoreCase = true) || 
                errors.contains("connection", ignoreCase = true) ->
                    "El servidor está experimentando problemas. Por favor, intenta más tarde."
                else -> errors
            }
        } catch (e: Exception) {
            "Error del servidor"
        }
    }
}
