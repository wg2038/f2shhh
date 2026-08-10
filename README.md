<p align="center">
  <h1 align="center">🤫 Flip to Shhh</h1>
  <p align="center">
    <strong>Ultra-Low Power, Precision Flip-to-Mute Utility Tailored for Samsung Galaxy S Series (Android 13+)</strong>
  </p>
  <p align="center">
    <a href="README.md">English</a> •
    <a href="README_zh-CN.md">简体中文</a> •
    <a href="README_zh-TW.md">繁體中文</a>
  </p>
  <p align="center">
    <a href="https://github.com/wg2038/f2shhh/releases/latest"><img src="https://img.shields.io/github/v/release/wg2038/f2shhh?style=flat-square&color=blue" alt="Latest Release"></a>
    <img src="https://img.shields.io/badge/Platform-Android_13%2B_(API_33%2B)-brightgreen?style=flat-square&logo=android" alt="Platform">
    <img src="https://img.shields.io/badge/Design-One_UI_6.x_/_Material_3-0057FF?style=flat-square" alt="One UI">
    <img src="https://img.shields.io/badge/Language-Kotlin_/_Jetpack_Compose-7F52FF?style=flat-square&logo=kotlin" alt="Kotlin">
    <img src="https://img.shields.io/badge/APK_Size-~2.2_MB-success?style=flat-square" alt="Size">
    <a href="LICENSE"><img src="https://img.shields.io/badge/License-Apache_2.0-blue?style=flat-square" alt="License"></a>
  </p>
</p>

<p align="center">
  <img src="docs/screenshots/main_screen.jpg" width="240" alt="Main Screen">
  &nbsp;&nbsp;
  <img src="docs/screenshots/permissions_expanded.jpg" width="240" alt="Permissions Panel">
  &nbsp;&nbsp;
  <img src="docs/screenshots/settings_sheet.jpg" width="240" alt="Settings Sheet">
</p>

---

## 📖 Introduction

**Flip to Shhh** is a high-performance, ultra-lightweight, and zero-ad background service designed specifically for Samsung Galaxy S Series flagship smartphones (Android 13+). Simply flip your phone face down on a surface to instantly activate Do Not Disturb (DND) mode and lock your screen, accompanied by authentic Samsung One UI haptic tactile feedback.

---

## ⚡ Core Technical Architecture & Highlights

### 1. Pure Gravity Vector Algorithm (No Light Sensor Dependency)
Unlike generic flip-to-mute apps that continuously poll proximity or under-display light sensors (which cause CPU wake locks and OLED panel battery drain on Samsung flagships), **Flip to Shhh** relies exclusively on the fused `TYPE_GRAVITY` and `TYPE_GYROSCOPE` hardware sensors. This guarantees zero impact on device Deep Sleep.

### 2. Hardware FIFO Batching (50ms Latency)
Utilizes hardware-level sensor batching (`BATCH_LATENCY_US = 50_000`) offloaded to the low-power Sensor Hub DSP. Sensor data is processed in batches every 50ms, drastically reducing CPU interrupts and eliminating power consumption during idle state.

### 3. Dual-Threshold Hysteresis & Stillness Verification
To prevent accidental triggers caused by micro-vibrations or placing the phone in a pocket:
- **Enter Condition**: $Z \le -9.0\text{ m/s}^2$, Horizontal Gravity $\sqrt{X^2+Y^2} \le 1.8\text{ m/s}^2$ (max ~23° tilt angle), Acceleration Delta $\Delta G \le 0.15\text{ m/s}^2$, and Rotational Velocity $\omega \le 0.08\text{ rad/s}$.
- **Exit Condition**: $Z > -7.0\text{ m/s}^2$ or Horizontal Gravity $> 2.8\text{ m/s}^2$.

### 4. Non-Destructive Lock Screen & Smart DND Ownership
- **Biometric Lock Preserved**: Built on Android's native Accessibility Service (`GLOBAL_ACTION_LOCK_SCREEN`), allowing seamless Fingerprint and Face Unlock afterwards (unlike legacy DeviceAdmin APIs which force PIN entry).
- **Smart DND Ownership Tracking**: Prevents conflicts with system-scheduled DND (e.g., 23:00–07:00). If DND was already active prior to flipping, flipping face-up will not override or forcibly disable your scheduled DND rule.

---

## 🛡️ Permissions & Privacy Safety

- 🔒 **Do Not Disturb Access** (`ACCESS_NOTIFICATION_POLICY`): Required to toggle system DND state.
- ♿ **Accessibility Service** (`GLOBAL_ACTION_LOCK_SCREEN`): Optional, used exclusively to lock screen on flip-down.
- 🔋 **Ignore Battery Optimization**: Ensures background sensor monitoring survives One UI memory management.
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

This project is licensed under the [Apache License 2.0](LICENSE).
