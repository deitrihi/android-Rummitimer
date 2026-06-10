# v3 장기 타이머 — 개발 플랜

## 목표
장기(절대 시간 방식)를 위한 2인 대국 타이머 개발.
스마트폰을 두 플레이어 사이에 놓고, 상단=P2(180도 회전), 하단=P1이 각자 자신의 시간을 보는 UI.

## 화면 흐름
```
MenuScreen
  └─ [장기 선택] → TwoPlayerSetupScreen
                      └─ [시작] → TwoPlayerTimerScreen
                                    └─ [결과 보기] → TwoPlayerResultScreen
                                                       ├─ [새 게임] → TwoPlayerTimerScreen
                                                       └─ [설정 변경] → TwoPlayerSetupScreen
```

## 레이아웃 (세로 모드)
```
┌─────────────────────────────┐
│  P2 영역 (180도 회전)        │
│  시간 표시 MM:SS             │
│  탭하여 착수 / 대기 중 / 승리 │
├─────────────────────────────┤
│  [게임종료]  [일시정지/재개]  │  컨트롤 스트립
├─────────────────────────────┤
│  탭하여 착수 / 대기 중 / 승리 │
│  시간 표시 MM:SS             │
│  P1 영역                     │
└─────────────────────────────┘
```

## 상태 설계 (TwoPlayerTimerScreen)
| 변수 | 타입 | 설명 |
|---|---|---|
| player1Time | Int | P1 남은 초 |
| player2Time | Int | P2 남은 초 |
| activePlayer | Int | -1=미시작/일시정지, 0=P1, 1=P2 |
| lastActivePlayer | Int | 일시정지 전 플레이어 (재개에 사용) |
| gameStarted | Boolean | 시작 버튼 눌렀는지 여부 |
| winner | Int | -1=없음, 0=P1 승, 1=P2 승 |

## 타이머 엔진
- LaunchedEffect(Unit) 단일 루프 (race condition 방지)
- 매초 activePlayer 확인 후 해당 플레이어 시간 차감
- 10초 이하: MetronomePlayer.tick(warning=true)
- winner 설정 후 break — 루프 종료

## 신규 파일
- TwoPlayerSetupScreen.kt — 제한 시간 선택 (1/3/5/10/30분 프리셋)
- TwoPlayerTimerScreen.kt — 2인 대국 타이머 화면
- TwoPlayerResultScreen.kt — 승패 표시 + 소요 시간

## 수정 파일
- MainActivity.kt — GameType enum, Screen 3개 추가, RummitimerApp 분기
- MenuScreen.kt — 장기 comingSoon=false, onSelectJanggi 파라미터
- strings.xml x 6 로케일 — 신규 문자열 10개
