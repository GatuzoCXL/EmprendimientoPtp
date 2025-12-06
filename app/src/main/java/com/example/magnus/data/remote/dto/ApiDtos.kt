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

// Validation Error Response (from .NET Problem Details)
data class ValidationErrorResponse(
    @SerializedName("type")
    val type: String?,
    
    @SerializedName("title")
    val title: String?,
    
    @SerializedName("status")
    val status: Int?,
    
    @SerializedName("errors")
    val errors: Map<String, List<String>>?,
    
    @SerializedName("traceId")
    val traceId: String?
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
    val password: String,
    
    @SerializedName("rol")
    val rol: Int = 0 // 0 = Cliente (default)
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
    
    @SerializedName("rol")
    val rol: Int = 0,
    
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
    
    @SerializedName("nombreEmpresa")
    val nombreEmpresa: String,
    
    @SerializedName("descripcion")
    val descripcion: String? = null,
    
    @SerializedName("telefono")
    val telefono: String,
    
    @SerializedName("direccion")
    val direccion: String? = null,
    
    @SerializedName("precioPorEvento")
    val precioPorEvento: Double,
    
    @SerializedName("añosExperiencia")
    val añosExperiencia: Int,
    
    @SerializedName("especialidad")
    val especialidad: String? = null,
    
    @SerializedName("verificado")
    val verificado: Boolean,
    
    @SerializedName("rating")
    val rating: Double,
    
    @SerializedName("cantidadReseñas")
    val cantidadReseñas: Int,
    
    @SerializedName("usuarioId")
    val usuarioId: String
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

// Event Invitation DTOs
data class EventoInvitadoDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("eventoId")
    val eventoId: String,
    
    @SerializedName("usuarioId")
    val usuarioId: String,
    
    @SerializedName("estado")
    val estado: Int,
    
    @SerializedName("esAutopostulacion")
    val esAutopostulacion: Boolean,
    
    @SerializedName("fechaInvitacion")
    val fechaInvitacion: String,
    
    @SerializedName("fechaRespuesta")
    val fechaRespuesta: String?,
    
    @SerializedName("mensaje")
    val mensaje: String?,
    
    @SerializedName("evento")
    val evento: EventoDto?,
    
    @SerializedName("usuario")
    val usuario: UserDto?
)

data class InvitarUsuarioRequest(
    @SerializedName("eventoId")
    val eventoId: String,
    
    @SerializedName("usuarioId")
    val usuarioId: String,
    
    @SerializedName("mensaje")
    val mensaje: String?
)

data class AutopostularseRequest(
    @SerializedName("eventoId")
    val eventoId: String,
    
    @SerializedName("mensaje")
    val mensaje: String?
)

data class CreateOrganizadorRequest(
    @SerializedName("nombreEmpresa")
    val nombreEmpresa: String,
    
    @SerializedName("descripcion")
    val descripcion: String?,
    
    @SerializedName("telefono")
    val telefono: String,
    
    @SerializedName("direccion")
    val direccion: String?,
    
    @SerializedName("precioPorEvento")
    val precioPorEvento: Double,
    
    @SerializedName("añosExperiencia")
    val añosExperiencia: Int,
    
    @SerializedName("especialidad")
    val especialidad: String?,
    
    @SerializedName("usuarioId")
    val usuarioId: String
)

data class OrganizadorStatsDto(
    @SerializedName("eventosOrganizados")
    val eventosOrganizados: Int,
    
    @SerializedName("ingresosTotales")
    val ingresosTotales: Double,
    
    @SerializedName("ratingPromedio")
    val ratingPromedio: Double,
    
    @SerializedName("clientesSatisfechos")
    val clientesSatisfechos: Int,
    
    @SerializedName("eventosPendientes")
    val eventosPendientes: Int,
    
    @SerializedName("eventosProximos")
    val eventosProximos: Int
)
