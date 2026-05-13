# Rummitimer

루미큐브 등 보드게임에서 각 플레이어의 턴 시간을 관리하는 Android 타이머 앱.

---

## 주요 기능

- **2~4인 플레이어** 지원
- **턴 제한 시간** 선택: 30초 / 60초 / 90초 / 120초
- **남은 시간 시각화**: 원형 프로그레스 바로 시간 흐름 표시
- **경고 알림**: 잔여 10초 이하 시 화면 색상 변경 + 메트로놈 경고음
- **자동 다음 턴**: 다음 버튼 클릭 후 1초 후 자동 시작
- **플레이어 인디케이터**: 현재 순서 시각적 강조
- **세로 게임 화면(SplitTimerCircle)**: 테이블 맞은편 플레이어도 읽을 수 있도록 양방향 시간 표시
- **가로 레이아웃**: 화면 좌우에 컨트롤 버튼, 중앙에 타이머 배치
- **다국어**: 한국어 / 영어 / 시스템 기본값
- **테마**: 라이트 / 다크 / 시스템 기본값
- **AdMob 전면 광고**: 게임 종료(리셋) 시 삽입

---

## 화면 구성

| 상태 | 세로 | 가로 |
|---|---|---|
| 초기 | 설정 칩 → 타이머 → 버튼 → 인디케이터 | 왼쪽: 설정 \| 오른쪽: 타이머 + 버튼 |
| 게임 중 | 상단 버튼(반전) / 중앙 SplitTimerCircle / 하단 버튼 | 왼쪽 버튼 \| 중앙 타이머 \| 오른쪽 버튼 |

---

## 기술 스택

| 항목 | 내용 |
|---|---|
| 언어 | Kotlin 2.2.10 |
| UI | Jetpack Compose (Material 3) |
| 상태 관리 | `rememberSaveable` (ViewModel 없음) |
| 비동기 | Coroutines + `LaunchedEffect` |
| 광고 | Google AdMob (전면 광고) |
| 크래시 리포트 | Firebase Crashlytics |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 36 |
| Compose BOM | 2025.12.00 |
| AGP | 9.2.1 |

---

## 프로젝트 구조

```
app/src/main/java/com/deitrihi/rummitimer/
├── MainActivity.kt          # 진입점, 테마/언어 적용
├── HomeScreen.kt            # 타이머 메인 화면 (세로/가로 레이아웃)
├── SettingsScreen.kt        # 언어 및 테마 설정 화면
├── MetronomePlayer.kt       # 경고음 재생 (ToneGenerator)
├── InterstitialAdManager.kt # AdMob 전면 광고 로드/표시
├── LocaleHelper.kt          # 언어 설정 및 적용
├── ThemeHelper.kt           # 테마 설정 저장 (SharedPreferences)
└── ui/theme/                # Material 3 색상, 타이포그래피, 테마
```

---

## 빌드 방법

```bash
# 전체 빌드
./gradlew build

# 디버그 APK
./gradlew assembleDebug

# 릴리즈 APK
./gradlew assembleRelease

# 단위 테스트
./gradlew test

# Lint 검사
./gradlew lint

# 빌드 결과물 초기화
./gradlew clean
```

---

## 의존성

모든 버전은 [`gradle/libs.versions.toml`](gradle/libs.versions.toml)에서 중앙 관리.

- `androidx.compose.bom` — Compose 라이브러리 버전 통합 관리
- `play-services-ads` 23.3.0 — AdMob
- `firebase-bom` 33.7.0 + `firebase-crashlytics` — 크래시 리포트

---

## 출시 전 체크리스트

- [ ] `strings.xml`의 AdMob App ID / Interstitial Unit ID를 실제 ID로 교체
- [ ] `google-services.json` 설정 확인
- [ ] 릴리즈 서명 키스토어 설정
