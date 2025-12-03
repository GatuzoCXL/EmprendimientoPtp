package com.example.magnus.data.repository

import android.content.Context
import com.example.magnus.data.model.CreateOrganizadorData
import com.example.magnus.data.model.Organizador
import com.example.magnus.data.remote.RetrofitClient
import com.example.magnus.data.remote.dto.CreateOrganizadorRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrganizadorRepository(context: Context) {
    private val appContext = context.applicationContext
    private val apiService = RetrofitClient.getApiService(appContext)

    suspend fun getAllOrganizadores(): Result<List<Organizador>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getAllOrganizadores()
            if (response.isSuccessful) {
                val organizadores = response.body()?.data?.map { dto ->
                    Organizador(
                        id = dto.id,
                        nombreEmpresa = dto.nombreEmpresa,
                        descripcion = dto.descripcion,
                        telefono = dto.telefono,
                        direccion = dto.direccion,
                        precioPorEvento = dto.precioPorEvento,
                        añosExperiencia = dto.añosExperiencia,
                        especialidad = dto.especialidad,
                        verificado = dto.verificado,
                        rating = dto.rating,
                        cantidadReseñas = dto.cantidadReseñas,
                        usuarioId = dto.usuarioId
                    )
                } ?: emptyList()
                Result.success(organizadores)
            } else {
                Result.failure(Exception(response.message() ?: "Error al obtener organizadores"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOrganizadorById(id: String): Result<Organizador> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getOrganizadorById(id)
            if (response.isSuccessful) {
                val dto = response.body()?.data
                    ?: return@withContext Result.failure(Exception("Organizador no encontrado"))
                val organizador = Organizador(
                    id = dto.id,
                    nombreEmpresa = dto.nombreEmpresa,
                    descripcion = dto.descripcion,
                    telefono = dto.telefono,
                    direccion = dto.direccion,
                    precioPorEvento = dto.precioPorEvento,
                    añosExperiencia = dto.añosExperiencia,
                    especialidad = dto.especialidad,
                    verificado = dto.verificado,
                    rating = dto.rating,
                    cantidadReseñas = dto.cantidadReseñas,
                    usuarioId = dto.usuarioId
                )
                Result.success(organizador)
            } else {
                Result.failure(Exception(response.message() ?: "Error al obtener organizador"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createOrganizador(data: CreateOrganizadorData): Result<Organizador> = withContext(Dispatchers.IO) {
        try {
            val request = CreateOrganizadorRequest(
                nombreEmpresa = data.nombreEmpresa,
                descripcion = data.descripcion,
                telefono = data.telefono,
                direccion = data.direccion,
                precioPorEvento = data.precioPorEvento,
                añosExperiencia = data.añosExperiencia,
                especialidad = data.especialidad,
                usuarioId = data.usuarioId
            )
            val response = apiService.createOrganizador(request)
            if (response.isSuccessful) {
                val dto = response.body()?.data
                    ?: return@withContext Result.failure(Exception("Error al crear organizador"))
                val organizador = Organizador(
                    id = dto.id,
                    nombreEmpresa = dto.nombreEmpresa,
                    descripcion = dto.descripcion,
                    telefono = dto.telefono,
                    direccion = dto.direccion,
                    precioPorEvento = dto.precioPorEvento,
                    añosExperiencia = dto.añosExperiencia,
                    especialidad = dto.especialidad,
                    verificado = dto.verificado,
                    rating = dto.rating,
                    cantidadReseñas = dto.cantidadReseñas,
                    usuarioId = dto.usuarioId
                )
                Result.success(organizador)
            } else {
                Result.failure(Exception(response.message() ?: "Error al crear organizador"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
