# 📐 Flip to Shhh — 架构设计与技术实现白皮书

本文档深入阐述 **Flip to Shhh** 的核心架构设计、Pixel 级高精度多传感器融合姿态算法、勿扰模式（DND）调用流程与状态机设计、触感震动引擎以及极限界限瘦身工程实践。

---

## 一、 核心架构组件与职责划分

项目遵循 Jetpack 现代 Android 架构设计规范，采用单 Activity + 前台服务 + 无障碍服务的组件化分工：

```
                             ┌─────────────────────────────────┐
                             │          MainActivity           │
                             │  (Jetpack Compose + One UI UI)  │
                             └────────────────┬────────────────┘
                                              │ 绑定/启动
                                              ▼
                             ┌─────────────────────────────────┐
                             │       FlipToShhhService         │
                             │   (Core Foreground Service)     │
                             └───────┬─────────────────┬───────┘
                                     │                 │
            ┌────────────────────────┴──────┐   ┌──────┴────────────────────────┐
            │                               │   │                               │
            ▼                               ▼   ▼                               ▼
 ┌─────────────────────┐       ┌──────────────────────┐   ┌───────────────────────────┐
 │   SensorManager     │       │ NotificationManager  │   │FlipLockAccessibilityService│
 │ • Gravity Sensor    │       │ • INTERRUPTION_FILTER│   │ • GLOBAL_ACTION_          │
 │ • Gyroscope Sensor  │       │ • DND Ownership Track│   │   LOCK_SCREEN (原生锁屏)   │
 │ • Proximity Sensor  │       └──────────────────────┘   └───────────────────────────┘
 └─────────────────────┘
```

| 组件模块 | 类型 | 核心职责 |
| :--- | :--- | :--- |
| [`MainActivity.kt`](app/src/main/java/com/example/f2shhh/MainActivity.kt) | `ComponentActivity` | 单 Activity 交互中心，承载极简呼吸感主控面板、核心权限向导、One UI 风格设置抽屉与关于页。 |
| [`FlipToShhhService.kt`](app/src/main/java/com/example/f2shhh/FlipToShhhService.kt) | `LifecycleService` | 常驻前台核心服务，负责多传感器事件监听、重力姿态解算、2.0 秒静止判定、DND 状态切换与触感震动反馈。 |
| [`FlipLockAccessibilityService.kt`](app/src/main/java/com/example/f2shhh/FlipLockAccessibilityService.kt) | `AccessibilityService` | 轻量无障碍服务，基于 `GLOBAL_ACTION_LOCK_SCREEN` 实现不破坏生物识别（指纹/面容）的原生锁屏。 |
| [`BootReceiver.kt`](app/src/main/java/com/example/f2shhh/BootReceiver.kt) | `BroadcastReceiver` | 监听系统 `BOOT_COMPLETED` 等广播，在系统冷启动或应用升级后自动拉起服务。 |
| [`AppIcons.kt`](app/src/main/java/com/example/f2shhh/AppIcons.kt) | `Object` | 手写矢量图标库，彻底剔除庞大的官方扩展图标依赖，实现包体极度瘦身。 |

---

## 二、 核心算法与实现原理

### 1. 空间向量姿态判定数学模型

为避免传统翻转工具在手机斜靠、放置在车载支架或手持倾斜时的频繁误触发，本项目采用多维空间向量约束：

#### (1) 垂直与水平重力约束
设设备当前重力分量为 $(X, Y, Z)$，重力加速度常量 $g \approx 9.81\text{ m/s}^2$：
- **屏幕朝下阈值**：$Z \le -9.0\text{ m/s}^2$（确保屏幕垂直向下）
- **水平倾角约束**：水平重力分量 $H = \sqrt{X^2 + Y^2} \le 2.5\text{ m/s}^2$
- **倾角换算**：
  $$\theta = \arctan\left(\frac{\sqrt{X^2 + Y^2}}{|Z|}\right) \le \arctan\left(\frac{2.5}{9.0}\right) \approx 15.5^\circ$$
  严格要求手机平放在水平面上，允许倾角在 $15^\circ$ 以内（兼容后置摄像头模组突起或保护壳垫高）。

#### (2) 退出恢复阈值（非对称滞后防抖）
- 当手机被拿起或倾斜时，满足 $Z > -7.5\text{ m/s}^2$ 或 $H > 3.5\text{ m/s}^2$（倾角 $\theta > 25^\circ$），仅需 300ms 即可迅速恢复响铃。非对称阈值设计彻底避免了临界角度下的频繁震荡。

---

### 2. 近距离传感器智能识别与平滑降级

