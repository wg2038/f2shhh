# Flip to Shhh v1.3.0 修复说明 / Release Notes

> 发布日期 / Date: 2026-08-29 ｜ versionCode 7 ｜ 官方正常版（无彩蛋）/ Official edition (no easter egg)

## 中文

### 🐛 修复 / Fixed

**1. 切换语言后不会马上生效（v1.1.0 遗留问题）**

- **根因**：v1.1.0 中 `FlipToShhhScreen`（主界面）与 `SettingsBottomSheet`（设置面板）各自持有一份独立的语言状态。在语言选择器里切换语言时，只写入了偏好存储并更新了设置面板内部的状态，主界面与设置面板标题等 UI 不会跟着变，必须关闭设置面板（`onDismiss` 里重读偏好）或把应用切到后台再回来才会刷新。
- **修复**：语言状态收敛为单一数据流（`MainActivity → FlipToShhhScreen → SettingsBottomSheet`），选择语言后立即逐层回调更新，整个界面即时重组切换，无需任何额外操作。
- **服务端补齐**：前台服务通知及其通知渠道名此前也要等下一次翻转触发或服务重启才换语言。现在服务注册了偏好变更监听，语言一改，渠道名与前台通知立即以新语言重建。

**2. 繁体中文识别不完整**

- 跟随系统语言时只判断了地区（TW/HK/MO），系统语言设为 `zh-Hant`（繁体、无地区信息，部分机型的"中文（繁體）"选项）时会错误回退到简体。现已补充 script（`Hant`）判定，`AppStrings` 与服务通知文案两处同步修正。

**3. 其他**

- 服务读取语言偏好的硬编码键名 `"language_mode"` 改为统一使用 `PrefsKeys.KEY_LANGUAGE_MODE` 常量，消除双处维护隐患。

### 🧹 变更 / Changed

- **正常版回归"小而美"**：v2 彩蛋终端（Ubuntu 终端模拟器、neofetch、珊瑚海歌词播放）整体从官方 v1.x 线移除，关于页的 7 连击彩蛋入口一并删除，版本行改为纯展示。彩蛋版冻结在 [v2.1.5](https://github.com/wg2038/f2shhh/releases/tag/v2.1.5) 不再修改，需要彩蛋请安装该版本。
- 内嵌音频资源（2.7 MB OGG）随彩蛋移除，**APK 体积从约 4.9 MB 降至约 2.1 MB**。
- v2.1.5 期间对勿扰（DND）状态机、前台通知状态恢复等官方功能的修复全部保留。
- `versionCode` 6 → 7。

### ✅ 验证 / Verification

- `assembleDebug` 与 `assembleRelease` 全部构建通过。
- 真机（Redmi K30 5G，Android 13，1080×2400）冒烟测试：安装启动无崩溃、服务正常前台运行、设置面板切换 语言 → English **即时生效**（未关闭面板）。

---

## English

### Fixed

- **Language switch did not apply immediately** (v1.1.0 regression). Root cause: the settings bottom sheet and the main screen each kept their own language state; picking a language only wrote the preference and updated the sheet's private copy, so the rest of the UI waited until the sheet was dismissed (or the app was resumed) to re-read it. Language state is now a single source of truth flowing down from `MainActivity`, so every string recomposes the moment a language is picked. The foreground-service notification and its channel name also rebuild instantly on language change (previously they only refreshed on the next flip event or service restart).
- **Traditional Chinese detection**: script-only locales such as `zh-Hant` (no region) no longer fall back to Simplified Chinese.
- Service now reads the language preference via the shared `PrefsKeys` constant instead of a hardcoded key.

### Changed

- The official v1.x line no longer bundles the v2 easter-egg terminal player; the easter-egg edition stays frozen at [v2.1.5](https://github.com/wg2038/f2shhh/releases/tag/v2.1.5).
- APK size reduced from ~4.9 MB to ~2.1 MB after dropping the embedded audio asset.
- `versionCode` 7.

### Verification

- Debug and release builds pass; smoke-tested on a physical device (install, launch, foreground service, and instant in-place language switching).
