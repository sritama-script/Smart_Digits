package com.example.smartdigits.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartdigits.data.model.WebBrowserEvent
import com.example.smartdigits.data.repository.WebBrowserTrackingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for web browser tracking.
 * 
 * Provides UI-friendly access to browser tracking data and settings.
 */
class WebBrowserTrackingViewModel(
    private val repository: WebBrowserTrackingRepository
) : ViewModel() {
    
    /**
     * Flow of all browser events.
     */
    val events: Flow<List<WebBrowserEvent>> = repository.getAllEvents()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    /**
     * Flow indicating if tracking is enabled.
     */
    val isTrackingEnabled: Flow<Boolean> = flow {
        emit(repository.isTrackingEnabled())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )
    
    /**
     * Gets events within a time range.
     * 
     * @param startTime Start timestamp in milliseconds
     * @param endTime End timestamp in milliseconds
     * @return Flow of events in the time range
     */
    fun getEventsInRange(startTime: Long, endTime: Long): Flow<List<WebBrowserEvent>> {
        return repository.getEventsInRange(startTime, endTime)
    }
    
    /**
     * Gets events by domain.
     * 
     * @param domain The domain to filter by
     * @return Flow of events for the domain
     */
    fun getEventsByDomain(domain: String): Flow<List<WebBrowserEvent>> {
        return repository.getEventsByDomain(domain)
    }
    
    /**
     * Gets events by source.
     * 
     * @param source The browser source to filter by
     * @return Flow of events from the source
     */
    fun getEventsBySource(source: WebBrowserEvent.BrowserSource): Flow<List<WebBrowserEvent>> {
        return repository.getEventsBySource(source)
    }
    
    /**
     * Clears all browser events.
     */
    fun clearAll() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }
    
    /**
     * Sets the tracking enabled state.
     * 
     * @param enabled true to enable tracking, false to disable
     */
    fun setTrackingEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setTrackingEnabled(enabled)
        }
    }
    
    /**
     * Factory for creating ViewModel instances.
     */
    class Factory(
        private val repository: WebBrowserTrackingRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WebBrowserTrackingViewModel(repository) as T
        }
    }
}
