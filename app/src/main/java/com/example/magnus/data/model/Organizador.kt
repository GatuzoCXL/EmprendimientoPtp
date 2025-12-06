package com.example.magnus.data.model

data class Organizador(
    val id: String,
    val nombreEmpresa: String,
    val descripcion: String? = null,
    val telefono: String,
    val direccion: String? = null,
    val precioPorEvento: Double,
    val añosExperiencia: Int,
    val especialidad: String? = null,
    val verificado: Boolean,
    val rating: Double,
    val cantidadReseñas: Int,
    val usuarioId: String
)

data class CreateOrganizadorData(
    val nombreEmpresa: String,
    val descripcion: String? = null,
    val telefono: String,
    val direccion: String? = null,
    val precioPorEvento: Double,
    val añosExperiencia: Int,
    val especialidad: String? = null,
    val usuarioId: String
)

data class OrganizadorStats(
    val eventosOrganizados: Int,
    val ingresosTotales: Double,
    val ratingPromedio: Double,
    val clientesSatisfechos: Int,
    val eventosPendientes: Int,
    val eventosProximos: Int
)
