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
    
    @GET("api/eventos/organizador/{organizadorId}")
    suspend fun getEventosPorOrganizador(
        @Path("organizadorId") organizadorId: String
    ): Response<ApiResponse<List<EventoDto>>>
    
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
    
    // ============ INVITACIONES ENDPOINTS ============
    
    @POST("api/EventoInvitados/invitar")
    suspend fun invitarUsuario(
        @Body request: InvitarUsuarioRequest
    ): Response<ApiResponse<EventoInvitadoDto>>
    
    @POST("api/EventoInvitados/autopostularse")
    suspend fun autopostularse(
        @Body request: AutopostularseRequest
    ): Response<ApiResponse<EventoInvitadoDto>>
    
    @PUT("api/EventoInvitados/{id}/aceptar")
    suspend fun aceptarInvitacion(
        @Path("id") id: String
    ): Response<ApiResponse<Unit>>
    
    @PUT("api/EventoInvitados/{id}/rechazar")
    suspend fun rechazarInvitacion(
        @Path("id") id: String
    ): Response<ApiResponse<Unit>>
    
    @PUT("api/EventoInvitados/{id}/aprobar")
    suspend fun aprobarAutopostulacion(
        @Path("id") id: String
    ): Response<ApiResponse<Unit>>
    
    @PUT("api/EventoInvitados/{id}/rechazar-organizador")
    suspend fun rechazarAutopostulacion(
        @Path("id") id: String
    ): Response<ApiResponse<Unit>>
    
    @GET("api/EventoInvitados/evento/{eventoId}")
    suspend fun getInvitacionesPorEvento(
        @Path("eventoId") eventoId: String
    ): Response<ApiResponse<List<EventoInvitadoDto>>>
    
    @GET("api/EventoInvitados/usuario/{usuarioId}")
    suspend fun getInvitacionesPorUsuario(
        @Path("usuarioId") usuarioId: String
    ): Response<ApiResponse<List<EventoInvitadoDto>>>
    
    // ============ ORGANIZADORES ENDPOINTS ============
    
    @GET("api/Organizadores")
    suspend fun getAllOrganizadores(): Response<ApiResponse<List<OrganizadorDto>>>
    
    @GET("api/Organizadores/{id}")
    suspend fun getOrganizadorById(
        @Path("id") id: String
    ): Response<ApiResponse<OrganizadorDto>>
    
    @GET("api/Organizadores/usuario/{usuarioId}")
    suspend fun getOrganizadorByUsuarioId(
        @Path("usuarioId") usuarioId: String
    ): Response<ApiResponse<OrganizadorDto>>
    
    @POST("api/Organizadores")
    suspend fun createOrganizador(
        @Body request: CreateOrganizadorRequest
    ): Response<ApiResponse<OrganizadorDto>>
}
