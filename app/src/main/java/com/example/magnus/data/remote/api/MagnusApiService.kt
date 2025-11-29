package com.example.magnus.data.remote.api

import com.example.magnus.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface MagnusApiService {
    
    // ============ AUTH ENDPOINTS ============
    
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<LoginResponse>>
    
    @POST("api/auth/registrar")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<ApiResponse<UserDto>>
    
    // ============ EVENTOS ENDPOINTS ============
    
    @GET("api/eventos")
    suspend fun getEventos(): Response<ApiResponse<List<EventoDto>>>
    
    @GET("api/eventos/{id}")
    suspend fun getEvento(
        @Path("id") id: String
    ): Response<ApiResponse<EventoDto>>
    
    @POST("api/eventos")
    suspend fun createEvento(
        @Body request: CreateEventRequest
    ): Response<ApiResponse<EventoDto>>
    
    @PUT("api/eventos/{id}")
    suspend fun updateEvento(
        @Path("id") id: String,
        @Body request: CreateEventRequest
    ): Response<ApiResponse<EventoDto>>
    
    @DELETE("api/eventos/{id}")
    suspend fun deleteEvento(
        @Path("id") id: String
    ): Response<ApiResponse<Unit>>
}
