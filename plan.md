# 바둑 타이머 · 체스 타이머 구현

## 왜

`MenuScreen`의 바둑·체스 타이머 항목이 `comingSoon = true`로 막혀 있다. `GameType` enum과 `TwoPlayerSetupScreen`/`TwoPlayerTimerScreen`/`TwoPlayerResultScreen`은 이미 세 종목을 대비해 파라미터화돼 있지만, 실제 로직은 장기와 동일한 절대시간 카운트다운뿐이다. 바둑(초읽기)·체스(증가시간)의 실제 대국 시계 규칙을 구현해 두 항목을 활성화한다.

## 확정된 규칙

- 체스: 증가시간(Fischer)만 지원. 착수 완료 직후 자기 시간에 +N초.
- 바둑: 기본 시간 + 초읽기(30/40/60초 × 1~5회). 기본 시간 소진 후 초읽기 진입, 매 착수 시 시간 리셋, 시간 초과 시 횟수 소모, 횟수 모두 소진 후 시간 초과하면 패배.

## 설계 원칙

`TwoPlayerSetupScreen`/`TwoPlayerTimerScreen`에 `byoyomiSeconds`, `byoyomiPeriods`, `incrementSeconds` 파라미터 추가 (기본값 0). 셋 다 0이면 기존 장기 로직과 완전히 동일 — 새 화면을 만들지 않고 기존 3개 화면을 확장.

## 변경 파일

1. `TwoPlayerSetupScreen.kt` — gameType별 설정 UI 분기, onStart 4-인자로 확장
2. `TwoPlayerTimerScreen.kt` — 초읽기/증가시간 상태 전이 로직, PlayerHalf 표시 확장
3. `MenuScreen.kt` — onSelectBaduk/onSelectChess 추가, comingSoon 해제
4. `MainActivity.kt` — 상태 3개 추가, last_timer 복원 로직 수정(BADUK/CHESS 케이스 + gameType 복원 버그 수정)
5. 6개 로케일 `strings.xml` — baduk_setup_title, chess_setup_title, main_time_label, byoyomi_time_label, byoyomi_periods_label, increment_time_label, byoyomi_periods_format, byoyomi_periods_remaining_format

상세 설계는 harness plan 파일(`C:\Users\deitr\.claude\plans\effervescent-enchanting-hellman.md`) 참조.

## 검증

- `./gradlew assembleDebug` 컴파일 확인
- 6개 로케일 strings.xml key 개수 일치 확인
- 장기(byoyomi/increment=0) 동작이 기존과 동일한지 코드 리뷰로 확인
