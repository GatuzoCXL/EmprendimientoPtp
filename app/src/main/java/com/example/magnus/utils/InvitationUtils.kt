package com.example.magnus.utils

import com.example.magnus.data.model.InvitationStatus

fun InvitationStatus.getDisplayText(): String {
    return when (this) {
        InvitationStatus.PENDIENTE_RESPUESTA -> "Pendiente de respuesta"
        InvitationStatus.PENDIENTE_APROBACION -> "Pendiente de aprobación"
        InvitationStatus.CONFIRMADO -> "Confirmado"
        InvitationStatus.RECHAZADO_POR_INVITADO -> "Rechazado"
        InvitationStatus.RECHAZADO_POR_ORGANIZADOR -> "Rechazado por organizador"
    }
}

fun InvitationStatus.getColorResource(): androidx.compose.ui.graphics.Color {
    return when (this) {
        InvitationStatus.PENDIENTE_RESPUESTA -> androidx.compose.ui.graphics.Color(0xFFFFA726) // Orange
        InvitationStatus.PENDIENTE_APROBACION -> androidx.compose.ui.graphics.Color(0xFF42A5F5) // Blue
        InvitationStatus.CONFIRMADO -> androidx.compose.ui.graphics.Color(0xFF66BB6A) // Green
        InvitationStatus.RECHAZADO_POR_INVITADO,
        InvitationStatus.RECHAZADO_POR_ORGANIZADOR -> androidx.compose.ui.graphics.Color(0xFFEF5350) // Red
    }
}

fun InvitationStatus.canAccept(): Boolean {
    return this == InvitationStatus.PENDIENTE_RESPUESTA
}

fun InvitationStatus.canReject(): Boolean {
    return this == InvitationStatus.PENDIENTE_RESPUESTA
}

fun InvitationStatus.canApprove(): Boolean {
    return this == InvitationStatus.PENDIENTE_APROBACION
}

fun InvitationStatus.canRejectByOrganizer(): Boolean {
    return this == InvitationStatus.PENDIENTE_APROBACION
}
