# families-policy-ad-fix

## 2026-06-24 | 패밀리 정책 광고 설정 수정

### 요청
- Google Play 업데이트 거부: "Families Policy Requirements: Ad Content"

### 행동
- `MainActivity.kt`에 `MobileAds.setRequestConfiguration()` 추가
  - `TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE`: 아동 대상 광고로 태깅
  - `MAX_AD_CONTENT_RATING_G`: 전체 관람가 광고만 노출

### 결정
- 앱이 패밀리(어린이 포함) 타겟으로 설정되어 있으므로 아동 친화 광고 설정 적용
- `MobileAds.initialize()` 호출 전에 `setRequestConfiguration()` 선행 필수

### 미해결
- 없음
