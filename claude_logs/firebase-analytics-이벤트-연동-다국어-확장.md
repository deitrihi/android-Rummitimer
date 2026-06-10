# firebase-analytics-이벤트-연동-다국어-확장
날짜: 2026-06-05

## 요청
Firebase Analytics 이벤트 연동 코드를 작성하고, 한국어·영어 외 4개 언어(일본어·독일어·스페인어·네덜란드어)를 추가해달라.

## 행동

### AnalyticsHelper.kt 신규 생성
- 13개 이벤트 상수 정의 (menu_open, player_count_change, turn_duration_change, game_start, game_pause, game_resume, next_turn, game_end, score_input_done, game_restart, language_change, theme_change, fruit_icon_select)
- `log(context, eventName)` 함수 작성 — `FirebaseAnalytics.getInstance(context).logEvent()` 래핑

### 이벤트 연동 (4개 화면)
- `HomeScreen.kt`: 메뉴 버튼, 인원 수·턴 시간 FilterChip, 시작/일시정지/다음/게임종료 버튼에 이벤트 추가
- `SettingsScreen.kt`: 뒤로가기, 언어 변경, 테마 변경, 과일 아이콘 선택에 이벤트 추가
- `ScoreInputScreen.kt`: 점수 입력 완료 버튼에 이벤트 추가
- `ResultScreen.kt`: 게임 재시작 버튼에 이벤트 추가

### 다국어 지원 확장
- `values-ja/strings.xml` — 일본어 로케일 신규 추가
- `values-de/strings.xml` — 독일어 로케일 신규 추가
- `values-es/strings.xml` — 스페인어 로케일 신규 추가
- `values-nl/strings.xml` — 네덜란드어 로케일 신규 추가
- `values/strings.xml`, `values-en/strings.xml` — 4개 언어명 문자열 추가
- `values-en/strings.xml` — v2에서 누락된 항목 14개 영어 번역 소급 추가
- `LocaleHelper.kt` — LANG_JAPANESE/GERMAN/SPANISH/DUTCH 상수 추가
- `SettingsScreen.kt` — 언어 선택 목록에 4개 언어 추가

### 지침 업데이트
- `CLAUDE.md` — 로케일 동기화 지침 추가 (문자열 추가 시 모든 로케일 파일 동시 업데이트)

## 결정
- 이벤트 파라미터 없이 이벤트명만 기록 (단순성 우선, 추후 필요 시 파라미터 추가)
- 언어 추가 시 기존 v2 번역 누락분도 함께 소급 처리

## 미해결
- 없음
