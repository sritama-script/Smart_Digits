package com.example.smartdigits.data.repository

import com.example.smartdigits.data.model.WebBrowserEvent
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for web browser tracking operations.
 * 
 * Provides a clean abstraction for tracking and retrieving web browser
 * events that occur within the app's scope.
 */
interface WebBrowserTrackingRepository {
    
    /**
     * Records a web browser event.
     * 
     * @param event The browser event to record
     */
    suspend fun recordEvent(event: WebBrowserEvent)
    
    /**
     * Retrieves all browser events.
     * 
     * @return Flow of list of browser events
     */
    fun getAllEvents(): Flow<List<WebBrowserEvent>>
    
    /**
     * Retrieves browser events within a time range.
     * 
     * @param startTime Start timestamp in milliseconds
     * @param endTime End timestamp in milliseconds
     * @return Flow of list of browser events in the time range
     */
    fun getEventsInRange(startTime: Long, endTime: Long): Flow<List<WebBrowserEvent>>
    
    /**
     * Retrieves browser events by domain.
     * 
     * @param domain The domain to filter by
     * @return Flow of list of browser events for the domain
     */
    fun getEventsByDomain(domain: String): Flow<List<WebBrowserEvent>>
    
    /**
     * Retrieves browser events by source.
     * 
     * @param source The browser source to filter by
     * @return Flow of list of browser events from the source
     */
    fun getEventsBySource(source: WebBrowserEvent.BrowserSource): Flow<List<WebBrowserEvent>>
    
    /**
     * Clears all browser events.
     */
    suspend fun clearAll()
    
    /**
     * Checks if tracking is enabled.
     * 
     * @return true if tracking is enabled, false otherwise
     */
    suspend fun isTrackingEnabled(): Boolean
    
    /**
     * Sets the tracking enabled state.
     * 
     * @param enabled true to enable tracking, false to disable
     */
    suspend fun setTrackingEnabled(enabled: Boolean)
}
