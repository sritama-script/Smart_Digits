package com.example.smartdigits.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartdigits.data.model.DeviceConnection
import com.example.smartdigits.data.repository.DeviceConnectionHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for device connection history.
 * 
 * Provides UI-friendly access to device connection history with
 * automatic TTL-based expiration.
 */
class DeviceConnectionHistoryViewModel(
    private val repository: DeviceConnectionHistoryRepository
) : ViewModel() {
    
    /**
     * Flow of all active device connections.
     * Automatically filters expired entries.
     */
    val connections: Flow<List<DeviceConnection>> = repository.getAllConnections()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    /**
     * Gets connections by device hash.
     * 
     * @param deviceIdHash The hashed device ID
     * @return Flow of matching connections
     */
    fun getConnectionsByDeviceHash(deviceIdHash: String): Flow<List<DeviceConnection>> {
        return repository.getConnectionsByDeviceHash(deviceIdHash)
    }
    
    /**
     * Clears all connection history.
     */
    fun clearAll() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }
    
    /**
     * Gets the current TTL setting.
     */
    fun getTtlMillis(): Flow<Long> {
        return flow {
            emit(repository.getTtlMillis())
        }
    }
    
    /**
     * Sets the TTL for connection expiration.
     * 
     * @param ttlMillis Time-to-live in milliseconds
     */
    fun setTtlMillis(ttlMillis: Long) {
        viewModelScope.launch {
            repository.setTtlMillis(ttlMillis)
        }
    }
    
    /**
     * Factory for creating ViewModel instances.
     */
    class Factory(
        private val repository: DeviceConnectionHistoryRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DeviceConnectionHistoryViewModel(repository) as T
        }
    }
}
