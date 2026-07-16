package com.example.smartdigits.data.model

import java.util.UUID

/**
 * Data model representing a device connection history entry.
 * 
 * This model stores information about previously connected devices
 * for quick access and history tracking.
 * 
 * @param id Unique identifier for this connection entry
 * @param deviceName Human-readable device name
 * @param deviceId Device identifier (MAC address, serial number, etc.)
 * @param deviceIdHash Hashed version of deviceId for privacy (SHA-256 hash)
 * @param connectionTimestamp Unix timestamp in milliseconds when connection occurred
 * @param connectionType Type of connection (BLUETOOTH, USB, WIFI, etc.)
 */
data class DeviceConnection(
    val id: String = UUID.randomUUID().toString(),
    val deviceName: String,
    val deviceId: String,
    val deviceIdHash: String, // SHA-256 hash for privacy
    val connectionTimestamp: Long = System.currentTimeMillis(),
    val connectionType: ConnectionType
) {
    /**
     * Enumeration of supported connection types.
     */
    enum class ConnectionType {
        BLUETOOTH,
        USB,
        WIFI,
        ETHERNET,
        UNKNOWN
    }
    
    /**
     * Checks if this connection entry has expired based on TTL.
     * 
     * @param ttlMillis Time-to-live in milliseconds
     * @return true if the entry has expired, false otherwise
     */
    fun isExpired(ttlMillis: Long): Boolean {
        val age = System.currentTimeMillis() - connectionTimestamp
        return age > ttlMillis
    }
}
