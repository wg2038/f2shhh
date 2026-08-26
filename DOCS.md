# 📐 Flip to Shhh — 架构设计与性能优化文档

本文档详细记录了 **Flip to Shhh** 的核心架构设计、Pixel 级高精度翻转算法、双脉冲触感引擎实现以及 APK 体积优化实践。

---

## 一、 核心架构设计

项目遵循 Jetpack 现代 Android 架构规范，组件职责划分清晰：

| 组件名称 | 类型 | 职责说明 |
| :--- | :--- | :--- |
| `MainActivity.kt` | `ComponentActivity` | 单 Activity 界面展示、核心权限设置入口引导、One UI 风格设置面板与全屏 About 页。 |
| `FlipToShhhService.kt` | `Foreground Service` | 核心后台服务，负责重力/陀螺仪/近距离传感器数据监听、Pixel 级姿态判定、DND 状态切换与触感反馈。 |
| `FlipLockAccessibilityService.kt` | `AccessibilityService` | 无障碍服务，调用 `GLOBAL_ACTION_LOCK_SCREEN` 原生 API 实现同步熄屏锁屏。 |
| `BootReceiver.kt` | `BroadcastReceiver` | 开机自启广播接收器，支持 Direct Boot 冷启动恢复服务。 |
| `AppIcons.kt` | `Object` | 手写矢量图标库，替代 40MB+ 庞大的官方扩展图标依赖库。 |
| `AppStrings` | `Object` | 多语言（简 / 繁 / 英）动态本地化管理引擎。 |

---

## 二、 核心算法与硬核优化

### 1. Pixel 级高精度翻转判定与近距离融合算法 (v1.1)
- **解决痛点**：传统方案在手机斜靠、放置在倾斜表面或手持微倾时易误触发。
- **优化算法**：
  1. **严格平放倾角约束**：垂直重力分量 $Z \le -9.3\text{ m/s}^2$ 且空间水平倾角严格限制在 $\le 15^\circ$（水平加速度分量 $\sqrt{X^2+Y^2} \le 2.0\text{ m/s}^2$）。
  2. **近距离传感器（Proximity）智能融合**：动态校验屏幕正对物理表面（桌面/平坦表面），防止在半空中手持倾斜时误触发，并对无硬件近距离传感器机型优雅降级。
  3. **2 秒连续物理静止窗口**：扣下进入候选区域后，倒计时 2000ms（2 秒）期间必须持续满足物理静止（$\Delta G \le 0.25\text{ m/s}^2, \omega \le 0.15\text{ rad/s}$），任何晃动或倾斜均直接取消并重置计时。
  4. **灵敏退出与 DND 归属保护**：手机拿起或翻转（$Z > -7.5\text{ m/s}^2$ 或近距离 FAR）后 300ms 迅速恢复，且若扣下前系统已处于外部定时勿扰，翻起时绝不误关闭系统定时勿扰。

### 2. 标准化双脉冲触感反馈引擎
- 采用原生系统 `VibrationEffect.Composition` API：
  ```kotlin
  VibrationEffect.startComposition()
      .addPrimitive(VibrationEffect.Composition.PRIMITIVE_THUD, 1.0f)
      .addPrimitive(VibrationEffect.Composition.PRIMITIVE_THUD, 1.0f, 65)
      .compose()
  ```
- **DND 静音免压制机制**：先触发震感，再开启勿扰模式，并为 Vibrator 注入 `AudioAttributes.USAGE_ASSISTANCE_SONIFICATION` 属性，防止系统 DND 框架误杀背景触感。

### 3. APK 体积极限瘦身 (54.1 MB ➔ 2.2 MB)
- **原因分析**：原先引入了 `androidx.compose.material:material-icons-extended` 扩展包，其包含 10,000+ 个矢量图标类。
- **瘦身方案**：
  - 彻底剔除 `material-icons-extended` 依赖。
  - 创建 [`AppIcons.kt`](file:///Users/cicada/AndroidStudioProjects/F2shhh/app/src/main/java/com/example/f2shhh/AppIcons.kt)，仅手写项目所需的矢量图标路径。
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
./gradlew assembleRelease

# 安装 Release 包至连接的设备
adb uninstall com.example.f2shhh
adb install app/build/outputs/apk/release/app-release.apk
```

