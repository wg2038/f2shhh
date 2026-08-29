package com.example.f2shhh

import android.app.Activity
import android.app.NotificationManager
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


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

            "perm_dnd_title" -> if (isEng) "Do Not Disturb Access" else if (isTrad) "勿擾模式存取權限" else "勿扰模式权限"
            "perm_dnd_required" -> if (isEng) "Tap to grant DND access" else if (isTrad) "未授權 · 點擊前往設定" else "未授权 · 点击前往设置"
            "perm_dnd_required_banner" -> if (isEng) "DND permission required to enable mute" else if (isTrad) "勿擾模式權限未授予，無法開啟靜音" else "勿扰模式权限未授予，无法开启静音"
            "perm_dnd_desc" -> if (isEng) "Allow app to automatically toggle DND & mute" else if (isTrad) "允許應用程式自動開啟勿擾模式" else "允许应用自动开启勿扰模式"

            "perm_battery_title" -> if (isEng) "Background Battery Optimization" else if (isTrad) "背景電池最佳化" else "后台电池优化"
            "perm_battery_desc" -> if (isEng) "Keep service running stably in background" else if (isTrad) "允許背景無限制執行，避免被系統清除" else "允许后台无限制运行，避免被系统清理"

            "perm_access_title" -> if (isEng) "Accessibility Lock Service" else if (isTrad) "無障礙螢幕鎖定服務" else "无障碍锁屏服务"

            "grant_btn" -> if (isEng) "Grant" else if (isTrad) "去授權" else "去授权"

            // 3. Settings Panel & Options
            "settings_title" -> if (isEng) "Settings" else if (isTrad) "設定" else "设置"
            "autostart_title" -> if (isEng) "Auto-start on Boot" else if (isTrad) "開機自動啟動" else "开机自动启动"
            "autolock_title" -> if (isEng) "Flip to Lock Screen" else if (isTrad) "翻轉自動鎖定螢幕" else "翻转自动锁屏"
            "lang_title" -> if (isEng) "Language" else if (isTrad) "語言" else "语言"
            "setting_about", "about_app_title" -> if (isEng) "About" else if (isTrad) "關於" else "关于"

            "group_permissions" -> if (isEng) "Core Permissions" else if (isTrad) "核心權限" else "核心权限"
            "group_features" -> if (isEng) "Features & Behaviors" else if (isTrad) "功能與行為" else "功能与行为"
            "group_appearance" -> if (isEng) "Appearance & Language" else if (isTrad) "外觀與語言" else "外观与语言"
            "group_about" -> if (isEng) "About" else if (isTrad) "關於" else "关于"

            // 4. Hero Texts
            "hero_tap_to_start" -> if (isEng) "Tap to Start" else if (isTrad) "點擊啟動服務" else "点击启动服务"
            "hero_running_title" -> if (isEng) "Flip to Shhh Ready" else if (isTrad) "翻轉靜音已就緒" else "翻转静音已就绪"
            "hero_dnd_active_title" -> if (isEng) "Do Not Disturb Active" else if (isTrad) "勿擾模式已生效" else "勿扰模式已生效"

            "theme_title" -> if (isEng) "Theme Mode" else if (isTrad) "主題模式" else "外观与主题"
            "sys_default" -> if (isEng) "System" else if (isTrad) "跟隨系統" else "跟随系统"
            "theme_dark" -> if (isEng) "Dark" else if (isTrad) "深色" else "深色"
            "theme_light" -> if (isEng) "Light" else if (isTrad) "淺色" else "浅色"

            "lang_sim_cn" -> "简体中文"
            "lang_trad_cn" -> "繁體中文"
            "lang_english" -> "English"

            // 5. About Screen
            "nav_back" -> if (isEng) "Back" else if (isTrad) "返回" else "返回"
            "about_developer" -> if (isEng) "Flip to enable Do Not Disturb" else if (isTrad) "翻轉手機開啟勿擾模式" else "翻转手机开启勿扰模式"
            "about_privacy_title" -> if (isEng) "Privacy" else if (isTrad) "隱私承諾" else "隐私承诺"
            "about_privacy_body" -> if (isEng) "Offline · Zero data collected" else if (isTrad) "完全離線 · 零資料收集" else "完全离线 · 零数据收集"
            "about_license_title" -> if (isEng) "Open Source License" else if (isTrad) "開放原始碼授權" else "开源许可"
            "about_license_body" -> "MIT License"
            "about_version_title" -> if (isEng) "Version" else if (isTrad) "版本號" else "版本号"

            // 6. Onboarding Screen
            "onboarding_welcome_title" -> if (isEng) "Flip to Shhh" else if (isTrad) "翻轉靜音" else "翻转静音"
            "onboarding_welcome_desc" -> if (isEng) "Place your phone face down to automatically enable Do Not Disturb & mute.\nUltra-low power precision sensor solution."
                                          else if (isTrad) "將手機螢幕朝下放置，自動開啟勿擾與靜音。\n超低功耗高精度感應器方案。"
                                          else "将手机翻转面朝下放置，自动开启勿扰与静音。\n超低功耗高精度传感器方案。"
            "onboarding_swipe_hint" -> if (isEng) "Swipe left to set permissions" else if (isTrad) "向左滑動設定權限" else "向左滑动设置权限"
            "onboarding_perm_title" -> if (isEng) "Set Permissions" else if (isTrad) "設定權限" else "设置权限"
            "onboarding_perm_desc" -> if (isEng) "Grant the following permissions to ensure normal operation"
                                        else if (isTrad) "開啟以下權限以確保功能正常運作"
                                        else "开启以下权限以确保功能正常运行"
            "onboarding_start_btn" -> if (isEng) "Get Started" else if (isTrad) "開始使用" else "开始使用"
            "onboarding_setup_required_btn" -> if (isEng) "Please set required permissions" else if (isTrad) "請先完成權限設定" else "请先完成权限设置"
            "btn_cancel" -> if (isEng) "Cancel" else if (isTrad) "取消" else "取消"

            else -> key
        }
    }
}

