# 📐 Flip to Shhh — 架构设计与性能优化文档

本文档详细记录了 **Flip to Shhh** 的核心架构设计、传感器姿态判定算法、One UI 震感引擎实现以及 APK 体积优化实践。

---

## 一、 核心架构设计

项目遵循 Jetpack 现代 Android 架构规范，组件职责划分清晰：

| 组件名称 | 类型 | 职责说明 |
| :--- | :--- | :--- |
| `MainActivity.kt` | `ComponentActivity` | 单 Activity 界面展示、权限状态引导、One UI 风格设置面板与全屏 About 页。 |
| `FlipToShhhService.kt` | `Foreground Service` | 核心后台服务，负责重力/陀螺仪传感器数据监听、姿态判定、DND 状态切换与触感反馈。 |
| `FlipLockAccessibilityService.kt` | `AccessibilityService` | 无障碍服务，调用 `GLOBAL_ACTION_LOCK_SCREEN` 原生 API 实现同步熄屏锁屏。 |
| `BootReceiver.kt` | `BroadcastReceiver` | 开机自启广播接收器，支持 Direct Boot 冷启动恢复服务。 |
| `AppIcons.kt` | `Object` | 手写矢量图标库，替代 40MB+ 庞大的官方扩展图标依赖库。 |
| `AppStrings` | `Object` | 多语言（简 / 繁 / 英）动态本地化管理引擎。 |

---

## 二、 核心算法与硬核优化

### 1. 0 毫秒前摇（Zero Pre-Delay）翻转防抖算法
- **传统方案痛点**：在手持晃动时频繁重置定时器，导致用户感觉需要翻转 3~4 秒才触发。
- **优化算法**：
  1. 传感器倾角进入平放朝下判定区域（重力 $Z \le -9.0 \text{ m/s}^2$ 且倾角 $< 25^\circ$）的瞬间，**立即锁定起始时间点 $T_0$**。
  2. 在后续倒计时（1s/2s/3s）中，微小手抖不重置 $T_0$。
  3. 倒计时结束瞬间进行最终姿态复核（Check Flat Face-Down），通过即触发。
  4. 陀螺仪与重力传感器采用 `SENSOR_DELAY_UI`（~50-60ms）采样率，兼顾高响应度与极低功耗。

### 2. 三星 One UI 沉浸双脉冲震感引擎
- 采用三星原生 `VibrationEffect.Composition` API：
  ```kotlin
  VibrationEffect.startComposition()
      .addPrimitive(VibrationEffect.Composition.PRIMITIVE_THUD, 1.0f, 0)
      .addPrimitive(VibrationEffect.Composition.PRIMITIVE_THUD, 1.0f, 65)
      .compose()
  ```
- **DND 静音免压制机制**：先触发震感，再开启勿扰模式，并为 Vibrator 注入 `AudioAttributes.USAGE_ASSISTANCE_SONIFICATION` 属性，防止系统 DND 框架误杀背景触感。

### 3. APK 体积极限瘦身 (54.1 MB ➔ 2.2 MB)
- **原因分析**：原先引入了 `androidx.compose.material:material-icons-extended` 扩展包，其包含 10,000+ 个矢量图标类。
- **瘦身方案**：
  - 彻底剔除 `material-icons-extended` 依赖。
  - 创建 [`AppIcons.kt`](file:///Users/cicada/AndroidStudioProjects/F2shhh/app/src/main/java/com/example/f2shhh/AppIcons.kt)，仅手写项目所需的 9 个矢量图标路径。
  - 正式 Release 包体积由 **54.1 MB 暴降至 2.2 MB**（缩减 96%）。

### 4. Direct Boot 冷启动支持
- 在 `AndroidManifest.xml` 中为 `BootReceiver` 与 `FlipToShhhService` 声明 `android:directBootAware="true"`。
- 监听 `Intent.ACTION_LOCKED_BOOT_COMPLETED` 广播，在用户重启手机且未输入锁屏密码前，提前拉起传感器监听服务。

---

## 三、 编译与构建说明

```bash
# 编译 Debug 测试包
./gradlew assembleDebug

# 编译 Release 正式包 (已配置 R8 Tree-Shaking 优化)
./gradlew assembleRelease -x lintVitalAnalyzeRelease

# 安装 Release 包至连接的设备
adb uninstall com.example.f2shhh
adb install app/build/outputs/apk/release/app-release.apk
```
