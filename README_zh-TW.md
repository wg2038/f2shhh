<p align="center">
  <h1 align="center">🤫 Flip to Shhh</h1>
  <p align="center">
    <strong>傾心專為 Samsung Galaxy 系列打造，兼具全 Android（13+）品牌裝置高精度相容的翻轉勿擾工具</strong>
  </p>
  <p align="center">
    <a href="README.md">English</a> •
    <a href="README_zh-CN.md">简体中文</a> •
    <a href="README_zh-TW.md">繁體中文</a>
  </p>
  <p align="center">
    <a href="https://github.com/wg2038/f2shhh/releases/latest"><img src="https://img.shields.io/github/v/release/wg2038/f2shhh?style=flat-square&color=blue" alt="Latest Release"></a>
    <img src="https://img.shields.io/badge/Platform-Android_13%2B_(API_33%2B)-brightgreen?style=flat-square&logo=android" alt="Platform">
    <img src="https://img.shields.io/badge/Tailored_For-Samsung_Galaxy_/_One_UI-0057FF?style=flat-square" alt="Samsung Galaxy">
    <img src="https://img.shields.io/badge/Language-Kotlin_/_Jetpack_Compose-7F52FF?style=flat-square&logo=kotlin" alt="Kotlin">
    <img src="https://img.shields.io/badge/APK_Size-~2.2_MB-success?style=flat-square" alt="Size">
    <a href="LICENSE"><img src="https://img.shields.io/badge/License-Apache_2.0-blue?style=flat-square" alt="License"></a>
  </p>
</p>

<br>

<p align="center">
  <a href="docs/screenshots/main_screen.jpg">
    <img src="docs/screenshots/main_screen.jpg" width="240" alt="主畫面">
  </a>
  &nbsp;&nbsp;&nbsp;&nbsp;
  <a href="docs/screenshots/permissions_expanded.jpg">
    <img src="docs/screenshots/permissions_expanded.jpg" width="240" alt="權限管理">
  </a>
  &nbsp;&nbsp;&nbsp;&nbsp;
  <a href="docs/screenshots/settings_sheet.jpg">
    <img src="docs/screenshots/settings_sheet.jpg" width="240" alt="高級設定">
  </a>
</p>

---

## 📖 專案簡介

**Flip to Shhh** 是一款無廣告、超低功耗的常駐翻轉靜音工具。本軟體**立項初衷與核心優化傾心專為 Samsung Galaxy 系列手機（One UI）深度客製**，提供媲美原生的 One UI 雙脈衝觸感震動與功耗調優；同時具備極佳的通用性，**全面相容其他所有 Android 13+ 品牌手機**。

只需將手機螢幕朝下放置在桌面上，即可瞬時開啟勿擾模式（DND）並自動熄屏鎖屏。

---

## ⚡ 核心技術架構與亮點

### 1. 專為三星客製，全品牌通用相容
軟體設計初衷旨在補全三星 Galaxy One UI 缺少的「翻轉靜音（Flip-to-Shhh）」原生手勢，針對三星硬體架構進行了深度契合與觸感調優；同時採用了通用的 Android 標準架構，完美相容小米、OPPO、vivo、榮耀、Pixel 等所有 Android 13+ 裝置。

### 2. 純重力向量演算法（不依賴光線/近距離感測器）
不同於傳統靜音應用依賴屏下光線/近距離感測器持續輪詢（會導致三星屏下光線感測器發熱與阻斷 CPU 深度休眠），**Flip to Shhh** 完全基於 `TYPE_GRAVITY`（重力向量）與 `TYPE_GYROSCOPE`（陀螺儀）融合演算法，保證裝置進入 Deep Sleep 深度休眠。

### 3. 硬體 FIFO 批量上報（50ms 延遲）
透過 `BATCH_LATENCY_US = 50_000` 訂閱 Sensor Hub 硬體 FIFO 隊列。感測器資料由低功耗 DSP 協處理器在 50ms 週期內批量處理，極大降低 CPU 喚醒頻率，實現近乎為零的後台功耗。

### 4. 雙閾值遲滯（Hysteresis）與物理靜止校驗
為防止手持走路、口袋晃動或桌面微顫引發誤觸發：
- **進入條件**：垂直重力 $Z \le -9.0\text{ m/s}^2$、水平傾角分量 $\sqrt{X^2+Y^2} \le 1.8\text{ m/s}^2$（傾角最大約 23°）、加速度變化率 $\Delta G \le 0.15\text{ m/s}^2$、陀螺儀角速度 $\omega \le 0.08\text{ rad/s}$。
- **退出條件**：$Z > -7.0\text{ m/s}^2$ 或水平分量 $> 2.8\text{ m/s}^2$。

### 5. 無損鎖屏與 DND 智慧歸屬追蹤
- **完美保留生物識別解鎖**：基於 Android 原生無障礙服務（`GLOBAL_ACTION_LOCK_SCREEN`），鎖屏後仍可順暢使用指紋與人臉解鎖（避免了傳統 DeviceAdmin API 導致必須輸入 PIN/密碼的缺陷）。
- **DND 智慧歸屬追蹤**：自動相容系統定時勿擾（如 23:00–07:00）。若扣下手機前系統已被定時器開啟勿擾，翻轉朝上時**絕不會誤將系統勿擾關閉或反向誤拉起**，完全尊重系統自身的生命周期。

---

## 🛡️ 權限說明與隱私安全

- 🔒 **勿擾權限** (`ACCESS_NOTIFICATION_POLICY`)：用於切換系統 Do Not Disturb 狀態。
- ♿ **無障礙服務** (`GLOBAL_ACTION_LOCK_SCREEN`)：可選權限，僅用於翻轉扣下時調用系統原生熄屏鎖屏。
- 🔋 **忽略電池優化**：確保後台感測器監聽在後台清理下穩定存活。
- 🛡️ **100% 完全離線**：Manifest 中**未聲明任何網絡權限**（`INTERNET`），無廣告、無任何資料統計與上報，零隱私洩露隱患。

---

## 🛠️ 編譯構建指南

### 環境要求
- Android Studio Ladybug | 2024.2.1 或更新版本
- JDK 17
- Android SDK API 34

### 編譯 Release 包

```bash
git clone https://github.com/wg2038/f2shhh.git
cd f2shhh
./gradlew assembleRelease
```

編譯產生的 Release APK 位於：
`app/build/outputs/apk/release/app-release.apk`

---

## 📄 開源許可

本專案基於 [Apache License 2.0](LICENSE) 開源協議發布。
