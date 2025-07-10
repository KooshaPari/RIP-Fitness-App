package com.fitnessapp.core.network.monitoring

import kotlinx.coroutines.flow.Flow

/**
 * Network connectivity monitoring interface
 */
interface NetworkMonitor {
    val isOnline: Flow<Boolean>
    val connectionType: Flow<ConnectionType>
    val isMetered: Flow<Boolean>
    
    suspend fun isCurrentlyOnline(): Boolean
    suspend fun getCurrentConnectionType(): ConnectionType
    suspend fun isCurrentlyMetered(): Boolean
}

enum class ConnectionType {
    WIFI,
    CELLULAR,
    ETHERNET,
    BLUETOOTH,
    VPN,
    UNKNOWN,
    NONE
}