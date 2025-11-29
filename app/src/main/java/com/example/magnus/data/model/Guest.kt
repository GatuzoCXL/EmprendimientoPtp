package com.example.magnus.data.model

enum class GuestStatus {
    PENDING,
    CONFIRMED,
    DECLINED
}

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
