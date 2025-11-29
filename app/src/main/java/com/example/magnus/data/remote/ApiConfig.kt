package com.example.magnus.data.remote

object ApiConfig {
    // URL de tu API en Render
    const val BASE_URL = "https://magnus-api-jc40.onrender.com/"
    
    // Headers
    const val HEADER_AUTHORIZATION = "Authorization"
    const val HEADER_CONTENT_TYPE = "Content-Type"
    const val CONTENT_TYPE_JSON = "application/json"
    
    // Timeout settings
    const val CONNECT_TIMEOUT = 30L // seconds
    const val READ_TIMEOUT = 30L // seconds
    const val WRITE_TIMEOUT = 30L // seconds
}
