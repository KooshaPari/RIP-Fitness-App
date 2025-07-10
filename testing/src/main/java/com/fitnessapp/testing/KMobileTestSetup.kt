package com.fitnessapp.testing

import io.kmobile.sdk.KMobile
import io.kmobile.sdk.config.KMobileConfig
import io.kmobile.sdk.device.DeviceManager
import io.kmobile.sdk.performance.PerformanceMonitor
import io.kmobile.sdk.security.SecurityTester
import kotlinx.coroutines.runBlocking
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * KMobile test setup and configuration
 */
class KMobileTestSetup : TestRule {
    
    private lateinit var kmobile: KMobile
    private lateinit var deviceManager: DeviceManager
    private lateinit var performanceMonitor: PerformanceMonitor
    private lateinit var securityTester: SecurityTester
    
    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                setupKMobile()
                try {
                    base.evaluate()
                } finally {
                    teardownKMobile()
                }
            }
        }
    }
    
    private fun setupKMobile() = runBlocking {
        // Initialize KMobile SDK
        val config = KMobileConfig.Builder()
            .setProjectName("fitness-app")
            .setEnvironment("testing")
            .setLogLevel(KMobileConfig.LogLevel.DEBUG)
            .enablePerformanceMonitoring(true)
            .enableSecurityTesting(true)
            .enableScreenshots(true)
            .enableVideoRecording(true)
            .build()
            
        kmobile = KMobile.initialize(config)
        deviceManager = kmobile.deviceManager
        performanceMonitor = kmobile.performanceMonitor
        securityTester = kmobile.securityTester
        
        // Setup test devices
        setupTestDevices()
        
        // Initialize performance monitoring
        performanceMonitor.startMonitoring()
        
        // Setup security testing
        securityTester.initialize()
    }
    
    private fun setupTestDevices() = runBlocking {
        // Physical devices
        deviceManager.registerDevice(
            id = "pixel-7-pro",
            name = "Pixel 7 Pro",
            model = "Pixel 7 Pro",
            osVersion = "14",
            capabilities = listOf("health-connect", "biometric", "camera")
        )
        
        deviceManager.registerDevice(
            id = "galaxy-s23-ultra",
            name = "Galaxy S23 Ultra", 
            model = "Galaxy S23 Ultra",
            osVersion = "14",
            capabilities = listOf("samsung-health", "spen", "camera")
        )
        
        // Emulators
        deviceManager.registerEmulator(
            id = "api-34-emulator",
            api = 34,
            abi = "x86_64",
            target = "google_apis",
            avdName = "Pixel_8_API_34"
        )
        
        // Wait for devices to be ready
        deviceManager.waitForDevicesReady(timeoutMs = 30000)
    }
    
    private fun teardownKMobile() = runBlocking {
        // Stop performance monitoring
        performanceMonitor.stopMonitoring()
        
        // Generate reports
        performanceMonitor.generateReport("./testing/reports/performance")
        securityTester.generateReport("./testing/reports/security")
        
        // Cleanup devices
        deviceManager.cleanup()
        
        // Shutdown KMobile
        kmobile.shutdown()
    }
    
    fun getDeviceManager(): DeviceManager = deviceManager
    fun getPerformanceMonitor(): PerformanceMonitor = performanceMonitor
    fun getSecurityTester(): SecurityTester = securityTester
}