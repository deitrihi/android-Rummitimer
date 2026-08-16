# CHANGELOG

## [미커밋]

- TwoPlayerSetupScreen.kt — gameType별 설정 UI 분기(장기: 기존 분 피커, 바둑: 기본시간+초읽기 시간/횟수, 체스: 기본시간+증가시간), onStart 4-인자로 확장
- TwoPlayerTimerScreen.kt — 바둑 초읽기(byoyomi) 상태 전이 로직 신규 구현 (기본시간 소진 시 진입, 제한시간 내 착수 시 리셋, 시간 초과 시 횟수 소모, 횟수 소진 시 패배)
- TwoPlayerTimerScreen.kt — 체스 증가시간(Fischer increment) 로직 신규 구현 (착수 완료 직후 자기 시간에 +N초)
- TwoPlayerTimerScreen.kt — PlayerHalf에 초읽기 중 "초읽기 N회" 라벨 표시 추가
- TwoPlayerTimerScreen.kt — 결과 화면 전달용 사용 시간 계산에 하한 0 클램프 추가 (증가시간으로 남은 시간이 초기값을 넘는 경우 대비)
- MenuScreen.kt — onSelectBaduk/onSelectChess 파라미터 추가, 바둑·체스 메뉴 항목 comingSoon 해제
- MainActivity.kt — twoPlayerByoyomiSeconds/twoPlayerByoyomiPeriods/twoPlayerIncrementSeconds 상태 추가 및 화면 연결
- MainActivity.kt — last_timer 복원 로직에 BADUK/CHESS 케이스 추가, twoPlayerGameType 복원 누락 버그 수정
- strings.xml (한/영/ja/de/es/nl) — baduk_setup_title, chess_setup_title, main_time_label, byoyomi_time_label, byoyomi_periods_label, increment_time_label, byoyomi_periods_format, byoyomi_periods_remaining_format 추가

## 2026-06-26 | e6d1e88

- AlertHelper.kt — 소리/진동/화면 깜박임 알림 설정 저장 및 실행 신규 생성
- AndroidManifest.xml — VIBRATE 권한 추가
- SettingsScreen.kt — 알림 섹션(소리·진동·화면 깜박임 토글) 추가
- MainActivity.kt — 알림 설정 상태 관리 및 SettingsScreen/RummitimerApp 파라미터 연결
- GeneralTimerScreen.kt — 타이머 종료 시 AlertHelper로 알림 실행, 앱 테두리 깜박임 오버레이 추가
- PomodoroScreen.kt — 단계 전환 시 AlertHelper로 알림 실행, 앱 테두리 깜박임 오버레이 추가
- PomodoroScreen.kt — 링 배경 제거, 경과 초만 accentColor 호로 표시하여 파이와 색상 분리
- HomeScreen.kt — 턴 종료 시 AlertHelper로 알림 실행, 경고 사운드 설정 연동, 앱 테두리 깜박임 오버레이 추가
- TwoPlayerTimerScreen.kt — 시간 소진 시 AlertHelper로 알림 실행(승자 결정 시점), 경고 사운드 설정 연동, 앱 테두리 깜박임 오버레이 추가
- strings.xml (한/영/ja/de/es/nl) — alert_label, alert_sound_label, alert_vibration_label, alert_flash_label 추가
- BannerAd.kt — navigationBarsPadding() 추가로 배너가 내비게이션 바 아래 표시되던 문제 수정
- strings.xml — 배너 광고 Unit ID를 실제 ID로 교체 (ca-app-pub-3949969956815888/7502245754)
- BannerAd.kt — 배너 광고 Composable 신규 생성
- GeneralTimerScreen.kt, StopwatchScreen.kt, PomodoroScreen.kt — 하단 배너 광고 추가
- MainActivity.kt — 설정 화면 뒤로 가기 시 직전 타이머로 복귀 (previousScreen 추적)
- SettingsScreen.kt — 시스템 뒤로가기(제스처/버튼)에 BackHandler 추가하여 onBack() 연결
- MainActivity.kt — 타이머 화면 더블 백 누름 종료: 첫 번째 뒤로가기에 토스트 표시, 2초 내 재입력 시 앱 종료
- PomodoroScreen.kt, StopwatchScreen.kt, GeneralTimerScreen.kt — 타이머 진행 중 뒤로가기 시 종료 확인 다이얼로그 추가 (확인 → 초기화, 취소 → 계속)
- TwoPlayerTimerScreen.kt — 시스템 뒤로가기에 BackHandler 추가: 게임 진행 중 → 확인 다이얼로그(확인 시 설정 화면으로), 미시작·종료 시 → 설정 화면으로 바로 이동
- HomeScreen.kt — 타이머 시작 후 뒤로가기 시 종료 확인 다이얼로그 추가 (확인 → 광고 노출 후 초기 상태, 취소 → 계속)
- MainActivity.kt — HomeScreen에 onShowAd 콜백 연결
- strings.xml (한/영/ja/de/es/nl) — timer_exit_title, timer_exit_message, btn_confirm, btn_cancel 추가
- strings.xml (한/영/ja/de/es/nl) — back_press_to_exit 문자열 추가

