package com.fitnessapp.android

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.health.connect.client.HealthConnectClient
import androidx.lifecycle.lifecycleScope
import com.fitnessapp.android.manager.PermissionManager
import com.fitnessapp.android.ui.navigation.FitnessAppScaffold
import com.fitnessapp.android.ui.onboarding.OnboardingFlow
import com.fitnessapp.android.ui.theme.FitnessAppTheme
import com.fitnessapp.android.viewmodel.MainViewModel
import com.fitnessapp.feature.health.permissions.HealthPermissionsManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
// Remove Timber import - not configured yet
// import timber.log.Timber
import javax.inject.Inject

/**
 * Main activity for the FitnessApp
 * 
 * Comprehensive integration of all features:
 * - Permission management (Health Connect, Camera, Location, etc.)
 * - Health data integration and sync
 * - Deep link handling
 * - Splash screen and onboarding flow
 * - Background service coordination
 * - Notification handling
 * - Edge-to-edge display with Material 3
 * 
 * This is the central hub that coordinates all app functionality
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var permissionManager: PermissionManager
    
    @Inject
    lateinit var healthPermissionsManager: HealthPermissionsManager
    
    private val viewModel: MainViewModel by viewModels()
    
    // Permission launcher for regular Android permissions
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.forEach { (permission, granted) ->
            permissionManager.updatePermissionState(permission, granted)
            // Timber.d("Permission $permission: ${if (granted) "granted" else "denied"}")
        }
        
        if (!permissionManager.hasAllRequiredPermissions()) {
            handleMissingPermissions()
        } else {
            viewModel.onPermissionsGranted()
        }
    }
    
    // Health Connect permission launcher
    private val healthConnectPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Timber.d("Health Connect permission result: ${result.resultCode}")
        lifecycleScope.launch {
            healthPermissionsManager.checkPermissions()
            viewModel.onHealthPermissionsChecked()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen with conditional keep logic
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition {
            viewModel.isLoading.value
        }
        
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display
        enableEdgeToEdge()
        
        // Initialize app components
        initializeApp()
        
        // Handle deep links
        handleIntent(intent)
        
        setContent {
            FitnessAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FitnessApp(
                        viewModel = viewModel,
                        onRequestPermissions = ::requestPermissions,
                        onRequestHealthPermissions = ::requestHealthPermissions
                    )
                }
            }
        }
    }
    
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }
    
    private fun initializeApp() {
        lifecycleScope.launch {
            try {
                // Check Health Connect availability
                val isHealthConnectAvailable = HealthConnectClient.isProviderAvailable(this@MainActivity)
                viewModel.setHealthConnectAvailable(isHealthConnectAvailable)
                
                // Check permissions
                checkAllPermissions()
                
                // Initialize user data if needed
                viewModel.initializeUserData()
                
                // Timber.i("App initialization completed")
            } catch (e: Exception) {
                // Timber.e(e, "App initialization failed")
                viewModel.setError("Failed to initialize app: ${e.message}")
            }
        }
    }
    
    private suspend fun checkAllPermissions() {
        // Check regular permissions
        val missingPermissions = permissionManager.getMissingPermissions()
        if (missingPermissions.isNotEmpty()) {
            viewModel.setMissingPermissions(missingPermissions)
        }
        
        // Check Health Connect permissions
        if (HealthConnectClient.isProviderAvailable(this)) {
            healthPermissionsManager.checkPermissions()
        }
    }
    
    private fun requestPermissions() {
        val missingPermissions = permissionManager.getMissingPermissions()
        if (missingPermissions.isNotEmpty()) {
            permissionLauncher.launch(missingPermissions.toTypedArray())
        }
    }
    
    private fun requestHealthPermissions() {
        lifecycleScope.launch {
            try {
                val permissionIntent = healthPermissionsManager.getPermissionIntent()
                healthConnectPermissionLauncher.launch(permissionIntent)
            } catch (e: Exception) {
                // Timber.e(e, "Failed to request Health Connect permissions")
                viewModel.setError("Failed to request health permissions")
            }
        }
    }
    
    private fun handleMissingPermissions() {
        val missingPermissions = permissionManager.getMissingPermissions()
        val criticalPermissions = missingPermissions.filter { permission ->
            when (permission) {
                Manifest.permission.CAMERA,
                Manifest.permission.ACTIVITY_RECOGNITION -> true
                else -> false
            }
        }
        
        if (criticalPermissions.isNotEmpty()) {
            // Show dialog to redirect to settings
            viewModel.setError("Critical permissions missing. Please enable them in settings.")
        }
    }
    
    private fun handleIntent(intent: Intent) {
        when (intent.action) {
            Intent.ACTION_VIEW -> {
                // Handle deep links
                val uri = intent.data
                uri?.let { handleDeepLink(it) }
            }
            "android.intent.action.VIEW_PERMISSION_USAGE" -> {
                // Handle Health Connect permission usage view
                viewModel.navigateToHealthPermissions()
            }
        }
    }
    
    private fun handleDeepLink(uri: Uri) {
        // Timber.d("Handling deep link: $uri")
        
        when (uri.pathSegments.firstOrNull()) {
            "workout" -> {
                viewModel.navigateToWorkout(uri.pathSegments.getOrNull(1))
            }
            "nutrition" -> {
                viewModel.navigateToNutrition()
            }
            "health" -> {
                viewModel.navigateToHealth()
            }
            "profile" -> {
                viewModel.navigateToProfile()
            }
        }
    }
    
    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }
}

/**
 * Root composable for the FitnessApp
 * 
 * Manages the complete app state including:
 * - Loading and initialization
 * - Permission requests and management
 * - Onboarding flow for new users
 * - Main app navigation and content
 * - Error handling and user feedback
 */
@Composable
fun FitnessApp(
    viewModel: MainViewModel,
    onRequestPermissions: () -> Unit,
    onRequestHealthPermissions: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(uiState.permissionRequests) {
        if (uiState.permissionRequests.isNotEmpty()) {
            onRequestPermissions()
        }
    }
    
    LaunchedEffect(uiState.healthPermissionRequest) {
        if (uiState.healthPermissionRequest) {
            onRequestHealthPermissions()
        }
    }
    
    when {
        uiState.isLoading -> {
            // Splash screen handles loading state
        }
        
        uiState.showOnboarding -> {
            OnboardingFlow(
                onComplete = {
                    viewModel.completeOnboarding()
                },
                onPermissionsRequested = onRequestPermissions,
                onHealthPermissionsRequested = onRequestHealthPermissions
            )
        }
        
        uiState.hasError -> {
            // Show error screen or dialog
            // You can implement an error composable here
        }
        
        else -> {
            FitnessAppScaffold(
                startDestination = uiState.startDestination,
                deepLinkDestination = uiState.deepLinkDestination
            )
        }
    }
}"