```kotlin
private fun isHardwareOpticalProximity(sensor: Sensor?): Boolean {
    sensor ?: return false
    val name = sensor.name.lowercase()
    val vendor = sensor.vendor.lowercase()
    val isVirtual = name.contains("palm") || name.contains("touch") || name.contains("virtual") ||
            name.contains("ultrasound") || name.contains("elliptic") || name.contains("ear") ||
            name.contains("gesture") || vendor.contains("elliptic") || vendor.contains("samsung")
    return !isVirtual
}
```

- **实体光学感应器（Pixel / 小米 / 一加等）**：校验 `distance == 0.0` 或处于遮蔽态（`isProximityNear == true`），确保屏幕确实紧贴物理桌面。
- **虚拟/超声波感应器（部分三星/特定机型）**：部分虚拟感应器仅在通话时由系统模拟生效。服务能精准识别虚拟感应器特征，自动降级启用双轨微加速度与陀螺仪静止校验，杜绝因感应器不可用导致无法触发的问题。

---

### 3. 生理性手颤滤波与 2.0s 连续物理静止窗口

在手机平扣进入候选状态后，开启固定的 2000ms 倒计时。为防止用户在空中手持手机朝下时误触发：

```kotlin
// 陀螺仪角速度与加速度微波动检测
val isPhysicallyStationary = (currentDeltaG <= MAX_DELTA_G_FINAL_CHECK) && (currentGyroRotation <= MAX_GYRO_FINAL_CHECK)
```

- **人体生理手颤特征**：手持悬空时存在 8–12 Hz 的生理性肌肉震颤（$\omega > 0.05\text{ rad/s}$ 或 $\Delta G > 0.07\text{ m/s}^2$）。
- **重置机制**：在 2.0 秒倒计时期间，若检测到上述手颤波动，倒计时起始时间点 `faceDownStartTime` 立即重置，必须在刚性物体（如桌面）上保持平稳静止方可通过校验。

---

### 4. 勿扰模式调用流程与智能归属追踪

```
【手机扣下】 ──> 触发触感反馈 ──> 检查当前 InterruptionFilter ──> 记录 wasDndActivatedByService ──> 开启勿扰 ──> 触发熄屏锁屏
                                                                                                        
【手机拿起】 ──> wasDndActivatedByService == true ? 恢复先前模式 : 保留系统原状态 ──> 触发轻微微触 ──> 更新通知栏
```

- **触感震动免压制机制**：先触发震感，再开启勿扰模式，并为 Vibrator 显式注入 `AudioAttributes.USAGE_ASSISTANCE_SONIFICATION` 属性，防止系统 DND 框架在切入勿扰瞬间压制震动输出。
- **智能归属追踪（DND Ownership）**：服务记录 DND 是否由本应用激活。若用户在翻转前已处于定时勿扰（如 23:00–07:00 睡眠模式），翻起手机时绝不会误将外部系统的勿扰关闭，完全尊重系统原有定时策略。

---

### 5. 无损锁屏设计与生物识别解锁保留

传统锁屏工具多采用废弃的 `DevicePolicyManager.lockNow()`，会导致下次亮屏时强制要求输入 PIN 或密码，破坏指纹与面容解锁体验。

本项目基于 Android 原生无障碍服务（`AccessibilityService`）：
```kotlin
fun performLock(): Boolean {
    val service = instance ?: return false
    return service.performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
}
```
调用 `GLOBAL_ACTION_LOCK_SCREEN` 进行原生级息屏，锁屏后**完美保留指纹、面容等生物识别解锁功能**。

---

### 6. APK 体积极限瘦身（54.1 MB ➔ 2.2 MB）

- **痛点分析**：Compose 官方扩展库 `androidx.compose.material:material-icons-extended` 包含超过 10,000 个矢量图标类，导致空包体积膨胀逾 50MB。
- **工程瘦身实践**：
  1. 彻底剔除 `material-icons-extended` 依赖。
  2. 提取应用所需的 12 个核心图标，在 [`AppIcons.kt`](app/src/main/java/com/example/f2shhh/AppIcons.kt) 中手写精简 Path 矢量定义。
  3. 结合 R8 Tree-Shaking 与资源压缩，最终 Release APK 仅 **~2.2 MB**，体积缩减达 96%。

---

## 三、 编译与测试指令

```bash
# 1. 编译 Debug 开发包
./gradlew assembleDebug

# 2. 编译 Release 正式包
./gradlew assembleRelease

# 3. 安装至已连接的实体测试机
adb install -r app/build/outputs/apk/release/app-release.apk

# 4. 查看运行日志过滤
adb logcat -s FlipToShhh FlipLockAccessibility BootReceiver
```
