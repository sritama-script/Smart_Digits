package com.example.smartdigits.data.repository

import com.example.smartdigits.data.model.DeviceConnection
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for device connection history operations.
 * 
 * Provides a clean abstraction for storing and retrieving device connection
 * history with TTL-based expiration.
 */
interface DeviceConnectionHistoryRepository {
    
    /**
     * Adds a new device connection to the history.
     * 
     * @param connection The device connection to add
     */
    suspend fun addConnection(connection: DeviceConnection)
    
    /**
     * Retrieves all non-expired device connections.
     * 
     * @return Flow of list of active device connections
     */
    fun getAllConnections(): Flow<List<DeviceConnection>>
    
    /**
     * Retrieves a specific device connection by ID.
     * 
     * @param connectionId The ID of the connection to retrieve
     * @return The device connection, or null if not found or expired
     */
    suspend fun getConnection(connectionId: String): DeviceConnection?
    
    /**
     * Retrieves connections by device ID hash (for privacy).
     * 
     * @param deviceIdHash The hashed device ID
     * @return Flow of matching device connections
     */
    fun getConnectionsByDeviceHash(deviceIdHash: String): Flow<List<DeviceConnection>>
    
    /**
     * Clears all expired connections based on TTL.
     * 
     * @param ttlMillis Time-to-live in milliseconds
     */
    suspend fun clearExpiredConnections(ttlMillis: Long)
    
    /**
     * Clears all connection history.
     */
    suspend fun clearAll()
    
    /**
     * Gets the configured TTL in milliseconds.
     * 
     * @return TTL in milliseconds (default: 7 days)
     */
    suspend fun getTtlMillis(): Long
    
    /**
     * Sets the TTL for connection history expiration.
     * 
     * @param ttlMillis Time-to-live in milliseconds
     */
    suspend fun setTtlMillis(ttlMillis: Long)
}
