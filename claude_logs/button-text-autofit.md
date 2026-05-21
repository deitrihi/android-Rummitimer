# button-text-autofit
날짜: 2026-05-21

## 요청
버튼 글자가 작은 화면에서 2줄로 내려오는 문제를 해결해달라. 버튼 크기는 고정하고 글자만 살짝 작아지게 해달라.

## 분석
- `ControlButtons` 내 버튼이 Row의 weight(1f):weight(1.4f):weight(1f)로 배분
- 360dp 폰에서 padding 제외 후 weight-1 버튼의 텍스트 영역 ≈ 37dp
- "게임종료"(4자) at 14sp ≈ 56dp → 영역 초과로 2줄 줄바꿈 발생

## 행동
- `HomeScreen.kt`: `BoxWithConstraints` + `TextMeasurer` 기반 `AutoFitText` 컴포저블 추가
  - 측정 후 폰트 크기를 0.5sp씩 줄여가며 1줄에 들어올 때까지 반복
  - minFontSize = 8.sp 하한 보장
- `HomeScreen.kt`: `ControlButtons`, `VerticalControlButtons` 내 Text → AutoFitText 교체
- `ResultScreen.kt`: "완료 (광고 후 초기화)" 버튼 텍스트 → AutoFitText 교체
- `assembleDebug` 빌드 성공 확인

## 결정
- `TextAutoSize` (Compose 1.8+ 실험적 API) 대신 `BoxWithConstraints` 방식 채택 → 안정적이고 Material3 Text와 완전 호환
- `AutoFitText`를 `internal fun`으로 선언 → 동일 모듈 내 ResultScreen.kt에서 공유 가능
- `remember(text, maxWidthPx, baseFontSize, fontWeight)` 키로 재측정 최소화

## 미해결
- 없음
