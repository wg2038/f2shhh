# Flip to Shhh v1.3.0 Release Notes

发布日期：2026-08-29 ｜ versionCode 7

## 修复

### 1. 语言切换后不立即生效（v1.1.0 遗留问题）
- 原因：v1.1.0 中主界面与设置面板各自维护一份语言状态，切换语言时仅写入偏好存储并更新设置面板内部状态，其余界面文案需在关闭设置面板或应用回到前台后才刷新。
- 修复：语言状态收敛为单一数据流（`MainActivity → FlipToShhhScreen → SettingsBottomSheet`），选择语言后立即更新全部界面文案，无需额外操作。
- 服务端：前台服务通知与通知渠道名此前需等待下一次翻转事件或服务重启才会更新语言；现服务监听语言偏好变更，切换后立即重建通知与渠道。

### 2. 繁体中文识别不完整
- 跟随系统语言时此前仅依据地区（TW/HK/MO）判断繁体，`zh-Hant`（无地区信息）等基于文字代码的系统语言会被错误回退到简体。已补充 script（`Hant`）判定，`AppStrings` 与服务通知文案两处同步修正。

### 3. 其他
- 服务读取语言偏好改用 `PrefsKeys.KEY_LANGUAGE_MODE` 常量，替换硬编码键名，统一维护入口。

## 变更

- 官方 v1.x 版本移除 v2 彩蛋终端（终端模拟器、neofetch、内嵌歌词播放器及 2.7 MB 音频资源），关于页的彩蛋触发入口一并移除。彩蛋版本保留于 [v2.1.5](https://github.com/wg2038/f2shhh/releases/tag/v2.1.5)，后续不再更新。
- APK 体积由约 4.9 MB 降至约 2.1 MB。
- 保留 v2.1.5 期间对 DND 状态机与前台通知状态恢复的既有修复。
- versionCode 6 → 7。

## 验证

- `assembleDebug` 与 `assembleRelease` 构建通过。
- Android 13 真机测试：安装启动正常、前台服务运行正常、设置面板内切换语言立即生效。

---

# Flip to Shhh v1.3.0 Release Notes (English)

Date: 2026-08-29 ｜ versionCode 7

## Fixed

### 1. Language switch did not apply immediately (v1.1.0 regression)
- Cause: the main screen and the settings sheet each kept their own language state. Picking a language only wrote the preference and updated the sheet's internal copy; the rest of the UI refreshed only after the sheet was dismissed or the app was resumed.
- Fix: language state is now a single source of truth (`MainActivity → FlipToShhhScreen → SettingsBottomSheet`); the whole UI updates the moment a language is picked.
- Service side: the foreground service notification and its channel name previously updated only on the next flip event or service restart; the service now observes the language preference and rebuilds them immediately on change.

### 2. Incomplete Traditional Chinese detection
- Following the system locale, Traditional Chinese was detected by region (TW/HK/MO) only, so script-only locales such as `zh-Hant` (no region) incorrectly fell back to Simplified Chinese. Script (`Hant`) detection has been added, applied consistently in both `AppStrings` and the service notification texts.

### 3. Other
- The service now reads the language preference via the shared `PrefsKeys.KEY_LANGUAGE_MODE` constant instead of a hardcoded key.

## Changed

- The official v1.x line no longer bundles the v2 easter-egg terminal (terminal emulator, neofetch, embedded-lyrics player, and a 2.7 MB audio asset); the About-page easter-egg trigger was removed as well. The easter-egg edition remains at [v2.1.5](https://github.com/wg2038/f2shhh/releases/tag/v2.1.5) and will not receive further updates.
- APK size reduced from ~4.9 MB to ~2.1 MB.
- Existing v2.1.5-era fixes for the DND state machine and foreground notification state are retained.
- versionCode 6 → 7.

## Verification

- `assembleDebug` and `assembleRelease` build successfully.
- Tested on an Android 13 device: install, launch, foreground service, and immediate in-place language switching all work as expected.
