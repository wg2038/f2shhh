<p align="center">
  <h1 align="center">🤫 Flip to Shhh</h1>
  <p align="center">
    <strong>为非 Pixel 等 Android（13+）全品牌设备提供的低功耗 Pixel 级翻转静音/勿扰工具</strong>
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
    <img src="docs/screenshots/main_screen.jpg" width="240" alt="主界面">
  </a>
  &nbsp;&nbsp;&nbsp;&nbsp;
  <a href="docs/screenshots/permissions_expanded.jpg">
    <img src="docs/screenshots/permissions_expanded.jpg" width="240" alt="权限管理">
  </a>
  &nbsp;&nbsp;&nbsp;&nbsp;
  <a href="docs/screenshots/settings_sheet.jpg">
    <img src="docs/screenshots/settings_sheet.jpg" width="240" alt="高级设置">
  </a>
</p>

---

## 📖 项目简介

**Flip to Shhh** 是一款无广告、超低功耗的常驻翻转静音工具。为广大**非 Pixel 的 Android 13+ 手机**（三星 One UI、小米 HyperOS、OPPO ColorOS、vivo OriginOS、一加 OxygenOS 等）带来原生级 Flip-to-Shhh 翻转勿扰体验。

只需将手机屏幕朝下平扣在桌面上保持 2 秒，即可伴随清脆的双脉冲触感震动（“咚 - 咚”）自动开启勿扰模式（DND）并联动熄屏锁屏。

---

## ⚡ 核心技术架构与亮点

### 1. Pixel 级精准手势算法
参考 Google Pixel 原生 Flip to Shhh 判定机制深度重构：
- **严格平放倾角约束**：垂直重力与空间水平倾角严格限制在 $\le 15^\circ$ 以内（$Z \le -9.3\text{ m/s}^2$、水平加速度分量 $\sqrt{X^2+Y^2} \le 2.0\text{ m/s}^2$），彻底杜绝手机斜靠、插袋或车架倾斜时误触发。
- **近距离传感器智能融合**：动态校验屏幕是否正对物理表面（`TYPE_PROXIMITY`），并在不同硬件方案间无缝优雅降级。
- **2 秒连续静止时间窗口**：扣下后需在桌面保持 2 秒平稳静止方可触发，手持晃动或未平放直接重置计时。

### 2. 标准化双脉冲触感反馈
默认采用系统级触觉引擎的双脉冲质感震动（“咚 - 咚”，基于 `PRIMITIVE_THUD`），翻转朝上时辅以轻柔微触（`PRIMITIVE_CLICK`），提供极致沉浸与明确的操作反馈。

### 3. 硬件 FIFO 批量上报（50ms 延时）
通过 `BATCH_LATENCY_US = 50_000` 订阅 Sensor Hub 硬件 FIFO 队列。传感器数据由低功耗 DSP 协处理器在 50ms 周期内批量处理，极大降低 CPU 唤醒频率，实现近乎为零的后台功耗。

### 4. 无损锁屏与 DND 智能归属追踪
- **完美保留生物识别解锁**：基于 Android 原生无障碍服务（`GLOBAL_ACTION_LOCK_SCREEN`），锁屏后仍可顺畅使用指纹与人脸解锁（避免了传统 DeviceAdmin API 导致必须输入 PIN/密码的缺陷）。
- **DND 智能归属追踪**：自动兼容系统定时勿扰（如 23:00–07:00）。若扣下手机前系统已被定时器开启勿扰，翻转朝上时**绝不会误将系统勿扰关掉或反向误拉起**，完全尊重系统自身的生命周期。

---

## 🛡️ 权限说明与隐私安全

- 🔒 **勿扰权限** (`ACCESS_NOTIFICATION_POLICY`)：用于切换系统 Do Not Disturb 状态。
- ♿ **无障碍服务** (`GLOBAL_ACTION_LOCK_SCREEN`)：可选权限，仅用于翻转扣下时调用系统原生熄屏锁屏。
- 🔋 **忽略电池优化**：确保后台传感器监听在后台清理下稳定存活。
- 🛡️ **100% 完全离线**：Manifest 中**未声明任何网络权限**（`INTERNET`），无广告、无任何数据统计与上报，零隐私泄露隐患。

---

## 🛠️ 编译构建指南

### 环境要求
- Android Studio Ladybug | 2024.2.1 或更新版本
- JDK 17
- Android SDK API 34

### 编译 Release 包

```bash
git clone https://github.com/wg2038/f2shhh.git
cd f2shhh
./gradlew assembleRelease
```

编译生成的 Release APK 位于：
`app/build/outputs/apk/release/app-release.apk`

---

## 📄 开源许可

本项目基于 [MIT License](LICENSE) 开源协议发布。
