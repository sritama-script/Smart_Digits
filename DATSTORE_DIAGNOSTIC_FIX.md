# DataStore Unresolved Reference - Complete Diagnostic & Fix Guide

## 🔍 Root Cause Analysis

### Issue Identified
Your project configuration is **mostly correct**, but there are several potential causes for the unresolved references:

### ✅ What's Correct
1. **Build System**: Using Groovy DSL (`build.gradle`) - ✅ Correct
2. **Dependency Syntax**: `implementation 'androidx.datastore:datastore-preferences:1.0.0'` - ✅ Correct Groovy syntax
3. **Repositories**: `google()` and `mavenCentral()` configured - ✅ Correct
4. **Kotlin Plugin**: `id 'org.jetbrains.kotlin.android'` - ✅ Present
5. **Gradle Version**: 8.9 - ✅ Compatible

### ⚠️ Potential Issues Found

#### Issue #1: Missing Explicit Core Dependency
**Problem**: `datastore-preferences` may require explicit `datastore-core` dependency in some Gradle configurations.

**Solution**: Add explicit core dependency (see fix below)

#### Issue #2: Version Catalog Not Used (Not Critical)
**Observation**: Your `libs.versions.toml` exists but DataStore isn't defined there. This is fine, but inconsistent with root build.gradle pattern.

#### Issue #3: Gradle Sync State Unknown
**Problem**: IDE may not have successfully synced Gradle, causing unresolved references.

#### Issue #4: IDE Cache Stale
**Problem**: Android Studio's internal cache may be outdated.

---

## 🔧 Complete Fix Implementation

### Fix #1: Update app/build.gradle (Groovy DSL)

**Current:**
```gradle
// DataStore (for cache-based storage)
implementation 'androidx.datastore:datastore-preferences:1.0.0'
```

**Fixed (Add explicit core dependency):**
```gradle
// DataStore (for cache-based storage)
implementation 'androidx.datastore:datastore-preferences:1.0.0'
implementation 'androidx.datastore:datastore-core:1.0.0'  // Explicit core
```

**OR use latest stable version:**
```gradle
// DataStore (for cache-based storage) - Latest stable
implementation 'androidx.datastore:datastore-preferences:1.1.1'
```

### Fix #2: Verify Plugin Configuration

Ensure your `app/build.gradle` has:
```gradle
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'  // ✅ Must be present
}
```

### Fix #3: Verify Repository Configuration

Your `settings.gradle` should have:
```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()      // ✅ Required for androidx.* dependencies
        mavenCentral() // ✅ Required for other dependencies
    }
}
```

---

## 📋 Step-by-Step Resolution Checklist

### Step 1: Update build.gradle
- [ ] Open `app/build.gradle`
- [ ] Update DataStore dependency (see Fix #1 above)
- [ ] Save the file

### Step 2: Sync Gradle
- [ ] Click **"Sync Now"** banner at top of Android Studio
- [ ] OR: **File → Sync Project with Gradle Files**
- [ ] Wait for sync to complete (check bottom status bar)
- [ ] Verify no sync errors in **Build** tab

### Step 3: Clean Build
- [ ] **Build → Clean Project**
- [ ] Wait for completion
- [ ] **Build → Rebuild Project**
- [ ] Wait for completion

### Step 4: Invalidate Caches (If Step 3 fails)
- [ ] **File → Invalidate Caches / Restart**
- [ ] Select **"Invalidate and Restart"**
- [ ] Wait for Android Studio to restart
- [ ] After restart, sync Gradle again

### Step 5: Verify Dependency Resolution
- [ ] Open **File → Project Structure → Dependencies**
- [ ] Navigate to `:app` module
- [ ] Search for "datastore"
- [ ] Verify `androidx.datastore:datastore-preferences` appears
- [ ] Check version matches your build.gradle

### Step 6: Check Build Output
- [ ] Open **Build** tab at bottom
- [ ] Look for any errors related to DataStore
- [ ] Check for "Failed to resolve" messages

---

## 🔍 Verification Commands

### Check if dependency is downloaded:
```bash
# Navigate to project root
cd C:\Users\srita\AndroidStudioProjects\SmartDigits

# List dependencies (if Gradle wrapper works)
.\gradlew.bat :app:dependencies --configuration implementation | findstr datastore
```

### Check Gradle sync status:
- Look at bottom-right corner of Android Studio
- Should show "Gradle sync finished" (not "Gradle sync failed")

---

## 🎯 When Cache Invalidation is Required

**Invalidate caches when:**
1. ✅ Dependency was added but IDE still shows errors after sync
2. ✅ Multiple "Unresolved reference" errors persist after clean rebuild
3. ✅ IDE shows different errors than Gradle command line
4. ✅ After updating Android Studio or Gradle version
5. ✅ After moving/renaming project files

**Invalidate caches is NOT required when:**
- ❌ First time adding a dependency (just sync Gradle)
- ❌ Simple code changes (just rebuild)
- ❌ Dependency version updates (sync + clean rebuild is enough)

---

## 📝 Alternative: Using Version Catalog (Optional)

If you want consistency with your root build.gradle pattern:

### Update `gradle/libs.versions.toml`:
```toml
[versions]
# ... existing versions ...
datastore = "1.1.1"

[libraries]
# ... existing libraries ...
datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastore" }
datastore-core = { group = "androidx.datastore", name = "datastore-core", version.ref = "datastore" }
```

### Update `app/build.gradle`:
```gradle
dependencies {
    // ... other dependencies ...
    
    // DataStore using version catalog
    implementation libs.datastore.preferences
    implementation libs.datastore.core
}
```

---

## 🚨 Common Mistakes to Avoid

1. **Wrong Build File**: Editing `build.gradle` in wrong location
   - ✅ Correct: `app/build.gradle`
   - ❌ Wrong: `build.gradle` (root level)

2. **Kotlin DSL vs Groovy**: Mixing syntax
   - ✅ Groovy: `implementation 'androidx.datastore:datastore-preferences:1.0.0'`
   - ❌ Kotlin DSL: `implementation("androidx.datastore:datastore-preferences:1.0.0")`

3. **Missing Quotes**: In Groovy, strings need quotes
   - ✅ Correct: `'androidx.datastore:datastore-preferences:1.0.0'`
   - ❌ Wrong: `androidx.datastore:datastore-preferences:1.0.0`

4. **Wrong Repository**: DataStore requires Google's Maven repository
   - ✅ Must have `google()` repository in `settings.gradle`

---

## ✅ Final Verification

After completing all steps, verify:

1. **No Red Underlines**: `PrivacyPreferences.kt` should have no red error underlines
2. **Auto-complete Works**: Typing `androidx.datastore.` should show suggestions
3. **Build Succeeds**: **Build → Make Project** should complete without errors
4. **Import Resolution**: Hover over imports - should show "from androidx.datastore..."

---

## 📞 If Issues Persist

If errors still occur after all steps:

1. **Check Gradle Logs**: `View → Tool Windows → Build` → Look for specific error messages
2. **Check Network**: Ensure internet connection (Gradle needs to download dependencies)
3. **Check Proxy**: If behind corporate firewall, configure Gradle proxy settings
4. **Try Different Version**: Use `1.1.1` instead of `1.0.0`
5. **Check Android Studio Version**: Ensure using Android Studio Hedgehog or newer

---

## 📚 Reference Links

- [DataStore Documentation](https://developer.android.com/topic/libraries/architecture/datastore)
- [DataStore Releases](https://developer.android.com/jetpack/androidx/releases/datastore)
- [Gradle Sync Issues](https://developer.android.com/studio/build/gradle-troubleshooting)
