package com.example.f2shhh

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityEvent

/**
 * FlipLockAccessibilityService — Light Accessibility Service for locking screen on flip-down.
 * Uses GLOBAL_ACTION_LOCK_SCREEN (Android 9+) which allows seamless Fingerprint/Face Unlock afterwards.
 */
class FlipLockAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {}

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.i(TAG, "FlipLockAccessibilityService connected successfully.")
    }

    override fun onDestroy() {
        instance = null
        Log.i(TAG, "FlipLockAccessibilityService destroyed.")
        super.onDestroy()
    }

    companion object {
        private const val TAG = "FlipLockAccessibility"
        var instance: FlipLockAccessibilityService? = null

        fun performLock(): Boolean {
            val service = instance ?: return false
            val success = service.performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
            Log.i(TAG, "performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN) result: $success")
            return success
        }

        fun isAccessibilityServiceEnabled(context: Context): Boolean {
            val expectedService = "${context.packageName}/${FlipLockAccessibilityService::class.java.canonicalName}"
            val enabledServices = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return false

            val colonSplitter = android.text.TextUtils.SimpleStringSplitter(':')
            colonSplitter.setString(enabledServices)
            while (colonSplitter.hasNext()) {
                val componentName = colonSplitter.next()
                if (componentName.equals(expectedService, ignoreCase = true)) {
                    return true
                }
            }
            return false
        }
    }
}
