package com.example.f2shhh

import android.app.Activity
import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import android.content.res.Configuration
import kotlin.math.roundToInt
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

private const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"
private const val PREFS_NAME = "flip_to_shhh_prefs"
private const val KEY_AUTO_START_BOOT = "auto_start_on_boot"
private const val KEY_AUTO_LOCK_SCREEN = "auto_lock_screen"

// ════════════════════════════════════════════════════════════════════════
// Samsung One UI Typography Standard Specs
// ════════════════════════════════════════════════════════════════════════

object OneUiTypography {
    val TitleLarge = TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.sp
    )
    val HeroTitle = TextStyle(
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.2).sp
    )
    val SectionHeader = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.5.sp
    )
    val ItemTitle = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.1).sp
    )
    val ItemSubtitle = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.sp
    )
    val ButtonText = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.sp
    )
}

// ════════════════════════════════════════════════════════════════════════
// Haptic Preview Helper
// ════════════════════════════════════════════════════════════════════════

fun playHapticPreview(context: Context, mode: Int) {
    try {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vm.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        if (!vibrator.hasVibrator()) return

        when (mode) {
            0 -> { // Double pulse: Solid & Distinct "咚 - 咚"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                    vibrator.areAllPrimitivesSupported(VibrationEffect.Composition.PRIMITIVE_THUD)
                ) {
                    val composition = VibrationEffect.startComposition()
                        .addPrimitive(VibrationEffect.Composition.PRIMITIVE_THUD, 1.0f)
                        .addPrimitive(VibrationEffect.Composition.PRIMITIVE_THUD, 1.0f, 65)
                        .compose()
                    vibrator.vibrate(composition)
                } else {
                    val timings = longArrayOf(0, 28, 65, 40)
                    val amplitudes = intArrayOf(0, 255, 0, 255)
                    vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                }
            }
            1 -> { // Single touch: Solid "咚"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                    vibrator.areAllPrimitivesSupported(VibrationEffect.Composition.PRIMITIVE_THUD)
                ) {
                    val composition = VibrationEffect.startComposition()
                        .addPrimitive(VibrationEffect.Composition.PRIMITIVE_THUD, 1.0f)
                        .compose()
                    vibrator.vibrate(composition)
                } else {
                    val timings = longArrayOf(0, 35)
                    val amplitudes = intArrayOf(0, 255)
                    vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                }
            }
            2 -> { // Off
                // Silent/no haptic
            }
        }
    } catch (e: Exception) {
        Log.e("HapticPreview", "Error playing preview haptic", e)
    }
}

// ════════════════════════════════════════════════════════════════════════
// AppStrings i18n Provider (Simplified, Traditional, English)
// ════════════════════════════════════════════════════════════════════════

