# Flip to Shhh v2.1.5.3 Release Notes

发布日期：2026-09-05 ｜ versionCode 7

彩蛋线的收尾版本。功能与 2.1.5.2 一致（含 neofetch 安装日期），只做维护性收尾，不再有行为变更。

## 变更

- `versionName` 2.1.5.2 → 2.1.5.3；`versionCode` 保持 7——彩蛋线沿用自身序列，不向官方线（v1.4.0 为 8）的数字爬升，两个版本保持独立、绝不互相覆盖升级。
- Android CI 现在同时构建彩蛋线的 release 分支（`release-v2.*`）。
- 三语 README 面向彩蛋线刷新：发布徽章与 APK 体积（含内嵌音频约 5.1 MB）。
- 移除长按手势重构遗留的无用 import。

## 升级说明

- 与 2.1.5.2 同为 versionCode 7、同一签名，可直接覆盖升级。
- 与官方版相互独立：从官方 v1.4.0 切换到彩蛋版（或反向）需先卸载当前版本。

---

# Flip to Shhh v2.1.5.3 Release Notes (English)

Date: 2026-09-05 ｜ versionCode 7

Closing release for the easter-egg line. Functionally identical to 2.1.5.2 (including the neofetch install date) — maintenance only, no behavior changes.

## Changed

- `versionName` 2.1.5.2 → 2.1.5.3; `versionCode` stays 7 — the easter-egg line keeps its own numbering and never climbs toward the official line's numbers (official v1.4.0 is 8), so the two editions remain independent and never upgrade over each other.
- Android CI now also builds the easter-egg line's release branches (`release-v2.*`).
- README (three languages) refreshed for the easter-egg line: release badge and APK size (~5.1 MB including the embedded audio).
- Removed an unused import left behind by the long-press gesture rework.

## Upgrade notes

- Same versionCode (7) and signature as 2.1.5.2 — installs directly over it.
- Independent from the official line: switching from official v1.4.0 to the easter-egg edition (or back) requires uninstalling the currently installed one first.
