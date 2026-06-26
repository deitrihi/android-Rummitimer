# v3 장기 타이머 — 체크리스트

## 2026-06-26 Families Policy Ad Content 대응
- [x] 공식 AdMob/Google Play Families 광고 요구사항 확인
- [x] 현재 광고 초기화, 광고 요청, 매니페스트 권한 확인
- [x] 가족 정책용 광고 설정을 모든 광고 요청에 일관 적용
- [x] 광고 SDK 버전 및 광고 ID 권한 처리 점검
- [x] 빌드 또는 테스트로 검증

## 플랜 문서
- [x] plan.md 작성
- [x] checklist.md 작성
- [ ] context-notes.md 작성

## enum / 공통
- [ ] GameType enum 추가 (JANGGI, BADUK, CHESS) — MainActivity.kt
- [ ] Screen enum에 TWO_PLAYER_SETUP, TWO_PLAYER_TIMER, TWO_PLAYER_RESULT 추가

## 신규 화면 (Kotlin)
- [ ] TwoPlayerSetupScreen.kt — 제한 시간 선택 (1/3/5/10/30분)
- [ ] TwoPlayerTimerScreen.kt — 2인 타이머, 착수 탭, 일시정지, 게임 종료
- [ ] TwoPlayerResultScreen.kt — 승자 표시, 소요 시간, 재시작/설정변경 버튼

## 기존 파일 수정
- [ ] MenuScreen.kt — onSelectJanggi 파라미터, 장기 항목 comingSoon=false
- [ ] MainActivity.kt (RummitimerApp) — 새 Screen 분기 3개 추가

## 리소스
- [ ] values/strings.xml (한국어)
- [ ] values-en/strings.xml (영어)
- [ ] values-ja/strings.xml (일본어)
- [ ] values-de/strings.xml (독일어)
- [ ] values-es/strings.xml (스페인어)
- [ ] values-nl/strings.xml (네덜란드어)

## 마무리
- [ ] CHANGELOG.md 업데이트 ([미커밋] 섹션)
- [ ] 세션 로그 작성 (claude_logs/)
- [ ] 빌드 확인 (./gradlew assembleDebug)
