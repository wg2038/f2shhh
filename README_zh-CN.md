<p align="center">
  <h1 align="center">🤫 Flip to Shhh</h1>
  <p align="center">
    <strong>专为 Android 13+ 打造的高精度、超低功耗常驻翻转勿扰工具（深度兼容三星 Galaxy、小米/HyperOS 等主流品牌）</strong>
  </p>
  <p align="center">
    <a href="README.md">English</a> •
    <a href="README_zh-CN.md">简体中文</a> •
    <a href="README_zh-TW.md">繁體中文</a>
  </p>
  <p align="center">
    <a href="https://github.com/wg2038/f2shhh/releases/latest"><img src="https://img.shields.io/github/v/release/wg2038/f2shhh?style=flat-square&color=blue" alt="Latest Release"></a>
    <img src="https://img.shields.io/badge/Platform-Android_13%2B_(API_33%2B)-brightgreen?style=flat-square&logo=android" alt="Platform">
    <img src="https://img.shields.io/badge/Compatibility-Samsung_/_Xiaomi_/_Pixel_/_Android-0057FF?style=flat-square" alt="Compatibility">
    <img src="https://img.shields.io/badge/Language-Kotlin_/_Jetpack_Compose-7F52FF?style=flat-square&logo=kotlin" alt="Kotlin">
    <img src="https://img.shields.io/badge/APK_Size-~2.2_MB-success?style=flat-square" alt="Size">
    <a href="LICENSE"><img src="https://img.shields.io/badge/License-Apache_2.0-blue?style=flat-square" alt="License"></a>
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

**Flip to Shhh** 是一款专为 Android 13+ 智能手机打造的无广告、超低功耗常驻翻转静音工具。只需将手机屏幕朝下扣在桌面上，即可瞬时开启勿扰模式（DND）并自动熄屏锁屏。

虽然初始设计深度针对三星 One UI 进行触感调优，但由于全量采用 Android 框架原生标准 API（`TYPE_GRAVITY` 重力向量、`GLOBAL_ACTION_LOCK_SCREEN` 原生锁屏等），应用在**小米/MIUI/HyperOS**、一加、Google Pixel 等各类 Android 机型上均具备极高的稳定度与兼容性。

---

## ⚡ 核心技术架构与亮点

### 1. 纯重力向量算法（不依赖光线/近距离传感器）
不同于传统静音应用依赖屏下光线/近距离传感器持续轮询（会导致屏下光线传感器发热与阻断 CPU 深度休眠），**Flip to Shhh** 完全基于 `TYPE_GRAVITY`（重力向量）与 `TYPE_GYROSCOPE`（陀螺仪）融合算法，保证设备进入 Deep Sleep 深度休眠。

### 2. 硬件 FIFO 批量上报（50ms 延时）
通过 `BATCH_LATENCY_US = 50_000` 订阅 Sensor Hub 硬件 FIFO 队列。传感器数据由低功耗 DSP 协处理器在 50ms 周期内批量处理，极大降低 CPU 唤醒频率，实现近乎为零的后台功耗。

### 3. 双阈值迟滞（Hysteresis）与物理静止校验
为防止手持走路、口袋晃动或桌面微颤引发误触发：
- **进入条件**：垂直重力 $Z \le -9.0\text{ m/s}^2$、水平倾角分量 $\sqrt{X^2+Y^2} \le 1.8\text{ m/s}^2$（倾角最大约 23°）、加速度变化率 $\Delta G \le 0.15\text{ m/s}^2$、陀螺仪角速度 $\omega \le 0.08\text{ rad/s}$。
- **退出条件**：$Z > -7.0\text{ m/s}^2$ 或水平分量 $> 2.8\text{ m/s}^2$。

### 4. 无损锁屏与 DND 智能归属追踪
- **完美保留生物识别解锁**：基于 Android 原生无障碍服务（`GLOBAL_ACTION_LOCK_SCREEN`），锁屏后仍可顺畅使用指纹与人脸解锁（避免了传统 DeviceAdmin API 导致必须输入 PIN/密码的缺陷）。
- **DND 智能归属追踪**：自动兼容系统定时勿扰（如 23:00–07:00）。若扣下手机前系统已被定时器开启勿扰，翻转朝上时**绝不会误将系统勿扰关掉或反向误拉起**，完全尊重系统自身的生命周期。

---

## 🛡️ 权限说明与隐私安全

- 🔒 **勿扰权限** (`ACCESS_NOTIFICATION_POLICY`)：用于切换系统 Do Not Disturb 状态。
- ♿ **无障碍服务** (`GLOBAL_ACTION_LOCK_SCREEN`)：可选权限，仅用于翻转扣下时调用系统原生熄屏锁屏。
- 🔋 **忽略电池优化**：确保后台传感器监听在系统内存清理下稳定存活。
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

本项目基于 [Apache License 2.0](LICENSE) 开源协议发布。
