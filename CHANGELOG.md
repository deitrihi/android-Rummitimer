# CHANGELOG

## [미커밋]

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
