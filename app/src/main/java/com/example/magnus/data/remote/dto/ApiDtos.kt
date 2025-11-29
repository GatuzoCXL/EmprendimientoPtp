package com.example.magnus.data.remote.dto

import com.google.gson.annotations.SerializedName

// API Response wrapper
data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("data")
    val data: T?,
    
    @SerializedName("message")
    val message: String?,
    
    @SerializedName("errors")
    val errors: List<String>?
)

// Auth DTOs
data class LoginRequest(
    @SerializedName("email")
    val email: String,
    
    @SerializedName("password")
    val password: String
)

data class RegisterRequest(
    @SerializedName("nombre")
    val nombre: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("password")
    val password: String
)

data class LoginResponse(
    @SerializedName("token")
    val token: String,
    
    @SerializedName("expiresAtUtc")
    val expiresAtUtc: String,
    
    @SerializedName("user")
    val user: UserDto
)

data class UserDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("nombre")
    val nombre: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("createdAt")
    val createdAt: String
)

// Event DTOs
data class EventoDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("titulo")
    val titulo: String,
    
    @SerializedName("descripcion")
    val descripcion: String?,
    
    @SerializedName("fechaInicio")
    val fechaInicio: String,
    
    @SerializedName("fechaFin")
    val fechaFin: String,
    
    @SerializedName("lugar")
    val lugar: String,
    
    @SerializedName("capacidad")
    val capacidad: Int,
    
    @SerializedName("organizadorId")
    val organizadorId: String,
    
    @SerializedName("createdAt")
    val createdAt: String,
    
    @SerializedName("organizador")
    val organizador: OrganizadorDto?
)

data class OrganizadorDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("nombre")
    val nombre: String,
    
    @SerializedName("email")
    val email: String
)

data class CreateEventRequest(
    @SerializedName("titulo")
    val titulo: String,
    
    @SerializedName("descripcion")
    val descripcion: String,
    
    @SerializedName("fechaInicio")
    val fechaInicio: String, // ISO 8601 format
    
    @SerializedName("fechaFin")
    val fechaFin: String,
    
    @SerializedName("lugar")
    val lugar: String,
    
    @SerializedName("capacidad")
    val capacidad: Int,
    
    @SerializedName("organizadorId")
    val organizadorId: String
)
