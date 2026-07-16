package com.example.smartdigits

import android.app.Application
import com.example.smartdigits.data.tracking.DeviceConnectionTracker
import com.example.smartdigits.data.tracking.WebBrowserTracker

/**
 * Application class for Smart Digits app.
 * 
 * Initializes tracking components and provides global access.
 */
class SmartDigitsApplication : Application() {
    
    lateinit var deviceConnectionTracker: DeviceConnectionTracker
        private set
    
    lateinit var webBrowserTracker: WebBrowserTracker
        private set
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize trackers
        deviceConnectionTracker = DeviceConnectionTracker.create(this)
        webBrowserTracker = WebBrowserTracker.create(this)
    }
    
    companion object {
        /**
         * Gets the Application instance.
         * 
         * @param context Any context
         * @return SmartDigitsApplication instance
         */
        fun get(context: android.content.Context): SmartDigitsApplication {
            return context.applicationContext as SmartDigitsApplication
        }
    }
}
