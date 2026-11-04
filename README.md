# Universe Launcher

Independent Android launcher project for Minecraft: Java Edition, with a custom Universe identity.

> **Development status: engine integration in progress.** The original Compose/audio foundation remains available. A separate CI workflow now builds a pinned upstream Minecraft Java launcher engine as an ARM64 debug APK, branded as Universe Launcher. This is an integration build, not yet a merged or device-tested release.

## Project identity

- Name: Universe Launcher
- Platform: Android
- Application identity for the original foundation: `com.universe.launcher`
- Visual direction: space black, nebula blue/cyan and violet
- Current integration target: ARM64 Android devices

## Existing Universe foundation

- Kotlin and Jetpack Compose app foundation.
- Custom Universe home screen and visual identity.
- Local audio discovery through Android MediaStore.
- Background audio playback using AndroidX Media3.
- Separate debug APK workflow for the original foundation.

## Minecraft Java engine integration

The new workflow `.github/workflows/minecraft-engine.yml` checks out a **pinned** revision of [Zalith Launcher 2](https://github.com/ZalithLauncher/ZalithLauncher2) and builds its Android launcher module for ARM64. Zalith Launcher 2 documents that it uses the PojavLauncher engine and includes the Java runtime, Minecraft launching, Microsoft account flow and version management components.

- Upstream revision: `9546e84b5174cef6d7062322fb33ed442bac3b90`
- Build task: `:ZalithLauncher:assembleDebug`
- Output: GitHub Actions artifact `UniverseLauncher-MinecraftJava-arm64-debug`
- Branding values: Universe Launcher, app label includes “Unofficial Modified Version”.
- Gradle: 9.5.0; Java 17; Android NDK 25.2.9519653.

**Authentication configuration:** Microsoft sign-in depends on an OAuth application/client ID. The workflow reads the optional GitHub Actions secret `MICROSOFT_OAUTH_CLIENT_ID`. Without a valid client ID and matching provider configuration, Microsoft online sign-in is not ready, even if the launcher UI and authentication code compile. Never put account passwords or access tokens in source code or GitHub commits.

**What this integration does not claim yet:** the upstream engine build is not the same as a fully merged Universe application. The original Universe home screen, local music UI, custom branding assets and in-game music controls have not yet been ported into the engine application. The produced APK must be installed and tested on a real ARM64 Android device before it can be called release-ready.

## License and upstream notices

Zalith Launcher 2 is licensed under GPL-3.0 and includes additional conditions under GPLv3 §7. Modified distributions must use a clearly distinct name, must not include “ZalithLauncher” or “ZL” in their name, must visibly identify themselves as an unofficial modified version on the startup/main screen, and must preserve copyright notices. Its source and license remain available at the upstream repository linked above.

The integration workflow pins upstream source rather than copying its implementation into this repository. Any future distribution of a combined modified work must include the corresponding complete source and all applicable license notices and comply with the upstream additional terms. Universe Launcher is not affiliated with or endorsed by Mojang or Microsoft.

Other references:
- [Cubiq Launcher](https://github.com/VivxKumar07/Cubiq-Launcher)
- [Amethyst Android](https://github.com/AngelAuraMC/Amethyst-Android)

## Build the original Universe foundation

```bash
gradle :app:assembleDebug
```

Expected output: `app/build/outputs/apk/debug/app-debug.apk`.

## Run the Minecraft engine integration build

Open GitHub Actions in this repository and run **Universe Minecraft engine APK**, or push changes to `feat/minecraft-engine-zalith-integration`. The workflow uploads an ARM64 debug APK artifact. It does not publish a release automatically.

## Remaining integration work

1. Port the Universe home screen, theme and audio player into the engine app without removing upstream legal notices.
2. Configure and test Microsoft OAuth using a properly registered client ID.
3. Verify version discovery, download, installation, Java runtime selection and game launch on an ARM64 phone.
4. Test graphics/renderers and device compatibility.
5. Add in-game music controls and test audio/game lifecycle behavior.
6. Publish only after a real-device test and a complete source/license bundle are available.

## Disclaimer

Minecraft is a trademark of its respective owners. Universe Launcher is an independent, unofficial project and is not affiliated with or endorsed by Mojang or Microsoft.
