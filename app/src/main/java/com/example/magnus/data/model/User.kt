package com.example.magnus.data.model

enum class UserRole(val value: Int) {
    CLIENTE(0),
    ORGANIZADOR(1),
    ADMINISTRADOR(2);
    
    companion object {
        fun fromInt(value: Int) = entries.firstOrNull { it.value == value } ?: CLIENTE
    }
}

data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val rol: UserRole = UserRole.CLIENTE,
    val profileImageUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val eventsCreated: List<String> = emptyList(),
    val eventsAttended: List<String> = emptyList()
)
