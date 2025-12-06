package com.example.magnus.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.magnus.ui.screens.auth.LoginScreen
import com.example.magnus.ui.screens.auth.RegisterScreen
import com.example.magnus.ui.screens.dashboard.DashboardScreen
import com.example.magnus.ui.screens.events.CreateEventScreen
import com.example.magnus.ui.screens.events.EventDetailScreen
import com.example.magnus.ui.screens.events.MyEventsScreen
import com.example.magnus.ui.screens.guests.GuestManagementScreen
import com.example.magnus.ui.screens.profile.ProfileScreen
import com.example.magnus.ui.screens.settings.SettingsScreen
import com.example.magnus.ui.screens.organizadores.OrganizadoresScreen
import com.example.magnus.ui.screens.organizadores.OrganizadorDetailScreen
import com.example.magnus.ui.screens.organizadores.BecomeOrganizerScreen
import com.example.magnus.viewmodel.AuthViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object CreateEvent : Screen("create_event")
    object MyEvents : Screen("my_events")
    object EventDetail : Screen("event_detail/{eventId}") {
        fun createRoute(eventId: String) = "event_detail/$eventId"
    }
    object GuestManagement : Screen("guest_management/{eventId}") {
        fun createRoute(eventId: String) = "guest_management/$eventId"
    }
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object EventMap : Screen("event_map/{eventId}") {
        fun createRoute(eventId: String) = "event_map/$eventId"
    }
    object Organizadores : Screen("organizadores")
    object OrganizadorDetail : Screen("organizador_detail/{organizadorId}") {
        fun createRoute(organizadorId: String) = "organizador_detail/$organizadorId"
    }
    object BecomeOrganizer : Screen("become_organizer")
}

@Composable
fun MagnusNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel(),
    eventViewModel: com.example.magnus.viewmodel.EventViewModel = viewModel()
) {
    val authState by authViewModel.uiState.collectAsState()
    
    val startDestination = if (authState.isSignedIn) {
        Screen.Dashboard.route
    } else {
        Screen.Login.route
    }
    
    androidx.compose.runtime.LaunchedEffect(authState.isSignedIn) {
        android.util.Log.d("MagnusNav", "Auth state changed: isSignedIn=${authState.isSignedIn}, currentRoute=${navController.currentDestination?.route}")
        if (!authState.isSignedIn && navController.currentDestination?.route != Screen.Login.route) {
            android.util.Log.d("MagnusNav", "Navigating to Login")
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideIntoContainer(
                androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            slideOutOfContainer(
                androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            slideIntoContainer(
                androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutOfContainer(
                androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        }
    ) {
        // Auth screens
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                authViewModel = authViewModel
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                authViewModel = authViewModel
            )
        }
        
        // Main app screens
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToCreateEvent = {
                    navController.navigate(Screen.CreateEvent.route)
                },
                onNavigateToMyEvents = {
                    navController.navigate(Screen.MyEvents.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToOrganizadores = {
                    navController.navigate(Screen.Organizadores.route)
                },
                onSignOut = {
                    authViewModel.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                authViewModel = authViewModel,
                eventViewModel = eventViewModel
            )
        }
        
        // Event screens
        composable(Screen.CreateEvent.route) {
            CreateEventScreen(
                onNavigateBack = { navController.popBackStack() },
                authViewModel = authViewModel,
                eventViewModel = eventViewModel
            )
        }
        
        composable(Screen.MyEvents.route) {
            MyEventsScreen(
                onNavigateToEventDetail = { eventId ->
                    navController.navigate(Screen.EventDetail.createRoute(eventId))
                },
                onNavigateToCreateEvent = {
                    navController.navigate(Screen.CreateEvent.route)
                },
                onNavigateBack = { navController.popBackStack() },
                authViewModel = authViewModel,
                eventViewModel = eventViewModel
            )
        }
        
        // Event Detail Screen
        composable(Screen.EventDetail.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventDetailScreen(
                eventId = eventId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToGuestManagement = { eventId ->
                    navController.navigate(Screen.GuestManagement.createRoute(eventId))
                }
            )
        }
        
        // Guest Management Screen
        composable(Screen.GuestManagement.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            GuestManagementScreen(
                eventId = eventId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Profile Screen
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                authViewModel = authViewModel
            )
        }
        
        // Settings Screen
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onSignOut = {
                    authViewModel.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                authViewModel = authViewModel
            )
        }
        
        // Organizadores Screens
        composable(Screen.Organizadores.route) {
            OrganizadoresScreen(
                navController = navController
            )
        }
        
        composable(Screen.OrganizadorDetail.route) { backStackEntry ->
            val organizadorId = backStackEntry.arguments?.getString("organizadorId") ?: ""
            OrganizadorDetailScreen(
                navController = navController,
                organizadorId = organizadorId
            )
        }
        
        composable(Screen.BecomeOrganizer.route) {
            BecomeOrganizerScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
    }
}
