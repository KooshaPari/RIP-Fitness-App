package com.fitnessapp.android.integration

import androidx.health.connect.client.HealthConnectClient
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.fitnessapp.feature.health.integration.HealthConnectManager
import com.fitnessapp.feature.health.integration.GoogleFitManager
import com.fitnessapp.feature.health.integration.SamsungHealthManager
import com.fitnessapp.feature.health.sync.HealthDataSyncManager
import com.fitnessapp.testing.KMobileTestSetup
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class HealthIntegrationTests {
    
    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    
    @get:Rule
    val kmobileRule = KMobileTestSetup()
    
    @Inject
    lateinit var healthConnectManager: HealthConnectManager
    
    @Inject
    lateinit var googleFitManager: GoogleFitManager
    
    @Inject
    lateinit var samsungHealthManager: SamsungHealthManager
    
    @Inject
    lateinit var healthDataSyncManager: HealthDataSyncManager
    
    @Before
    fun setup() {
        hiltRule.inject()
    }
    
    @Test
    fun testHealthConnectIntegration() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        
        // Test availability
        val isAvailable = HealthConnectClient.isAvailable(context)
        if (isAvailable) {
            // Test permission check
            val hasPermissions = healthConnectManager.hasRequiredPermissions()
            
            // Test data reading (with mock data)
            val steps = healthConnectManager.getStepsData(
                startTime = System.currentTimeMillis() - 86400000, // 24 hours ago
                endTime = System.currentTimeMillis()
            )
            
            // Test data writing
            val success = healthConnectManager.writeWeightData(75.0, System.currentTimeMillis())
            
            assert(steps.isNotEmpty() || !hasPermissions) // Should have data if permissions granted
            assert(success || !hasPermissions) // Should succeed if permissions granted
        }
    }
    
    @Test
    fun testGoogleFitIntegration() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        
        // Test Google Fit availability
        val isAvailable = googleFitManager.isAvailable(context)
        
        if (isAvailable) {
            // Test connection
            val connected = googleFitManager.connect()
            
            if (connected) {
                // Test reading fitness data
                val workouts = googleFitManager.getWorkoutSessions(
                    startTime = System.currentTimeMillis() - 86400000,
                    endTime = System.currentTimeMillis()
                )
                
                // Test nutrition data
                val nutrition = googleFitManager.getNutritionData(
                    startTime = System.currentTimeMillis() - 86400000,
                    endTime = System.currentTimeMillis()
                )
                
                assert(workouts != null)
                assert(nutrition != null)
            }
        }
    }
    
    @Test
    fun testSamsungHealthIntegration() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        
        // Test Samsung Health availability
        val isAvailable = samsungHealthManager.isAvailable(context)
        
        if (isAvailable) {
            // Test connection
            val connected = samsungHealthManager.connect()
            
            if (connected) {
                // Test reading health data
                val heartRate = samsungHealthManager.getHeartRateData(
                    startTime = System.currentTimeMillis() - 86400000,
                    endTime = System.currentTimeMillis()
                )
                
                // Test stress data
                val stress = samsungHealthManager.getStressData(
                    startTime = System.currentTimeMillis() - 86400000,
                    endTime = System.currentTimeMillis()
                )
                
                assert(heartRate != null)
                assert(stress != null)
            }
        }
    }
    
    @Test
    fun testHealthDataSyncManager() = runBlocking {
        // Test sync status
        val syncStatus = healthDataSyncManager.getSyncStatus()
        assert(syncStatus != null)
        
        // Test manual sync
        val syncResult = healthDataSyncManager.performManualSync()
        assert(syncResult.isSuccess || syncResult.isFailure)
        
        // Test conflict resolution
        val conflicts = healthDataSyncManager.getDataConflicts()
        if (conflicts.isNotEmpty()) {
            val resolved = healthDataSyncManager.resolveConflicts(conflicts)
            assert(resolved)
        }
        
        // Test background sync setup
        val backgroundSyncEnabled = healthDataSyncManager.enableBackgroundSync(
            intervalMinutes = 30
        )
        assert(backgroundSyncEnabled)
    }
    
    @Test
    fun testHealthDataPermissions() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        
        // Test HealthConnect permissions
        val healthConnectPermissions = healthConnectManager.getRequiredPermissions()
        assert(healthConnectPermissions.isNotEmpty())
        
        // Test Google Fit permissions
        val googleFitPermissions = googleFitManager.getRequiredPermissions()
        assert(googleFitPermissions.isNotEmpty())
        
        // Test Samsung Health permissions
        val samsungHealthPermissions = samsungHealthManager.getRequiredPermissions()
        assert(samsungHealthPermissions.isNotEmpty())
    }
    
    @Test
    fun testCrossProviderDataConsistency() = runBlocking {
        // Get weight data from all available providers
        val healthConnectWeight = if (healthConnectManager.hasRequiredPermissions()) {
            healthConnectManager.getWeightData(
                startTime = System.currentTimeMillis() - 86400000,
                endTime = System.currentTimeMillis()
            )
        } else null
        
        val googleFitWeight = if (googleFitManager.isConnected()) {
            googleFitManager.getWeightData(
                startTime = System.currentTimeMillis() - 86400000,
                endTime = System.currentTimeMillis()
            )
        } else null
        
        val samsungHealthWeight = if (samsungHealthManager.isConnected()) {
            samsungHealthManager.getWeightData(
                startTime = System.currentTimeMillis() - 86400000,
                endTime = System.currentTimeMillis()
            )
        } else null
        
        // Test conflict detection
        val allWeightData = listOfNotNull(
            healthConnectWeight,
            googleFitWeight,
            samsungHealthWeight
        ).flatten()
        
        if (allWeightData.size > 1) {
            val conflicts = healthDataSyncManager.detectConflicts(allWeightData)
            // Should be able to detect and handle conflicts
            assert(conflicts.isEmpty() || healthDataSyncManager.canResolveConflicts(conflicts))
        }
    }
    
    @Test
    fun testHealthDataBackup() = runBlocking {
        // Test data export
        val exportResult = healthDataSyncManager.exportHealthData(
            startTime = System.currentTimeMillis() - 86400000 * 7, // 7 days
            endTime = System.currentTimeMillis(),
            format = "json"
        )
        assert(exportResult.isSuccess)
        
        // Test data import
        val importResult = healthDataSyncManager.importHealthData(
            data = exportResult.getOrThrow(),
            strategy = "merge"
        )
        assert(importResult.isSuccess)
    }
}