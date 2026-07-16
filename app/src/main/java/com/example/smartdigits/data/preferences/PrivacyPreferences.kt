package com.example.smartdigits.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


// Remove this line: import java.util.prefs.Preferences

/**
 * Manages privacy-related preferences.
 *
 * Centralized access to privacy settings such as
 * browser tracking opt-in / opt-out.
 */
class PrivacyPreferences(private val context: Context) {

    // DataStore instance
    private val dataStore: DataStore<Preferences> =
        context.privacyPreferencesDataStore

    companion object {

        // Extension property for DataStore
        private val Context.privacyPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
            name = "privacy_preferences"
        )

        // Preference keys
        private val BROWSER_TRACKING_ENABLED_KEY =
            booleanPreferencesKey("browser_tracking_enabled")

        // Default value (tracking enabled by default)
        private const val DEFAULT_BROWSER_TRACKING_ENABLED = true
    }

    /**
     * Observe whether browser tracking is enabled.
     */
    fun isBrowserTrackingEnabled(): Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[BROWSER_TRACKING_ENABLED_KEY]
                ?: DEFAULT_BROWSER_TRACKING_ENABLED
        }

    /**
     * Update browser tracking preference.
     */
    suspend fun setBrowserTrackingEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[BROWSER_TRACKING_ENABLED_KEY] = enabled
        }
    }
}