object AppStrings {
    fun get(context: Context, key: String, langMode: Int): String {
        val isTrad: Boolean
        val isEng: Boolean

        if (langMode == 0) {
            val locale = context.resources.configuration.locales.get(0)
            val lang = locale.language
            val country = locale.country
            if (lang.startsWith("zh")) {
                isEng = false
                isTrad = country.equals("TW", ignoreCase = true) ||
                         country.equals("HK", ignoreCase = true) ||
                         country.equals("MO", ignoreCase = true)
            } else {
                isEng = true
                isTrad = false
            }
        } else {
            isEng = (langMode == 3)
            isTrad = (langMode == 2)
        }

        return when (key) {
            "app_name" -> "Flip to Shhh"
            "master_title" -> if (isEng) "Flip to Shhh" else if (isTrad) "翻轉靜音" else "翻转静音"
            "header_sub_running" -> if (isEng) "Gesture detection ready · Flip phone to mute" else if (isTrad) "手勢檢測就緒 · 翻轉手機開啟勿擾" else "手势检测就绪 · 翻转手机开启勿扰"
            "header_sub_dnd" -> if (isEng) "Do Not Disturb active" else if (isTrad) "勿擾模式已開啟" else "勿扰模式已开启"
            "header_sub_stopped" -> if (isEng) "Gesture detection paused" else if (isTrad) "手勢檢測已暫停" else "手势检测已暂停"
            "status_not_running" -> if (isEng) "Service Stopped" else if (isTrad) "服務未啟動" else "服务未启动"
            "status_flipped_dnd" -> if (isEng) "Face Down · DND Active" else if (isTrad) "已翻轉 · 勿擾中" else "已翻转 · 勿扰中"
            "status_running" -> if (isEng) "Do Not Disturb" else if (isTrad) "勿擾模式" else "勿扰模式"
            "master_sub_running" -> if (isEng) "Listening · Flip face down to mute" else if (isTrad) "服務監聽中 · 翻轉開啟勿擾" else "服务监听中 · 翻转开启勿扰"
            "master_sub_stopped" -> if (isEng) "Tap to start gesture detection service" else if (isTrad) "點擊開啟手勢防抖服務" else "点击开启手势防抖服务"
            "hero_dnd_pill" -> if (isEng) "DND Active" else if (isTrad) "勿擾中" else "勿扰中"

            // 1. Core Permissions Panel Header & Fold Buttons
            "perm_header_ready", "core_permissions_ready" -> if (isEng) "Core permissions ready" else if (isTrad) "核心權限已就緒" else "核心权限已就绪"
            "perm_header_need" -> if (isEng) "Action required" else if (isTrad) "需要授權" else "需要授权"
            "perm_expand" -> if (isEng) "Expand" else if (isTrad) "展開" else "展开"
            "perm_collapse" -> if (isEng) "Collapse" else if (isTrad) "收起" else "收起"

            // 2. Permission Items & Statuses
            "perm_dnd_title" -> if (isEng) "Do Not Disturb Access" else if (isTrad) "勿擾模式權限" else "勿扰模式权限"
            "perm_dnd_granted" -> if (isEng) "Granted" else if (isTrad) "已授權" else "已授权"
            "perm_dnd_missing", "perm_dnd_required" -> if (isEng) "Tap to grant DND permission" else if (isTrad) "未授權 · 點擊前往設定" else "未授权 · 点击前往设置"
            "perm_dnd_required_banner" -> if (isEng) "DND permission required to enable mute" else if (isTrad) "勿擾模式權限未授予，無法開啟靜音" else "勿扰模式权限未授予，无法开启静音"
            "perm_dnd_desc" -> if (isEng) "Allow automatic DND & mute toggling" else if (isTrad) "允許自動切換勿擾與靜音" else "允许自动切换勿扰与静音"

            "perm_battery_title" -> if (isEng) "Background Battery Optimization" else if (isTrad) "後台電池優化" else "后台电池优化"
            "perm_battery_granted" -> if (isEng) "Battery optimization ignored" else if (isTrad) "已忽略電池優化" else "已忽略电池优化"
            "perm_battery_missing", "perm_battery_required", "perm_battery_desc" -> if (isEng) "Tap to disable optimization" else if (isTrad) "未忽略 · 點擊允許後台運行" else "未忽略 · 点击允许后台运行"

            "perm_accessibility_title", "perm_access_title" -> if (isEng) "Accessibility Lock Service" else if (isTrad) "無障礙鎖屏服務" else "无障碍锁屏服务"
            "perm_accessibility_granted", "perm_access_granted" -> if (isEng) "Granted" else if (isTrad) "已授權" else "已授权"
            "perm_accessibility_missing", "perm_access_required", "perm_access_desc" -> if (isEng) "Tap to enable accessibility" else if (isTrad) "未授權 · 點擊開啟無障礙" else "未授权 · 点击开启无障碍"

            "perm_notification_title", "perm_notif_optional_title" -> if (isEng) "Notification Access" else if (isTrad) "通知權限" else "通知权限"
            "perm_notification_granted" -> if (isEng) "Granted" else if (isTrad) "已授權" else "已授权"
            "perm_notification_missing", "perm_notif_optional_sub" -> if (isEng) "Disabled · Silent operation" else if (isTrad) "未開啟 · 適合極簡靜默運行" else "未开启 · 适合极简静默运行"
            "perm_notif_sub" -> if (isEng) "Display foreground service notification" else if (isTrad) "顯示前台服務運行狀態" else "显示前台服务运行状态"
            "grant_btn" -> if (isEng) "Grant" else if (isTrad) "去授權" else "去授权"

            // 3. Settings Panel & Options
            "setting_title", "settings_title" -> if (isEng) "Settings" else if (isTrad) "設定" else "设置"
            "setting_autostart", "autostart_title" -> if (isEng) "Auto-start on Boot" else if (isTrad) "開機自動啟動" else "开机自动启动"
            "setting_autostart_sub", "autostart_sub" -> if (isEng) "Start service automatically after device reboots" else if (isTrad) "裝置開機後自動拉起服務" else "设备开机后自动拉起服务"
            "setting_lock", "autolock_title" -> if (isEng) "Flip to Lock Screen" else if (isTrad) "翻轉自動鎖屏" else "翻转自动锁屏"
            "setting_lock_sub", "autolock_sub" -> if (isEng) "Lock screen simultaneously when muted" else if (isTrad) "翻轉開啟勿擾時同步熄屏鎖屏" else "翻转开启勿扰时同步熄屏锁屏"
            "setting_language", "lang_title" -> if (isEng) "Language" else if (isTrad) "語言" else "语言"
            "setting_about", "about_app_title" -> "Flip to Shhh"

            "group_response" -> if (isEng) "Response" else if (isTrad) "響應" else "响应"
            "group_appearance" -> if (isEng) "Appearance" else if (isTrad) "外觀" else "外观"
            "group_about" -> if (isEng) "About" else if (isTrad) "關於" else "关于"

            "haptic_title" -> if (isEng) "Haptic Feedback" else if (isTrad) "震動觸感反饋" else "震动触感反馈"
            "haptic_double" -> if (isEng) "Double Pulse" else if (isTrad) "雙脈衝" else "双脉冲"
            "haptic_single" -> if (isEng) "Single Tick" else if (isTrad) "輕觸" else "轻触"
            "haptic_off" -> if (isEng) "Off" else if (isTrad) "關閉" else "关闭"

            "advanced_title" -> if (isEng) "Advanced Settings" else if (isTrad) "高級設定" else "高级设置"
            "debounce_title" -> if (isEng) "Flip-down Debounce" else if (isTrad) "翻轉等待時間" else "翻转等待时间"

            "theme_title" -> if (isEng) "Theme Mode" else if (isTrad) "外觀與主題" else "外观与主题"
            "sys_default" -> if (isEng) "System" else if (isTrad) "跟隨系統" else "跟随系统"
            "theme_dark" -> if (isEng) "Dark" else if (isTrad) "深色" else "深色"
            "theme_light" -> if (isEng) "Light" else if (isTrad) "淺色" else "浅色"

            "lang_sim_cn" -> "简体中文"
            "lang_trad_cn" -> "繁體中文"
            "lang_english" -> "English"

            // 4. About Screen
            "nav_back" -> if (isEng) "Back" else if (isTrad) "返回" else "返回"
            "about_developer" -> if (isEng) "Customized for Samsung Galaxy S" else if (isTrad) "為 Samsung Galaxy S 定製" else "为三星 Galaxy S 定制"
            "about_privacy_title" -> if (isEng) "Privacy" else if (isTrad) "隱私承諾" else "隐私承诺"
            "about_privacy_body" -> if (isEng) "Fully offline, zero data collected." else if (isTrad) "完全離線，資料零收集。" else "完全离线，数据零收集。"
            "about_license_title" -> if (isEng) "Open Source License" else if (isTrad) "開源許可" else "开源许可"
            "about_license_body" -> if (isEng) "Apache License 2.0" else if (isTrad) "Apache License 2.0" else "Apache License 2.0"

            "onboarding_welcome_title" -> if (isEng) "Flip to Shhh" else if (isTrad) "翻轉靜音" else "翻转静音"
            "onboarding_welcome_desc" -> if (isEng) "Place your phone face down to automatically enable Do Not Disturb & mute.\nDesigned for Samsung Galaxy, ultra-low power sensor solution."
                                          else if (isTrad) "將手機翻轉面朝下放置，自動開啟勿擾與靜音。\n專為三星旗艦打造，超低功耗感應器方案。"
                                          else "将手机翻转面朝下放置，自动开启勿扰与静音。\n专为三星旗舰打造，超低功耗传感器方案。"
            "onboarding_swipe_hint" -> if (isEng) "Swipe left to set permissions" else if (isTrad) "向左滑動設定權限" else "向左滑动设置权限"
            "onboarding_perm_title" -> if (isEng) "Set Permissions" else if (isTrad) "設定權限" else "设置权限"
            "onboarding_perm_desc" -> if (isEng) "Grant the following permissions to ensure normal operation"
                                        else if (isTrad) "開啟以下權限以確保功能正常運行"
                                        else "开启以下权限以确保功能正常运行"
            "onboarding_start_btn" -> if (isEng) "Get Started" else if (isTrad) "開始使用" else "开始使用"
            "onboarding_setup_required_btn" -> if (isEng) "Please set required permissions" else if (isTrad) "請先完成權限設定" else "请先完成权限设置"

            else -> key
        }
    }
}

