# Quick Fix Checklist for DataStore Unresolved References

## ⚡ Quick Fix (5 minutes)

### 1. Update Dependency ✅ DONE
The `app/build.gradle` has been updated with explicit core dependency:
```gradle
implementation 'androidx.datastore:datastore-core:1.0.0'
implementation 'androidx.datastore:datastore-preferences:1.0.0'
```

### 2. Sync Gradle (YOU NEED TO DO THIS)
- Click **"Sync Now"** button at top of Android Studio
- OR: **File → Sync Project with Gradle Files** (Ctrl+Shift+O)
- Wait for sync to finish (check bottom status bar)

### 3. Clean & Rebuild (IF SYNC DOESN'T FIX IT)
- **Build → Clean Project**
- **Build → Rebuild Project**

### 4. Invalidate Caches (ONLY IF STILL BROKEN)
- **File → Invalidate Caches / Restart**
- Select **"Invalidate and Restart"**

---

## ✅ Verification

After sync, check:
- [ ] No red underlines in `PrivacyPreferences.kt`
- [ ] Auto-complete works for `androidx.datastore.*`
- [ ] Build succeeds: **Build → Make Project**

---

## 🔍 Root Causes Identified

1. ✅ **Missing explicit core dependency** - FIXED
2. ⚠️ **Gradle not synced** - YOU NEED TO SYNC
3. ⚠️ **IDE cache stale** - Invalidate if needed

---

## 📝 File Locations

- **Build file**: `app/build.gradle` (Groovy DSL - ✅ Correct)
- **Problem file**: `app/src/main/java/com/example/smartdigits/data/preferences/PrivacyPreferences.kt`
- **Dependency**: Line 50-51 in `app/build.gradle`

---

## 🚨 If Still Broken After Sync

1. Check **Build** tab for specific error messages
2. Verify internet connection (Gradle downloads dependencies)
3. Try version `1.1.1` instead of `1.0.0`
4. See `DATSTORE_DIAGNOSTIC_FIX.md` for detailed troubleshooting
