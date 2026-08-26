<p align="center">
  <h1 align="center">🤫 Flip to Shhh</h1>
  <p align="center">
    <strong>Ultra-Low Power, Pixel-Grade Flip-to-DND Utility for Non-Pixel Android (13+) Devices</strong>
  </p>
  <p align="center">
    <a href="README.md">English</a> •
    <a href="README_zh-CN.md">简体中文</a> •
    <a href="README_zh-TW.md">繁體中文</a>
  </p>
  <p align="center">
    <a href="https://github.com/wg2038/f2shhh/releases/latest"><img src="https://img.shields.io/github/v/release/wg2038/f2shhh?style=flat-square&color=blue" alt="Latest Release"></a>
    <img src="https://img.shields.io/badge/Platform-Android_13%2B_(API_33%2B)-brightgreen?style=flat-square&logo=android" alt="Platform">
    <img src="https://img.shields.io/badge/Designed_For-Non--Pixel_Android_13%2B-0057FF?style=flat-square" alt="Android 13+">
    <img src="https://img.shields.io/badge/Language-Kotlin_/_Jetpack_Compose-7F52FF?style=flat-square&logo=kotlin" alt="Kotlin">
    <img src="https://img.shields.io/badge/APK_Size-~2.2_MB-success?style=flat-square" alt="Size">
    <a href="LICENSE"><img src="https://img.shields.io/badge/License-MIT-blue?style=flat-square" alt="License"></a>
  </p>
</p>

<br>

<p align="center">
  <a href="docs/screenshots/main_screen.jpg">
    <img src="docs/screenshots/main_screen.jpg" width="240" alt="Main Screen">
  </a>
  &nbsp;&nbsp;&nbsp;&nbsp;
  <a href="docs/screenshots/permissions_expanded.jpg">
    <img src="docs/screenshots/permissions_expanded.jpg" width="240" alt="Permissions Panel">
  </a>
  &nbsp;&nbsp;&nbsp;&nbsp;
  <a href="docs/screenshots/settings_sheet.jpg">
    <img src="docs/screenshots/settings_sheet.jpg" width="240" alt="Settings Sheet">
  </a>
</p>

---

## 📖 Introduction

**Flip to Shhh** is a high-performance, ultra-lightweight, and zero-ad background utility that brings Google Pixel's signature **Flip to Shhh (Flip to Do Not Disturb)** feature to all non-Pixel Android 13+ devices (Samsung One UI, Xiaomi HyperOS, OPPO ColorOS, vivo OriginOS, OnePlus OxygenOS, etc.).

Simply place your phone face down flat on a surface for 2 seconds to activate Do Not Disturb (DND) mode with crisp dual-pulse tactile haptic feedback and optional screen lock.

---

## ⚡ Core Technical Architecture & Highlights

### 1. Pixel-Grade Precision Gesture Detection
Re-engineered based on Google Pixel's Flip-to-Shhh detection principles:
- **Strict Flat Surface Constraint**: Evaluates device spatial tilt angle $\le 15^\circ$ ($Z \le -9.3\text{ m/s}^2$, horizontal acceleration $\sqrt{X^2+Y^2} \le 2.0\text{ m/s}^2$), preventing accidental triggers when the phone is leaning, resting on slanted cushions, or in vehicle cradles.
- **Proximity Sensor Fusion**: Dynamically verifies that the screen is facing a flat physical surface (`TYPE_PROXIMITY`), with graceful zero-overhead fallback for hardware variants.
- **Continuous 2.0s Stillness Window**: Requires continuous physical stillness during the 2-second debounce period. Any pickup or tilt instantly resets the timer.

### 2. Standardized Dual-Pulse Haptic Feedback
Defaults to native dual-pulse haptic vibration ("咚 - 咚") on flip-down via `VibrationEffect.Composition.PRIMITIVE_THUD` and a subtle tactile click on flip-up, delivering a refined and consistent user experience.

### 3. Hardware FIFO Batching (50ms Latency)
Utilizes hardware-level sensor batching (`BATCH_LATENCY_US = 50_000`) offloaded to low-power Sensor Hub DSP. Sensor data is processed in batches every 50ms, eliminating CPU wake locks and preserving Deep Sleep battery life.

### 4. Non-Destructive Lock Screen & Smart DND Ownership
- **Biometric Unlock Preserved**: Built on Android's native Accessibility Service (`GLOBAL_ACTION_LOCK_SCREEN`), allowing seamless Fingerprint and Face Unlock afterwards (unlike legacy DeviceAdmin APIs which force PIN entry).
- **Smart DND Ownership Tracking**: Prevents conflicts with system-scheduled DND (e.g., 23:00–07:00). If DND was already active prior to flipping, flipping face-up will not override or forcibly disable your scheduled DND rule.

---

## 🛡️ Permissions & Privacy Safety

- 🔒 **Do Not Disturb Access** (`ACCESS_NOTIFICATION_POLICY`): Required to toggle system DND state.
- ♿ **Accessibility Service** (`GLOBAL_ACTION_LOCK_SCREEN`): Optional, used exclusively to lock screen on flip-down.
- 🔋 **Ignore Battery Optimization**: Ensures background sensor monitoring survives battery management.
- 🛡️ **100% Offline & Zero Data Collection**: Contains **ZERO network permissions** (`INTERNET` permission is omitted in manifest), zero ads, and zero analytics.

---

## 🛠️ Build & Installation Guide

### Prerequisites
- Android Studio Ladybug | 2024.2.1 or newer
- JDK 17
- Android SDK API 34

### Building Release APK
Clone the repository and compile the release binary:

```bash
git clone https://github.com/wg2038/f2shhh.git
cd f2shhh
./gradlew assembleRelease
```

The optimized APK will be generated at:
`app/build/outputs/apk/release/app-release.apk`

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).

