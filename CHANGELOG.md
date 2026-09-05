# 📝 Changelog

All notable changes to **Flip to Shhh** will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.4.0] - 2026-09-05 (Official Release)

### Fixed
- **Hand-tremor filter no longer defeated by sampling aliasing**: gravity/gyroscope now oversample at ~50 Hz (`SENSOR_DELAY_GAME`) while a flip-down countdown is running, so 8–12 Hz hand tremor can no longer fold below the stillness thresholds at the previous ~15 Hz UI rate. The low-power UI rate is restored as soon as the countdown ends or is cancelled.
- **Language switching no longer flickers the foreground notification**: each locale gets its own notification channel; the old channel is retired only after the notification has been re-posted, instead of deleting the channel hosting the live notification.
- **Warning banners follow the in-app theme**: onboarding permission cards and the DND warning banner now derive dark/light warning colors from the active app theme instead of the system theme (mismatched colors when the in-app theme override differs from the system).
- **Flip-to-Lock switch reflects reality**: the settings switch now shows the effective state (preference AND accessibility service actually enabled) and re-checks whenever the app resumes.
- Bottom layout no longer double-counts the navigation-bar inset.
- Shared locale resolution extracted into `Localization` (used by both `AppStrings` and the service notification texts).

### Changed
- Removed the dead "previous interruption filter" bookkeeping: the service only ever activates DND from `INTERRUPTION_FILTER_ALL`, so restoring to ALL always reproduces the pre-flip state.
- Removed the unused `MODIFY_AUDIO_SETTINGS` permission.

---

## [1.3.0] - 2026-08-29 (Official Release)

### Fixed
- **Language switching now applies immediately**: v1.1.0 kept separate language states in the settings sheet and the main screen, so a newly picked language only appeared after closing the sheet or resuming the app. Language state is now a single source of truth (`MainActivity → FlipToShhhScreen → SettingsBottomSheet`) and recomposes the whole UI instantly.
- **Foreground service notification follows the language live**: the service observes the language preference and rebuilds its channel name and notification right away, instead of waiting for the next flip event or service restart.
- **Traditional Chinese detection**: script-only system locales such as `zh-Hant` (no region) no longer fall back to Simplified Chinese.
- Service reads the language preference via the shared `PrefsKeys.KEY_LANGUAGE_MODE` constant.

### Changed
- The official v1.x line no longer ships the v2 easter-egg terminal player (Ubuntu terminal, neofetch, embedded-lyrics music player); the About page easter-egg trigger was removed as well. The easter-egg edition stays frozen at [2.1.5].
- APK size reduced from ~4.9 MB to ~2.1 MB by dropping the embedded audio asset.
- All v2.1.5-era robustness fixes for the DND state machine and foreground notification state are retained.
- Upgraded build configuration to `versionCode 7` and `versionName 1.3.0`.

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
