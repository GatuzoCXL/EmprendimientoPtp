package com.example.magnus.utils

import com.example.magnus.data.model.Event
import com.example.magnus.ui.components.EventStatus

/**
 * Calcula el estado actual de un evento basándose en sus fechas de inicio y fin
 * 
 * Lógica:
 * - UPCOMING: El evento aún no ha comenzado (fecha de inicio > ahora)
 * - IN_PROGRESS: El evento está en curso (ahora está entre fecha inicio y fin)
 * - COMPLETED: El evento ya terminó (fecha de fin < ahora)
 */
fun Event.getStatus(): EventStatus {
    val now = System.currentTimeMillis()
    
    return when {
        endDate < now -> EventStatus.COMPLETED           // Ya terminó
        date <= now && endDate >= now -> EventStatus.IN_PROGRESS  // En curso
        date > now -> EventStatus.UPCOMING               // Aún no empieza
        else -> EventStatus.UPCOMING                      // Fallback por seguridad
    }
}

/**
 * Determina si un evento puede ser eliminado
 * Solo se pueden eliminar eventos que aún no han comenzado
 */
fun Event.canBeDeleted(): Boolean {
    return getStatus() == EventStatus.UPCOMING
}

/**
 * Determina si un evento puede ser editado
 * Solo se pueden editar eventos que aún no han comenzado
 */
fun Event.canBeEdited(): Boolean {
    return getStatus() == EventStatus.UPCOMING
}

/**
 * Obtiene un mensaje descriptivo del estado del evento
 */
fun EventStatus.getDescription(): String {
    return when (this) {
        EventStatus.UPCOMING -> "Este evento aún no ha comenzado"
        EventStatus.IN_PROGRESS -> "Este evento está en curso"
        EventStatus.COMPLETED -> "Este evento ya finalizó"
        EventStatus.CANCELLED -> "Este evento fue cancelado"
    }
}
