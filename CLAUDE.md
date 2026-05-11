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
- State is currently held locally via `rememberSaveable` in `HomeScreen` — no ViewModels yet
- Theme lives in `ui/theme/` using Material 3 dynamic color (Android 12+)

## 레이아웃 구조 (`HomeScreen.kt`)

`currentLayoutType()`이 `LocalConfiguration.orientation`만 읽어 두 가지로 분기한다. 디바이스 종류(폰/태블릿)는 구분하지 않는다.

| 조건 | LayoutType | 진입 composable |
|---|---|---|
| `orientation == LANDSCAPE` | `LANDSCAPE` | `LandscapeContent` |
| 나머지 (세로) | `PORTRAIT` | `PortraitContent` |

각 composable은 내부에서 `timerStarted` 값에 따라 **초기 상태**와 **게임 중** 두 분기를 처리한다.

### PortraitContent (세로)
- 초기: `Column(SpaceBetween)` — 설정 칩 → 타이머(300dp) → 버튼 → 인디케이터
- 게임: `Box` — 상단 버튼(180도 회전) / 중앙 `SplitTimerCircle(300dp)` / 하단 버튼

### LandscapeContent (가로)
- 초기: `Row` — 왼쪽 설정 칩 | `VerticalDivider` | 오른쪽 타이머(200dp)+버튼
- 게임: `Row` — 왼쪽 `VerticalControlButtons` | 중앙 `PlayerIndicators`+`TimerDisplay(200dp, playerLabel 포함)` | 오른쪽 `VerticalControlButtons`

### 공통 컴포넌트
- `TimerDisplay`: `size`, `strokeWidth`, 폰트 크기, `playerLabel(String?)` 파라미터 수신. `playerLabel` 전달 시 원 안 상단에 플레이어 이름 표시.
- `SplitTimerCircle`: 세로 게임 중 전용. 원 안에 상대방 시간(180도 회전) / 플레이어 레이블 / 현재 시간을 함께 표시.
- `VerticalControlButtons`: 가로 게임 중 전용. Next → Pause/Start → End 순 세로 배치.

## Key Configuration

- **Min SDK**: 24 | **Target/Compile SDK**: 36
- **Kotlin**: 2.2.10 | **AGP**: 9.1.1
- **Compose BOM**: 2025.12.00
- **Java compatibility**: Java 11
- All dependency versions are centralized in `gradle/libs.versions.toml`
- App ID: `com.deitrihi.rummitimer`
