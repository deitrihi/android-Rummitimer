# 스톱워치 · 일반 타이머 구현

## 날짜
2026-06-20

## 요청
일반 타이머 섹션에서 스톱워치와 일반 타이머를 만들어달라는 요청.

## 행동
- `StopwatchScreen.kt` 신규 생성 — 카운트업, 랩 기록 지원
- `GeneralTimerScreen.kt` 신규 생성 — h/m/s 피커, 카운트다운
- `MainActivity.kt` — Screen enum에 STOPWATCH·GENERAL_TIMER 추가, 라우팅 및 last_timer 복원
- `MenuScreen.kt` — 두 항목 comingSoon 해제, 콜백 추가
- strings.xml 6개 파일 — btn_lap, stopwatch_lap_format, general_timer_done 추가
- `CHANGELOG.md` — [미커밋] 항목 추가
- 빌드 성공 확인 (BUILD SUCCESSFUL)

## 결정
- StopwatchScreen 비주얼: tertiaryContainer 색상, 60초 단위 시계방향 링
- GeneralTimerScreen 비주얼: primaryContainer 색상, 반시계방향 남은 시간 링
- 랩 목록: 최신 순으로 표시, 랩 구간 시간 + 누적 시간 모두 표시
- 일반 타이머 시간 입력: ▲/▼ 텍스트 버튼 + h/m/s 레이블 피커
- laps 리스트: rememberSaveable 대신 remember (직렬화 복잡성 회피, 구성 변경 시 초기화 허용)

## 미해결
- 없음
