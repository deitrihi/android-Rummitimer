# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

> 프로젝트 구조 및 설계 문서는 [PROJECT.md](PROJECT.md)를 참고한다.

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

## Project Stack
- Kotlin only
- Jetpack Compose (no XML)
- MVVM architecture
- Hilt for DI
- Coroutines + Flow only (no LiveData)

## Architecture Rules
- UI layer contains composables only
- No business logic in composables
- ViewModels handle state and logic

## Forbidden
- No LiveData
- No XML layouts
- No Java files
