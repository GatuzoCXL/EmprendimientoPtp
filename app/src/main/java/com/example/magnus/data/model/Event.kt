package com.example.magnus.data.model

data class Event(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val date: Long = 0L,
    val location: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val creatorId: String = "",
    val creatorName: String = "",
    val imageUrl: String = "",
    val maxGuests: Int = 0,
    val currentGuests: Int = 0,
    val isPublic: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
