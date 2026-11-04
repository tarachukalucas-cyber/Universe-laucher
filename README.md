# Universe Launcher

An independent Android project for Minecraft: Java Edition, with an original space-inspired interface.

> **Current status: alpha 0.2.0.** The repository now contains an Android/Compose app foundation and a local audio library with background playback. The Minecraft Java runtime is **not integrated yet**, so this build is not yet able to install or launch Minecraft.

## Project identity

- **Name:** Universe Launcher
- **Platform:** Android
- **Language/UI:** Kotlin + Jetpack Compose
- **Minimum Android:** 8.0 (API 26)
- **Application ID:** `com.universe.launcher`
- **Visual direction:** space black, nebula blue/cyan and subtle violet accents
- **Version:** `0.2.0-alpha`

## Implemented

- Android Gradle application module and Compose UI.
- Custom Universe home screen and navigation foundation.
- Local audio discovery through Android MediaStore, with runtime permission handling.
- Playback through AndroidX Media3 in a MediaSessionService, allowing audio to continue while the user switches apps or uses a game in the foreground (subject to Android background-execution rules).
- Refreshable audio library listing indexed audio files from shared device storage.
- GitHub Actions workflow to build and upload a debug APK artifact.

## Not implemented yet

- Minecraft Java runtime / launch engine.
- Microsoft account authentication and profile management.
- Minecraft version installation and per-version configuration.
- Fabric, Forge, NeoForge and Quilt installation.
- Renderer selection and device-specific graphics compatibility.
- Skin management, server list and multiplayer integrations.
- In-game overlay or direct controls for the music player while Minecraft is running.

## Upstream engineering references

These projects are being evaluated as upstream implementation references:

- [Zalith Launcher 2](https://github.com/ZalithLauncher/ZalithLauncher2) — GPL-3.0 with additional conditions under GPLv3 §7. Any distributed modified version must use a clearly distinct name, must not include “ZalithLauncher” or “ZL” in its name, must visibly identify itself as an unofficial modified version on the launch/main screen, and must preserve copyright notices.
- [Cubiq Launcher](https://github.com/VivxKumar07/Cubiq-Launcher) — GPL-3.0.
- [Amethyst Android](https://github.com/AngelAuraMC/Amethyst-Android) — LGPLv3; Android launcher/runtime lineage related to PojavLauncher.

No complete upstream launcher engine has been copied into this repository yet. The next integration step is to select and port a compatible engine, preserving its complete license notices, source code and modification obligations. This repository is not affiliated with Mojang or Microsoft.

## Build

The GitHub Actions workflow at `.github/workflows/android-debug.yml` builds the debug APK when code is pushed to the development branch, when a pull request targets `main`, or when manually started from Actions.

The workflow uses Java 17 and Gradle 8.10.2:

```bash
gradle :app:assembleDebug
```

Expected output:

```
app/build/outputs/apk/debug/app-debug.apk
```

The CI workflow installs Gradle directly; a Gradle wrapper has not yet been added.

## Planned development

1. Validate the Android build and test audio playback on a physical ARM64 phone.
2. Integrate a Java runtime and Minecraft launch engine.
3. Add account sign-in, game versions, mod loaders and renderer selection.
4. Add skin, server and multiplayer tools.
5. Add music playback controls that remain accessible while Minecraft is foregrounded.
6. Add device-specific performance profiles and crash diagnostics.

## Disclaimer

Universe Launcher is an independent, unofficial community project. Minecraft is a trademark of its respective owners. This project is not affiliated with or endorsed by Mojang or Microsoft.