// ════════════════════════════════════════════════════════════════════════
// Activity
// ════════════════════════════════════════════════════════════════════════

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val prefs = remember {
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            }
            var themeMode by remember { mutableStateOf(prefs.getInt("theme_mode", 0)) }

            FlipToShhhTheme(themeMode = themeMode) {
                var onboardingComplete by remember {
                    mutableStateOf(prefs.getBoolean(KEY_ONBOARDING_COMPLETE, false))
                }

                AnimatedContent(
                    targetState = onboardingComplete,
                    label = "onboarding",
                    transitionSpec = {
                        fadeIn(tween(300)) togetherWith fadeOut(tween(200))
                    }
                ) { done ->
                    if (!done) {
                        OnboardingScreen(
                            onComplete = {
                                prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETE, true).apply()
                                onboardingComplete = true
                            }
                        )
                    } else {
                        LaunchedEffect(Unit) {
                            if (checkDndPermission(context) && !FlipToShhhService.isRunning.value) {
                                startFlipService(context)
                            }
                        }
                        FlipToShhhScreen(
                            onThemeModeChanged = { newTheme ->
                                themeMode = newTheme
                            }
                        )
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════
// Theme — One UI inspired scheme
// ════════════════════════════════════════════════════════════════════════

private val CrispLightColorScheme = lightColorScheme(
    primary = Color(0xFF1E56A0),            // One UI Royal Blue
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E4FF),
    onPrimaryContainer = Color(0xFF001B3D),
    secondary = Color(0xFF475569),          // Slate gray
    onSecondary = Color.White,
    background = Color(0xFFFAFAFA),         // One UI light porcelain background (#FAFAFA)
    onBackground = Color(0xFF0F172A),       // Deep obsidian text (#0F172A)
    surface = Color(0xFFFFFFFF),            // Pure white card surface (#FFFFFF)
    onSurface = Color(0xFF0F172A),
    surfaceContainerLow = Color(0xFFFFFFFF),// Pure white card container
    surfaceContainerHigh = Color(0xFFF1F5F9),// Light slate container for pills/buttons
    surfaceContainerHighest = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF475569),   // Slate gray
    outline = Color(0xFFE2E8F0),
    outlineVariant = Color(0xFFE2E8F0)
)

private val OneUiDarkColorScheme = darkColorScheme(
    primary = Color(0xFFA8C8FF),
    onPrimary = Color(0xFF003062),
    primaryContainer = Color(0xFF004689),
    onPrimaryContainer = Color(0xFFD6E3FF),
    secondary = Color(0xFFBBC7DB),
    onSecondary = Color(0xFF253140),
    background = Color(0xFF111318),
    onBackground = Color(0xFFE2E2E9),
    surface = Color(0xFF191C22),
    onSurface = Color(0xFFE2E2E9),
    surfaceContainerLow = Color(0xFF1D2026),
    surfaceContainerHigh = Color(0xFF282B32),
    surfaceContainerHighest = Color(0xFF33363E),
    onSurfaceVariant = Color(0xFFC3C6CF),
    outline = Color(0xFF8D9199),
    outlineVariant = Color(0xFF43474E)
)

