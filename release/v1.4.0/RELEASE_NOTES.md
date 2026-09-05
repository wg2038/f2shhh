# Flip to Shhh v1.4.0 Release Notes

发布日期：2026-09-05 ｜ versionCode 8

## 修复

### 1. 手抖滤波在低速采样下可能失效（误触发风险）
- 原因：重力与陀螺仪此前以 `SENSOR_DELAY_UI`（约 15 Hz）注册，对 8–12 Hz 生理性手抖属于欠采样（奈奎斯特频率约 7.5 Hz），混叠后相邻帧差值可能长时间低于静止阈值，导致无光学接近传感器的设备（采用虚拟接近方案的机型）在手持悬空、屏幕朝下时被误判为静置桌面并误触发勿扰。
- 修复：仅在翻面倒计时运行期间将运动传感器过采样至约 50 Hz（`SENSOR_DELAY_GAME`），倒计时结束或取消后立即恢复低功耗 UI 档。抗手抖能力恢复至设计目标，平均功耗几乎不变。

### 2. 切换语言时前台通知闪断
- 原因：此前切换语言会删除当前承载前台通知的通知渠道并重建，删除动作连带取消渠道内的前台通知，通知栏出现闪烁，且重建会重置用户对该渠道的设置。
- 修复：通知渠道 ID 按显示语言派生；切换语言时先创建新渠道并把通知迁移过去，再退役旧渠道，全程通知不中断。

### 3. 警告横幅配色与主题不一致
- 原因：引导页权限卡片与主界面勿扰权限警告横幅按系统深浅色模式选色；当应用内主题（跟随系统/深色/浅色）与系统设置不一致时，会出现深色警告底色落在浅色卡片上的问题。
- 修复：两处均改为按当前生效主题取色。

### 4. 设置面板“翻转自动锁屏”开关与实际状态脱节
- 原因：开关此前仅反映偏好存储值，无障碍锁屏服务未启用时仍显示开启。
- 修复：开关现反映真实生效状态（偏好开启且无障碍服务已启用），并在应用每次回到前台时重新校验。

### 5. 其他
- 主界面底部间距不再重复计入导航栏高度（此前底部多出约一倍导航栏高度的空隙）。
- 移除未使用的 `MODIFY_AUDIO_SETTINGS` 权限。
- 简繁英语言判定逻辑抽取为共享模块 `Localization`，`AppStrings` 与前台服务通知文案共用同一实现，避免两处逻辑漂移。

## 变更

- 移除“上次的勿扰过滤器”持久化机制：服务只会在 `INTERRUPTION_FILTER_ALL`（勿扰关闭）时激活 DND，因此恢复到 ALL 即为翻转前状态，原机制为无实际作用的死代码，恢复逻辑已改为直接写明。
- versionCode 7 → 8。

## 验证

- `assembleDebug` 与 `assembleRelease` 构建通过。
- 真机（Redmi K30 5G，LineageOS，Elliptic 虚拟接近传感器）：安装启动正常、前台服务运行正常、平时传感器注册于低功耗档；语言切换双向验证通知渠道迁移，通知全程存活，无崩溃。
- 模拟器端到端：注入加速度 (0,0,-9.81) + 接近 NEAR 模拟屏幕朝下平放，2 秒去抖确认后 DND 自动开启，倒计时期间传感器自动升至约 50 Hz，结束后降回；翻回面朝上后 300 ms 去抖内 DND 恢复。全程零崩溃。

## 升级说明

- 本版起改用专用正式签名密钥（`CN=Flip to Shhh`，有效期 30 年；此前所有版本均为调试签名）。从 v1.3.0 或更早版本升级需先卸载旧版再安装一次，此后所有版本将可直接覆盖升级。
- 应用不产生用户数据，卸载重装无损失。

---

# Flip to Shhh v1.4.0 Release Notes (English)

Date: 2026-09-05 ｜ versionCode 8

## Fixed

### 1. Hand-tremor filter could be defeated by low-rate sampling (false trigger risk)
- Cause: gravity and gyroscope were registered at `SENSOR_DELAY_UI` (~15 Hz), which under-samples 8–12 Hz physiological hand tremor (Nyquist ≈ 7.5 Hz). After aliasing, frame-to-frame deltas can stay below the stillness thresholds, so on devices without an optical proximity sensor (virtual proximity implementations) a phone held face-down mid-air could be misjudged as resting on a table and trigger DND.
- Fix: while a flip-down countdown is running, the motion sensors are oversampled at ~50 Hz (`SENSOR_DELAY_GAME`); the low-power UI rate is restored as soon as the countdown ends or is cancelled. Tremor rejection is back to design targets with almost no change in average power draw.

### 2. Foreground notification flickered on language switch
- Cause: switching the language deleted the notification channel currently hosting the foreground notification and recreated it; deleting a channel cancels the notifications inside it, causing a visible flicker and resetting user settings for that channel.
- Fix: the channel ID is now derived from the display language. On a language switch the new channel is created, the notification is migrated to it, and only then is the old channel retired — the notification stays up throughout.

### 3. Warning banner colors did not follow the in-app theme
- Cause: the onboarding permission cards and the DND warning banner picked dark/light warning colors from the SYSTEM theme. When the in-app theme override (system/dark/light) differed from the system setting, dark warning colors could land on a light card.
- Fix: both places now derive colors from the active app theme.

### 4. “Flip to Lock Screen” switch did not reflect the actual state
- Cause: the switch only mirrored the stored preference and stayed on even when the accessibility lock service was disabled.
- Fix: the switch now shows the effective state (preference on AND accessibility service enabled) and re-checks whenever the app resumes.

### 5. Other
- The main-screen bottom spacing no longer double-counts the navigation-bar inset (previously produced an extra gap of roughly one navigation-bar height).
- Removed the unused `MODIFY_AUDIO_SETTINGS` permission.
- The Simplified/Traditional/English locale resolution was extracted into a shared `Localization` module used by both `AppStrings` and the service notification texts.

## Changed

- Removed the dead “previous interruption filter” bookkeeping: the service only ever activates DND from `INTERRUPTION_FILTER_ALL`, so restoring to ALL always reproduces the pre-flip state; the restore path now states this directly.
- versionCode 7 → 8.

## Verification

- `assembleDebug` and `assembleRelease` builds pass.
- Physical device (Redmi K30 5G, LineageOS, Elliptic virtual proximity sensor): install, launch and foreground service all normal; sensors registered at the low-power rate while idle; language switched both ways with the notification surviving channel migration; no crashes.
- Emulator end-to-end: injected acceleration (0,0,-9.81) + proximity NEAR to simulate face-down placement — DND activated after the 2 s debounce, sensors automatically bumped to ~50 Hz during the countdown and restored afterwards; flipping back restored DND within the 300 ms exit debounce. Zero crashes throughout.

## Upgrade notes

- Starting with this release the APK is signed with a dedicated release key (`CN=Flip to Shhh`, valid for 30 years; all previous builds used a debug signature). Upgrading from v1.3.0 or earlier requires a one-time uninstall before installing; every later version upgrades in place.
- The app stores no user data, so the uninstall loses nothing.
