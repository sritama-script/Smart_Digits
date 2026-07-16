package com.example.smartdigits.util

import android.content.Context
import android.content.Intent
import com.example.smartdigits.SmartDigitsApplication
import com.example.smartdigits.data.model.DeviceConnection
import com.example.smartdigits.data.tracking.DeviceConnectionTracker
import com.example.smartdigits.data.tracking.WebBrowserTracker

/**
 * Helper class for easy integration of tracking from Java code.
 * 
 * Provides static-like methods that can be called from Java Activities.
 */
object TrackingHelper {
    
    /**
     * Gets the device connection tracker.
     * 
     * @param context Any context
     * @return DeviceConnectionTracker instance
     */
    @JvmStatic
    fun getDeviceTracker(context: Context): DeviceConnectionTracker {
        return SmartDigitsApplication.get(context).deviceConnectionTracker
    }
    
    /**
     * Gets the web browser tracker.
     * 
     * @param context Any context
     * @return WebBrowserTracker instance
     */
    @JvmStatic
    fun getBrowserTracker(context: Context): WebBrowserTracker {
        return SmartDigitsApplication.get(context).webBrowserTracker
    }
    
    /**
     * Records a WiFi connection.
     * 
     * @param context Any context
     * @param ssid WiFi network name (SSID)
     * @param bssid WiFi network MAC address (BSSID)
     */
    @JvmStatic
    fun recordWiFiConnection(context: Context, ssid: String, bssid: String) {
        getDeviceTracker(context).recordWiFiConnection(ssid, bssid)
    }
    
    /**
     * Records a Bluetooth connection.
     * 
     * @param context Any context
     * @param deviceName Device name
     * @param macAddress MAC address
     */
    @JvmStatic
    fun recordBluetoothConnection(context: Context, deviceName: String, macAddress: String) {
        getDeviceTracker(context).recordBluetoothConnection(deviceName, macAddress)
    }
    
    /**
     * Records a USB connection.
     * 
     * @param context Any context
     * @param deviceName Device name
     * @param serialNumber Serial number
     */
    @JvmStatic
    fun recordUsbConnection(context: Context, deviceName: String, serialNumber: String) {
        getDeviceTracker(context).recordUsbConnection(deviceName, serialNumber)
    }
    
    /**
     * Records a generic device connection.
     * 
     * @param context Any context
     * @param deviceName Device name
     * @param deviceId Device identifier
     * @param connectionType Connection type (BLUETOOTH, USB, WIFI, ETHERNET, UNKNOWN)
     */
    @JvmStatic
    fun recordConnection(
        context: Context,
        deviceName: String,
        deviceId: String,
        connectionType: DeviceConnection.ConnectionType
    ) {
        getDeviceTracker(context).recordConnection(deviceName, deviceId, connectionType)
    }
    
    /**
     * Tracks a browser intent before launching it.
     * 
     * Call this before startActivity() for browser intents.
     * 
     * @param context Any context
     * @param intent The browser intent to track
     */
    @JvmStatic
    fun trackBrowserIntent(context: Context, intent: Intent) {
        getBrowserTracker(context).trackBrowserIntent(intent)
    }
}