@Composable
fun FlipToShhhTheme(
    themeMode: Int = 0,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val darkTheme = when (themeMode) {
        1 -> true
        2 -> false
        else -> isSystemInDarkTheme()
    }

    val colorScheme = remember(darkTheme, context) {
        when {
            darkTheme -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) dynamicDarkColorScheme(context) else OneUiDarkColorScheme
            }
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) dynamicLightColorScheme(context) else CrispLightColorScheme
            }
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode && context is Activity) {
        SideEffect {
            val window = context.window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

// ════════════════════════════════════════════════════════════════════════
// Onboarding
// ════════════════════════════════════════════════════════════════════════

@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val prefs = remember { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }
    val languageMode = prefs.getInt("language_mode", 0)

    val pagerState = rememberPagerState(pageCount = { 2 })

    var hasDndPermission by remember { mutableStateOf(checkDndPermission(context)) }
    var isIgnoringBatteryOptimization by remember { mutableStateOf(checkBatteryOptimization(context)) }
    var hasNotificationPermission by remember { mutableStateOf(checkNotificationPermission(context)) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasDndPermission = checkDndPermission(context)
                isIgnoringBatteryOptimization = checkBatteryOptimization(context)
                hasNotificationPermission = checkNotificationPermission(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val allGranted = hasDndPermission

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> OnboardingWelcomePage(languageMode = languageMode)
                1 -> OnboardingPermissionsPage(
                    hasDndPermission = hasDndPermission,
                    isIgnoringBatteryOptimization = isIgnoringBatteryOptimization,
                    hasNotificationPermission = hasNotificationPermission,
                    onGrantDnd = { openDndPermissionSettings(context) },
                    onRequestBatteryOptimization = { requestIgnoreBatteryOptimization(context) },
                    onRequestNotificationPermission = { requestNotificationPermission(context) },
                    allGranted = allGranted,
                    languageMode = languageMode,
                    onComplete = onComplete
                )
            }
        }

        // Page Indicator & Swipe Hint
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (pagerState.currentPage == 0) {
                Text(
                    text = AppStrings.get(context, "onboarding_swipe_hint", languageMode) + " →",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                )
            } else {
                Spacer(modifier = Modifier.height(18.dp))
            }

            // 2 Dots Indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(2) { index ->
                    val isSelected = pagerState.currentPage == index
                    val dotColor by animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                        label = "dotColor"
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(dotColor)
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingWelcomePage(languageMode: Int) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = AppIcons.DoNotDisturbOn,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = AppStrings.get(context, "onboarding_welcome_title", languageMode),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = AppStrings.get(context, "onboarding_welcome_desc", languageMode),
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}

@Composable
fun OnboardingPermissionsPage(
    hasDndPermission: Boolean,
    isIgnoringBatteryOptimization: Boolean,
    hasNotificationPermission: Boolean,
    onGrantDnd: () -> Unit,
    onRequestBatteryOptimization: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
    allGranted: Boolean,
    languageMode: Int,
    onComplete: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = AppStrings.get(context, "onboarding_perm_title", languageMode),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = AppStrings.get(context, "onboarding_perm_desc", languageMode),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        OnboardingPermissionItem(
            title = AppStrings.get(context, "perm_dnd_title", languageMode),
            desc = AppStrings.get(context, "perm_dnd_desc", languageMode),
            isGranted = hasDndPermission,
            languageMode = languageMode,
            onAction = onGrantDnd
        )
        Spacer(modifier = Modifier.height(12.dp))
        OnboardingPermissionItem(
            title = AppStrings.get(context, "perm_battery_title", languageMode),
            desc = AppStrings.get(context, "perm_battery_desc", languageMode),
            isGranted = isIgnoringBatteryOptimization,
            languageMode = languageMode,
            onAction = onRequestBatteryOptimization
        )
        Spacer(modifier = Modifier.height(12.dp))
        OnboardingPermissionItem(
            title = AppStrings.get(context, "perm_notif_optional_title", languageMode),
            desc = AppStrings.get(context, "perm_notif_optional_sub", languageMode),
            isGranted = hasNotificationPermission,
            languageMode = languageMode,
            onAction = onRequestNotificationPermission
        )

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = onComplete,
            enabled = allGranted,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(vertical = 14.dp)
        ) {
            Text(
                text = if (allGranted) AppStrings.get(context, "onboarding_start_btn", languageMode) else AppStrings.get(context, "onboarding_setup_required_btn", languageMode),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun OnboardingPermissionItem(
    title: String,
    desc: String,
    isGranted: Boolean,
    languageMode: Int,
    onAction: () -> Unit
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val warningColor = if (isDark) Color(0xFFFBBF24) else Color(0xFFD97706)
    val warningBgColor = if (isDark) Color(0xFF78350F).copy(alpha = 0.4f) else Color(0xFFFFFBEB)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isGranted) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        else warningBgColor
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isGranted) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (isGranted) MaterialTheme.colorScheme.primary else warningColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                Text(text = desc, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (!isGranted) {
                TextButton(onClick = onAction) { Text(AppStrings.get(context, "grant_btn", languageMode), fontSize = 13.sp) }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════
// Main Screen
// ════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlipToShhhScreen(
    onThemeModeChanged: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val prefs = remember { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    var languageMode by remember { mutableStateOf(prefs.getInt("language_mode", 0)) }

    val isServiceRunning by FlipToShhhService.isRunning.collectAsState()
    val isFlippedDown by FlipToShhhService.isFlippedDown.collectAsState()
    val isDndActive by FlipToShhhService.isDndActive.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    var hasDndPermission by remember { mutableStateOf(true) }
    var isIgnoringBatteryOptimization by remember { mutableStateOf(true) }
    var hasNotificationPermission by remember { mutableStateOf(true) }
    var hasAccessibilityPermission by remember { mutableStateOf(true) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    var showAboutScreen by remember { mutableStateOf(false) }

    val updatePermissions: suspend () -> Unit = remember(context) {
        {
            val dnd = withContext(Dispatchers.IO) { checkDndPermission(context) }
            val batt = withContext(Dispatchers.IO) { checkBatteryOptimization(context) }
            val notif = withContext(Dispatchers.IO) { checkNotificationPermission(context) }
            val access = withContext(Dispatchers.IO) { FlipLockAccessibilityService.isAccessibilityServiceEnabled(context) }
            hasDndPermission = dnd
            isIgnoringBatteryOptimization = batt
            hasNotificationPermission = notif
            hasAccessibilityPermission = access
        }
    }

    LaunchedEffect(Unit) {
        updatePermissions()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                coroutineScope.launch {
                    updatePermissions()
                    languageMode = prefs.getInt("language_mode", 0)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text(AppStrings.get(context, "perm_dnd_title", languageMode), style = OneUiTypography.ItemTitle) },
            text = { Text(AppStrings.get(context, "perm_dnd_required", languageMode), style = OneUiTypography.ItemSubtitle) },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionDialog = false
                    openDndPermissionSettings(context)
                }) { Text(AppStrings.get(context, "grant_btn", languageMode), style = OneUiTypography.ButtonText) }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) { Text("取消", style = OneUiTypography.ButtonText) }
            }
        )
    }

    AnimatedContent(
        targetState = showAboutScreen,
        label = "about_screen",
        transitionSpec = {
            if (targetState) {
                (slideInVertically(animationSpec = tween(350, easing = FastOutSlowInEasing)) { fullHeight -> fullHeight } + fadeIn(tween(350, easing = FastOutSlowInEasing)))
                    .togetherWith(fadeOut(tween(250, easing = FastOutSlowInEasing)))
            } else {
                fadeIn(tween(300, easing = FastOutSlowInEasing))
                    .togetherWith(slideOutVertically(animationSpec = tween(300, easing = FastOutSlowInEasing)) { fullHeight -> fullHeight } + fadeOut(tween(250, easing = FastOutSlowInEasing)))
            }
        }
    ) { showAbout ->
        if (showAbout) {
            AboutScreen(
                languageMode = languageMode,
                onBack = { showAboutScreen = false }
            )
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 20.dp)
                            .consumeWindowInsets(WindowInsets.navigationBars)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // 1. One UI Large Title Header
                        OneUiHeader(
                            isRunning = isServiceRunning,
                            isDndActive = isDndActive,
                            languageMode = languageMode,
                            onOpenSettings = { showSettingsSheet = true }
                        )

                        // 2. Interactive Hero Status Card
                        HeroServiceCard(
                            isRunning = isServiceRunning,
                            isFlippedDown = isFlippedDown,
                            isDndActive = isDndActive,
                            languageMode = languageMode,
                            onToggleService = { enable ->
                                if (enable) {
                                    if (!hasDndPermission) {
                                        showPermissionDialog = true
                                    } else {
                                        startFlipService(context)
                                    }
                                } else {
                                    stopFlipService(context)
                                }
                            }
                        )

                        // 3. Feature Settings Card (Auto-Start & Auto-Lock Screen)
                        FeatureSettingsCard(
                            languageMode = languageMode,
                            onRequestAccessibilityPermission = { openAccessibilitySettings(context) }
                        )

                        // 4. System Permissions Group Card
                        PermissionsCard(
                            hasDndPermission = hasDndPermission,
                            isIgnoringBatteryOptimization = isIgnoringBatteryOptimization,
                            hasNotificationPermission = hasNotificationPermission,
                            hasAccessibilityPermission = hasAccessibilityPermission,
                            languageMode = languageMode,
                            onGrantDnd = { openDndPermissionSettings(context) },
                            onRequestBatteryOptimization = { requestIgnoreBatteryOptimization(context) },
                            onRequestNotificationPermission = { requestNotificationPermission(context) },
                            onGrantAccessibility = { openAccessibilitySettings(context) }
                        )

                        Spacer(modifier = Modifier.height(28.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()))
                    }
                }

                // Settings sheet overlay — rendered AFTER Scaffold so it covers everything
                SettingsBottomSheet(
                    visible = showSettingsSheet,
                    onDismiss = {
                        showSettingsSheet = false
                        languageMode = prefs.getInt("language_mode", 0)
                        onThemeModeChanged(prefs.getInt("theme_mode", 0))
                    },
                    onShowAbout = {
                        showSettingsSheet = false
                        showAboutScreen = true
                    },
                    onThemeModeSelected = { newTheme ->
                        onThemeModeChanged(newTheme)
                    }
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════
// One UI Header — Large title & Status Subtitle
// ════════════════════════════════════════════════════════════════════════

@Composable
fun OneUiHeader(
    isRunning: Boolean,
    isDndActive: Boolean,
    languageMode: Int,
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 36.dp, bottom = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Flip to Shhh",
                style = OneUiTypography.TitleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = AppStrings.get(context, "settings_title", languageMode),
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════
// Interactive Hero Status Card
// ════════════════════════════════════════════════════════════════════════

@Composable
fun HeroServiceCard(
    isRunning: Boolean,
    isFlippedDown: Boolean,
    isDndActive: Boolean,
    languageMode: Int,
    onToggleService: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    val heroActive = isRunning && isFlippedDown && isDndActive

    val iconBgColor by animateColorAsState(
        targetValue = when {
            heroActive -> MaterialTheme.colorScheme.primary
            isRunning -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.surfaceContainerHigh
        },
        label = "heroIconBg"
    )

    val iconTint by animateColorAsState(
        targetValue = when {
            heroActive -> MaterialTheme.colorScheme.onPrimary
            isRunning -> MaterialTheme.colorScheme.onPrimaryContainer
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        },
        label = "heroIconTint"
    )

    val heroTitle = when {
        !isRunning -> AppStrings.get(context, "status_not_running", languageMode)
        heroActive -> AppStrings.get(context, "status_flipped_dnd", languageMode)
        else -> AppStrings.get(context, "master_title", languageMode)
    }

    val heroSubtitle = when {
        !isRunning -> AppStrings.get(context, "master_sub_stopped", languageMode)
        heroActive -> if (languageMode == 2) "手機面朝下 · 來電與通知已靜音" else if (languageMode == 3) "Face down · Calls & notifications muted" else "手机面朝下 · 来电与通知已静音"
        else -> AppStrings.get(context, "master_sub_running", languageMode)
    }

    val heroIcon = when {
        !isRunning -> AppIcons.Smartphone
        heroActive -> AppIcons.Bedtime
        else -> AppIcons.DoNotDisturbOn
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(iconBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = heroIcon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = heroTitle,
                            style = OneUiTypography.HeroTitle,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (heroActive) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = AppStrings.get(context, "hero_dnd_pill", languageMode),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = heroSubtitle,
                        style = OneUiTypography.ItemSubtitle,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Switch(
                checked = isRunning,
                onCheckedChange = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onToggleService(it)
                }
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════════
// Feature Settings Card — Auto-Start & Auto-Lock Grouped
// ════════════════════════════════════════════════════════════════════════

@Composable
fun FeatureSettingsCard(
    languageMode: Int,
    onRequestAccessibilityPermission: () -> Unit = {}
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val prefs = remember { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }
    var autoStartEnabled by remember { mutableStateOf(prefs.getBoolean(KEY_AUTO_START_BOOT, true)) }
    var autoLockEnabled by remember {
        mutableStateOf(
            prefs.getBoolean(
                KEY_AUTO_LOCK_SCREEN,
                FlipLockAccessibilityService.isAccessibilityServiceEnabled(context)
            )
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Auto-Start Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = AppIcons.PowerSettingsNew,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = AppStrings.get(context, "autostart_title", languageMode),
                            style = OneUiTypography.ItemTitle,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = AppStrings.get(context, "autostart_sub", languageMode),
                            style = OneUiTypography.ItemSubtitle,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Switch(
                    checked = autoStartEnabled,
                    onCheckedChange = { enabled ->
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        autoStartEnabled = enabled
                        prefs.edit().putBoolean(KEY_AUTO_START_BOOT, enabled).apply()
                    }
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // 2. Auto-Lock Screen Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = AppIcons.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = AppStrings.get(context, "autolock_title", languageMode),
                            style = OneUiTypography.ItemTitle,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = AppStrings.get(context, "autolock_sub", languageMode),
                            style = OneUiTypography.ItemSubtitle,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Switch(
                    checked = autoLockEnabled,
                    onCheckedChange = { enabled ->
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        autoLockEnabled = enabled
                        prefs.edit().putBoolean(KEY_AUTO_LOCK_SCREEN, enabled).apply()
                        if (enabled && !FlipLockAccessibilityService.isAccessibilityServiceEnabled(context)) {
                            onRequestAccessibilityPermission()
                        }
                    }
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════
// Permissions Group Card — Core vs Optional Separation
// ════════════════════════════════════════════════════════════════════════

data class PermissionStatusItem(
    val id: String,
    val titleKey: String,
    val descKey: String,
    val isGranted: Boolean,
    val isOptional: Boolean,
    val icon: ImageVector,
    val onAction: () -> Unit
)

@Composable
fun PermissionsCard(
    hasDndPermission: Boolean,
    isIgnoringBatteryOptimization: Boolean,
    hasNotificationPermission: Boolean,
    hasAccessibilityPermission: Boolean,
    languageMode: Int,
    onGrantDnd: () -> Unit,
    onRequestBatteryOptimization: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
    onGrantAccessibility: () -> Unit
) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(false) }

    val coreReady = hasDndPermission

    val isDark = isSystemInDarkTheme()
    val greenColor = if (isDark) Color(0xFF34D399) else Color(0xFF059669)
    val amberBgColor = if (isDark) Color(0xFF78350F).copy(alpha = 0.4f) else Color(0xFFFFFBEB)
    val amberTextColor = if (isDark) Color(0xFFFBBF24) else Color(0xFFD97706)

    val items = remember(hasDndPermission, isIgnoringBatteryOptimization, hasNotificationPermission, hasAccessibilityPermission, languageMode) {
        listOf(
            PermissionStatusItem(
                id = "dnd",
                titleKey = "perm_dnd_title",
                descKey = if (hasDndPermission) "perm_dnd_granted" else "perm_dnd_required",
                isGranted = hasDndPermission,
                isOptional = false,
                icon = AppIcons.NotificationsActive,
                onAction = onGrantDnd
            ),
            PermissionStatusItem(
                id = "battery",
                titleKey = "perm_battery_title",
                descKey = if (isIgnoringBatteryOptimization) "perm_battery_granted" else "perm_battery_desc",
                isGranted = isIgnoringBatteryOptimization,
                isOptional = true,
                icon = AppIcons.BatterySaver,
                onAction = onRequestBatteryOptimization
            ),
            PermissionStatusItem(
                id = "access",
                titleKey = "perm_access_title",
                descKey = if (hasAccessibilityPermission) "perm_access_granted" else "perm_access_desc",
                isGranted = hasAccessibilityPermission,
                isOptional = true,
                icon = AppIcons.Lock,
                onAction = onGrantAccessibility
            ),
            PermissionStatusItem(
                id = "notif",
                titleKey = "perm_notif_optional_title",
                descKey = if (hasNotificationPermission) "perm_notif_sub" else "perm_notif_optional_sub",
                isGranted = hasNotificationPermission,
                isOptional = true,
                icon = AppIcons.Notifications,
                onAction = onRequestNotificationPermission
            )
        )
    }

    if (coreReady && !isExpanded) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable { isExpanded = true }
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = AppIcons.CheckCircle,
                contentDescription = null,
                tint = greenColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = AppStrings.get(context, "perm_header_ready", languageMode),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = greenColor
            )
        }
    } else {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                if (!coreReady) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(amberBgColor)
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = AppIcons.Warning,
                            contentDescription = null,
                            tint = amberTextColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = AppStrings.get(context, "perm_dnd_required_banner", languageMode),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = amberTextColor
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isExpanded = false }
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = AppIcons.CheckCircle,
                                contentDescription = null,
                                tint = greenColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = AppStrings.get(context, "perm_header_ready", languageMode),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = greenColor
                            )
                        }
                        Text(
                            text = AppStrings.get(context, "perm_collapse", languageMode),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items.forEachIndexed { index, permItem ->
                        if (index > 0) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        }
                        PermissionItemRow(
                            title = AppStrings.get(context, permItem.titleKey, languageMode),
                            subtitle = AppStrings.get(context, permItem.descKey, languageMode),
                            isGranted = permItem.isGranted,
                            isOptional = permItem.isOptional,
                            icon = permItem.icon,
                            languageMode = languageMode,
                            onAction = permItem.onAction
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionItemRow(
    title: String,
    subtitle: String,
    isGranted: Boolean,
    isOptional: Boolean = false,
    icon: ImageVector,
    languageMode: Int,
    onAction: () -> Unit
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val greenColor = if (isDark) Color(0xFF34D399) else Color(0xFF059669)
    val warningColor = if (isDark) Color(0xFFFBBF24) else Color(0xFFD97706)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isGranted) MaterialTheme.colorScheme.onSurfaceVariant else if (isOptional) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f) else warningColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = OneUiTypography.ItemTitle, color = MaterialTheme.colorScheme.onSurface)
            Text(
                text = subtitle,
                style = OneUiTypography.ItemSubtitle,
                color = if (isGranted) greenColor else if (isOptional) MaterialTheme.colorScheme.onSurfaceVariant else warningColor
            )
        }
        if (!isGranted) {
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onAction,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                colors = if (isOptional) ButtonDefaults.filledTonalButtonColors() else ButtonDefaults.buttonColors()
            ) {
                Text(AppStrings.get(context, "grant_btn", languageMode), style = OneUiTypography.ButtonText)
            }
        } else {
            Icon(
                imageVector = AppIcons.CheckCircle,
                contentDescription = "Granted",
                tint = greenColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════════
// Helpers
// ════════════════════════════════════════════════════════════════════════

private fun checkDndPermission(context: Context): Boolean {
    val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    return nm.isNotificationPolicyAccessGranted
}

private fun openDndPermissionSettings(context: Context) {
    context.startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
}

private fun checkBatteryOptimization(context: Context): Boolean {
    val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    return pm.isIgnoringBatteryOptimizations(context.packageName)
}

private fun requestIgnoreBatteryOptimization(context: Context) {
    try {
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
            data = Uri.parse("package:${context.packageName}")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        context.startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
    }
}

private fun checkNotificationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context, Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED
}

private fun requestNotificationPermission(context: Context) {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    }
    context.startActivity(intent)
}

private fun openAccessibilitySettings(context: Context) {
    context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
}

// ════════════════════════════════════════════════════════════════════════
// Settings Modal Bottom Sheet & About Dialog
// ════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> OptionPickerSheet(
    title: String,
    options: List<Pair<T, String>>,
    selectedKey: T,
    onSelect: (T) -> Unit,
    onDismiss: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = { BottomSheetDefaults.DragHandle() },
        contentWindowInsets = { WindowInsets.navigationBars }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 10.dp, bottom = 18.dp, start = 8.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                options.forEach { (value, label) ->
                    val isSelected = selectedKey == value
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onSelect(value)
                                onDismiss()
                            }
                            .padding(horizontal = 8.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary,
                                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = label,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    onShowAbout: () -> Unit,
    onThemeModeSelected: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val prefs = remember { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    var debounceMs by remember { mutableStateOf(prefs.getLong("debounce_ms", 2000L)) }
    var hapticMode by remember { mutableStateOf(prefs.getInt("haptic_mode", 0)) }
    var themeMode by remember { mutableStateOf(prefs.getInt("theme_mode", 0)) }
    var languageMode by remember { mutableStateOf(prefs.getInt("language_mode", 0)) }

    var showDebouncePicker by remember { mutableStateOf(false) }
    var showLanguagePicker by remember { mutableStateOf(false) }

    // Use screen height as the "hidden" offset
    val screenHeightPx = with(density) {
        context.resources.configuration.screenHeightDp.dp.toPx()
    }
    val offsetY = remember { Animatable(screenHeightPx) }

    // Animate show/hide
    LaunchedEffect(visible) {
        if (visible) {
            offsetY.animateTo(0f, tween(350, easing = FastOutSlowInEasing))
        } else {
            offsetY.animateTo(screenHeightPx, tween(280, easing = FastOutSlowInEasing))
        }
    }

    val progress = (1f - offsetY.value / screenHeightPx).coerceIn(0f, 1f)
    val isShowing = progress > 0.01f

    BackHandler(enabled = visible) {
        scope.launch {
            offsetY.animateTo(screenHeightPx, tween(280, easing = FastOutSlowInEasing))
            onDismiss()
        }
    }

    // Drag-to-dismiss
    val draggableState = rememberDraggableState { delta ->
        scope.launch {
            offsetY.snapTo((offsetY.value + delta).coerceAtLeast(0f))
        }
    }

    // Scrim — only rendered when visible
    if (isShowing) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.32f * progress))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    scope.launch {
                        offsetY.animateTo(screenHeightPx, tween(280, easing = FastOutSlowInEasing))
                        onDismiss()
                    }
                }
        )
    }

    // Sheet panel — ALWAYS in composition tree, positioned off-screen when hidden
    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset { IntOffset(0, offsetY.value.roundToInt()) },
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .draggable(
                    state = draggableState,
                    orientation = Orientation.Vertical,
                    onDragStopped = { velocity ->
                        if (offsetY.value > screenHeightPx * 0.25f || velocity > 800f) {
                            offsetY.animateTo(screenHeightPx, tween(280, easing = FastOutSlowInEasing))
                            onDismiss()
                        } else {
                            offsetY.animateTo(0f, tween(200, easing = FastOutSlowInEasing))
                        }
                    }
                ),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp)
                    .padding(top = 0.dp, bottom = 46.dp)
            ) {
                // MD3 Drag Handle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp, bottom = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(32.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                    )
                }

                // Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = AppStrings.get(context, "settings_title", languageMode),
                        style = OneUiTypography.TitleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Group 1: Response (响应)
                    Column {
                        SectionHeader(text = AppStrings.get(context, "group_response", languageMode))
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // 1. Haptic Feedback (Segmented)
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = AppStrings.get(context, "haptic_title", languageMode),
                                        style = OneUiTypography.ItemTitle,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    val hapticItems = remember(languageMode) {
                                        listOf(
                                            0 to AppStrings.get(context, "haptic_double", languageMode),
                                            1 to AppStrings.get(context, "haptic_single", languageMode),
                                            2 to AppStrings.get(context, "haptic_off", languageMode)
                                        )
                                    }
                                    SegmentedControlBar(
                                        items = hapticItems,
                                        selectedKey = hapticMode,
                                        onSelect = { mode ->
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            hapticMode = mode
                                            prefs.edit().putInt("haptic_mode", mode).apply()
                                            playHapticPreview(context, mode)
                                        }
                                    )
                                }

                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                                // 2. Debounce Time Row
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) { showDebouncePicker = true }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = AppStrings.get(context, "debounce_title", languageMode),
                                        style = OneUiTypography.ItemTitle,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        val debounceSecStr = when (debounceMs) {
                                            1000L -> "1"
                                            3000L -> "3"
                                            else -> "2"
                                        }
                                        val currentDebounceLabel = if (languageMode == 3) "${debounceSecStr}s" else "${debounceSecStr} 秒"
                                        Text(
                                            text = currentDebounceLabel,
                                            style = OneUiTypography.ItemSubtitle,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = AppIcons.ChevronRight,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Group 2: Appearance (外观)
                    Column {
                        SectionHeader(text = AppStrings.get(context, "group_appearance", languageMode))
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // 1. Theme Mode (Segmented)
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = AppStrings.get(context, "theme_title", languageMode),
                                        style = OneUiTypography.ItemTitle,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    val themeItems = remember(languageMode) {
                                        listOf(
                                            0 to AppStrings.get(context, "sys_default", languageMode),
                                            1 to AppStrings.get(context, "theme_dark", languageMode),
                                            2 to AppStrings.get(context, "theme_light", languageMode)
                                        )
                                    }
                                    SegmentedControlBar(
                                        items = themeItems,
                                        selectedKey = themeMode,
                                        onSelect = { mode ->
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            themeMode = mode
                                            prefs.edit().putInt("theme_mode", mode).apply()
                                            onThemeModeSelected(mode)
                                        }
                                    )
                                }

                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                                // 2. Language Picker Row
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) { showLanguagePicker = true }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = AppStrings.get(context, "lang_title", languageMode),
                                        style = OneUiTypography.ItemTitle,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        val currentLangLabel = when (languageMode) {
                                            1 -> AppStrings.get(context, "lang_sim_cn", languageMode)
                                            2 -> AppStrings.get(context, "lang_trad_cn", languageMode)
                                            3 -> AppStrings.get(context, "lang_english", languageMode)
                                            else -> AppStrings.get(context, "sys_default", languageMode)
                                        }
                                        Text(
                                            text = currentLangLabel,
                                            style = OneUiTypography.ItemSubtitle,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = AppIcons.ChevronRight,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Group 3: About (关于)
                    Column {
                        SectionHeader(text = AppStrings.get(context, "group_about", languageMode))
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onShowAbout()
                                },
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = AppIcons.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Text(
                                        text = AppStrings.get(context, "about_app_title", languageMode),
                                        style = OneUiTypography.ItemTitle,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Icon(
                                    imageVector = AppIcons.ChevronRight,
                                    contentDescription = "查看详情",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDebouncePicker) {
        val debounceOptions = remember(languageMode) {
            val isEng = (languageMode == 3)
            val isTrad = (languageMode == 2)
            listOf(
                1000L to (if (isEng) "1s" else if (isTrad) "1 秒" else "1 秒"),
                2000L to (if (isEng) "2s" else if (isTrad) "2 秒" else "2 秒"),
                3000L to (if (isEng) "3s" else if (isTrad) "3 秒" else "3 秒")
            )
        }
        OptionPickerSheet(
            title = AppStrings.get(context, "debounce_title", languageMode),
            options = debounceOptions,
            selectedKey = debounceMs,
            onSelect = { ms ->
                debounceMs = ms
                prefs.edit().putLong("debounce_ms", ms).apply()
            },
            onDismiss = { showDebouncePicker = false }
        )
    }

    if (showLanguagePicker) {
        val langOptions = remember(languageMode) {
            listOf(
                0 to AppStrings.get(context, "sys_default", languageMode),
                1 to AppStrings.get(context, "lang_sim_cn", languageMode),
                2 to AppStrings.get(context, "lang_trad_cn", languageMode),
                3 to AppStrings.get(context, "lang_english", languageMode)
            )
        }
        OptionPickerSheet(
            title = AppStrings.get(context, "lang_title", languageMode),
            options = langOptions,
            selectedKey = languageMode,
            onSelect = { mode ->
                languageMode = mode
                prefs.edit().putInt("language_mode", mode).apply()
            },
            onDismiss = { showLanguagePicker = false }
        )
    }
}

@Composable
fun SectionHeader(text: String) {
    Text(
        text = text,
        style = OneUiTypography.SectionHeader,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
fun <T> SegmentedControlBar(
    items: List<Pair<T, String>>,
    selectedKey: T,
    onSelect: (T) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items.forEach { (key, label) ->
                val isSelected = selectedKey == key
                val bgAnim by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                    animationSpec = tween(durationMillis = 150),
                    label = "segBg"
                )
                val textAnim by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                    animationSpec = tween(durationMillis = 150),
                    label = "segText"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(bgAnim)
                        .clickable { onSelect(key) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = OneUiTypography.ItemSubtitle,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = textAnim,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════
// About — One UI style full-screen page
// ════════════════════════════════════════════════════════════════════════

@Composable
fun AboutScreen(languageMode: Int, onBack: () -> Unit) {
    val context = LocalContext.current

    BackHandler { onBack() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        // 1. Top bar — back button + large title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = AppStrings.get(context, "nav_back", languageMode),
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = AppStrings.get(context, "about_app_title", languageMode),
                style = OneUiTypography.TitleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // 2. Brand section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = AppIcons.DoNotDisturbOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(44.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh
                ) {
                    Text(
                        text = "Release",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = AppStrings.get(context, "about_developer", languageMode),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Privacy & Open Source License card (combined card)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Row 1: Privacy
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = AppIcons.Shield,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = AppStrings.get(context, "about_privacy_title", languageMode),
                            style = OneUiTypography.ItemTitle,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = AppStrings.get(context, "about_privacy_body", languageMode),
                            style = OneUiTypography.ItemSubtitle,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                // Row 2: License
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = AppIcons.Code,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = AppStrings.get(context, "about_license_title", languageMode),
                            style = OneUiTypography.ItemTitle,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = AppStrings.get(context, "about_license_body", languageMode),
                            style = OneUiTypography.ItemSubtitle,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 4. Footer
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Copyright © 2026 Michael Zhang",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))
    }
}

private fun startFlipService(context: Context) {
    ContextCompat.startForegroundService(context, Intent(context, FlipToShhhService::class.java))
}

private fun stopFlipService(context: Context) {
    context.stopService(Intent(context, FlipToShhhService::class.java))
}
