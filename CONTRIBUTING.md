# Contributing to Flip to Shhh

Thank you for your interest in contributing to **Flip to Shhh**! 🎉

This document outlines the guidelines and best practices for submitting issues, feature proposals, and pull requests.

---

## 🧭 Code of Conduct

Please be respectful, constructive, and kind in all communications within issues and discussions.

---

## 🛠️ Development Setup

1. **Prerequisites**:
   - Android Studio Ladybug (2024.2.1) or newer
   - JDK 17
   - Android SDK Platform 34 (Android 14)
   - Physical or virtual device running Android 13+ (API 33+)

2. **Building the Project**:
   ```bash
   git clone https://github.com/wg2038/f2shhh.git
   cd f2shhh
   ./gradlew assembleDebug
   ```

---

## 📐 Coding Conventions

- **Strict Offline Rule**: This project strictly prohibits adding network permissions (`android.permission.INTERNET`). All features must operate 100% locally.
- **APK Slimming**: Do not add heavy dependencies. Use lightweight vector paths in `AppIcons.kt` instead of full icon packs.
- **Compose Best Practices**: Maintain clean Separation of Concerns (StateFlow / remember / LaunchedEffect lifecycle awareness).
- **Kotlin Style**: Follow standard Android Kotlin coding conventions and formatting.

---

## 🔀 Pull Request Process

1. Fork the repository and create a feature branch (`feature/your-feature-name` or `fix/issue-description`).
2. Ensure your changes compile cleanly without warnings:
   ```bash
   ./gradlew assembleRelease
   ```
3. Submit a Pull Request targeting the `main` branch with a clear description of the problem and solution.
