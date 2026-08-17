# 바둑 타이머 · 체스 타이머 구현 — 컨텍스트 노트

## 결정 사항

- **체스 시간 규칙**: 증가시간(Fischer)만 지원, 지연시간(Bronstein/US) 미지원. 이유: 사용자가 캐주얼 앱에서는 증가시간이 표준이고 지연시간은 UI/로직만 늘린다고 판단해 선택.
- **바둑 초읽기 규칙**: 기본 시간(0/10/20/30/60분) + 초읽기(30/40/60초 × 1~5회). 기본 시간 0분 선택 시 초읽기만으로 블리츠 대국 가능.
- **새 화면을 만들지 않고 기존 TwoPlayerSetupScreen/TwoPlayerTimerScreen 확장**: `GameType` enum과 두 화면이 이미 gameType 파라미터를 받도록 만들어져 있었음(코드 작성 당시부터 대비된 구조로 추정). byoyomiSeconds/byoyomiPeriods/incrementSeconds를 기본값 0으로 추가하면 장기 로직이 숫자 계산까지 완전히 동일하게 유지됨.
- **결과 화면(TwoPlayerResultScreen)은 변경하지 않음**: p1UsedSeconds/p2UsedSeconds는 여전히 `initialTimeSeconds - mainTimeRemaining`으로 계산. 초읽기 소모 시간은 반영하지 않는 단순화 — 사용자에게 별도 확인하지 않고 진행(사소한 표시상 트레이드오프로 판단).

## 발견한 기존 버그

`MainActivity.kt`의 `initialScreen` 복원 로직(`last_timer` prefs 읽기)이 "JANGGI"일 때만 `Screen.TWO_PLAYER_SETUP`으로 복원하고, `twoPlayerGameType` 상태 자체는 복원하지 않음. 지금까지는 `twoPlayerGameType`의 기본값이 `GameType.JANGGI`라 우연히 문제가 드러나지 않았음. 바둑/체스 추가 시 이 복원 로직도 함께 고쳐야 함 (그렇지 않으면 앱 재시작 시 마지막으로 쓰던 바둑/체스 설정이 장기로 복원됨).

## 결과 화면 시간 표시 관련 트레이드오프

체스 증가시간(Fischer) 도입으로 남은 시간이 초기 설정 시간을 넘어설 수 있어(+N초 누적), `initialTimeSeconds - remaining`이 음수가 될 수 있음을 발견. `onSeeResult`에서 `maxOf(0, ...)`로 클램프해 결과 화면에 음수 시간이 표시되는 것을 막음.

## 빌드

`./gradlew assembleDebug` — BUILD SUCCESSFUL (23s)

## 미해결

없음. 실기기 테스트는 진행하지 못했으므로(에뮬레이터 미가동), 초읽기/증가시간 로직은 코드 리뷰 수준으로만 검증됨.

---

# 스토어 feature graphic 업데이트 — 컨텍스트 노트

## 결정 사항

- 기존 이미지는 "루미큐브 턴 타이머" 메시지와 2~4인, 30~120초, 메트로놈 알림 칩으로 루미큐브 전용성이 강함.
- 현재 핵심 기능은 루미큐브 타이머, 장기·바둑·체스 같은 2인 보드게임 타이머, 일반 타이머·뽀모도로까지 넓어진 상태이므로 스토어 이미지도 범용 타이머 허브처럼 보여야 함.
- 앱 이름 `Rummitimer`와 기존 어두운 블루 배경, 노란 진행 링은 유지해 브랜드 연속성을 살림.
- 오른쪽 메인 원형 타이머는 유지하되, 주변에 루미큐브 타일, 2인 보드게임 시계, 일반 타이머 미니 카드를 배치해 기능 확장을 시각적으로 전달함.
- Google Play 업로드용 실제 이미지 파일도 필요하므로 `store_assets/feature_graphic.svg`에서 `store_assets/feature_graphic.png`를 1024×500으로 재생성함.