// ════════════════════════════════════════════════════════════════════════
// Activity
// ════════════════════════════════════════════════════════════════════════

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val prefs = remember {
                context.getSharedPreferences(PrefsKeys.PREFS_NAME, Context.MODE_PRIVATE)
            }
            var themeMode by rememberSaveable { mutableStateOf(prefs.getInt(PrefsKeys.KEY_THEME_MODE, 0)) }
            var languageMode by rememberSaveable { mutableStateOf(prefs.getInt(PrefsKeys.KEY_LANGUAGE_MODE, 0)) }
            val notifPermissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { }

            FlipToShhhTheme(themeMode = themeMode) {
                var onboardingComplete by remember {
                    mutableStateOf(prefs.getBoolean(PrefsKeys.KEY_ONBOARDING_COMPLETE, false))
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
                            languageMode = languageMode,
                            onComplete = {
                                prefs.edit()
                                    .putBoolean(PrefsKeys.KEY_ONBOARDING_COMPLETE, true)
                                    .putBoolean(PrefsKeys.KEY_SERVICE_USER_ENABLED, true)
                                    .apply()
                                onboardingComplete = true
                            }
                        )
                    } else {
                        LaunchedEffect(Unit) {
                            // POST_NOTIFICATIONS is a runtime permission on API 33+ (this app's minSdk);
                            // without it the foreground service status notification is suppressed.
                            if (ContextCompat.checkSelfPermission(
                                    context, android.Manifest.permission.POST_NOTIFICATIONS
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                notifPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                            }
                            val isUserEnabled = prefs.getBoolean(PrefsKeys.KEY_SERVICE_USER_ENABLED, true)
                            if (isUserEnabled && checkDndPermission(context) && !FlipToShhhService.isRunning.value) {
                                startFlipService(context)
                            }
                        }
                        FlipToShhhScreen(
                            languageMode = languageMode,
                            onLanguageModeChanged = { newLang ->
                                languageMode = newLang
                            },
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

private fun extractSystemSeedColor(context: Context): Color? {
    // 1. Try reading system theme customization overlay settings (Samsung One UI, AOSP, Pixel)
    try {
        val themeJson = Settings.Secure.getString(
            context.contentResolver,
            "theme_customization_overlay_packages"
        )
        if (!themeJson.isNullOrEmpty()) {
            val json = JSONObject(themeJson)
            val hexColor = when {
                json.has("android.theme.customization.accent_color") ->
                    json.optString("android.theme.customization.accent_color")
                json.has("android.theme.customization.system_palette") ->
                    json.optString("android.theme.customization.system_palette")
                else -> null
            }
            if (!hexColor.isNullOrEmpty()) {
                val cleanHex = hexColor.trim().removePrefix("#")
                val parsedLong = cleanHex.toLongOrNull(16)
                if (parsedLong != null) {
                    val argb = if (cleanHex.length <= 6) (0xFF000000 or parsedLong).toInt() else parsedLong.toInt()
                    return Color(argb)
                }
            }
        }
    } catch (_: Exception) {}

    // 2. Try WallpaperManager getWallpaperColors (Android 8.1+)
    try {
        val wm = WallpaperManager.getInstance(context)
        val colors = wm.getWallpaperColors(WallpaperManager.FLAG_SYSTEM)
        if (colors != null) {
            val primary = colors.primaryColor.toArgb()
            return Color(primary)
        }
    } catch (_: Exception) {}

    return null
}

private fun dynamicColorSchemeFromSeed(seedColor: Color, isDark: Boolean): ColorScheme {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(seedColor.toArgb(), hsl)
    val h = hsl[0]
    val s = hsl[1].coerceIn(0.30f, 0.90f)

    fun color(hue: Float, sat: Float, light: Float): Color {
        val intColor = ColorUtils.HSLToColor(
            floatArrayOf(
                (hue % 360f + 360f) % 360f,
                sat.coerceIn(0f, 1f),
                light.coerceIn(0f, 1f)
            )
        )
        return Color(intColor)
    }

    return if (isDark) {
        darkColorScheme(
            primary = color(h, s, 0.78f),
            onPrimary = color(h, s * 0.7f, 0.15f),
            primaryContainer = color(h, s * 0.80f, 0.32f),
            onPrimaryContainer = color(h, s * 0.5f, 0.92f),
            secondary = color(h, s * 0.35f, 0.72f),
            onSecondary = color(h, s * 0.35f, 0.18f),
            secondaryContainer = color(h, s * 0.30f, 0.25f),
            onSecondaryContainer = color(h, s * 0.30f, 0.90f),
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
    } else {
        lightColorScheme(
            primary = color(h, s * 0.95f, 0.38f),
            onPrimary = Color.White,
            primaryContainer = color(h, s * 0.65f, 0.88f),
            onPrimaryContainer = color(h, s * 0.9f, 0.12f),
            secondary = color(h, s * 0.30f, 0.42f),
            onSecondary = Color.White,
            secondaryContainer = color(h, s * 0.35f, 0.90f),
            onSecondaryContainer = color(h, s * 0.50f, 0.15f),
            background = Color(0xFFFAFAFA),
            onBackground = Color(0xFF0F172A),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF0F172A),
            surfaceContainerLow = Color(0xFFFFFFFF),
            surfaceContainerHigh = Color(0xFFF1F5F9),
            surfaceContainerHighest = Color(0xFFE2E8F0),
            onSurfaceVariant = Color(0xFF475569),
            outline = Color(0xFFE2E8F0),
            outlineVariant = Color(0xFFE2E8F0)
        )
    }
}

@Composable
fun SystemBarsColorEffect(darkTheme: Boolean) {
    val view = LocalView.current
    val context = LocalContext.current
    SideEffect {
        var parent: Any? = view
        var dialogWindow: android.view.Window? = null
        while (parent != null) {
            if (parent is DialogWindowProvider) {
                dialogWindow = parent.window
                break
            }
            parent = (parent as? android.view.View)?.parent
        }
        val targetWindow = dialogWindow ?: (context as? Activity)?.window
        if (targetWindow != null) {
            val insetsController = WindowCompat.getInsetsController(targetWindow, targetWindow.decorView)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }
}

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

    val lifecycleOwner = LocalLifecycleOwner.current
    var systemSeedColor by remember { mutableStateOf(extractSystemSeedColor(context)) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                systemSeedColor = extractSystemSeedColor(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val colorScheme = remember(darkTheme, systemSeedColor, context) {
        val seed = systemSeedColor
        if (seed != null) {
            dynamicColorSchemeFromSeed(seed, darkTheme)
        } else {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
    }

    SystemBarsColorEffect(darkTheme = darkTheme)

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

// ════════════════════════════════════════════════════════════════════════
// Onboarding
// ════════════════════════════════════════════════════════════════════════

@Composable
fun OnboardingScreen(
    languageMode: Int,
    onComplete: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val prefs = remember { context.getSharedPreferences(PrefsKeys.PREFS_NAME, Context.MODE_PRIVATE) }

    val pagerState = rememberPagerState(pageCount = { 2 })

    var hasDndPermission by remember { mutableStateOf(checkDndPermission(context)) }
    var isIgnoringBatteryOptimization by remember { mutableStateOf(checkBatteryOptimization(context)) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasDndPermission = checkDndPermission(context)
                isIgnoringBatteryOptimization = checkBatteryOptimization(context)
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
                    onGrantDnd = { openDndPermissionSettings(context) },
                    onRequestBatteryOptimization = { requestIgnoreBatteryOptimization(context) },
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
    onGrantDnd: () -> Unit,
    onRequestBatteryOptimization: () -> Unit,
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
                    imageVector = if (isGranted) AppIcons.CheckCircle else AppIcons.Warning,
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
    languageMode: Int,
    onLanguageModeChanged: (Int) -> Unit = {},
    onThemeModeChanged: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val prefs = remember { context.getSharedPreferences(PrefsKeys.PREFS_NAME, Context.MODE_PRIVATE) }

    val isServiceRunning by FlipToShhhService.isRunning.collectAsState()
    val isFlippedDown by FlipToShhhService.isFlippedDown.collectAsState()
    val isDndActive by FlipToShhhService.isDndActive.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    var hasDndPermission by remember { mutableStateOf(true) }
    var isIgnoringBatteryOptimization by remember { mutableStateOf(true) }
    var hasAccessibilityPermission by remember { mutableStateOf(true) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    var showAboutScreen by rememberSaveable { mutableStateOf(false) }
    var showEasterEggTerminal by rememberSaveable { mutableStateOf(false) }

    val updatePermissions: suspend () -> Unit = remember(context) {
        {
            val dnd = withContext(Dispatchers.IO) { checkDndPermission(context) }
            val batt = withContext(Dispatchers.IO) { checkBatteryOptimization(context) }
            val access = withContext(Dispatchers.IO) { FlipLockAccessibilityService.isAccessibilityServiceEnabled(context) }
            hasDndPermission = dnd
            isIgnoringBatteryOptimization = batt
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
                    onLanguageModeChanged(prefs.getInt(PrefsKeys.KEY_LANGUAGE_MODE, 0))
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
                TextButton(onClick = { showPermissionDialog = false }) { Text(AppStrings.get(context, "btn_cancel", languageMode), style = OneUiTypography.ButtonText) }
            }
        )
    }

    AnimatedContent(
        targetState = showEasterEggTerminal,
        label = "easter_egg_terminal",
        transitionSpec = {
            fadeIn(tween(350, easing = FastOutSlowInEasing)) togetherWith fadeOut(tween(250, easing = FastOutSlowInEasing))
        }
    ) { isEggOpen ->
        if (isEggOpen) {
            EasterEggTerminalScreen(
                onExit = { showEasterEggTerminal = false }
            )
        } else {
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
                        onBack = { showAboutScreen = false },
                        onEasterEggTriggered = {
                            showAboutScreen = false
                            showEasterEggTerminal = true
                        }
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
                                    .consumeWindowInsets(WindowInsets.navigationBars),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // 1. One UI Large Title Header
                                OneUiHeader(
                                    languageMode = languageMode,
                                    onOpenSettings = { showSettingsSheet = true }
                                )

                                // 2. Missing DND permission warning banner (only when DND not granted)
                                if (!hasDndPermission) {
                                    val isDark = isSystemInDarkTheme()
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { openDndPermissionSettings(context) },
                                        shape = RoundedCornerShape(20.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isDark) Color(0xFF78350F).copy(alpha = 0.5f) else Color(0xFFFFFBEB)
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 18.dp, vertical = 14.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                modifier = Modifier.weight(1f),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = AppIcons.Warning,
                                                    contentDescription = null,
                                                    tint = if (isDark) Color(0xFFFBBF24) else Color(0xFFD97706),
                                                    modifier = Modifier.size(22.dp)
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text(
                                                    text = AppStrings.get(context, "perm_dnd_required_banner", languageMode),
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = if (isDark) Color(0xFFFDE68A) else Color(0xFF92400E)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Button(
                                                onClick = { openDndPermissionSettings(context) },
                                                shape = RoundedCornerShape(12.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Text(AppStrings.get(context, "grant_btn", languageMode), style = OneUiTypography.ButtonText)
                                            }
                                        }
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    PureMinimalistHeroCenterpiece(
                                        isRunning = isServiceRunning,
                                        isFlippedDown = isFlippedDown,
                                        isDndActive = isDndActive,
                                        languageMode = languageMode,
                                        onToggle = {
                                            if (!isServiceRunning) {
                                                if (!hasDndPermission) {
                                                    showPermissionDialog = true
                                                } else {
                                                    prefs.edit().putBoolean(PrefsKeys.KEY_SERVICE_USER_ENABLED, true).apply()
                                                    startFlipService(context)
                                                }
                                            } else {
                                                prefs.edit().putBoolean(PrefsKeys.KEY_SERVICE_USER_ENABLED, false).apply()
                                                stopFlipService(context)
                                            }
                                        }
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()))
                            }
                        }

                        SettingsBottomSheet(
                            visible = showSettingsSheet,
                            languageMode = languageMode,
                            onLanguageModeSelected = { newLang ->
                                onLanguageModeChanged(newLang)
                            },
                            onThemeModeSelected = { newTheme ->
                                onThemeModeChanged(newTheme)
                            },
                            onGrantDnd = { openDndPermissionSettings(context) },
                            onRequestBatteryOptimization = { requestIgnoreBatteryOptimization(context) },
                            onGrantAccessibility = { openAccessibilitySettings(context) },
                            onDismiss = {
                                showSettingsSheet = false
                            },
                            onShowAbout = {
                                showSettingsSheet = false
                                showAboutScreen = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OneUiHeader(
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

@Composable
fun PureMinimalistHeroCenterpiece(
    isRunning: Boolean,
    isFlippedDown: Boolean,
    isDndActive: Boolean,
    languageMode: Int,
    onToggle: () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val heroActive = isRunning && isFlippedDown && isDndActive

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMediumLow),
        label = "buttonScale"
    )

    val breathingTransition = rememberInfiniteTransition(label = "etherealBreathing")

    val pulseScale1 by breathingTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.22f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (heroActive) 4800 else 3200,
                easing = CubicBezierEasing(0.35f, 0.0f, 0.25f, 1.0f)
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale1"
    )
    val pulseAlpha1 by breathingTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (heroActive) 4800 else 3200,
                easing = CubicBezierEasing(0.35f, 0.0f, 0.25f, 1.0f)
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha1"
    )

    val pulseScale2 by breathingTransition.animateFloat(
        initialValue = 1.05f,
        targetValue = 1.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (heroActive) 5600 else 4200,
                easing = CubicBezierEasing(0.40f, 0.0f, 0.20f, 1.0f)
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale2"
    )
    val pulseAlpha2 by breathingTransition.animateFloat(
        initialValue = 0.50f,
        targetValue = 0.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (heroActive) 5600 else 4200,
                easing = CubicBezierEasing(0.40f, 0.0f, 0.20f, 1.0f)
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha2"
    )

    val coreBreathingScale by breathingTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.025f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (heroActive) 4800 else 3200,
                easing = CubicBezierEasing(0.35f, 0.0f, 0.25f, 1.0f)
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "coreBreathingScale"
    )

    val haloColor = when {
        heroActive -> MaterialTheme.colorScheme.primary
        isRunning -> MaterialTheme.colorScheme.primary
        else -> Color.Transparent
    }

    val buttonBgColor by animateColorAsState(
        targetValue = when {
            heroActive -> MaterialTheme.colorScheme.primary
            isRunning -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.surfaceContainerHigh
        },
        animationSpec = tween(350),
        label = "buttonBgColor"
    )

    val iconColor by animateColorAsState(
        targetValue = when {
            heroActive -> MaterialTheme.colorScheme.onPrimary
            isRunning -> MaterialTheme.colorScheme.onPrimaryContainer
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(350),
        label = "iconColor"
    )

    val heroIcon = when {
        heroActive -> AppIcons.Bedtime
        isRunning -> AppIcons.DoNotDisturbOn
        else -> AppIcons.PowerSettingsNew
    }

    val statusHeadline = when {
        heroActive -> AppStrings.get(context, "hero_dnd_active_title", languageMode)
        isRunning -> AppStrings.get(context, "hero_running_title", languageMode)
        else -> AppStrings.get(context, "hero_tap_to_start", languageMode)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(240.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isRunning) {
                Canvas(modifier = Modifier.size(240.dp)) {
                    val centerPt = this.center
                    val baseRadius = size.minDimension / 2f
                    val r2 = (baseRadius * 0.96f) * pulseScale2
                    drawCircle(
                        brush = Brush.radialGradient(
                            0.0f to haloColor.copy(alpha = pulseAlpha2 * 0.42f),
                            0.45f to haloColor.copy(alpha = pulseAlpha2 * 0.22f),
                            0.80f to haloColor.copy(alpha = pulseAlpha2 * 0.05f),
                            1.0f to Color.Transparent,
                            center = centerPt,
                            radius = r2
                        ),
                        radius = r2,
                        center = centerPt
                    )
                    val r1 = (baseRadius * 0.78f) * pulseScale1
                    drawCircle(
                        brush = Brush.radialGradient(
                            0.0f to haloColor.copy(alpha = pulseAlpha1 * 0.65f),
                            0.50f to haloColor.copy(alpha = pulseAlpha1 * 0.35f),
                            0.85f to haloColor.copy(alpha = pulseAlpha1 * 0.08f),
                            1.0f to Color.Transparent,
                            center = centerPt,
                            radius = r1
                        ),
                        radius = r1,
                        center = centerPt
                    )
                    val r0 = (baseRadius * 0.60f) * pulseScale1
                    drawCircle(
                        color = haloColor.copy(alpha = pulseAlpha1 * 0.20f),
                        radius = r0,
                        center = centerPt
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .graphicsLayer {
                        val s = buttonScale * (if (isRunning) coreBreathingScale else 1.0f)
                        scaleX = s
                        scaleY = s
                        shape = CircleShape
                        clip = true
                    }
                    .background(buttonBgColor)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onToggle()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = heroIcon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(44.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = statusHeadline,
            style = OneUiTypography.HeroTitle,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}

// ════════════════════════════════════════════════════════════════════════
// Permission Row Component
// ════════════════════════════════════════════════════════════════════════

@Composable
fun PermissionItemRow(
    title: String,
    icon: ImageVector,
    onAction: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onAction()
            }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                style = OneUiTypography.ItemTitle,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Icon(
            imageVector = AppIcons.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
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
    val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    if (!pm.isIgnoringBatteryOptimizations(context.packageName)) {
        try {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:${context.packageName}")
            }
            context.startActivity(intent)
            return
        } catch (e: Exception) {
        }
    }

    try {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${context.packageName}")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        try {
            context.startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
        } catch (e2: Exception) {
        }
    }
}

private fun openAccessibilitySettings(context: Context) {
    context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
}

// ════════════════════════════════════════════════════════════════════════
// Settings Modal Bottom Sheet & Option Picker
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
        // ModalBottomSheet hosts its own dialog window; its bar appearance must follow the
        // app theme, which can disagree with the system theme the fallback would read.
        val darkTheme = MaterialTheme.colorScheme.background.luminance() < 0.5f
        SystemBarsColorEffect(darkTheme = darkTheme)

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    visible: Boolean,
    languageMode: Int,
    onLanguageModeSelected: (Int) -> Unit = {},
    onThemeModeSelected: (Int) -> Unit = {},
    onGrantDnd: () -> Unit,
    onRequestBatteryOptimization: () -> Unit,
    onGrantAccessibility: () -> Unit,
    onDismiss: () -> Unit,
    onShowAbout: () -> Unit
) {
    if (!visible) return

    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val prefs = remember { context.getSharedPreferences(PrefsKeys.PREFS_NAME, Context.MODE_PRIVATE) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var themeMode by remember { mutableStateOf(prefs.getInt(PrefsKeys.KEY_THEME_MODE, 0)) }
    var showLanguagePicker by remember { mutableStateOf(false) }

    var autoStartEnabled by remember { mutableStateOf(prefs.getBoolean(PrefsKeys.KEY_AUTO_START_BOOT, true)) }
    // Keep the default identical to FlipToShhhService's PrefsKeys.KEY_AUTO_LOCK_SCREEN default (true).
    var autoLockEnabled by remember { mutableStateOf(prefs.getBoolean(PrefsKeys.KEY_AUTO_LOCK_SCREEN, true)) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f))
                )
            }
        },
        contentWindowInsets = { WindowInsets.navigationBars }
    ) {
        val darkTheme = when (themeMode) {
            1 -> true
            2 -> false
            else -> isSystemInDarkTheme()
        }
        SystemBarsColorEffect(darkTheme = darkTheme)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp, start = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = AppStrings.get(context, "settings_title", languageMode),
                    style = OneUiTypography.TitleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    SectionHeader(text = AppStrings.get(context, "group_permissions", languageMode))
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            PermissionItemRow(
                                title = AppStrings.get(context, "perm_dnd_title", languageMode),
                                icon = AppIcons.NotificationsActive,
                                onAction = onGrantDnd
                            )
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                            PermissionItemRow(
                                title = AppStrings.get(context, "perm_battery_title", languageMode),
                                icon = AppIcons.BatterySaver,
                                onAction = onRequestBatteryOptimization
                            )
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                            PermissionItemRow(
                                title = AppStrings.get(context, "perm_access_title", languageMode),
                                icon = AppIcons.Lock,
                                onAction = onGrantAccessibility
                            )
                        }
                    }
                }

                Column {
                    SectionHeader(text = AppStrings.get(context, "group_features", languageMode))
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
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
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Text(
                                        text = AppStrings.get(context, "autostart_title", languageMode),
                                        style = OneUiTypography.ItemTitle,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Switch(
                                    checked = autoStartEnabled,
                                    onCheckedChange = { enabled ->
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        autoStartEnabled = enabled
                                        prefs.edit().putBoolean(PrefsKeys.KEY_AUTO_START_BOOT, enabled).apply()
                                    }
                                )
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
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
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Text(
                                        text = AppStrings.get(context, "autolock_title", languageMode),
                                        style = OneUiTypography.ItemTitle,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Switch(
                                    checked = autoLockEnabled,
                                    onCheckedChange = { enabled ->
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        autoLockEnabled = enabled
                                        prefs.edit().putBoolean(PrefsKeys.KEY_AUTO_LOCK_SCREEN, enabled).apply()
                                        if (enabled && !FlipLockAccessibilityService.isAccessibilityServiceEnabled(context)) {
                                            onGrantAccessibility()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                Column {
                    SectionHeader(text = AppStrings.get(context, "group_appearance", languageMode))
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
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
                                        prefs.edit().putInt(PrefsKeys.KEY_THEME_MODE, mode).apply()
                                        onThemeModeSelected(mode)
                                    }
                                )
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
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

                Column {
                    SectionHeader(text = AppStrings.get(context, "group_about", languageMode))
                    Spacer(modifier = Modifier.height(6.dp))
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
                                .padding(horizontal = 16.dp, vertical = 14.dp),
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
                                    text = AppStrings.get(context, "setting_about", languageMode),
                                    style = OneUiTypography.ItemTitle,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
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

        if (showLanguagePicker) {
            val langOptions = listOf(
                0 to AppStrings.get(context, "sys_default", languageMode),
                1 to AppStrings.get(context, "lang_sim_cn", languageMode),
                2 to AppStrings.get(context, "lang_trad_cn", languageMode),
                3 to AppStrings.get(context, "lang_english", languageMode)
            )
            OptionPickerSheet(
                title = AppStrings.get(context, "lang_title", languageMode),
                options = langOptions,
                selectedKey = languageMode,
                onSelect = { mode ->
                    prefs.edit().putInt(PrefsKeys.KEY_LANGUAGE_MODE, mode).apply()
                    onLanguageModeSelected(mode)
                },
                onDismiss = { showLanguagePicker = false }
            )
        }
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
fun AboutScreen(
    languageMode: Int,
    onBack: () -> Unit,
    onEasterEggTriggered: () -> Unit = {}
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    var clickCount by remember { mutableIntStateOf(0) }
    var lastClickTime by remember { mutableLongStateOf(0L) }

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
        // 1. Top bar — clean natural back navigation + title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = AppStrings.get(context, "nav_back", languageMode),
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
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
                .padding(top = 28.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = AppIcons.DoNotDisturbOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = AppStrings.get(context, "app_name", languageMode),
                style = OneUiTypography.HeroTitle,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = AppStrings.get(context, "about_developer", languageMode),
                style = OneUiTypography.ItemSubtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        val versionName = remember(context) {
            try {
                context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: BuildConfig.VERSION_NAME
            } catch (e: Exception) {
                BuildConfig.VERSION_NAME
            }
        }

        // 3. Privacy, License & Version card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Row 1: Privacy
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = AppIcons.Shield,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = AppStrings.get(context, "about_privacy_title", languageMode),
                            style = OneUiTypography.ItemTitle,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = AppStrings.get(context, "about_privacy_body", languageMode),
                            style = OneUiTypography.ItemSubtitle,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                // Row 2: License
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = AppIcons.Code,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = AppStrings.get(context, "about_license_title", languageMode),
                            style = OneUiTypography.ItemTitle,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = AppStrings.get(context, "about_license_body", languageMode),
                            style = OneUiTypography.ItemSubtitle,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                // Row 3: Version (Click 7 times to trigger Easter Egg Terminal with Scheme 2 Inline TTY expansion)
                LaunchedEffect(clickCount, lastClickTime) {
                    if (clickCount in 1..6) {
                        delay(2500)
                        if (System.currentTimeMillis() - lastClickTime >= 2400L) {
                            clickCount = 0
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            val now = System.currentTimeMillis()
                            if (now - lastClickTime > 2200L) {
                                clickCount = 1
                            } else {
                                clickCount++
                            }
                            lastClickTime = now

                            if (clickCount >= 7) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                coroutineScope.launch {
                                    delay(260)
                                    clickCount = 0
                                    onEasterEggTriggered()
                                }
                            } else if (clickCount >= 4) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            } else {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        }
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = AppIcons.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = AppStrings.get(context, "about_version_title", languageMode),
                                style = OneUiTypography.ItemTitle,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "v$versionName",
                                style = OneUiTypography.ItemSubtitle,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Scheme 2: Inline TTY expansion animation
                    AnimatedVisibility(
                        visible = clickCount >= 4,
                        enter = expandVertically(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) + fadeIn(),
                        exit = shrinkVertically(animationSpec = tween(300)) + fadeOut()
                    ) {
                        val statusText = when (clickCount) {
                            4 -> "> [4/7] Probing /dev/ttyS0..."
                            5 -> "> [5/7] Loading Ubuntu 26.04 kernel..."
                            6 -> "> [6/7] Spawning shell session..."
                            else -> "> [7/7] ACCESS GRANTED. Launching TTY1..."
                        }
                        val statusColor = if (clickCount >= 7) Color(0xFF8AE234) else Color(0xFF729FCF)

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF300A24)) // Ubuntu Aubergine
                                .padding(horizontal = 12.dp, vertical = 9.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = statusText,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = statusColor
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "█",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Footer
        Spacer(modifier = Modifier.height(36.dp))
        Text(
            text = "Copyright © 2026 Cicada",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ════════════════════════════════════════════════════════════════════════
// Easter Egg Terminal Screen (Authentic Linux / Ubuntu 26.04 Terminal)
// ════════════════════════════════════════════════════════════════════════

enum class LyricSinger {
    MALE,    // 周杰伦男声 -> ANSI Light Blue (#729FCF)
    FEMALE,  // 梁心颐女声 -> ANSI Light Yellow (#FCE94F)
    DUET     // 合唱       -> ANSI Light Green (#8AE234)
}

data class CleanLyric(
    val timestampMs: Long,
    val text: String,
    val singer: LyricSinger
)

// Per-line typing schedule, all relative to the line's LRC vocal timestamp:
//  - the line activates LYRIC_LEAD_MS early: blank line, cursor and scroll pre-position,
//    but no text is exposed yet;
//  - typing begins AT the onset and the first character renders immediately there;
//  - every later character is pulled forward by LYRIC_BODY_LEAD_MS on top of the
//    end-anchored pacing, so the line completes LEAD + BODY_LEAD before the next vocal.
//    Body characters never render before the onset — on fast lines they bunch at it.
// Safety: the typing window is lineDuration - LEAD; the closest cleaned lines are ~0.66s
// apart, so keep LEAD below ~600ms or fast lines lose the typing effect and snap to full.
private const val LYRIC_LEAD_MS = 300L
private const val LYRIC_TYPE_DELAY_MS = 0L
private const val LYRIC_BODY_LEAD_MS = 300L

// Optional global shift for every lyric timestamp (0 = trust the embedded LRC as-is).
// The per-line timing knobs below stay within the safe envelope regardless.
private const val LYRICS_GLOBAL_OFFSET_MS = 0L

private fun parseAndCleanLrc(content: String): List<CleanLyric> {
    val regex = Regex("""\[(\d{2}):(\d{2})\.(\d{2,3})\](.*)""")
    val rawList = mutableListOf<CleanLyric>()
    var currentSinger = LyricSinger.MALE

    content.lineSequence().forEach { rawLine ->
        val trimmed = rawLine.trim()
        val match = regex.find(trimmed)
        if (match != null) {
            val minStr = match.groupValues[1]
            val secStr = match.groupValues[2]
            val millisStr = match.groupValues[3]
            val min = minStr.toLongOrNull() ?: 0L
            val sec = secStr.toLongOrNull() ?: 0L
            val millis = if (millisStr.length == 2) millisStr.toLong() * 10 else millisStr.toLong()
            var text = match.groupValues[4].trim()

            // State machine singer identification
            if (text.startsWith("男：") || text.startsWith("男:") || text.startsWith("男；") || text.startsWith("男;")) {
                currentSinger = LyricSinger.MALE
                text = text.substring(2).trim()
            } else if (text.startsWith("女：") || text.startsWith("女:") || text.startsWith("女；") || text.startsWith("女;")) {
                currentSinger = LyricSinger.FEMALE
                text = text.substring(2).trim()
            } else if (text.startsWith("合：") || text.startsWith("合:") || text.startsWith("合；") || text.startsWith("合;")) {
                currentSinger = LyricSinger.DUET
                text = text.substring(2).trim()
            } else if (text == "男" || text == "(男)" || text == "（男）") {
                currentSinger = LyricSinger.MALE
                text = ""
            } else if (text == "女" || text == "(女)" || text == "（女）") {
                currentSinger = LyricSinger.FEMALE
                text = ""
            } else if (text == "合" || text == "(合)" || text == "（合）") {
                currentSinger = LyricSinger.DUET
                text = ""
            }

            // Filter out metadata lines
            val isMeta = text.startsWith("词") ||
                    text.startsWith("曲") ||
                    text.startsWith("编曲") ||
                    text.startsWith("制作人") ||
                    text.startsWith("作词") ||
                    text.startsWith("作曲") ||
                    text.startsWith("演唱") ||
                    text == "珊瑚海"

            if (text.isNotEmpty() && !isMeta) {
                rawList.add(CleanLyric(min * 60000 + sec * 1000 + millis, text, currentSinger))
            }
        }
    }

    return rawList.sortedBy { it.timestampMs }.map {
        it.copy(timestampMs = it.timestampMs + LYRICS_GLOBAL_OFFSET_MS)
    }
}

private fun extractLyricsFromOgg(context: Context): String {
    return try {
        context.resources.openRawResource(R.raw.coralsea).use { inputStream ->
            // Read the whole file: a single read() call may underfill, and the LYRICS
            // comment is not guaranteed to land in the first 64KB of the stream.
            val buffer = inputStream.readBytes()
            val target = "LYRICS=".toByteArray(Charsets.UTF_8)
            var targetIndex = -1
            for (i in 0 until (buffer.size - target.size)) {
                var match = true
                for (j in target.indices) {
                    if (buffer[i + j] != target[j]) {
                        match = false
                        break
                    }
                }
                if (match) {
                    targetIndex = i
                    break
                }
            }
            if (targetIndex != -1) {
                val lenOffset = targetIndex - 4
                val len = if (lenOffset >= 0) {
                    (buffer[lenOffset].toInt() and 0xFF) or
                    ((buffer[lenOffset + 1].toInt() and 0xFF) shl 8) or
                    ((buffer[lenOffset + 2].toInt() and 0xFF) shl 16) or
                    ((buffer[lenOffset + 3].toInt() and 0xFF) shl 24)
                } else 4096
                val lyricsLen = (len - target.size).coerceIn(0, buffer.size - targetIndex - target.size)
                String(buffer, targetIndex + target.size, lyricsLen, Charsets.UTF_8)
            } else ""
        }
    } catch (_: Exception) {
        ""
    }
}

@Composable
fun EasterEggTerminalScreen(onExit: () -> Unit) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    BackHandler { onExit() }

    val lyrics = remember(context) {
        try {
            val content = extractLyricsFromOgg(context)
            parseAndCleanLrc(content)
        } catch (_: Exception) {
            emptyList()
        }
    }

    var currentPosMs by remember { mutableLongStateOf(0L) }
    var totalDurationMs by remember { mutableLongStateOf(0L) }
    var mediaPlayerInstance by remember { mutableStateOf<MediaPlayer?>(null) }
    var playerFailed by remember { mutableStateOf(false) }
    var showPlayerError by remember { mutableStateOf(false) }
    var isSongFinished by remember { mutableStateOf(false) }
    var rmCommandTyped by remember { mutableStateOf("") }
    var showRmOutput by remember { mutableStateOf(false) }

    // Command typing animation on entry (Authentic Linux CLI invocation)
    val fullCommand = "./coralsea-cli -f coralsea.ogg --lyrics"
    var typedCommand by remember { mutableStateOf("") }
    var isCommandEntered by remember { mutableStateOf(false) }

    // Hard terminal-style cursor blink: a boolean toggle recomposes only the cursor
    // readers twice per blink, instead of an alpha animation running at display rate.
    var cursorVisible by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(530)
            cursorVisible = !cursorVisible
        }
    }

    // Progressive command typing effect on launch
    LaunchedEffect(Unit) {
        delay(250)
        for (i in 1..fullCommand.length) {
            typedCommand = fullCommand.substring(0, i)
            delay(55)
        }
        delay(200)
        isCommandEntered = true
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(context, lifecycleOwner) {
        val player = try {
            MediaPlayer.create(context, R.raw.coralsea)?.apply {
                isLooping = false
                setOnCompletionListener {
                    isSongFinished = true
                }
            }
        } catch (_: Exception) {
            null
        }
        mediaPlayerInstance = player
        playerFailed = player == null
        if (player != null) {
            totalDurationMs = player.duration.toLong()
        }

        // Take media audio focus so background players pause instead of mixing
        // with the easter egg song.
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        val focusListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS ||
                focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT
            ) {
                try {
                    if (player?.isPlaying == true) {
                        player?.pause()
                    }
                } catch (_: Exception) {}
            }
        }
        val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setOnAudioFocusChangeListener(focusListener)
            .build()
        audioManager?.requestAudioFocus(focusRequest)

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP, Lifecycle.Event.ON_PAUSE -> {
                    try {
                        if (player != null && player.isPlaying) {
                            player.pause()
                        }
                    } catch (_: Exception) {}
                }
                Lifecycle.Event.ON_RESUME -> {
                    try {
                        if (player != null && isCommandEntered && !player.isPlaying && !isSongFinished) {
                            player.start()
                        }
                    } catch (_: Exception) {}
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            try {
                if (player != null) {
                    if (player.isPlaying) {
                        player.stop()
                    }
                    player.release()
                }
            } catch (_: Exception) {}
            audioManager?.abandonAudioFocusRequest(focusRequest)
            mediaPlayerInstance = null
        }
    }

    // Start audio ONLY after command is fully typed and entered
    LaunchedEffect(isCommandEntered, mediaPlayerInstance) {
        if (isCommandEntered) {
            val player = mediaPlayerInstance
            if (player != null && !player.isPlaying && !isSongFinished) {
                player.start()
            }
        }
    }

    // Global lead: a line becomes current LYRIC_LEAD_MS before its vocal timestamp.
    val effectivePos = currentPosMs + LYRIC_LEAD_MS
    val activeIndex = remember(effectivePos, lyrics) {
        lyrics.indexOfLast { it.timestampMs <= effectivePos }
    }

    LaunchedEffect(mediaPlayerInstance) {
        val player = mediaPlayerInstance ?: return@LaunchedEffect
        while (true) {
            try {
                if (player.isPlaying) {
                    currentPosMs = player.currentPosition.toLong()
                }
            } catch (_: Exception) {}
            if (isSongFinished) break
            delay(80)
        }
    }

    // End-of-song sequence: execute authentic sudo rm -rf --no-preserve-root /* and wait 3s before exit
    LaunchedEffect(isSongFinished) {
        if (!isSongFinished) return@LaunchedEffect
        delay(600)
        val fullCmd = "sudo rm -rf --no-preserve-root /*"
        for (i in 1..fullCmd.length) {
            rmCommandTyped = fullCmd.substring(0, i)
            delay(55)
        }
        delay(350)
        showRmOutput = true
        delay(3000) // 停留 3 秒
        onExit()
    }

    // Player creation failed: print the failure output once the command prompt has
    // finished typing, then leave the session so the screen never hangs.
    LaunchedEffect(playerFailed, isCommandEntered) {
        if (!playerFailed || !isCommandEntered) return@LaunchedEffect
        delay(500)
        showPlayerError = true
        delay(3200)
        onExit()
    }

    val displayedCount = if (isCommandEntered && !playerFailed) (activeIndex + 1).coerceAtLeast(0) else 0

    // 4 Authentic Linux kernel & audio pipeline logs during the 10-second piano instrumental gap (1:1 realtime aligned)
    val introLogs = remember {
        listOf(
            Triple(50L, "[0.042] ", "ALSA: Initialized PCM (44.1kHz stereo)"),
            Triple(2150L, "[2.150] ", "PipeWire: Bound sink 'coralsea.ogg'"),
            Triple(5820L, "[5.820] ", "Vorbis: Jay Chou & Lara"),
            Triple(8940L, "[8.940] ", "ANSI: Streaming embedded lyrics...")
        )
    }

    val displayedIntroCount = if (!isCommandEntered) 0 else {
        introLogs.count { it.first <= currentPosMs }
    }

    // Lyric items read playback position through this State so that only the active
    // line subscribes to position updates; past lines never recompose on a tick.
    val effectivePosState = rememberUpdatedState(effectivePos)

    // Pause auto-scroll while the user drags the list; resume after a grace period.
    var userDragging by remember { mutableStateOf(false) }
    var lastDragEndTime by remember { mutableLongStateOf(0L) }
    LaunchedEffect(listState.interactionSource) {
        listState.interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is DragInteraction.Start -> userDragging = true
                is DragInteraction.Stop, is DragInteraction.Cancel -> {
                    userDragging = false
                    lastDragEndTime = System.currentTimeMillis()
                }
            }
        }
    }

    // Auto-scroll seamlessly with visual offset
    LaunchedEffect(activeIndex, isSongFinished, showRmOutput, displayedIntroCount, showPlayerError, userDragging) {
        if (userDragging) return@LaunchedEffect
        val sinceDragEnd = System.currentTimeMillis() - lastDragEndTime
        if (sinceDragEnd < 4000L) delay(4000L - sinceDragEnd)
        if (isSongFinished || showPlayerError) {
            if (listState.layoutInfo.totalItemsCount > 0) {
                listState.animateScrollToItem(listState.layoutInfo.totalItemsCount - 1)
            }
        } else if (activeIndex >= 0 && isCommandEntered) {
            val targetScroll = (displayedIntroCount + activeIndex - 2).coerceAtLeast(0)
            listState.animateScrollToItem(targetScroll)
        } else if (displayedIntroCount > 0) {
            listState.animateScrollToItem(displayedIntroCount - 1)
        }
    }

    SystemBarsColorEffect(darkTheme = true)

    val motdDate = remember {
        SimpleDateFormat("EEE MMM d yyyy", Locale.US).format(Date())
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF300A24)) // Ubuntu Aubergine
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            // 1. Pinned Ubuntu 26.04 LTS MOTD (Long press 3s jumps to last 10s)
            Text(
                text = "Welcome to Ubuntu 26.04 LTS (GNU/Linux 6.14.0-generic aarch64)\n\n" +
                        " * Documentation:  https://help.ubuntu.com\n" +
                        " * Support:        https://ubuntu.com/pro\n\n" +
                        "System information as of $motdDate\n",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = Color(0xFFD3D7CF),
                lineHeight = 18.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(mediaPlayerInstance, totalDurationMs) {
                        detectTapGestures(
                            onPress = {
                                val job = coroutineScope.launch {
                                    delay(3000)
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    val player = mediaPlayerInstance
                                    val duration = if (totalDurationMs > 0) totalDurationMs else (player?.duration?.toLong() ?: 0L)
                                    if (player != null && duration > 10000L) {
                                        typedCommand = fullCommand
                                        isCommandEntered = true
                                        val targetMs = (duration - 10000L).coerceAtLeast(0L).toInt()
                                        player.seekTo(targetMs)
                                        currentPosMs = targetMs.toLong()
                                    }
                                }
                                tryAwaitRelease()
                                job.cancel()
                            }
                        )
                    }
            )

            // 2. Pinned Command Prompt (Slow typing animation for ./coralsea-cli --lyrics)
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color(0xFF8AE234), fontWeight = FontWeight.Bold)) {
                        append("cicada@ubuntu")
                    }
                    withStyle(SpanStyle(color = Color.White)) {
                        append(":")
                    }
                    withStyle(SpanStyle(color = Color(0xFF729FCF), fontWeight = FontWeight.Bold)) {
                        append("~")
                    }
                    withStyle(SpanStyle(color = Color.White)) {
                        append("$ $typedCommand")
                    }
                    if (!isCommandEntered && cursorVisible) {
                        withStyle(SpanStyle(color = Color.White)) {
                            append("█")
                        }
                    }
                },
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Typing Lyrics Terminal Stream (ANSI colors: Male -> Blue, Female -> Yellow, Duet -> Green)
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black),
                                startY = 0f,
                                endY = 48f
                            ),
                            blendMode = BlendMode.DstIn
                        )
                    },
                contentPadding = PaddingValues(top = 10.dp, bottom = 140.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Player failure output (fallback when MediaPlayer.create fails)
                if (showPlayerError) {
                    item(key = "player_error") {
                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text(
                                text = "alsa: open /dev/snd/pcmC0D0p failed: No such device or address",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.5.sp,
                                color = Color(0xFFD3D7CF)
                            )
                            Text(
                                text = "coralsea-cli: audio pipeline aborted — nothing to stream",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.5.sp,
                                color = Color(0xFFD3D7CF)
                            )
                            Text(
                                text = "Segmentation fault (core dumped)",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF2929)
                            )
                        }
                    }
                }

                // Intro authentic Linux system logs during the instrumental piano gap
                items(displayedIntroCount) { logIdx ->
                    val log = introLogs[logIdx]
                    val isLatestLog = logIdx == displayedIntroCount - 1 && displayedCount == 0

                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = Color(0xFF8AE234))) {
                                append(log.second)
                            }
                            when (logIdx) {
                                0 -> {
                                    withStyle(SpanStyle(color = Color(0xFF729FCF), fontWeight = FontWeight.Bold)) { append("ALSA: ") }
                                    withStyle(SpanStyle(color = Color(0xFFD3D7CF))) { append("Initialized PCM (44.1kHz stereo)") }
                                }
                                1 -> {
                                    withStyle(SpanStyle(color = Color(0xFF729FCF), fontWeight = FontWeight.Bold)) { append("PipeWire: ") }
                                    withStyle(SpanStyle(color = Color(0xFFD3D7CF))) { append("Bound sink 'coralsea.ogg'") }
                                }
                                2 -> {
                                    withStyle(SpanStyle(color = Color(0xFF729FCF), fontWeight = FontWeight.Bold)) { append("Vorbis: ") }
                                    withStyle(SpanStyle(color = Color(0xFF729FCF), fontWeight = FontWeight.Bold)) { append("Jay Chou ") }
                                    withStyle(SpanStyle(color = Color(0xFFD3D7CF))) { append("& ") }
                                    withStyle(SpanStyle(color = Color(0xFFFCE94F), fontWeight = FontWeight.Bold)) { append("Lara") }
                                }
                                3 -> {
                                    withStyle(SpanStyle(color = Color(0xFF729FCF), fontWeight = FontWeight.Bold)) { append("ANSI: ") }
                                    withStyle(SpanStyle(color = Color(0xFF8AE234), fontWeight = FontWeight.SemiBold)) { append("Streaming embedded lyrics...") }
                                }
                            }
                            if (isLatestLog && cursorVisible) {
                                withStyle(SpanStyle(color = Color.White)) {
                                    append("█")
                                }
                            }
                        },
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.5.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (displayedIntroCount == introLogs.size && displayedCount == 0) {
                    item(key = "intro_divider_spacer") {
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

                // Fallback notice when the OGG container carries no LYRICS= comment
                if (displayedIntroCount == introLogs.size && lyrics.isEmpty()) {
                    item(key = "no_lyrics_notice") {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(color = Color(0xFF729FCF), fontWeight = FontWeight.Bold)) {
                                    append("Vorbis: ")
                                }
                                withStyle(SpanStyle(color = Color(0xFFD3D7CF))) {
                                    append("no embedded lyrics in container — streaming audio only")
                                }
                            },
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.5.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                items(displayedCount, key = { it }) { index ->
                    val line = lyrics[index]
                    val isCurrent = index == activeIndex && !isSongFinished

                    // ANSI color selection according to singer
                    val (textColor, activeColor) = when (line.singer) {
                        LyricSinger.MALE -> Pair(Color(0xFF6B92BC), Color(0xFF729FCF))     // ANSI Blue
                        LyricSinger.FEMALE -> Pair(Color(0xFFC7B935), Color(0xFFFCE94F))   // ANSI Yellow
                        LyricSinger.DUET -> Pair(Color(0xFF76A83C), Color(0xFF8AE234))     // ANSI Green
                    }

                    if (isCurrent) {
                        val lineDuration = if (index < lyrics.size - 1) {
                            // Floor stays above LEAD so a line always has a real typing
                            // window; gaps below it (fastest chorus lines ~0.66s) type at
                            // their true pace instead of an inflated one.
                            (lyrics[index + 1].timestampMs - line.timestampMs).coerceIn(700L, 5000L)
                        } else {
                            4000L
                        }
                        // Typing is keyed to real playback position. Keep the RAW elapsed
                        // value: it is negative during the activation lead-in, and only
                        // that negative state (not a coerced 0) may gate the first char.
                        val rawElapsedInLine = effectivePosState.value - line.timestampMs - LYRIC_LEAD_MS - LYRIC_TYPE_DELAY_MS
                        val typingWindowMs = (lineDuration - LYRIC_LEAD_MS - LYRIC_TYPE_DELAY_MS).coerceAtLeast(300L)
                        // The body track (every char after the first) runs BODY_LEAD ahead:
                        // folding it into the elapsed measure shifts each later char earlier
                        // by exactly that much, while the rawElapsed<0 gate above keeps the
                        // first char anchored to the vocal onset.
                        val progress = ((rawElapsedInLine + LYRIC_BODY_LEAD_MS).toFloat() / typingWindowMs.toFloat()).coerceIn(0f, 1f)
                        // First character renders on the vocal onset; the rest pace across
                        // the window so the line completes LYRIC_LEAD_MS before the next
                        // vocal. Truncating the full length here would hide the first char
                        // for window/(len+1) ms — clearly after the first sung syllable.
                        val typedCharsCount = if (rawElapsedInLine < 0L) 0 else
                            (progress * (line.text.length - 1)).toInt().coerceIn(0, line.text.length - 1) + 1

                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    SpanStyle(color = activeColor, fontWeight = FontWeight.SemiBold)
                                ) {
                                    append(line.text.substring(0, typedCharsCount))
                                }
                                if (cursorVisible) {
                                    withStyle(SpanStyle(color = activeColor)) {
                                        append("█")
                                    }
                                }
                            },
                            fontFamily = FontFamily.Monospace,
                            fontSize = 15.sp,
                            lineHeight = 24.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(
                            text = line.text,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 15.sp,
                            lineHeight = 24.sp,
                            color = textColor,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // End-of-song sequence: Authentic Linux sudo rm -rf --no-preserve-root /*
                if (isSongFinished) {
                    item(key = "rm_rf_sequence") {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(color = Color(0xFF8AE234), fontWeight = FontWeight.Bold)) {
                                    append("cicada@ubuntu")
                                }
                                withStyle(SpanStyle(color = Color.White)) {
                                    append(":")
                                }
                                withStyle(SpanStyle(color = Color(0xFF729FCF), fontWeight = FontWeight.Bold)) {
                                    append("~")
                                }
                                withStyle(SpanStyle(color = Color.White)) {
                                    append("$ $rmCommandTyped")
                                }
                                if (!showRmOutput && cursorVisible) {
                                    withStyle(SpanStyle(color = Color.White)) {
                                        append("█")
                                    }
                                }
                            },
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (showRmOutput) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text(
                                    text = "rm: removing all roots: '/bin', '/usr', '/etc', '/dev/soul' ...",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.5.sp,
                                    color = Color(0xFFD3D7CF)
                                )
                                Text(
                                    text = "[SYSTEM PURGED] Segmentation fault (core dumped)",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFEF2929) // Ubuntu Red
                                )
                                Text(
                                    text = "Connection to ubuntu:22 closed by remote host.",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.5.sp,
                                    color = Color(0xFF888888)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun startFlipService(context: Context) {
    ContextCompat.startForegroundService(context, Intent(context, FlipToShhhService::class.java))
}

private fun stopFlipService(context: Context) {
    context.stopService(Intent(context, FlipToShhhService::class.java))
}
