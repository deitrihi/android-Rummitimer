# v3 장기 타이머 — Context Notes

## 2026-06-10

### LaunchedEffect 전략
LaunchedEffect(activePlayer) 대신 LaunchedEffect(Unit) 단일 루프 채택.
- 이유: activePlayer가 바뀔 때 LaunchedEffect가 cancel/restart되는 타이밍과
  delay 재개 타이밍 사이에 race condition 발생 가능.
- 단일 루프에서 매 tick마다 activePlayer 현재값을 읽으면 안전.
- 단점: 일시정지 중에도 1초마다 루프가 돌지만 아무것도 하지 않으므로 무시 가능한 수준.

### 착수(Move) UX
플레이어 영역 전체를 탭 가능한 Surface로 만들어 착수 처리.
- 기존 "착수" 버튼 대신 전체 영역 탭 방식 — 체스 시계 앱과 동일한 UX.
- enabled 조건: isActive && gameStarted && !isGameOver
- 탭 불가 상태일 때 ripple 없음 (clickable(enabled=false)).

### P2 영역 회전
Modifier.rotate(180f)를 PlayerHalf 컴포저블에 적용.
- rotate()는 layout transform이 아닌 draw transform이므로 weight(1f)와 충돌 없음.
- 내부 텍스트, 힌트 모두 P2 방향에서 읽을 수 있음.

### TwoPlayerResultScreen — onRestart 동작
onRestart 호출 시 twoPlayerWinner = -1 초기화 후 TWO_PLAYER_TIMER로 이동.
TwoPlayerTimerScreen은 새 컴포저블로 생성되므로 플레이어 시간 상태가 리셋됨.
(rememberSaveable이지만 컴포저블 자체가 새로 생성되면 초기화됨)

### AutoFitText 접근성
HomeScreen.kt에 internal로 선언된 AutoFitText를 TwoPlayerTimerScreen/ResultScreen에서 사용.
Kotlin internal = 같은 모듈(app) 내 접근 가능 → 별도 이동 불필요.

### GameType enum 위치
MainActivity.kt에 Screen enum과 함께 위치. 추후 BADUK/CHESS 화면 구현 시 재사용.
지금은 JANGGI만 구현; SetupScreen의 titleRes 분기에 TODO 없이 일단 janggi_setup_title 사용.
