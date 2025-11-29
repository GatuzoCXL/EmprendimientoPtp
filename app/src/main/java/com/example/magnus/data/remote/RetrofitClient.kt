package com.example.magnus.data.remote

import android.content.Context
import com.example.magnus.data.DataStoreManager
import com.example.magnus.data.remote.api.MagnusApiService
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    
    @Volatile
    private var apiService: MagnusApiService? = null
    
    @Volatile
    private var dataStoreManager: DataStoreManager? = null
    
    fun initialize(context: Context) {
        if (dataStoreManager == null) {
            dataStoreManager = DataStoreManager(context.applicationContext)
        }
    }
    
    fun getApiService(context: Context): MagnusApiService {
        return apiService ?: synchronized(this) {
            apiService ?: buildApiService(context).also { apiService = it }
        }
    }
    
    private fun buildApiService(context: Context): MagnusApiService {
        initialize(context)
        
        val gson = GsonBuilder()
            .setLenient()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create()
        
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(ApiConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(ApiConfig.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(ApiConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor())
            .addInterceptor(loggingInterceptor())
            .build()
        
        return Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(MagnusApiService::class.java)
    }
    
    private fun authInterceptor() = Interceptor { chain ->
        val request = chain.request()
        val newRequest = request.newBuilder()
        
        // Get token from DataStore
        val token = runBlocking {
            dataStoreManager?.getAuthToken()?.first()
        }
        
        if (!token.isNullOrEmpty()) {
            newRequest.addHeader(ApiConfig.HEADER_AUTHORIZATION, "Bearer $token")
        }
        
        newRequest.addHeader(ApiConfig.HEADER_CONTENT_TYPE, ApiConfig.CONTENT_TYPE_JSON)
        
        chain.proceed(newRequest.build())
    }
    
    private fun loggingInterceptor() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
}
