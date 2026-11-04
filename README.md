# Universe Launcher

A new Android launcher project for Minecraft: Java Edition, with an original space-inspired interface.

> **Current status: foundation / alpha 0.1.0.** This branch introduces the Android Gradle project and the first Compose home screen. It is not yet a functional Minecraft launcher and does not yet include the game runtime, account authentication, version installation, mods, renderers, or in-game music playback.

## Project direction

- **Name:** Universe Launcher
- **Platform:** Android
- **Language/UI:** Kotlin + Jetpack Compose
- **Minimum Android version:** Android 8.0 (API 26)
- **Initial application ID:** `com.universe.launcher`
- **Visual direction:** deep space black, nebula blue/cyan and subtle violet accents
- **Current version:** `0.1.0-alpha`

## Current implementation

- Android application module and Gradle configuration.
- Compose-based home screen with a custom procedural starfield background.
- Initial navigation tiles for Accounts, Files and Music (visual placeholders only).
- GitHub Actions workflow to build and upload a debug APK artifact.

## Planned development stages

1. Establish a reproducible Android build and validate it on a real ARM64 device.
2. Integrate a Minecraft Java runtime/launcher engine from a compatible upstream project, preserving all required license notices and source-distribution obligations.
3. Add version installation and management, Java runtime selection, memory settings and renderer selection.
4. Add Microsoft account sign-in using the supported authentication flow, plus profile/skin management.
5. Add mod-loader and content management for compatible Fabric/Forge/NeoForge versions.
6. Add local music library access using Android's Storage Access Framework and an audio playback service.
7. Add server list, multiplayer connection support and performance diagnostics.
8. Replace the procedural placeholder background with the approved Universe artwork when its image asset is added to the repository.

## Build

The GitHub Actions workflow at `.github/workflows/android-debug.yml` builds the debug APK on pushes to the configured branches or when manually started from the Actions tab.

The workflow uses Java 17 and Gradle 8.10.2:

```bash
gradle :app:assembleDebug
```

Expected output:

```
app/build/outputs/apk/debug/app-debug.apk
```

This initial scaffold does not include a Gradle wrapper yet. The CI workflow installs the specified Gradle version directly.

## Upstream projects and licensing

Potential upstream references include:

- [Zalith Launcher 2](https://github.com/ZalithLauncher/ZalithLauncher2)
- [Cubiq Launcher](https://github.com/VivxKumar07/Cubiq-Launcher)
- [Amethyst Android](https://github.com/AngelAuraMC/Amethyst-Android)

No upstream source code has been copied into this initial scaffold. Before integrating any upstream implementation, its exact license, notices, additional conditions, trademarks and source-distribution requirements must be reviewed and preserved. In particular, a modified Zalith-based distribution must follow the upstream repository's additional GPL conditions and must not be presented as the official Zalith Launcher.

The existing `RVCLapk1.0.md` file refers to original Cryogenic Launcher materials; it is retained as historical repository documentation and does not grant rights to third-party code or assets.

## Disclaimer

Universe Launcher is an independent, unofficial community project. Minecraft is a trademark of its respective owners. This project is not affiliated with or endorsed by Mojang or Microsoft.
