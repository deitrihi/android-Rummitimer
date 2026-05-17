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
- Jetpack Compose, Material 3 (no XML)
- Single-Activity, single-module (MVVM/Hilt 미도입)
- Coroutines + `LaunchedEffect` (no LiveData)
- SharedPreferences — 설정(테마·언어·과일 아이콘) 영속

## Navigation
- `Screen` enum: `HOME | SETTINGS | SCORE_INPUT | RESULT`
- `RummitimerApp`에서 `when(currentScreen)` 분기로 화면 전환
- NavController 없음

## State Management
- 공유 상태 (`playerCount`, `penalties`, `scores`, `fruitIndices`) → `RummitimerApp`에서 호이스팅
- 화면별 로컬 상태 → `rememberSaveable` / `remember`
- 광고 (`InterstitialAdManager`) → `RummitimerApp`에서 앱 시작 시 미리 로드

## Key Files (v2 추가)
- `FruitHelper.kt` — 과일 이모지 목록(`FRUIT_EMOJIS`) + SharedPreferences 저장/로드
- `ScoreInputScreen.kt` — 게임 종료 후 잔여 타일 점수 입력
- `ResultScreen.kt` — 점수·패널티 기준 랭킹 결과 표시

## Forbidden
- No LiveData
- No XML layouts
- No Java files
- No ViewModel (현재 단계)
