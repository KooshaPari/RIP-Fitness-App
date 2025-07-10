package com.fitnessapp.core.network.cache

import android.content.Context
import androidx.room.*
import com.fitnessapp.core.network.monitoring.NetworkMonitor
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Network response cache manager for offline support
 */
@Singleton
class NetworkCacheManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val networkMonitor: NetworkMonitor
) {
    
    private val database by lazy {
        Room.databaseBuilder(
            context,
            NetworkCacheDatabase::class.java,
            "network_cache_database"
        ).build()
    }

    private val cacheDao by lazy { database.cacheDao() }
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Cache API response
     */
    suspend fun cacheResponse(
        url: String,
        response: String,
        ttlMinutes: Int = 60
    ): Boolean {
        return try {
            val expirationTime = System.currentTimeMillis() + (ttlMinutes * 60 * 1000)
            val cacheEntry = CacheEntry(
                url = url,
                response = response,
                timestamp = System.currentTimeMillis(),
                expirationTime = expirationTime
            )
            cacheDao.insertCacheEntry(cacheEntry)
            Timber.d("Cached response for: $url")
            true
        } catch (e: Exception) {
            Timber.e(e, "Failed to cache response for: $url")
            false
        }
    }

    /**
     * Get cached response if available and valid
     */
    suspend fun getCachedResponse(url: String): String? {
        return try {
            val cacheEntry = cacheDao.getCacheEntry(url)
            if (cacheEntry != null && cacheEntry.expirationTime > System.currentTimeMillis()) {
                Timber.d("Cache hit for: $url")
                cacheEntry.response
            } else {
                if (cacheEntry != null) {
                    // Remove expired entry
                    cacheDao.deleteCacheEntry(url)
                    Timber.d("Removed expired cache for: $url")
                }
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to get cached response for: $url")
            null
        }
    }

    /**
     * Check if we should use cache based on network status
     */
    suspend fun shouldUseCache(): Boolean {
        return try {
            !networkMonitor.isCurrentlyOnline()
        } catch (e: Exception) {
            Timber.e(e, "Error checking network status")
            true // Default to using cache if we can't determine network status
        }
    }

    /**
     * Clear expired cache entries
     */
    suspend fun clearExpiredEntries(): Int {
        return try {
            val deletedCount = cacheDao.deleteExpiredEntries(System.currentTimeMillis())
            Timber.d("Cleared $deletedCount expired cache entries")
            deletedCount
        } catch (e: Exception) {
            Timber.e(e, "Failed to clear expired cache entries")
            0
        }
    }

    /**
     * Clear all cache entries
     */
    suspend fun clearAllCache(): Boolean {
        return try {
            cacheDao.deleteAllEntries()
            Timber.d("Cleared all cache entries")
            true
        } catch (e: Exception) {
            Timber.e(e, "Failed to clear all cache entries")
            false
        }
    }

    /**
     * Get cache size information
     */
    suspend fun getCacheInfo(): CacheInfo {
        return try {
            val totalEntries = cacheDao.getCacheEntryCount()
            val totalSize = cacheDao.getTotalCacheSize()
            val expiredCount = cacheDao.getExpiredEntryCount(System.currentTimeMillis())
            
            CacheInfo(
                totalEntries = totalEntries,
                totalSizeBytes = totalSize,
                expiredEntries = expiredCount
            )
        } catch (e: Exception) {
            Timber.e(e, "Failed to get cache info")
            CacheInfo(0, 0, 0)
        }
    }
}

@Serializable
data class CacheInfo(
    val totalEntries: Int,
    val totalSizeBytes: Long,
    val expiredEntries: Int
)

@Entity(tableName = "network_cache")
data class CacheEntry(
    @PrimaryKey val url: String,
    val response: String,
    val timestamp: Long,
    val expirationTime: Long
)

@Dao
interface NetworkCacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCacheEntry(entry: CacheEntry)

    @Query("SELECT * FROM network_cache WHERE url = :url")
    suspend fun getCacheEntry(url: String): CacheEntry?

    @Query("DELETE FROM network_cache WHERE url = :url")
    suspend fun deleteCacheEntry(url: String)

    @Query("DELETE FROM network_cache WHERE expirationTime < :currentTime")
    suspend fun deleteExpiredEntries(currentTime: Long): Int

    @Query("DELETE FROM network_cache")
    suspend fun deleteAllEntries()

    @Query("SELECT COUNT(*) FROM network_cache")
    suspend fun getCacheEntryCount(): Int

    @Query("SELECT SUM(LENGTH(response)) FROM network_cache")
    suspend fun getTotalCacheSize(): Long

    @Query("SELECT COUNT(*) FROM network_cache WHERE expirationTime < :currentTime")
    suspend fun getExpiredEntryCount(currentTime: Long): Int
}

@Database(
    entities = [CacheEntry::class],
    version = 1,
    exportSchema = false
)
abstract class NetworkCacheDatabase : RoomDatabase() {
    abstract fun cacheDao(): NetworkCacheDao
}