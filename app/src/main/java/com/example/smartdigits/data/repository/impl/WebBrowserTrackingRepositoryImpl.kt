package com.example.smartdigits.data.repository.impl

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.smartdigits.data.model.WebBrowserEvent
import com.example.smartdigits.data.repository.WebBrowserTrackingRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first

/**
 * Implementation of WebBrowserTrackingRepository using DataStore Preferences.
 *
 * Architecture: Privacy-aware tracking that only records events initiated
 * within the app's scope (WebView or explicit browser intents).
 *
 * Privacy Safeguards:
 * - Only tracks events explicitly initiated by the app
 * - User can opt-out via settings
 * - No system-wide browsing tracking
 * - Data stored locally only (no automatic backend sync)
 */
class WebBrowserTrackingRepositoryImpl(
    private val context: Context
) : WebBrowserTrackingRepository {

    private val dataStore: DataStore<Preferences> = context.webBrowserTrackingDataStore
    private val gson = Gson()

    companion object {
        private val Context.webBrowserTrackingDataStore: DataStore<Preferences> by preferencesDataStore(
            name = "web_browser_tracking"
        )

        private val EVENTS_KEY = stringPreferencesKey("events")
        private val TRACKING_ENABLED_KEY = booleanPreferencesKey("tracking_enabled")

        // Default: tracking enabled (user can opt-out)
        private const val DEFAULT_TRACKING_ENABLED = true
    }

    override suspend fun recordEvent(event: WebBrowserEvent) {
        // Only record if tracking is enabled
        val isEnabled = isTrackingEnabled()
        if (!isEnabled) {
            return
        }

        dataStore.edit { preferences ->
            val events = getEventsList(preferences).toMutableList()
            events.add(event)

            // Keep only last 1000 events to prevent unbounded growth
            val trimmedEvents = if (events.size > 1000) {
                events.sortedByDescending { it.timestamp }.take(1000)
            } else {
                events
            }

            preferences[EVENTS_KEY] = gson.toJson(trimmedEvents)
        }
    }

    override fun getAllEvents(): Flow<List<WebBrowserEvent>> {
        return dataStore.data.map { preferences ->
            getEventsList(preferences)
                .sortedByDescending { it.timestamp }
        }
    }

    override fun getEventsInRange(startTime: Long, endTime: Long): Flow<List<WebBrowserEvent>> {
        return getAllEvents().map { events ->
            events.filter { it.timestamp in startTime..endTime }
        }
    }

    override fun getEventsByDomain(domain: String): Flow<List<WebBrowserEvent>> {
        return getAllEvents().map { events ->
            events.filter { it.domain.equals(domain, ignoreCase = true) }
        }
    }

    override fun getEventsBySource(source: WebBrowserEvent.BrowserSource): Flow<List<WebBrowserEvent>> {
        return getAllEvents().map { events ->
            events.filter { it.source == source }
        }
    }

    override suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.remove(EVENTS_KEY)
        }
    }

    override suspend fun isTrackingEnabled(): Boolean {
        val preferences = dataStore.data.first()
        return preferences[TRACKING_ENABLED_KEY] ?: DEFAULT_TRACKING_ENABLED
    }

    override suspend fun setTrackingEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[TRACKING_ENABLED_KEY] = enabled
        }
    }

    /**
     * Deserializes events from preferences.
     *
     * @param preferences The DataStore preferences
     * @return List of WebBrowserEvent
     */
    private fun getEventsList(preferences: Preferences): List<WebBrowserEvent> {
        val json = preferences[EVENTS_KEY] ?: return emptyList()
        return try {
            val type = object : TypeToken<List<WebBrowserEvent>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}