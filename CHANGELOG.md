# 📝 Changelog

All notable changes to **Flip to Shhh** will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [2.1.5] - 2026-08-28 (Easter Egg Special Edition · 珊瑚海彩蛋特别版)

> Special emotionally-inspired release featuring an embedded Linux CLI terminal music player easter egg, coexisting alongside the official release line.

### Added
- **Ubuntu 26.04 CLI Terminal Player**: 7-click version easter egg with realistic TTY probe sequences and system cleanup animations.
- **Embedded Vorbis Comments Lyrics Engine**: Zero-network binary lyric parser decoding UTF-8 synchronized lyric streams directly from raw OGG metadata.
- **ANSI Singer Color-Coded Typography**: Dynamic terminal lyric highlighting (Male blue, Female yellow, Duet green).

### Changed
- Refactored `FlipToShhhService` DND teardown logic to guarantee idempotent StateFlow and persistent storage resets upon external permission revocation.
- Upgraded Gradle build configuration to `versionCode 5` and `versionName 2.1.5`.

---

## [1.1.0] - 2026-08-27 (Official Stable Release)

### Added
- **Samsung One UI Inspired Aesthetic**: Smooth breathing centerpiece, dynamic palette integration, and minimalist settings modal bottom sheet.
- **Full Tri-lingual Localization**: Seamless runtime switching between Simplified Chinese, Traditional Chinese, and English.
- **Biometric-Friendly Screen Locking**: Native `GLOBAL_ACTION_LOCK_SCREEN` integration via Accessibility Service.
- **Hand Tremor Stillness Filter**: 2.0s continuous table physical stillness evaluation preventing false triggers in mid-air.

### Changed
- Stripped 50MB+ material-icons-extended library down to 12 custom vector definitions in `AppIcons.kt`, achieving an ultra-lean ~2.2 MB APK size.
- Improved DND ownership tracking logic to respect existing system sleep schedules.

---

## [1.0.0] - 2026-08-10 (Initial Release)

### Added
- Core Pixel-grade Flip-to-Shhh gesture detection algorithm for Android 13+ devices.
- High-precision gravity vector and proximity sensor fusion.
- Dual-pulse haptic vibration engine.
- 100% offline architecture with zero network permissions.
