package com.example.magnus.data.model

data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val profileImageUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val eventsCreated: List<String> = emptyList(),
    val eventsAttended: List<String> = emptyList()
)
