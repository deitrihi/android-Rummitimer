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

---

# 스토어 feature graphic 업데이트

## 목표

`store_assets/feature_graphic.svg`를 현재 기능 범위에 맞게 갱신한다. 루미큐브 전용 타이머처럼 보이던 이미지를 루미큐브, 보드게임 타이머, 일반 타이머를 모두 포함하는 스토어 feature graphic으로 바꾼다.

## 작업

1. 현재 SVG 구조 확인 후 브랜드 색상과 1024×500 규격 유지
2. 왼쪽 카피를 "루미큐브 턴 타이머" 중심에서 다목적 타이머 메시지로 변경
3. 기능 칩을 루미큐브, 보드게임 타이머, 일반 타이머 중심으로 변경
4. 오른쪽 비주얼에 루미큐브 타일, 2인 보드게임 시계, 일반 타이머 카드를 함께 표현

## 검증

- SVG XML 파싱 확인
- 변경 diff 확인

---

# 스토어 feature graphic 다국어 버전

## 목표

최신 feature graphic 디자인을 영어, 일본어, 독일어, 스페인어, 네덜란드어 스토어 등록용 파일로 확장한다.

## 작업

1. `feature_graphic_en.svg/png`를 최신 다기능 디자인으로 교체
2. `feature_graphic_ja.svg/png`, `feature_graphic_de.svg/png`, `feature_graphic_es.svg/png`, `feature_graphic_nl.svg/png` 신규 생성
3. 기능 카드, 칩, 서브타이틀, 태그라인을 언어별로 현지화
4. 각 PNG를 1024×500으로 렌더링

## 검증

- 전체 SVG XML 파싱 확인
- 전체 PNG 크기 1024×500 확인
- 다국어 렌더링 이미지 육안 확인
