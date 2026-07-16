package com.example.smartdigits.ui

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.smartdigits.R
import com.example.smartdigits.SmartDigitsApplication
import com.example.smartdigits.data.tracking.WebBrowserTracker

/**
 * Example Activity demonstrating WebView tracking integration.
 * 
 * This shows how to track web navigation within a WebView component.
 * 
 * Usage:
 * 1. Create a WebView in your layout
 * 2. Get the WebBrowserTracker instance
 * 3. Set a tracking-enabled WebViewClient
 * 4. Load your URL
 */
class WebViewActivity : AppCompatActivity() {
    
    private lateinit var webView: WebView
    private lateinit var browserTracker: WebBrowserTracker
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        
        // Initialize tracker
        browserTracker = SmartDigitsApplication.get(this).webBrowserTracker
        
        // Setup WebView
        webView = findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        
        // Set tracking-enabled WebViewClient
        // This will automatically track all page navigations
        webView.webViewClient = browserTracker.createTrackingWebViewClient()
        
        // Load URL from intent or default
        val url = intent.getStringExtra("url") ?: "https://www.example.com"
        webView.loadUrl(url)
    }
    
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
    
    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}
