package com.example.magnus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.magnus.data.remote.RetrofitClient
import com.example.magnus.navigation.MagnusNavigation
import com.example.magnus.ui.theme.MagnusTheme
import com.example.magnus.viewmodel.AuthViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inicializar RetrofitClient con applicationContext
        RetrofitClient.initialize(applicationContext)
        
        // Configurar ventana para edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            val authViewModel: AuthViewModel = viewModel()
            val eventViewModel: com.example.magnus.viewmodel.EventViewModel = viewModel()
            
            MagnusTheme {
                // Configurar colores del system UI (status bar y navigation bar)
                val systemUiController = rememberSystemUiController()
                val statusBarColor = MaterialTheme.colorScheme.background
                val navigationBarColor = MaterialTheme.colorScheme.background
                
                SideEffect {
                    systemUiController.setStatusBarColor(
                        color = androidx.compose.ui.graphics.Color(statusBarColor.value.toLong()),
                        darkIcons = true // Iconos oscuros en light theme
                    )
                    systemUiController.setNavigationBarColor(
                        color = androidx.compose.ui.graphics.Color(navigationBarColor.value.toLong()),
                        darkIcons = true
                    )
                }
                
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MagnusNavigation(
                        authViewModel = authViewModel,
                        eventViewModel = eventViewModel
                    )
                }
            }
        }
    }
}