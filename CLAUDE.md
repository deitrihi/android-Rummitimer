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

## Timer Screen Layout (표준)

모든 타이머/게임 화면은 아래 구조를 따른다.

```
TopAppBar
  title   : 타이머 또는 게임 종류 이름 (예: "뽀모도로", "장기 타이머")
  actions : IconButton — ic_menu 아이콘, contentDescription = menu_open
            클릭 시 → currentScreen = Screen.MENU
```

- `navigationIcon` (좌상단 뒤로가기)는 사용하지 않는다.
- 타이머 컴포저블의 진입 파라미터는 `onMenuClick: () -> Unit`으로 통일한다.
- 예외: `TwoPlayerTimerScreen`처럼 전체 화면을 두 플레이어가 점유하는 경우 TopAppBar 없이 `ControlStrip` 내부에 종료 버튼을 둘 수 있다.

## Localization

지원 로케일 6개 — 항목 수와 key가 항상 완전히 일치해야 한다.

| 폴더 | 언어 |
|---|---|
| `values/` | 한국어 (기본값) |
| `values-en/` | 영어 |
| `values-ja/` | 일본어 |
| `values-de/` | 독일어 |
| `values-es/` | 스페인어 |
| `values-nl/` | 네덜란드어 |

**규칙:**
- `strings.xml`에 항목을 추가·수정·삭제할 때는 **위 6개 파일을 반드시 동시에 업데이트**한다. 일부만 수정하는 것은 허용하지 않는다.
- `translatable="false"` 항목(AdMob ID 등)은 `values/`에만 두고 나머지 로케일에는 추가하지 않는다.
- 번역이 확정되지 않은 경우에도 영어 fallback 텍스트라도 넣어 key를 맞춘다.

## Timer Visual Direction (원형 타이머 진행 방향)

두 경우 모두 **양수 sweepAngle**을 사용한다.

- **남은 시간 링** (카운트다운) → `남은 시간 / 전체 시간 × 360f` (양수, CW 호)  
  링 끝점이 반시계 방향으로 후퇴 → 링이 반시계 방향으로 줄어드는 효과
- **경과 시간 링** (카운트업) → `경과 시간 / 기준 시간 × 360f` (양수, CW 호)  
  링 끝점이 시계 방향으로 전진 → 링이 시계 방향으로 채워지는 효과

PomodoroScreen의 `remainingOuterSweep = (360f - sweepAngle)` 패턴이 기준 구현이다.

## Forbidden
- No LiveData
- No XML layouts
- No Java files
- No ViewModel (현재 단계)
