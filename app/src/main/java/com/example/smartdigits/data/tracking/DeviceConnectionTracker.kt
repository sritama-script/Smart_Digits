package com.example.smartdigits.data.tracking

import android.content.Context
import com.example.smartdigits.data.model.DeviceConnection
import com.example.smartdigits.data.repository.DeviceConnectionHistoryRepository
import com.example.smartdigits.data.repository.impl.DeviceConnectionHistoryRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Manager class for tracking device connections.
 * 
 * Provides a simple API for recording device connections with automatic
 * hashing and connection type detection.
 * 
 * Thread-safe and lifecycle-aware.
 */
class DeviceConnectionTracker(
    private val repository: DeviceConnectionHistoryRepository,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) {
    
    /**
     * Records a device connection.
     * 
     * @param deviceName Human-readable device name
     * @param deviceId Device identifier (MAC, serial number, etc.)
     * @param connectionType Type of connection
     */
    fun recordConnection(
        deviceName: String,
        deviceId: String,
        connectionType: DeviceConnection.ConnectionType
    ) {
        val deviceIdHash = DeviceConnectionHistoryRepositoryImpl.hashDeviceId(deviceId)
        
        val connection = DeviceConnection(
            deviceName = deviceName,
            deviceId = deviceId,
            deviceIdHash = deviceIdHash,
            connectionType = connectionType
        )
        
        coroutineScope.launch {
            repository.addConnection(connection)
            // Clean up expired entries periodically
            val ttl = repository.getTtlMillis()
            repository.clearExpiredConnections(ttl)
        }
    }
    
    /**
     * Records a WiFi connection (convenience method).
     */
    fun recordWiFiConnection(ssid: String, bssid: String) {
        recordConnection(
            deviceName = ssid,
            deviceId = bssid,
            connectionType = DeviceConnection.ConnectionType.WIFI
        )
    }
    
    /**
     * Records a Bluetooth connection (convenience method).
     */
    fun recordBluetoothConnection(deviceName: String, macAddress: String) {
        recordConnection(
            deviceName = deviceName,
            deviceId = macAddress,
            connectionType = DeviceConnection.ConnectionType.BLUETOOTH
        )
    }
    
    /**
     * Records a USB connection (convenience method).
     */
    fun recordUsbConnection(deviceName: String, serialNumber: String) {
        recordConnection(
            deviceName = deviceName,
            deviceId = serialNumber,
            connectionType = DeviceConnection.ConnectionType.USB
        )
    }
    
    companion object {
        /**
         * Creates a DeviceConnectionTracker instance.
         * 
         * @param context Application context
         * @return DeviceConnectionTracker instance
         */
        fun create(context: Context): DeviceConnectionTracker {
            val repository = DeviceConnectionHistoryRepositoryImpl(context.applicationContext)
            return DeviceConnectionTracker(repository)
        }
    }
}
