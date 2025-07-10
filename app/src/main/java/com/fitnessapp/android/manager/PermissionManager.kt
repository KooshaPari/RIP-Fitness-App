package com.fitnessapp.android.manager

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interface for managing app permissions
 */
interface PermissionManager {
    val permissionStates: StateFlow<Map<String, Boolean>>
    
    fun checkPermission(permission: String): Boolean
    fun checkMultiplePermissions(permissions: List<String>): Map<String, Boolean>
    fun requestPermissions(permissions: List<String>, launcher: ActivityResultLauncher<Array<String>>)
    fun updatePermissionState(permission: String, granted: Boolean)
    fun hasAllRequiredPermissions(): Boolean
    fun getMissingPermissions(): List<String>
    
    companion object {
        val REQUIRED_PERMISSIONS = listOf(
            // Health permissions (Android 14+)
            "android.permission.health.READ_STEPS",
            "android.permission.health.READ_HEART_RATE",
            "android.permission.health.READ_DISTANCE",
            "android.permission.health.READ_ACTIVE_CALORIES_BURNED",
            "android.permission.health.READ_EXERCISE",
            "android.permission.health.READ_WEIGHT",
            "android.permission.health.READ_NUTRITION",
            "android.permission.health.WRITE_STEPS",
            "android.permission.health.WRITE_HEART_RATE",
            "android.permission.health.WRITE_DISTANCE",
            "android.permission.health.WRITE_ACTIVE_CALORIES_BURNED",
            "android.permission.health.WRITE_EXERCISE",
            "android.permission.health.WRITE_WEIGHT",
            "android.permission.health.WRITE_NUTRITION",
            
            // Camera for food scanning
            Manifest.permission.CAMERA,
            
            // Activity recognition
            Manifest.permission.ACTIVITY_RECOGNITION,
            
            // Location for outdoor workouts
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
        
        val OPTIONAL_PERMISSIONS = listOf(
            // Notifications (Android 13+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.POST_NOTIFICATIONS
            } else null,
            
            // Bluetooth for wearables
            Manifest.permission.BLUETOOTH_CONNECT,
        ).filterNotNull()
    }
}

/**
 * Implementation of PermissionManager
 */
@Singleton
class PermissionManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PermissionManager {
    
    private val _permissionStates = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    override val permissionStates: StateFlow<Map<String, Boolean>> = _permissionStates.asStateFlow()
    
    init {
        updateAllPermissionStates()
    }
    
    override fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
    
    override fun checkMultiplePermissions(permissions: List<String>): Map<String, Boolean> {
        return permissions.associateWith { checkPermission(it) }
    }
    
    override fun requestPermissions(
        permissions: List<String>,
        launcher: ActivityResultLauncher<Array<String>>
    ) {
        launcher.launch(permissions.toTypedArray())
    }
    
    override fun updatePermissionState(permission: String, granted: Boolean) {
        val currentStates = _permissionStates.value.toMutableMap()
        currentStates[permission] = granted
        _permissionStates.value = currentStates
    }
    
    override fun hasAllRequiredPermissions(): Boolean {
        return PermissionManager.REQUIRED_PERMISSIONS.all { checkPermission(it) }
    }
    
    override fun getMissingPermissions(): List<String> {
        return PermissionManager.REQUIRED_PERMISSIONS.filter { !checkPermission(it) }
    }
    
    private fun updateAllPermissionStates() {
        val allPermissions = PermissionManager.REQUIRED_PERMISSIONS + PermissionManager.OPTIONAL_PERMISSIONS
        val states = allPermissions.associateWith { checkPermission(it) }
        _permissionStates.value = states
    }
}