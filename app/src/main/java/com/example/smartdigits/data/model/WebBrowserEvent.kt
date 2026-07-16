package com.example.smartdigits.data.model

import java.util.UUID

/**
 * Data model representing a web browser event tracked within the app.
 * 
 * This model stores information about web browsing activity that occurs
 * within the app's scope (WebView or intent-based browser launches).
 * 
 * @param id Unique identifier for this browser event
 * @param url The full URL that was visited
 * @param domain The domain extracted from the URL
 * @param timestamp Unix timestamp in milliseconds when the event occurred
 * @param source The source of the browser event (WEBVIEW or EXTERNAL_INTENT)
 */
data class WebBrowserEvent(
    val id: String = UUID.randomUUID().toString(),
    val url: String,
    val domain: String,
    val timestamp: Long = System.currentTimeMillis(),
    val source: BrowserSource
) {
    /**
     * Enumeration of browser event sources.
     */
    enum class BrowserSource {
        WEBVIEW,        // Opened in app's WebView
        EXTERNAL_INTENT // Opened via Intent to external browser
    }
    
    companion object {
        /**
         * Extracts domain from a URL.
         * 
         * @param url The full URL
         * @return The domain name, or "unknown" if extraction fails
         */
        fun extractDomain(url: String): String {
            return try {
                val uri = android.net.Uri.parse(url)
                uri.host ?: "unknown"
            } catch (e: Exception) {
                "unknown"
            }
        }
    }
}
