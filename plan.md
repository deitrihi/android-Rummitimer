# Rummitimer 다음 업데이트 플랜

## Context
루미큐브 게임 중 실제로 필요한 기능들을 추가한다.
현재는 순수 타이머 기능만 있으며, 점수 기록이나 플레이어 식별이 불편하다.
선정된 기능: 플레이어 과일 아이콘 선택, 점수판(Score Tracker), 패널티 기록.

---

## 목표 기능

### 1. 플레이어 과일 아이콘 선택
- SettingsScreen에서 각 플레이어별 과일 이미지 선택 UI 추가
- 제공할 과일 옵션: 사과, 바나나, 포도, 딸기, 수박, 오렌지, 복숭아, 체리 (8종)
- 각 플레이어는 서로 다른 과일을 선택하도록 유도 (중복 허용하되 시각적 구분 표시)
- 선택 결과는 SharedPreferences에 저장 (앱 재시작 후에도 유지)
- 기본값: 플레이어 1=사과, 2=바나나, 3=포도, 4=딸기
- 이미지 에셋: Android unicode 과일 문자 사용 (별도 이미지 파일 불필요)
  - 🍎 🍌 🍇 🍓 🍉 🍊 🍑 🍒
- TimerDisplay, SplitTimerCircle, PlayerIndicators에서 P1~P4 대신 과일 이모지 표시
- ResultScreen에서도 과일 이모지로 플레이어 식별

### 2. 점수판 (Score Tracker) + 결과 화면

**흐름:**
```
게임 중 → End 버튼 → ScoreInputScreen (점수 입력, 선택사항)
  → 완료 버튼 → ResultScreen (결과 표시)
  → 완료 버튼 → 광고 → 타이머 초기화 화면 복귀
```

**ScoreInputScreen (점수 입력):**
- 기존 End 버튼을 누르면 이 화면으로 이동 (광고 대신)
- 각 플레이어의 남은 타일 점수를 입력하는 TextField (숫자 키패드)
- 과일 이모지로 플레이어 식별
- 점수 입력은 선택사항 — 입력 안 해도 완료 가능
- "완료" 버튼 → ResultScreen으로 이동

**ResultScreen (결과 화면):**
- 각 플레이어의 점수 합계 표시 (점수 입력이 없으면 점수 항목 생략)
- 패널티 횟수는 항상 표시
- 점수 입력이 있을 경우: 순위 표시 (낮은 점수가 유리)
- 과일 이모지로 플레이어 식별
- "완료" 버튼 → 광고 표시 → 타이머 초기화 화면으로 복귀

### 3. 패널티 기록
- timeRemaining이 0이 됐을 때 (isTimeUp = true 전환 시) 해당 플레이어 패널티 +1 자동 카운트
- PlayerIndicators에 패널티 배지(숫자)로 표시
- ResultScreen에도 플레이어별 패널티 횟수 표시

---

## 아키텍처 변경

### 네비게이션
현재 `showSettings: Boolean` → `currentScreen: Screen` enum으로 교체

```kotlin
enum class Screen { HOME, SETTINGS, SCORE_INPUT, RESULT }
```

MainActivity.kt에서 `when(currentScreen)`으로 분기.

### 새 파일
- `FruitHelper.kt` — 과일 선택 저장/로드 (SharedPreferences)
- `ScoreInputScreen.kt` — 라운드 점수 입력 화면
- `ResultScreen.kt` — 결과 표시 화면

### 수정 파일
| 파일 | 변경 내용 |
|------|-----------|
| `MainActivity.kt` | showSettings → currentScreen enum, 새 화면 라우팅, 점수/패널티 상태 최상위 이동 |
| `HomeScreen.kt` | 패널티 카운트 로직, 과일 이모지 수신, End 버튼 → ScoreInputScreen 이동 |
| `SettingsScreen.kt` | 플레이어별 과일 선택 그리드 섹션 추가 |
| `strings.xml` | 새 문자열 리소스 추가 |

---

## 구현 체크리스트

- [ ] FruitHelper.kt 생성 — 과일 인덱스 저장/로드 (SharedPreferences)
- [ ] MainActivity.kt — Screen enum, 네비게이션 리팩터, 상태 최상위 이동
- [ ] SettingsScreen.kt — 플레이어별 과일 선택 그리드 섹션 추가
- [ ] HomeScreen.kt — 과일 이모지 파라미터 수신, 패널티 카운트 로직, End 버튼 동작 변경
- [ ] ScoreInputScreen.kt 신규 작성
- [ ] ResultScreen.kt 신규 작성
- [ ] strings.xml — 새 문자열 추가
- [ ] 빌드 & 동작 확인