## 2026-06-24 | 0d19bab

- MainActivity.kt — 패밀리 정책 준수를 위한 아동 안전 광고 설정 추가 (TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE, MAX_AD_CONTENT_RATING_G)

## 2026-06-22 | 1609a54

- ThemeHelper.kt — 항상 켜짐 설정 persist 메서드 추가 (getKeepScreenOn / setKeepScreenOn)
- SettingsScreen.kt — 화면 섹션 및 항상 켜짐 토글(Switch) 추가
- MainActivity.kt — keepScreenOn 상태 관리 및 FLAG_KEEP_SCREEN_ON 플래그 적용
- strings.xml (한/영/ja/de/es/nl) — display_label, keep_screen_on_label 추가
- .claude/commands/작업종료.md — /작업종료 커스텀 스킬 추가 (커밋·병합·푸시 자동화)

## 2026-06-20 | 312cefe

- GeneralTimerScreen.kt — 카운트다운 링 방향 수정 (음수 sweepAngle → 양수, 반시계 방향으로 줄어듦)
- CLAUDE.md — 타이머 진행 방향 지침 수정 (양수 sweepAngle 기준으로 통일, PomodoroScreen 패턴 명시)
- StopwatchScreen.kt — 스톱워치 신규 구현 (카운트업 원형 링, 랩 기록 목록)
- GeneralTimerScreen.kt — 일반 타이머 신규 구현 (h/m/s 피커, 카운트다운 원형 링)
- MainActivity.kt — Screen enum에 STOPWATCH·GENERAL_TIMER 추가, 라우팅 및 last_timer 복원 연결
- MenuScreen.kt — 스톱워치·일반 타이머 comingSoon 해제, onSelectStopwatch·onSelectGeneralTimer 콜백 연결
- strings.xml (한/영/ja/de/es/nl) — btn_lap, stopwatch_lap_format, general_timer_done 추가
- gradle/libs.versions.toml, app/build.gradle.kts — androidx.fragment 1.8.9 명시적 선언 (전이 의존성 버전 고정)
- TwoPlayerTimerScreen.kt — safeDrawingPadding() 추가로 edge-to-edge 인셋 처리 (SDK 35+ 대응)
- PomodoroScreen.kt — 외부 링 반지름을 디스크 가장자리에 맞춰 테두리 제거
- PomodoroScreen.kt — 안쪽 링 그리기 전 배경 리셋으로 두 링 트랙 색상 통일

## 2026-06-18 | b85291c

- PomodoroScreen.kt — 분침 시계를 링 진행 표시기로 교체 (60초 1바퀴 원호 스트로크, 배경 링 + 진행 링)
- PomodoroScreen.kt — 뽀모도로 타이머 화면 신규 구현 (집중 25분·짧은 휴식 5분·긴 휴식 15분 사이클, 세션 도트, 건너뛰기·초기화 버튼)
- PomodoroScreen.kt — 동심 3링 비주얼 (바깥쪽: 단계 진행, 가운데: 분 내 초 진행, 안쪽: 텍스트 원) — 밝은 커버 반시계 줄어드는 방식
- MainActivity.kt — 마지막 타이머 복원 로직에 뽀모도로 추가 (last_timer = "POMODORO" 저장 및 복원)
- MainActivity.kt — Screen enum에 POMODORO 추가 및 navigation 분기 연결
- MenuScreen.kt — 뽀모도로 항목 comingSoon 해제, onSelectPomodoro 콜백 연결
- strings.xml (한/영/ja/de/es/nl) — 뽀모도로 관련 문자열 추가 (단계 레이블, 초기화, 건너뛰기)
- TwoPlayerSetupScreen.kt — TopAppBar 레이아웃 통일: 좌상단 뒤로가기 → 우상단 햄버거 메뉴
- CLAUDE.md — 타이머 진행 방향 지침 추가 (카운트다운 반시계, 카운트업 시계)

## 2026-06-17 | b1dfeca

- TwoPlayerTimerScreen.kt — 가로 모드(landscape) 레이아웃 추가 (P1 좌 / ControlStrip 세로 / P2 우, 회전 없음); 회전을 Surface 외부에서 내부 Column으로 이동
- MainActivity.kt — 마지막 선택 타이머를 SharedPreferences에 저장, 앱 시작 시 해당 화면으로 복원
- MainActivity.kt — 장기 타이머 결과 화면(다시하기·설정 변경) 버튼에 전면 광고 삽입
- MainActivity.kt — 장기 타이머 게임 종료 버튼에 전면 광고 삽입 (광고 닫힌 후 설정 화면으로 이동)

## 2026-06-10 | d170abd

