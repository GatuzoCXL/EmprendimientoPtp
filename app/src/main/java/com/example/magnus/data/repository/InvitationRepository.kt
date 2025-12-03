package com.example.magnus.data.repository

import android.content.Context
import com.example.magnus.data.DataStoreManager
import com.example.magnus.data.model.EventInvitation
import com.example.magnus.data.model.InvitationStatus
import com.example.magnus.data.remote.RetrofitClient
import com.example.magnus.data.remote.dto.AutopostularseRequest
import com.example.magnus.data.remote.dto.EventoInvitadoDto
import com.example.magnus.data.remote.dto.InvitarUsuarioRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class InvitationRepository(context: Context) {
    private val appContext = context.applicationContext
    private val apiService = RetrofitClient.getApiService(appContext)
    private val dataStore = DataStoreManager(appContext)
    
    suspend fun inviteUser(
        eventoId: String,
        usuarioId: String,
        mensaje: String?
    ): Result<EventInvitation> = withContext(Dispatchers.IO) {
        return@withContext try {
            val request = InvitarUsuarioRequest(
                eventoId = eventoId,
                usuarioId = usuarioId,
                mensaje = mensaje
            )
            
            val response = apiService.invitarUsuario(request)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val dto = response.body()!!.data!!
                val invitation = mapDtoToModel(dto)
                Result.success(invitation)
            } else {
                val errorMessage = response.body()?.message ?: "Error al invitar usuario"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
    
    suspend fun selfRegister(
        eventoId: String,
        mensaje: String?
    ): Result<EventInvitation> = withContext(Dispatchers.IO) {
        return@withContext try {
            val request = AutopostularseRequest(
                eventoId = eventoId,
                mensaje = mensaje
            )
            
            val response = apiService.autopostularse(request)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val dto = response.body()!!.data!!
                val invitation = mapDtoToModel(dto)
                Result.success(invitation)
            } else {
                val errorMessage = response.body()?.message ?: "Error al autopostularse"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
    
    suspend fun acceptInvitation(invitationId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.aceptarInvitacion(invitationId)
            
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                val errorMessage = response.body()?.message ?: "Error al aceptar invitación"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
    
    suspend fun rejectInvitation(invitationId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.rechazarInvitacion(invitationId)
            
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                val errorMessage = response.body()?.message ?: "Error al rechazar invitación"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
    
    suspend fun approveInvitation(invitationId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.aprobarAutopostulacion(invitationId)
            
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                val errorMessage = response.body()?.message ?: "Error al aprobar autopostulación"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
    
    suspend fun rejectByOrganizer(invitationId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.rechazarAutopostulacion(invitationId)
            
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                val errorMessage = response.body()?.message ?: "Error al rechazar autopostulación"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
    
    suspend fun getInvitationsByEvent(eventoId: String): Result<List<EventInvitation>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.getInvitacionesPorEvento(eventoId)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val invitationsDto = response.body()!!.data ?: emptyList()
                val invitations = invitationsDto.map { mapDtoToModel(it) }
                Result.success(invitations)
            } else {
                val errorMessage = response.body()?.message ?: "Error al cargar invitaciones"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
    
    suspend fun getMyInvitations(): Result<List<EventInvitation>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val userId = dataStore.getCurrentUserId() 
                ?: return@withContext Result.failure(Exception("Usuario no autenticado"))
            
            val response = apiService.getInvitacionesPorUsuario(userId)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val invitationsDto = response.body()!!.data ?: emptyList()
                val invitations = invitationsDto.map { mapDtoToModel(it) }
                Result.success(invitations)
            } else {
                val errorMessage = response.body()?.message ?: "Error al cargar mis invitaciones"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
    
    private fun mapDtoToModel(dto: EventoInvitadoDto): EventInvitation {
        return EventInvitation(
            id = dto.id,
            eventoId = dto.eventoId,
            usuarioId = dto.usuarioId,
            estado = InvitationStatus.fromInt(dto.estado),
            esAutopostulacion = dto.esAutopostulacion,
            fechaInvitacion = parseIsoToMillis(dto.fechaInvitacion),
            fechaRespuesta = dto.fechaRespuesta?.let { parseIsoToMillis(it) },
            mensaje = dto.mensaje,
            eventoTitulo = dto.evento?.titulo,
            eventoDescripcion = dto.evento?.descripcion,
            eventoFechaInicio = dto.evento?.fechaInicio?.let { parseIsoToMillis(it) },
            eventoLugar = dto.evento?.lugar,
            usuarioNombre = dto.usuario?.nombre,
            usuarioEmail = dto.usuario?.email
        )
    }
    
    private fun parseIsoToMillis(isoDate: String): Long {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            format.parse(isoDate)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }
}
