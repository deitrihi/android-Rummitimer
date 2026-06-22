# 항상 켜짐 설정 추가

## 2026-06-22

### 요청
- 설정 메뉴에 "항상 켜짐" 토글 옵션 추가
- 토글이 켜지면 앱 실행 중 화면이 꺼지지 않도록 처리

### 행동
- `ThemeHelper.kt` — `KEY_KEEP_SCREEN_ON`, `getKeepScreenOn`, `setKeepScreenOn` 추가
- `SettingsScreen.kt` — `화면` 섹션과 `SettingsSwitchRow` 컴포저블 추가, `keepScreenOn`/`onKeepScreenOnChange` 파라미터 추가
- `MainActivity.kt` — `keepScreenOn` 상태 초기화, `SideEffect`로 `FLAG_KEEP_SCREEN_ON` 적용, `RummitimerApp` 파라미터 추가
- `strings.xml` 6개 로케일 — `display_label`, `keep_screen_on_label` 추가
- 브랜치: `2026-06-22` 생성

### 결정
- `SideEffect`를 `setContent` 블록 안에서 사용해 recomposition마다 플래그를 동기화
- 설정값은 기존 `ThemeHelper`의 `rummitimer_settings` SharedPreferences에 함께 저장

### 미해결
- 없음
