package com.example.magnus.data.model

enum class InvitationStatus(val value: Int) {
    PENDIENTE_RESPUESTA(0),
    PENDIENTE_APROBACION(1),
    CONFIRMADO(2),
    RECHAZADO_POR_INVITADO(3),
    RECHAZADO_POR_ORGANIZADOR(4);
    
    companion object {
        fun fromInt(value: Int) = entries.firstOrNull { it.value == value } ?: PENDIENTE_RESPUESTA
    }
}

data class EventInvitation(
    val id: String = "",
    val eventoId: String = "",
    val usuarioId: String = "",
    val estado: InvitationStatus = InvitationStatus.PENDIENTE_RESPUESTA,
    val esAutopostulacion: Boolean = false,
    val fechaInvitacion: Long = System.currentTimeMillis(),
    val fechaRespuesta: Long? = null,
    val mensaje: String? = null,
    // Datos del evento (cuando se incluye)
    val eventoTitulo: String? = null,
    val eventoDescripcion: String? = null,
    val eventoFechaInicio: Long? = null,
    val eventoLugar: String? = null,
    // Datos del usuario (cuando se incluye)
    val usuarioNombre: String? = null,
    val usuarioEmail: String? = null
)

// Mantener Guest para compatibilidad con código existente
@Deprecated("Use EventInvitation instead", ReplaceWith("EventInvitation"))
enum class GuestStatus {
    PENDING,
    CONFIRMED,
    DECLINED
}

@Deprecated("Use EventInvitation instead", ReplaceWith("EventInvitation"))
data class Guest(
    val id: String = "",
    val eventId: String = "",
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val status: GuestStatus = GuestStatus.PENDING,
    val invitedAt: Long = System.currentTimeMillis(),
    val respondedAt: Long? = null,
    val notes: String = ""
)
