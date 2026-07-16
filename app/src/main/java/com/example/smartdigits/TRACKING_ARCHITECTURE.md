# Smart Digits Tracking Architecture

## Overview

This document describes the architecture and implementation of two tracking features:
1. **Device Connection History** - Cache-based storage of device connections
2. **Web Browser Tracking** - Privacy-aware tracking of in-app browser activity

## Architecture Pattern

The implementation follows **Clean Architecture** principles with **MVVM** pattern:

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  (Activities, ViewModels, UI)          │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│           Domain Layer                   │
│  (Repositories, Use Cases)              │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│            Data Layer                    │
│  (DataStore, Models, Trackers)          │
└─────────────────────────────────────────┘
```

## Feature 1: Device Connection History

### Storage Approach: DataStore Preferences

**Why DataStore over alternatives?**

- ✅ **SharedPreferences**: DataStore is the modern replacement, type-safe, async, handles corruption better
- ✅ **Room DB**: Overkill for cache data with TTL; DataStore is lighter and faster
- ✅ **DiskLruCache**: Good for large files/blobs, but DataStore is better for structured key-value data

**Performance Characteristics:**
- **Access Time**: O(1) via HashMap lookup after deserialization
- **Memory**: Lightweight, only loads active connections
- **Persistence**: Survives app restarts
- **TTL**: Automatic expiration on read operations

### Data Model

```kotlin
data class DeviceConnection(
    val id: String,
    val deviceName: String,
    val deviceId: String,
    val deviceIdHash: String,  // SHA-256 hash for privacy
    val connectionTimestamp: Long,
    val connectionType: ConnectionType  // BLUETOOTH, USB, WIFI, etc.
)
```

### Usage Examples

#### Recording a WiFi Connection
```kotlin
val tracker = SmartDigitsApplication.get(context).deviceConnectionTracker
tracker.recordWiFiConnection("MyWiFi", "00:11:22:33:44:55")
```

#### Recording a Bluetooth Connection
```kotlin
tracker.recordBluetoothConnection("MyDevice", "AA:BB:CC:DD:EE:FF")
```

#### Recording a USB Connection
```kotlin
tracker.recordUsbConnection("USB Device", "SN123456")
```

#### From Java Code
```java
TrackingHelper.recordWiFiConnection(context, "MyWiFi", "00:11:22:33:44:55");
```

### TTL Configuration

Default TTL: **7 days** (configurable)

```kotlin
val repository = DeviceConnectionHistoryRepositoryImpl(context)
repository.setTtlMillis(7L * 24 * 60 * 60 * 1000) // 7 days
```

Expired entries are automatically filtered on read operations.

## Feature 2: Web Browser Tracking

### Architecture Overview

The tracking logic lives in the **data layer** (`WebBrowserTracker`), which can be integrated into:
- **WebView components** - via custom `WebViewClient`
- **Browser intents** - via Intent tracking wrapper

### Privacy Safeguards

✅ **Only tracks in-app activity** - No system-wide browsing tracking
✅ **User opt-out** - Can be disabled via app settings
✅ **Local storage only** - No automatic backend sync
✅ **Compliant** - Follows Android privacy policies and Play Store guidelines

### Implementation

#### WebView Tracking

```kotlin
val browserTracker = SmartDigitsApplication.get(context).webBrowserTracker
val webView: WebView = findViewById(R.id.webView)

// Set tracking-enabled WebViewClient
webView.webViewClient = browserTracker.createTrackingWebViewClient()

// Optionally wrap existing WebViewClient
val existingClient = MyCustomWebViewClient()
webView.webViewClient = browserTracker.createTrackingWebViewClient(existingClient)
```

#### Intent-Based Browser Tracking

```kotlin
val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://example.com"))
browserTracker.trackBrowserIntent(intent)
startActivity(intent)
```

#### From Java Code

```java
Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
TrackingHelper.trackBrowserIntent(context, browserIntent);
startActivity(browserIntent);
```

### Data Model

```kotlin
data class WebBrowserEvent(
    val id: String,
    val url: String,
    val domain: String,  // Extracted from URL
    val timestamp: Long,
    val source: BrowserSource  // WEBVIEW or EXTERNAL_INTENT
)
```

### Privacy Settings

Users can opt-out via app settings:

```kotlin
val repository = WebBrowserTrackingRepositoryImpl(context)
repository.setTrackingEnabled(false)  // Disable tracking
```

When tracking is disabled, `recordEvent()` calls are silently ignored.

## Analytics Abstraction (Future Backend Sync)

The architecture is designed to easily add backend synchronization:

```kotlin
interface AnalyticsBackend {
    suspend fun syncDeviceConnections(connections: List<DeviceConnection>)
    suspend fun syncBrowserEvents(events: List<WebBrowserEvent>)
}

// In repository implementation:
class DeviceConnectionHistoryRepositoryImpl(
    private val context: Context,
    private val analyticsBackend: AnalyticsBackend? = null  // Optional
) {
    override suspend fun addConnection(connection: DeviceConnection) {
        // ... local storage ...
        
        // Optional backend sync
        analyticsBackend?.syncDeviceConnections(listOf(connection))
    }
}
```

## Module Structure

```
com.example.smartdigits/
├── data/
│   ├── model/
│   │   ├── DeviceConnection.kt
│   │   └── WebBrowserEvent.kt
│   ├── repository/
│   │   ├── DeviceConnectionHistoryRepository.kt
│   │   ├── WebBrowserTrackingRepository.kt
│   │   └── impl/
│   │       ├── DeviceConnectionHistoryRepositoryImpl.kt
│   │       └── WebBrowserTrackingRepositoryImpl.kt
│   ├── tracking/
│   │   ├── DeviceConnectionTracker.kt
│   │   └── WebBrowserTracker.kt
│   └── preferences/
│       └── PrivacyPreferences.kt
├── ui/
│   ├── viewmodel/
│   │   ├── DeviceConnectionHistoryViewModel.kt
│   │   └── WebBrowserTrackingViewModel.kt
│   └── WebViewActivity.kt
└── util/
    └── TrackingHelper.kt
```

## Best Practices

1. **Thread Safety**: All repository operations use coroutines and are thread-safe
2. **Memory Management**: Trackers use `SupervisorJob` to prevent crashes from affecting the app
3. **Lifecycle Awareness**: ViewModels use `viewModelScope` for automatic cancellation
4. **Error Handling**: DataStore handles corruption gracefully with fallback to empty data
5. **Performance**: Expired entries are filtered on read, not stored indefinitely

## Testing

### Unit Tests
- Repository implementations can be tested with in-memory DataStore
- ViewModels can be tested with mock repositories

### Integration Tests
- Test TTL expiration
- Test privacy opt-out functionality
- Test WebView tracking integration

## Future Enhancements

- [ ] Add Room DB option for large-scale deployments
- [ ] Implement backend sync with retry logic
- [ ] Add analytics dashboard UI
- [ ] Export tracking data (CSV/JSON)
- [ ] Add connection quality metrics
- [ ] Implement connection frequency analysis
