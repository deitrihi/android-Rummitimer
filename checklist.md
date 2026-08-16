# 바둑 타이머 · 체스 타이머 구현 체크리스트

- [x] strings.xml (6개 로케일) — 신규 문자열 키 추가
- [x] TwoPlayerSetupScreen.kt — gameType별 설정 UI 분기 (JANGGI/BADUK/CHESS), onStart 4-인자 확장
- [x] TwoPlayerTimerScreen.kt — 초읽기 상태 전이 로직 (메인시간→초읽기 진입, 착수 시 리셋, 횟수 소모)
- [x] TwoPlayerTimerScreen.kt — 증가시간(Fischer) 로직 (착수 완료 시 +N초)
- [x] TwoPlayerTimerScreen.kt — PlayerHalf에 초읽기 표시 추가
- [x] MenuScreen.kt — onSelectBaduk/onSelectChess 파라미터 추가, comingSoon 해제
- [x] MainActivity.kt — twoPlayerByoyomiSeconds/Periods/incrementSeconds 상태 추가
- [x] MainActivity.kt — MenuScreen 콜백 연결 (BADUK/CHESS)
- [x] MainActivity.kt — last_timer 복원 로직에 BADUK/CHESS 추가 + gameType 복원 버그 수정
- [x] MainActivity.kt — TwoPlayerSetupScreen/TwoPlayerTimerScreen 호출부 업데이트
- [x] ./gradlew assembleDebug 빌드 확인 (BUILD SUCCESSFUL, 23s)
- [x] CHANGELOG.md [미커밋]에 항목 추가
