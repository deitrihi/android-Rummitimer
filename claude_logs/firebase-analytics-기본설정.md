# firebase-analytics-기본설정
날짜: 2026-05-31

## 요청
Firebase Analytics를 앱에 추가하고, 기존 play-services-ads와의 manifest 충돌을 해소해달라.

## 행동
- `gradle/libs.versions.toml`: firebase-analytics 라이브러리 항목 추가
- `app/build.gradle.kts`: firebase-analytics 의존성 추가
- `app/src/main/AndroidManifest.xml`: AD_SERVICES_CONFIG 중복 선언으로 인한 병합 충돌 → `tools:replace` 어트리뷰트로 해소
- `app/build.gradle.kts`: compileSdk/targetSdk 36 → 37 업그레이드
- `gradle/libs.versions.toml`: core-ktx 1.10.1 → 1.19.0, lifecycle-runtime-ktx 2.6.1 → 2.10.0, activity-compose 1.8.0 → 1.13.0 업그레이드
- 빌드 성공 확인

## 결정
- manifest 충돌 원인: play-services-ads와 firebase-analytics 모두 AD_SERVICES_CONFIG 메타데이터를 선언 → `tools:replace`로 앱 측 선언이 우선하도록 설정
- SDK 업그레이드를 함께 진행하여 최신 버전 베이스라인 확보

## 미해결
- 이벤트 연동 코드 미작성 (다음 세션에서 진행)
