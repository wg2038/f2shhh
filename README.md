<p align="center">
  <h1 align="center">🤫 Flip to Shhh</h1>
  <p align="center">
    <strong>Exclusive Flip-to-Mute Utility Tailored for Samsung Galaxy Devices</strong><br>
    三星专属常驻翻转静音工具 · 三星專屬常駐翻轉靜音工具
  </p>
  <p align="center">
    <a href="#-flip-to-shhh">English</a> •
    <a href="#-flip-to-shhh-简体中文">简体中文</a> •
    <a href="#-flip-to-shhh-繁體中文">繁體中文</a>
  </p>
  <p align="center">
    <img src="https://img.shields.io/badge/Platform-Android_13%2B_(API_33%2B)-brightgreen?style=flat-square&logo=android" alt="Platform">
    <img src="https://img.shields.io/badge/Design-One_UI_6.x_/_Material_3-0057FF?style=flat-square" alt="One UI">
    <img src="https://img.shields.io/badge/Language-Kotlin_/_Jetpack_Compose-7F52FF?style=flat-square&logo=kotlin" alt="Kotlin">
    <img src="https://img.shields.io/badge/APK_Size-2.2_MB-success?style=flat-square" alt="Size">
    <img src="https://img.shields.io/badge/License-Apache_2.0-blue?style=flat-square" alt="License">
  </p>
</p>

<p align="center">
  <img src="docs/screenshots/main_screen.jpg" width="280" alt="Main Screen">
  &nbsp;&nbsp;&nbsp;&nbsp;
  <img src="docs/screenshots/permissions_expanded.jpg" width="280" alt="Permissions Expanded">
</p>

---

## 🇬🇧 Flip to Shhh

**Flip to Shhh** is an ultra-lightweight, high-performance background utility designed specifically for Samsung Galaxy flagship devices. Place your phone face down to instantly activate Do Not Disturb (DND) and mute all sounds, accompanied by authentic Samsung One UI haptic pulse feedback.

### ✨ Key Features

- ⚡ **Zero-Pre-Delay Gesture Detection**: Sub-100ms initial posture locking powered by gravity and gyroscope sensors with zero debounce delay.
- 📳 **One UI Native Haptic Engine**: Powered by Samsung's native `VibrationEffect.Composition` (`PRIMITIVE_THUD`), delivering crisp double-pulse tactile feedback.
- 🔒 **Accessibility Auto-Lock Screen**: Optional integration with Android native Accessibility Service (`GLOBAL_ACTION_LOCK_SCREEN`) to automatically turn off and lock the screen on flip.
- 🚀 **Direct Boot Resilience**: Full support for Android 13 Direct Boot (`ACTION_LOCKED_BOOT_COMPLETED`), restoring gesture monitoring immediately after device reboots before first unlock.
- 📦 **Featherweight Footprint**: Custom hand-drawn vector icon architecture resulting in an official Release APK size of **only ~2.2 MB**.
- 🛡️ **Privacy & Offline First**: 100% offline with zero network permissions, zero analytics, and zero data collection.

### 🛠️ Tech Stack & Architecture

- **UI & Theme**: Kotlin + Jetpack Compose (Material 3 / One UI Dynamic Color)
- **Core Service**: `Foreground Service` + `SensorEventListener` (TYPE_GRAVITY & TYPE_GYROSCOPE)
- **System Integration**: `NotificationManager` (DND Policy Access) + `AccessibilityService` (System Lock)
- **Boot Persistence**: `BroadcastReceiver` with Direct Boot Awareness

---

## 🇨🇳 Flip to Shhh (简体中文)

**Flip to Shhh** 是一款专为三星 Galaxy S 系列旗舰设备打造的高性能、超低功耗常驻翻转静音小工具。只需将手机屏幕朝下扣在桌面上，即可瞬时开启勿扰（DND）与静音，并伴随地道的 One UI 双脉冲触感反馈。

### ✨ 核心特性

- ⚡ **零前摇极速响应**：基于重力与陀螺仪的高精防抖算法，翻转完成瞬间锁帧判定，响应毫无延迟。
- 📳 **One UI 沉浸震感**：深度调用三星原生 `VibrationEffect.Composition` API（`PRIMITIVE_THUD`），提供紧凑而清脆的双脉冲翻转触感。
- 🔒 **无障碍一键熄屏**：可选配合 Android 原生无障碍服务（`GLOBAL_ACTION_LOCK_SCREEN`），翻转开启勿扰时同步熄屏锁屏。
- 🚀 **Direct Boot (冷启动保护)**：支持 Android 13 Direct Boot 机制，设备重启后无需解锁屏幕即可自动恢复手势监听。
- 📦 **羽量级极简体积**：采用纯手写矢量图标库，正式 Release APK 体积**仅约 2.2 MB**，不占用系统内存。
- 🛡️ **完全离线与隐私保护**：无任何网络权限、无广告、无后台数据上报，零隐私泄露风险。

---

## 🇭🇰 🇹🇼 Flip to Shhh (繁體中文)

**Flip to Shhh** 是一款專為 Samsung Galaxy S 系列旗艦裝置打造的高效能、超低功耗常駐翻轉靜音小工具。只需將手機螢幕朝下放置在桌面上，即可順時開啟勿擾（DND）與靜音，並伴隨地道的 One UI 雙脈衝觸感反饋。

### ✨ 核心特色

- ⚡ **零前搖極速響應**：基於重力與陀螺儀的高精防抖演算法，翻轉完成瞬間鎖幀判定，響應毫無延遲。
- 📳 **One UI 沉浸震感**：深度調用三星原生 `VibrationEffect.Composition` API（`PRIMITIVE_THUD`），提供緊湊而清脆的雙脈衝翻轉觸感。
- 🔒 **無障礙一鍵熄屏**：可選配合 Android 原生無障礙服務（`GLOBAL_ACTION_LOCK_SCREEN`），翻轉開啟勿擾時同步熄屏鎖屏。
- 🚀 **Direct Boot (冷啟動保護)**：支援 Android 13 Direct Boot 機制，裝置重啟後無需解鎖螢幕即可自動恢復手勢監聽。
- 📦 **羽量級極簡體積**：採用純手寫向量圖示庫，正式 Release APK 體積**僅約 2.2 MB**，不佔用系統記憶體。
- 🛡️ **完全離線與隱私保護**：無任何網路權限、無廣告、無後台資料上報，零隱私洩露風險。

---

## 📄 License

This project is licensed under the [Apache License 2.0](LICENSE).
