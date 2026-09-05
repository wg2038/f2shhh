# Flip to Shhh v2.1.5.2 Release Notes

发布日期：2026-09-05 ｜ versionCode 9

彩蛋特别版（珊瑚海终端播放器）的维护版本：把官方 v1.4.0 的全部健壮性修复移植到 v2 线，并修复审阅中发现的彩蛋终端问题。本版起彩蛋版恢复维护，不再冻结于 2.1.5。

## 修复

### 1. 彩蛋终端与其他音频应用抢播
- 原因：其他应用取得音频焦点时终端会暂停播放，但回到应用时的自动恢复只检查播放状态，不重新请求焦点；此前被抢焦后切回即无条件恢复播放，与对方混音。
- 修复：恢复播放前先重新请求音频焦点，仅在被授予时恢复。

### 2. 慢速拖动歌词超过 3 秒误触发长按快进
- 原因：3 秒长按跳转用点击检测器实现，只要手指按住满 3 秒就触发——在歌词列表上慢速拖动时同样成立。
- 修复：改为原始手势循环，指针位移超过 touch slop 即解除快进计时，正常滚动不受影响。

### 3. 彩蛋界面主线程 I/O
- 原因：neofetch 信息（/proc、/sys 文件读取、StatFs）与 2.8 MB OGG 歌词扫描均在主线程执行。
- 修复：全部移至 Dispatchers.IO，就绪后渲染。

### 4. 退出终端后音乐残留
- 原因：退出过渡动画期间终端组合仍持有 MediaPlayer，音频持续到过渡结束才释放。
- 修复：退出时立即暂停播放（释放仍在 dispose 中完成）。

### 5. 其他
- OGG 歌词提取加固：注释长度字段异常时判定为未对齐并放弃，避免把音频字节当歌词；"无内嵌歌词"提示在解析完成后再显示。
- 官方线修复全量移植（详见 CHANGELOG）：翻面倒计时期间传感器 ~50 Hz 过采样（手抖混叠）、移除恒为 ALL 的勿扰过滤器死机制、通知渠道按语言分 ID（切语言通知不再闪断）、zh-Hant 繁体判定、共享 `Localization` 模块、警告横幅跟随应用主题、底部导航栏高度重复计入、自动锁屏开关反映无障碍服务真实状态。

## 变更

- versionCode 6 → 7（沿用彩蛋线自身序列，低于官方 v1.4.0 的 8：两个版本相互独立、绝不互相覆盖升级，切换需显式卸载）；versionName 2.1.5 → 2.1.5.2。
- Release APK 改用与官方线一致的专用签名密钥；CI 通过加密 Secrets 签名并发布正确命名的产物。

## 验证

- `assembleDebug` 与 `assembleRelease` 构建通过，签名证书指纹为 `df3f630a…`（与 v1.4.0 相同）。
- 模拟器端到端：全新安装后 7 连击进入彩蛋终端，播放正常；慢速拖动歌词超过 3 秒不触发跳转；3 秒长按跳转到最后 10 秒并正常播放结尾 `sudo rm` 序列后自动退出；全程零崩溃。

## 升级说明

- 本版与官方版相互独立、互不覆盖：从官方 v1.4.0 切换到彩蛋版（或反向）需先卸载当前版本再安装。
- v2.1.5 / v2.1.5.1 为调试签名，升级到本版同样需先卸载。应用不产生用户数据。

---

# Flip to Shhh v2.1.5.2 Release Notes (English)

Date: 2026-09-05 ｜ versionCode 9

Maintenance release for the easter-egg edition (Coral Sea terminal player): ports all official-line robustness fixes through 1.4.0 onto the v2 code and fixes the easter-egg terminal issues found in review. The edition resumes maintenance and is no longer frozen at 2.1.5.

## Fixed

### 1. The terminal fought other audio apps
- Cause: the terminal paused on audio-focus loss, but the auto-resume on returning to the app never re-requested focus, so playback restarted silently on top of whatever app had taken focus.
- Fix: re-request audio focus before resuming; resume only when granted.

### 2. Slow lyric-list drags fired the long-press seek
- Cause: the 3-second hold-to-skip used a tap detector that fired whenever the finger stayed down for 3 s — including while slowly scrolling the lyrics.
- Fix: reimplemented as a raw gesture loop that disarms the seek timer as soon as the pointer moves beyond the touch slop.

### 3. Main-thread I/O on the easter-egg screen
- Cause: neofetch data (procfs/sysfs reads, StatFs) and the 2.8 MB OGG lyrics scan ran on the main thread.
- Fix: both moved to Dispatchers.IO.

### 4. Audio kept playing after exiting the terminal
- Cause: during the exit fade-out the leaving composition still held the MediaPlayer.
- Fix: playback is paused immediately on exit; the release still happens on dispose.

### 5. Other
- Lyrics extraction hardened: a misaligned OGG comment length is rejected instead of being read as lyric text; the "no embedded lyrics" notice only shows after parsing finished.
- All official-line fixes through 1.4.0 ported (see CHANGELOG): adaptive ~50 Hz sensor oversampling during the flip countdown, removal of the dead "previous interruption filter" mechanism, per-locale notification channels, zh-Hant script detection, shared `Localization` module, theme-aware warning banners, navigation-bar double padding, and the flip-to-lock switch reflecting the real accessibility state.

## Changed

- versionCode 6 → 7, following the easter-egg line's own numbering and staying below the official line (official v1.4.0 is 8): the two editions are independent and never upgrade over each other — switching requires an explicit uninstall. versionName 2.1.5 → 2.1.5.2.
- Release APKs are now signed with the dedicated release key shared with the official line; the release workflow signs via encrypted secrets and publishes a correctly named APK.

## Verification

- `assembleDebug` and `assembleRelease` build; APK signed with cert fingerprint `df3f630a…` (same as v1.4.0).
- Emulator end-to-end: fresh install, 7-click easter-egg entry, playback normal; a slow 3.5 s drag of the lyrics did NOT trigger the seek; the 3 s long-press seek jumped to the last 10 s, the closing `sudo rm` sequence played and the session exited on its own; zero crashes.

## Upgrade notes

- This edition and the official line are independent and never upgrade over each other: switching from official v1.4.0 to the easter-egg edition (or back) requires uninstalling the currently installed one first.
- v2.1.5 / v2.1.5.1 were debug-signed; upgrading from them also requires a one-time uninstall. The app stores no user data.
