# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew build                    # Full build
./gradlew assembleDebug            # Debug APK
./gradlew assembleRelease          # Release APK
./gradlew test                     # Unit tests
./gradlew connectedAndroidTest     # Instrumented tests (requires device/emulator)
./gradlew lint                     # Lint checks
./gradlew clean                    # Clean build artifacts

# Run a single test class
./gradlew test --tests "com.deitrihi.rummitimer.ExampleUnitTest"
```

# Project Stack
- Kotlin only
- Jetpack Compose (no XML)
- MVVM architecture
- Hilt for DI
- Coroutines + Flow only (no LiveData)

# Architecture Rules
- UI layer contains composables only
- No business logic in composables
- ViewModels handle state and logic

# Forbidden
- No LiveData
- No XML layouts
- No Java files

## Architecture

**Single-module, single-Activity Jetpack Compose app** — no MVVM or DI layer yet.

- `MainActivity` sets up edge-to-edge and hosts the single Compose entry point `RummitimerApp()`
- Navigation uses `NavigationSuiteScaffold` (Material 3 adaptive navigation — automatically switches between bottom bar and side rail based on screen size)
- Navigation destinations are defined as the `AppDestinations` enum in `MainActivity.kt`
- State is currently held locally via `rememberSaveable` in the main composable — no ViewModels yet
- Theme lives in `ui/theme/` using Material 3 dynamic color (Android 12+)

## Key Configuration

- **Min SDK**: 24 | **Target/Compile SDK**: 36
- **Kotlin**: 2.2.10 | **AGP**: 9.1.1
- **Compose BOM**: 2025.12.00
- **Java compatibility**: Java 11
- All dependency versions are centralized in `gradle/libs.versions.toml`
- App ID: `com.deitrihi.rummitimer`
