<p align="center">
  <h1 align="center">🤫 Flip to Shhh</h1>
  <p align="center">
    <strong>為非 Pixel 等 Android（13+）全品牌裝置提供的低功耗 Pixel 級翻轉靜音/勿擾工具</strong>
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

**Flip to Shhh** 是一款無廣告、超低功耗的常駐翻轉靜音工具。為廣大**非 Pixel 的 Android 13+ 手機**（Samsung One UI、小米 HyperOS、OPPO ColorOS、vivo OriginOS、OnePlus OxygenOS 等）帶來原生級 Flip-to-Shhh 翻轉勿擾體驗。

只需將手機螢幕朝下平放在桌面上保持 2 秒，即可伴隨清脆的雙脈衝觸感震動（「咚 - 咚」）自動開啟勿擾模式（DND）並聯動熄屏鎖屏。

---

## ⚡ 核心技術架構與亮點

### 1. Pixel 級精準手勢演算法
參考 Google Pixel 原生 Flip to Shhh 判定機制深度重構：
- **嚴格平放傾角約束**：垂直重力與空間水平傾角嚴格限制在 $\le 15^\circ$ 以內（$Z \le -9.3\text{ m/s}^2$、水平加速度分量 $\sqrt{X^2+Y^2} \le 2.0\text{ m/s}^2$），徹底杜絕手機斜靠、插袋或車架傾斜時誤觸發。
- **近距離感測器智慧融合**：動態校驗螢幕是否正對物理表面（`TYPE_PROXIMITY`），並在不同硬體方案間無縫優雅降級。
- **2 秒連續靜止時間窗口**：扣下後需在桌面保持 2 秒平穩靜止方可觸發，手持晃動或未平放直接重置計時。

### 2. 標準化雙脈衝觸感反饋
預設採用系統級觸覺引擎的雙脈衝質感震動（「咚 - 咚」，基於 `PRIMITIVE_THUD`），翻轉朝上時輔以輕柔微觸（`PRIMITIVE_CLICK`），提供極致沉浸與明確的操作反饋。

### 3. 硬體 FIFO 批量上報（50ms 延遲）
透過 `BATCH_LATENCY_US = 50_000` 訂閱 Sensor Hub 硬體 FIFO 隊列。感測器資料由低功耗 DSP 協處理器在 50ms 週期內批量處理，極大降低 CPU 喚醒頻率，實現近乎為零的後台功耗。

### 4. 無損鎖屏與 DND 智慧歸屬追蹤
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

本專案基於 [MIT License](LICENSE) 開源協議發布。
