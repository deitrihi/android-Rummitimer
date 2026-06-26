# v3 장기 타이머 — Context Notes

## 2026-06-26 Families Policy Ad Content 대응

### 초기 판단
Google Play 반려 사유가 `Families Policy Requirements: Ad Content`이므로 소스에서 광고 SDK 버전, 광고 요청 설정, 광고 ID 권한 병합 여부를 점검한다.
공식 AdMob 문서 기준으로 아동 또는 연령 미확인 사용자에게 광고를 제공할 때 child-directed treatment와 최대 광고 콘텐츠 등급 G가 필요하다.
현재 `MainActivity.kt`에 전역 TFCD와 `MAX_AD_CONTENT_RATING_G` 설정은 있으나, 배너와 전면 광고 요청이 개별적으로 `AdRequest.Builder().build()`를 직접 호출하고 있어 정책 대응 코드가 분산되어 있다.
AdMob 문서는 GMA SDK 20.6.0 이상에서 TFCD/TFUA 설정 시 AAID 전송을 막는다고 설명하지만, 앱 전체에서 광고 ID 권한 병합을 막는 선택지도 제시한다.

### 적용 결정
`AdMobPolicy.kt`를 추가해 광고 SDK 초기화와 광고 요청 생성을 한 곳으로 모은다.
GMA SDK는 Maven 기준 최신 릴리스인 `25.4.0`으로 올리고, Android SDK 공개 API에서 지원되는 child-directed treatment와 `MAX_AD_CONTENT_RATING_G`를 적용한다.
병합 매니페스트에서 `com.google.android.gms.permission.AD_ID` 권한이 들어오지 않도록 `tools:node="remove"`를 명시한다.

### 빌드 중 정정
초기에는 `AgeRestrictedTreatment.CHILD` 적용을 시도했으나 `play-services-ads:25.4.0` Android 공개 API에서 해당 타입이 없어 컴파일 실패했다.
해당 시도는 제거하고 공식 Android API로 제공되는 TFCD와 최대 광고 콘텐츠 등급 G만 유지한다.

`AndroidManifest.xml`이 기존에 `@xml/gma_ad_services_config`를 참조하고 있었지만 실제 앱 리소스가 없어 `processDebugResources`에서 실패했다.
광고 SDK가 제공하던 표준 `gma_ad_services_config.xml` 내용을 앱 리소스로 추가해 manifest 참조를 유효하게 만들었다.

빌드 후 debug 병합 매니페스트에서 `com.google.android.gms.permission.AD_ID`는 제거됐지만 `android.permission.ACCESS_ADSERVICES_AD_ID`가 남아 있는 것을 확인했다.
가족 정책 리스크를 더 줄이기 위해 Privacy Sandbox 광고 ID 권한도 `tools:node="remove"`로 제거한다.

### 검증
`assembleDebug test` 통과.
debug 병합/패키징 manifest에서 `AD_ID`, `ACCESS_ADSERVICES_AD_ID`, `com.google.android.gms.permission.AD_ID` 검색 결과 0건.
`assembleRelease` 통과.
release 병합/패키징 manifest에서도 동일 검색 결과 0건.

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
