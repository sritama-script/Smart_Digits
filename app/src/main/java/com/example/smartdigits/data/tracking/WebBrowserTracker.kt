package com.example.smartdigits.data.tracking

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.smartdigits.data.model.WebBrowserEvent
import com.example.smartdigits.data.repository.WebBrowserTrackingRepository
import com.example.smartdigits.data.repository.impl.WebBrowserTrackingRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Manager class for tracking web browser activity within the app.
 * 
 * Provides tracking for:
 * - WebView navigation (via custom WebViewClient)
 * - External browser intents (via Intent tracking wrapper)
 * 
 * Privacy: Only tracks events explicitly initiated by the app.
 */
class WebBrowserTracker(
    private val repository: WebBrowserTrackingRepository,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) {
    
    /**
     * Records a browser event.
     * 
     * @param url The URL that was visited
     * @param source The source of the browser event
     */
    fun recordEvent(url: String, source: WebBrowserEvent.BrowserSource) {
        val domain = WebBrowserEvent.extractDomain(url)
        
        val event = WebBrowserEvent(
            url = url,
            domain = domain,
            source = source
        )
        
        coroutineScope.launch {
            repository.recordEvent(event)
        }
    }
    
    /**
     * Creates a WebViewClient that tracks navigation events.
     * 
     * Wrap your existing WebViewClient with this to enable tracking.
     * 
     * @param baseClient Optional base WebViewClient to delegate to
     * @return WebViewClient that tracks navigation
     */
    fun createTrackingWebViewClient(
        baseClient: WebViewClient? = null
    ): WebViewClient {
        return object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                url?.let {
                    recordEvent(it, WebBrowserEvent.BrowserSource.WEBVIEW)
                }
                return baseClient?.shouldOverrideUrlLoading(view, url) ?: false
            }
            
            override fun onPageFinished(view: WebView?, url: String?) {
                url?.let {
                    recordEvent(it, WebBrowserEvent.BrowserSource.WEBVIEW)
                }
                baseClient?.onPageFinished(view, url)
            }
        }
    }
    
    /**
     * Wraps an Intent to track browser launches.
     * 
     * Call this before starting an external browser intent.
     * 
     * @param intent The browser intent to track
     */
    fun trackBrowserIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_VIEW) {
            val uri = intent.data
            uri?.toString()?.let { url ->
                recordEvent(url, WebBrowserEvent.BrowserSource.EXTERNAL_INTENT)
            }
        }
    }
    
    companion object {
        /**
         * Creates a WebBrowserTracker instance.
         * 
         * @param context Application context
         * @return WebBrowserTracker instance
         */
        fun create(context: Context): WebBrowserTracker {
            val repository = WebBrowserTrackingRepositoryImpl(context.applicationContext)
            return WebBrowserTracker(repository)
        }
    }
}
