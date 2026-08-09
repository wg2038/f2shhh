package com.example.f2shhh

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.TriggerEvent
import android.hardware.TriggerEventListener
import android.media.AudioAttributes
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Flip to Shhh Service — optimized for Samsung Galaxy flagship devices (Android 13+).
 *
 * Sensor strategy:
 *  - TYPE_SIGNIFICANT_MOTION (hardware trigger, ultra-low power) as the always-on listener.
 *  - TYPE_GRAVITY (virtual sensor, fused from accel+gyro) for short confirmation bursts (~1.5s).
 *  - No light sensor dependency — Samsung flagships use under-display light sensors not designed
 *    for continuous polling, and the gravity vector alone reliably detects face-down orientation.
 *
 * DND management:
 *  - Saves both previous ringer mode AND previous interruption filter.
 *  - Restores to the user's original state on flip-back or service shutdown.
 *  - State persisted to SharedPreferences to survive service kills.
 */
class FlipToShhhService : LifecycleService(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var notifManager: NotificationManager
    private lateinit var audioManager: AudioManager
    private var vibrator: Vibrator? = null
    private lateinit var prefs: SharedPreferences
    private val handler = Handler(Looper.getMainLooper())

    private var gravitySensor: Sensor? = null
    private var accelerometerSensor: Sensor? = null
    private var gyroscopeSensor: Sensor? = null
    private var activeSensor: Sensor? = null

    private var currentXValue: Float = 0f
    private var currentYValue: Float = 0f
    private var currentZValue: Float = 0f

    private var prevXValue: Float = 0f
    private var prevYValue: Float = 0f
    private var prevZValue: Float = 0f
    private var currentDeltaG: Float = 0f
    private var currentGyroRotation: Float = 0f
    private var faceDownStartTime: Long = 0L

    private var previousRingerMode: Int = AudioManager.RINGER_MODE_NORMAL
    private var previousInterruptionFilter: Int = NotificationManager.INTERRUPTION_FILTER_ALL

    private enum class TargetFlipState { NONE, DOWN, UP }
    private var pendingTargetState: TargetFlipState = TargetFlipState.NONE

    private val debounceRunnable: Runnable = Runnable {
        val currentlyFlipped = _isFlippedDown.value
        val hMag = kotlin.math.sqrt(currentXValue * currentXValue + currentYValue * currentYValue)
        val isPhysicallyStationary = (currentDeltaG <= MAX_DELTA_G_STILLNESS) && (currentGyroRotation <= MAX_GYRO_ROTATION_STILLNESS)
        val isFlatFaceDown = (currentZValue <= FACE_DOWN_ENTER_Z_THRESHOLD) && (hMag <= MAX_HORIZONTAL_GRAVITY) && isPhysicallyStationary
        val isFaceUpOrTilted = (currentZValue > FACE_DOWN_EXIT_Z_THRESHOLD) || (hMag > MAX_HORIZONTAL_GRAVITY + 1.0f)

        if (pendingTargetState == TargetFlipState.DOWN && !currentlyFlipped) {
            val now = SystemClock.elapsedRealtime()
            val totalDebounceMs = getDebounceMs()
            val elapsedTime = now - faceDownStartTime
            val remainingMs = totalDebounceMs - elapsedTime

            if (remainingMs > 30L) {
                if (currentZValue <= FACE_DOWN_ENTER_Z_THRESHOLD && hMag <= MAX_HORIZONTAL_GRAVITY) {
                    handler.postDelayed(debounceRunnable, remainingMs)
                    return@Runnable
                } else {
                    pendingTargetState = TargetFlipState.NONE
                    return@Runnable
                }
            }

            if (isFlatFaceDown) {
                Log.i(TAG, "Debounce confirmed (${elapsedTime}ms elapsed since T0) -> entering Face Down mode (DND ON), Z=$currentZValue, H=$hMag, dG=$currentDeltaG, gyro=$currentGyroRotation")
                _isFlippedDown.value = true
                enableDoNotDisturb()
            } else {
                Log.i(TAG, "Debounce expired but phone is not fully stationary (dG=$currentDeltaG, gyro=$currentGyroRotation) -> skipping DND trigger")
            }
        } else if (pendingTargetState == TargetFlipState.UP && currentlyFlipped) {
            if (isFaceUpOrTilted) {
                Log.i(TAG, "Debounce confirmed -> exiting Face Down mode (DND OFF), Z=$currentZValue, H=$hMag")
                _isFlippedDown.value = false
                disableDoNotDisturb()
            }
        }
        pendingTargetState = TargetFlipState.NONE
    }

    // ── Lifecycle ──────────────────────────────────────────────────────────

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate")
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        activeSensor = gravitySensor ?: accelerometerSensor

        Log.i(TAG, "Active orientation sensor: ${activeSensor?.name} (type ${activeSensor?.type}), Gyro: ${gyroscopeSensor?.name}")

        // Restore persisted state if service was killed and restarted.
        if (prefs.getBoolean(KEY_DND_ACTIVE, false)) {
            previousRingerMode = prefs.getInt(KEY_PREV_RINGER_MODE, AudioManager.RINGER_MODE_NORMAL)
            previousInterruptionFilter = prefs.getInt(
                KEY_PREV_INTERRUPTION_FILTER, NotificationManager.INTERRUPTION_FILTER_ALL
            )
            _isDndActive.value = true
            _isFlippedDown.value = true
        }

        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.i(TAG, "onStartCommand")
        startForeground(NOTIFICATION_ID, createForegroundNotification())

        if (!_isRunning.value) {
            registerBatchedSensor()
        }
        _isRunning.value = true
        return START_STICKY
    }

    override fun onDestroy() {
        unregisterAllSensors()
        restoreDndIfNeeded()
        handler.removeCallbacks(debounceRunnable)
        pendingTargetState = TargetFlipState.NONE
        _isRunning.value = false
        _isFlippedDown.value = false
        _isDndActive.value = false
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    // ── Hardware FIFO Batched Sensor Listener ──────────────────────────────

    private fun registerBatchedSensor() {
        activeSensor?.let { sensor ->
            val registered = sensorManager.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_UI,
                BATCH_LATENCY_US
            )
            Log.i(TAG, "Registered batched sensor listener: ${sensor.name}, success=$registered, batchLatency=${BATCH_LATENCY_US}us")
        } ?: run {
            Log.e(TAG, "Neither GRAVITY nor ACCELEROMETER sensor is available!")
        }

        gyroscopeSensor?.let { gyro ->
            val registered = sensorManager.registerListener(
                this,
                gyro,
                SensorManager.SENSOR_DELAY_UI,
                BATCH_LATENCY_US
            )
            Log.i(TAG, "Registered batched gyroscope listener: ${gyro.name}, success=$registered")
        }
    }

    private fun unregisterAllSensors() {
        activeSensor?.let { sensorManager.unregisterListener(this, it) }
        gyroscopeSensor?.let { sensorManager.unregisterListener(this, it) }
        handler.removeCallbacksAndMessages(null)
    }

    // ── Sensor Events ──────────────────────────────────────────────────────

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return

        if (event.sensor.type == Sensor.TYPE_GYROSCOPE) {
            val gx = event.values[0]
            val gy = event.values[1]
            val gz = event.values[2]
            currentGyroRotation = kotlin.math.sqrt(gx * gx + gy * gy + gz * gz)
            return
        }

        val targetType = activeSensor?.type ?: return
        if (event.sensor.type != targetType) return

        val newX = event.values[0]
        val newY = event.values[1]
        val newZ = event.values[2]

        val dx = newX - prevXValue
        val dy = newY - prevYValue
        val dz = newZ - prevZValue
        currentDeltaG = kotlin.math.sqrt(dx * dx + dy * dy + dz * dz)

        prevXValue = newX
        prevYValue = newY
        prevZValue = newZ

        currentXValue = newX
        currentYValue = newY
        currentZValue = newZ
        checkFlipState()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    // ── Flip Detection Logic ───────────────────────────────────────────────

    private fun getDebounceMs(): Long {
        return prefs.getLong(KEY_DEBOUNCE_MS, DEFAULT_DEBOUNCE_MS)
    }

    private fun checkFlipState() {
        val hMag = kotlin.math.sqrt(currentXValue * currentXValue + currentYValue * currentYValue)
        val isOrientationFaceDown = (currentZValue <= FACE_DOWN_ENTER_Z_THRESHOLD) && (hMag <= MAX_HORIZONTAL_GRAVITY)
        val isFaceUpOrTilted = (currentZValue > FACE_DOWN_EXIT_Z_THRESHOLD) || (hMag > MAX_HORIZONTAL_GRAVITY + 1.0f)
        val currentlyFlipped = _isFlippedDown.value
        val debounceDownMs = getDebounceMs()
        val debounceUpMs = DEBOUNCE_UP_MS

        if (!currentlyFlipped && isOrientationFaceDown) {
            if (pendingTargetState != TargetFlipState.DOWN) {
                handler.removeCallbacks(debounceRunnable)
                pendingTargetState = TargetFlipState.DOWN
                faceDownStartTime = SystemClock.elapsedRealtime()
                handler.postDelayed(debounceRunnable, debounceDownMs)
                Log.i(TAG, "Scheduled DOWN debounce timer (${debounceDownMs}ms from T0), Z=$currentZValue, H=$hMag")
            }
        } else if (currentlyFlipped && isFaceUpOrTilted) {
            if (pendingTargetState != TargetFlipState.UP) {
                handler.removeCallbacks(debounceRunnable)
                pendingTargetState = TargetFlipState.UP
                handler.postDelayed(debounceRunnable, debounceUpMs)
                Log.i(TAG, "Scheduled UP debounce timer (${debounceUpMs}ms restore), Z=$currentZValue, H=$hMag")
            }
        } else if (!currentlyFlipped && isFaceUpOrTilted) {
            if (pendingTargetState == TargetFlipState.DOWN) {
                handler.removeCallbacks(debounceRunnable)
                pendingTargetState = TargetFlipState.NONE
                Log.i(TAG, "Cancelled pending DOWN debounce timer (phone turned face up or tilted), Z=$currentZValue, H=$hMag")
            }
        }
    }

    // ── DND Management ─────────────────────────────────────────────────────

    private fun enableDoNotDisturb() {
        if (!notifManager.isNotificationPolicyAccessGranted) {
            Log.e(TAG, "Cannot enable DND: Notification Policy Access not granted!")
            return
        }
        try {
            // 1. Trigger haptic feedback FIRST before DND activation & screen locking
            triggerFlipDownHaptic()

            // 2. Turn on DND mode
            previousInterruptionFilter = notifManager.currentInterruptionFilter
            notifManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)

            persistState(active = true)
            _isDndActive.value = true
            updateNotification(active = true)
            
            // 3. Perform lock screen after haptic has been executed
            val autoLockPref = prefs.getBoolean(KEY_AUTO_LOCK_SCREEN, true)
            val accessibilityEnabled = FlipLockAccessibilityService.isAccessibilityServiceEnabled(this)
            Log.i(TAG, "DND mode successfully enabled! autoLockPref=$autoLockPref, accessibilityEnabled=$accessibilityEnabled")

            if (autoLockPref && accessibilityEnabled) {
                val locked = FlipLockAccessibilityService.performLock()
                Log.i(TAG, "Auto lock screen performed via AccessibilityService, success=$locked")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error enabling DND", e)
        }
    }

    private fun disableDoNotDisturb() {
        if (!notifManager.isNotificationPolicyAccessGranted) {
            Log.e(TAG, "Cannot disable DND: Notification Policy Access not granted!")
            return
        }
        try {
            // Only restore if we were the ones who changed it.
            if (!_isDndActive.value) return

            notifManager.setInterruptionFilter(previousInterruptionFilter)

            persistState(active = false)
            _isDndActive.value = false
            triggerFlipUpHaptic()
            updateNotification(active = false)
            Log.i(TAG, "DND disabled, restored interruptionFilter=$previousInterruptionFilter")
        } catch (e: Exception) {
            Log.e(TAG, "Error disabling DND", e)
        }
    }

    private fun restoreDndIfNeeded() {
        if (_isDndActive.value && notifManager.isNotificationPolicyAccessGranted) {
            try {
                notifManager.setInterruptionFilter(previousInterruptionFilter)
                persistState(active = false)
                _isDndActive.value = false
            } catch (e: Exception) {
                Log.e(TAG, "Error restoring DND state", e)
            }
        }
    }

    // ── Persistence ────────────────────────────────────────────────────────

    private fun persistState(active: Boolean) {
        prefs.edit()
            .putBoolean(KEY_DND_ACTIVE, active)
            .putInt(KEY_PREV_RINGER_MODE, previousRingerMode)
            .putInt(KEY_PREV_INTERRUPTION_FILTER, previousInterruptionFilter)
            .apply()
    }

    // ── Haptics ────────────────────────────────────────────────────────────

    private fun triggerFlipDownHaptic() {
        val vib = vibrator ?: return
        if (!vib.hasVibrator()) return
        val hapticMode = prefs.getInt(KEY_HAPTIC_MODE, 0)
        val audioAttrs = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .build()

        when (hapticMode) {
            0 -> { // Double pulse: Solid & Distinct "咚 - 咚"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                    vib.areAllPrimitivesSupported(VibrationEffect.Composition.PRIMITIVE_THUD)
                ) {
                    val composition = VibrationEffect.startComposition()
                        .addPrimitive(VibrationEffect.Composition.PRIMITIVE_THUD, 1.0f)
                        .addPrimitive(VibrationEffect.Composition.PRIMITIVE_THUD, 1.0f, 65)
                        .compose()
                    vib.vibrate(composition, audioAttrs)
                } else {
                    val timings = longArrayOf(0, 28, 65, 40)
                    val amplitudes = intArrayOf(0, 255, 0, 255)
                    vib.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1), audioAttrs)
                }
            }
            1 -> { // Single touch: Solid "咚"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                    vib.areAllPrimitivesSupported(VibrationEffect.Composition.PRIMITIVE_THUD)
                ) {
                    val composition = VibrationEffect.startComposition()
                        .addPrimitive(VibrationEffect.Composition.PRIMITIVE_THUD, 1.0f)
                        .compose()
                    vib.vibrate(composition, audioAttrs)
                } else {
                    val timings = longArrayOf(0, 35)
                    val amplitudes = intArrayOf(0, 255)
                    vib.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1), audioAttrs)
                }
            }
            2 -> { // Off
                // Silent/no haptic
            }
        }
    }

    private fun triggerFlipUpHaptic() {
        val vib = vibrator ?: return
        if (!vib.hasVibrator()) return
        val hapticMode = prefs.getInt(KEY_HAPTIC_MODE, 0)
        if (hapticMode == 2) return
        val audioAttrs = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
            vib.areAllPrimitivesSupported(VibrationEffect.Composition.PRIMITIVE_CLICK)
        ) {
            val composition = VibrationEffect.startComposition()
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, 1.0f)
                .compose()
            vib.vibrate(composition, audioAttrs)
        } else {
            val timings = longArrayOf(0, 22)
            val amplitudes = intArrayOf(0, 200)
            vib.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1), audioAttrs)
        }
    }

    // ── Notifications ──────────────────────────────────────────────────────

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Flip to Shhh 服务",
            NotificationManager.IMPORTANCE_MIN
        ).apply {
            description = "三星 Flip to Shhh 翻转静音服务"
            setShowBadge(false)
        }
        notifManager.createNotificationChannel(channel)
    }

    private fun createForegroundNotification(): Notification = buildNotification(active = false)

    private fun updateNotification(active: Boolean) {
        notifManager.notify(NOTIFICATION_ID, buildNotification(active = active))
    }

    private fun buildNotification(active: Boolean): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val langMode = prefs.getInt("language_mode", 0)
        val isEng = (langMode == 3)
        val isTrad = (langMode == 2)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_shhh)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)

        if (active) {
            builder
                .setContentTitle("已开启勿扰模式")
                .setContentText("手机面朝下 · 来电与通知已静音")
        } else {
            builder
                .setContentTitle("Flip to Shhh 运行中")
                .setContentText("翻转手机面朝下即可自动开启勿扰")
        }

        return builder.build()
    }

    // ── Companion ──────────────────────────────────────────────────────────

    companion object {
        const val CHANNEL_ID = "flip_to_shhh_channel"
        const val NOTIFICATION_ID = 1001
        private const val TAG = "FlipToShhh"

        private const val PREFS_NAME = "flip_to_shhh_prefs"
        private const val KEY_DND_ACTIVE = "dnd_active"
        private const val KEY_PREV_RINGER_MODE = "prev_ringer_mode"
        private const val KEY_PREV_INTERRUPTION_FILTER = "prev_interruption_filter"

        const val KEY_DEBOUNCE_MS = "debounce_ms"
        const val DEFAULT_DEBOUNCE_MS = 2000L
        const val DEBOUNCE_UP_MS = 1000L
        const val KEY_HAPTIC_MODE = "haptic_mode" // 0: double pulse, 1: single touch, 2: off
        const val KEY_AUTO_LOCK_SCREEN = "auto_lock_screen"

        // Gravity/Accelerometer vector: Z ≈ -9.8 when face-down flat.
        private const val FACE_DOWN_ENTER_Z_THRESHOLD = -9.0f
        private const val FACE_DOWN_EXIT_Z_THRESHOLD = -7.0f

        // Horizontal tilt constraint: sqrt(X^2 + Y^2) must be <= 1.8 m/s^2 for true flat orientation (~23° tilt).
        private const val MAX_HORIZONTAL_GRAVITY = 1.8f

        // Physical stillness threshold: Delta G must be <= 0.15 m/s^2.
        private const val MAX_DELTA_G_STILLNESS = 0.15f

        // Gyroscope rotational stillness threshold: <= 0.08 rad/s.
        private const val MAX_GYRO_ROTATION_STILLNESS = 0.08f

        // Batch latency 50,000 microseconds (50ms) for real-time hardware FIFO delivery.
        private const val BATCH_LATENCY_US = 50_000

        private val _isRunning = MutableStateFlow(false)
        val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

        private val _isFlippedDown = MutableStateFlow(false)
        val isFlippedDown: StateFlow<Boolean> = _isFlippedDown.asStateFlow()

        private val _isDndActive = MutableStateFlow(false)
        val isDndActive: StateFlow<Boolean> = _isDndActive.asStateFlow()
    }
}
