# Security Policy

## 🛡️ Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 2.1.x   | :white_check_mark: |
| 1.1.x   | :white_check_mark: |
| < 1.1.0 | :x:                |

---

## 🔒 Privacy & Security Guarantee

**Flip to Shhh** is built upon privacy-first principles:
- **Zero Network Permissions**: The `INTERNET` permission is completely omitted from `AndroidManifest.xml`. The application is incapable of network transmission.
- **Zero Data Collection**: No telemetry, analytics SDKs, crash trackers, or cloud sync services are included.
- **Minimal Accessibility Privileges**: The Accessibility Service configuration explicitly sets `canRetrieveWindowContent="false"` and `accessibilityEventTypes=""`. It is strictly dedicated to executing `GLOBAL_ACTION_LOCK_SCREEN`.

---

## 📢 Reporting a Vulnerability

If you discover a potential security issue or vulnerability, please do **not** open a public issue.

Instead, please report it privately via GitHub Security Advisory:
[https://github.com/wg2038/f2shhh/security/advisories/new](https://github.com/wg2038/f2shhh/security/advisories/new)

We will respond promptly to address any valid concerns.
