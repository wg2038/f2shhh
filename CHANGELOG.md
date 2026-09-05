# 📝 Changelog

All notable changes to **Flip to Shhh** will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [2.1.5.3] - 2026-09-05 (Easter Egg Polish · 彩蛋收尾)

### Added
- **Easter-egg neofetch install date**: the fictional system now reports when it was "installed". (Shipped in 2.1.5.2's build; versioned here.)

### Changed
- `versionName` 2.1.5.2 → 2.1.5.3; `versionCode` stays 7 — the easter-egg line keeps its own numbering and never climbs toward the official line's numbers (official v1.4.0 is 8), so the two editions remain independent and never upgrade over each other.
- Android CI now also builds the easter-egg line's release branches (`release-v2.*`).
- README (three languages) refreshed for the easter-egg line: release badge and APK size (~5.1 MB including the embedded audio).

### Fixed
- Removed an unused import left behind by the long-press gesture rework.

---

## [2.1.5.2] - 2026-09-05 (Easter Egg Maintenance · 彩蛋维护版)

> Maintenance release for the easter-egg edition: ports the official v1.4.0 robustness fixes onto the v2 line and fixes the easter-egg terminal issues found in review. The edition resumes maintenance; it is no longer frozen at 2.1.5.

### Added
- **Easter-egg neofetch install date**: the fictional system now reports when it was "installed".

### Fixed
- **Easter-egg terminal no longer fights other audio apps**: returning to the app after another player took audio focus re-requests focus before resuming instead of silently restarting playback on top of it.
- **Slow drags of the lyric list no longer fire the long-press seek**: the 3-second hold-to-skip gesture is now a raw gesture loop that disarms as soon as the pointer moves beyond the touch slop, so scrolling the lyrics for longer than 3 s cannot jump to the last 10 seconds mid-scroll.
- **Main-thread I/O removed from the easter-egg screen**: neofetch data (procfs/sysfs reads, StatFs) and the OGG lyrics scan now run on Dispatchers.IO.
- **Exiting the terminal stops the audio immediately** instead of letting the song ring on through the AnimatedContent fade-out.
- **Lyrics extraction hardened**: a misaligned OGG comment length field is rejected instead of swallowing audio bytes as lyric text; the "no embedded lyrics" notice only appears after parsing has finished.
- **Ported all official-line fixes through 1.4.0**: adaptive ~50 Hz sensor oversampling during the flip countdown (hand-tremor aliasing), removal of the dead "previous interruption filter" mechanism, per-locale notification channels (no flicker on language switch), zh-Hant script detection, shared `Localization` module, theme-aware warning banners, navigation-bar double padding, and the flip-to-lock switch reflecting the real accessibility state.

### Changed
- `versionCode` 6 → 7, following the easter-egg line's own numbering and staying below the official line (official v1.4.0 is 8): the two editions are independent and never upgrade over each other — switching requires an explicit uninstall. `versionName` 2.1.5 → 2.1.5.2.
- Release APKs are now signed with the dedicated release key shared with the official line; the release workflow signs via encrypted secrets and publishes a correctly named APK.

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
