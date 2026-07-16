package com.example.smartdigits.data.repository.impl

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.smartdigits.data.model.DeviceConnection
import com.example.smartdigits.data.repository.DeviceConnectionHistoryRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import java.security.MessageDigest

/**
 * Implementation of DeviceConnectionHistoryRepository using DataStore Preferences.
 *
 * Storage Approach: DataStore Preferences with JSON serialization
 *
 * Why DataStore over alternatives:
 * - SharedPreferences: DataStore is the modern replacement, type-safe, async, and handles data corruption better
 * - Room DB: Overkill for cache data that needs TTL expiration; DataStore is lighter and faster
 * - DiskLruCache: Good for large files/blobs, but DataStore is better for structured key-value data
 *
 * Performance: O(1) access via HashMap lookup after deserialization
 * Memory: Lightweight, only loads active connections into memory
 * TTL: Automatic expiration on read operations
 */
class DeviceConnectionHistoryRepositoryImpl(
    private val context: Context
) : DeviceConnectionHistoryRepository {

    private val dataStore: DataStore<Preferences> = context.deviceConnectionDataStore
    private val gson = Gson()

    companion object {
        private val Context.deviceConnectionDataStore: DataStore<Preferences> by preferencesDataStore(
            name = "device_connection_history"
        )

        private val CONNECTIONS_KEY = stringPreferencesKey("connections")
        private val TTL_KEY = longPreferencesKey("ttl_millis")

        // Default TTL: 7 days
        private const val DEFAULT_TTL_MILLIS = 7L * 24 * 60 * 60 * 1000

        /**
         * Hashes a device ID using SHA-256 for privacy.
         *
         * @param deviceId The device ID to hash
         * @return SHA-256 hash as hexadecimal string
         */
        fun hashDeviceId(deviceId: String): String {
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(deviceId.toByteArray())
            return hashBytes.joinToString("") { "%02x".format(it) }
        }
    }

    override suspend fun addConnection(connection: DeviceConnection) {
        dataStore.edit { preferences ->
            val connections = getConnectionsMap(preferences).toMutableMap()
            connections[connection.id] = connection
            preferences[CONNECTIONS_KEY] = gson.toJson(connections)
        }
    }

    override fun getAllConnections(): Flow<List<DeviceConnection>> {
        return dataStore.data.map { preferences ->
            val connections = getConnectionsMap(preferences)
            val ttl = preferences[TTL_KEY] ?: DEFAULT_TTL_MILLIS

            // Filter out expired connections
            connections.values
                .filter { !it.isExpired(ttl) }
                .sortedByDescending { it.connectionTimestamp }
        }
    }

    override suspend fun getConnection(connectionId: String): DeviceConnection? {
        val preferences = dataStore.data.first()
        val connections = getConnectionsMap(preferences)
        val ttl = preferences[TTL_KEY] ?: DEFAULT_TTL_MILLIS

        return connections[connectionId]?.takeIf { !it.isExpired(ttl) }
    }

    override fun getConnectionsByDeviceHash(deviceIdHash: String): Flow<List<DeviceConnection>> {
        return getAllConnections().map { connections ->
            connections.filter { it.deviceIdHash == deviceIdHash }
        }
    }

    override suspend fun clearExpiredConnections(ttlMillis: Long) {
        dataStore.edit { preferences ->
            val connections = getConnectionsMap(preferences)
            val activeConnections = connections.values
                .filter { !it.isExpired(ttlMillis) }
                .associateBy { it.id }

            preferences[CONNECTIONS_KEY] = gson.toJson(activeConnections)
        }
    }

    override suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.remove(CONNECTIONS_KEY)
        }
    }

    override suspend fun getTtlMillis(): Long {
        val preferences = dataStore.data.first()
        return preferences[TTL_KEY] ?: DEFAULT_TTL_MILLIS
    }

    override suspend fun setTtlMillis(ttlMillis: Long) {
        dataStore.edit { preferences ->
            preferences[TTL_KEY] = ttlMillis
        }
        // Clean up expired entries after TTL change
        clearExpiredConnections(ttlMillis)
    }

    /**
     * Deserializes connections from preferences.
     *
     * @param preferences The DataStore preferences
     * @return Map of connection ID to DeviceConnection
     */
    private fun getConnectionsMap(preferences: Preferences): Map<String, DeviceConnection> {
        val json = preferences[CONNECTIONS_KEY] ?: return emptyMap()
        return try {
            val type = object : TypeToken<Map<String, DeviceConnection>>() {}.type
            gson.fromJson(json, type) ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }
}