- TwoPlayerSetupScreen.kt — v3 장기 타이머 설정 화면 신규 추가 (1/3/5/10/30분 프리셋)
- TwoPlayerTimerScreen.kt — v3 2인 대국 타이머 화면 신규 추가 (절대 시간, 착수 탭, 일시정지)
- TwoPlayerResultScreen.kt — v3 대국 결과 화면 신규 추가 (승자 표시, 소요 시간)
- MainActivity.kt — GameType enum, Screen에 TWO_PLAYER_SETUP/TIMER/RESULT 추가, RummitimerApp 분기 3개 추가
- MenuScreen.kt — 장기 타이머 항목 활성화 (comingSoon=false), onSelectJanggi 파라미터 추가
- values/strings.xml (한국어) — v3 2인 타이머 문자열 10개 추가
- values-en/strings.xml — v3 English 문자열 10개 추가
- values-ja/strings.xml — v3 일본어 문자열 10개 추가
- values-de/strings.xml — v3 독일어 문자열 10개 추가
- values-es/strings.xml — v3 스페인어 문자열 10개 추가
- values-nl/strings.xml — v3 네덜란드어 문자열 10개 추가
- plan.md, checklist.md, context-notes.md — v3 장기 타이머 개발 문서 추가

## 2026-06-10 | ebe1292

- claude_logs/firebase-analytics-기본설정.md — 2026-05-31 세션 로그 소급 추가
- claude_logs/firebase-analytics-이벤트-연동-다국어-확장.md — 2026-06-05 세션 로그 소급 추가
- claude_logs/타이머-선택-메뉴-화면-추가.md — 2026-06-08 세션 로그 소급 추가

## 2026-06-08 | 25f71ca

- MenuScreen.kt — 게임·타이머 선택 메뉴 화면 신규 추가 (보드게임 타이머 4종, 일반 타이머 3종, 설정 진입)
- MainActivity.kt — Screen enum에 MENU 추가, 햄버거 버튼 → MENU 화면으로 라우팅 변경
- values/strings.xml, values-en/strings.xml — v3 메뉴 문자열 11개 추가
- values-ja/strings.xml, values-de/strings.xml, values-es/strings.xml, values-nl/strings.xml — v3 메뉴 문자열 11개 각 언어 번역 추가

## 2026-06-05 | e670473

- values-en/strings.xml — v2 누락 항목 14개 영어 번역 추가 (과일 아이콘, 점수 입력, 결과 화면)
- CLAUDE.md — 로케일 동기화 지침 추가 (문자열 추가 시 모든 로케일 동시 업데이트)
- values-ja/strings.xml — 신규 일본어 로케일 추가
- values-de/strings.xml — 신규 독일어 로케일 추가
- values-es/strings.xml — 신규 스페인어 로케일 추가
- values-nl/strings.xml — 신규 네덜란드어 로케일 추가
- values/strings.xml, values-en/strings.xml — 신규 4개 언어명 문자열 추가
- LocaleHelper.kt — LANG_JAPANESE/GERMAN/SPANISH/DUTCH 상수 추가
- SettingsScreen.kt — 언어 선택 목록에 4개 언어 추가


- AnalyticsHelper.kt — 신규 파일, Firebase Analytics 이벤트 로깅 헬퍼 (13개 이벤트 상수 + log() 함수)
- HomeScreen.kt — 메뉴 버튼, 인원 수/턴 시간 FilterChip, 시작/일시정지/다음/게임종료 버튼에 Analytics 이벤트 추가
- SettingsScreen.kt — 뒤로가기, 언어 변경, 테마 변경, 과일 아이콘 선택에 Analytics 이벤트 추가
- ScoreInputScreen.kt — 점수 입력 완료 버튼에 Analytics 이벤트 추가
- ResultScreen.kt — 게임 재시작 버튼에 Analytics 이벤트 추가

- gradle/libs.versions.toml — core-ktx 1.10.1→1.19.0, lifecycle-runtime-ktx 2.6.1→2.10.0, activity-compose 1.8.0→1.13.0 업데이트
- app/build.gradle.kts — compileSdk/targetSdk 36→37 업데이트

- gradle/libs.versions.toml — firebase-analytics 라이브러리 항목 추가
- app/build.gradle.kts — firebase-analytics 의존성 추가
- app/src/main/AndroidManifest.xml — AD_SERVICES_CONFIG manifest 충돌 해소 (tools:replace)

- HomeScreen.kt — AutoFitText 컴포저블 추가, ControlButtons/VerticalControlButtons 버튼 텍스트 1줄 자동 축소 적용
- ResultScreen.kt — 완료 버튼 텍스트에 AutoFitText 적용
- mipmap-*/ic_launcher.png — SVG 아이콘(v5)으로 레거시 런처 아이콘 교체 (mdpi~xxxhdpi)
- mipmap-*/ic_launcher_round.png — 라운드 런처 아이콘 교체
- mipmap-*/ic_launcher_foreground.png — 어댑티브 아이콘 전경 레이어 교체 (배경 투명)
- rummikub-timer-icon-v5.svg — 소스 SVG 추가
