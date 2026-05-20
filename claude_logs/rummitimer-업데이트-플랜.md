# rummitimer-업데이트-플랜
날짜: 2026-05-13

## 세션 개요
루미큐브 타이머 앱의 다음 업데이트 기능을 논의하고 플랜을 작성한다.

## 작업 로그

### 기능 논의
- 요청: 루미큐브 게임에 도움이 될 기능 제안
- 제안된 기능: 점수판, 패널티 기록, 게임 통계, 플레이어 이름 입력, 타일 계산기
- 결정: 플레이어 과일 아이콘 선택, 점수판(Score Tracker), 패널티 기록 3가지 선정

### 플랜 작성 (Plan 모드)
- 요청: 선정된 기능을 Plan 모드로 정리
- 주요 결정 사항.
  - 플레이어 식별: 텍스트 이름 → 과일 이모지(🍎🍌🍇🍓🍉🍊🍑🍒) 선택 방식으로 변경
  - 점수 입력 시점: End 버튼 클릭 시 ScoreInputScreen으로 이동
  - 점수 입력은 선택사항 (미입력 시 패널티 배지만 ResultScreen에 표시)
  - 흐름: End 버튼 → ScoreInputScreen → ResultScreen → 완료 버튼 → 광고 → 타이머 초기화
  - 기존 End 버튼의 광고를 ResultScreen 완료 버튼으로 이동
- 산출물: `plan.md` 프로젝트 루트에 저장

### CLAUDE.md 수정 (글로벌 지침 추가)
- 요청 1: 플랜 파일을 항상 프로젝트 폴더에 저장하도록 지침 추가
  - 행동: `~/.claude/CLAUDE.md` 섹션 7에 프로젝트 루트 저장 지침 추가
- 요청 2: 세션 로그를 남기는 지침 추가
  - 행동: `~/.claude/CLAUDE.md` 섹션 11 신규 추가 (`logs/YYYY-MM-DD.md` 형식)
- 요청 3: 로그 폴더를 `claude_logs`로, 파일명을 세션명 형식으로 변경
  - 행동: 섹션 11 수정 (`claude_logs/<세션명>.md` 형식으로 업데이트)

## 미해결 이슈
- 없음. 플랜 작성 완료, 구현은 다음 세션에서 진행 예정.

---

## 2026-05-20 | mipmap-아이콘-생성

### 요청
- rummikub-timer-icon-v5.svg를 Android mipmap 규격 아이콘 셋으로 변환

### 행동
- SVG를 프로젝트 루트에 저장
- resvg-js(Node.js, Rust 기반 SVG 렌더러)를 사용해 PNG 변환
- 레거시(48~192px): ic_launcher.png, ic_launcher_round.png
- 어댑티브 전경(108~432px): ic_launcher_foreground.png (배경 rect 제거, 투명)
- 기존 webp 파일 삭제
- ic_launcher_background.xml의 기존 색상(#191C32)이 SVG 배경색과 근사하므로 유지
- `./gradlew assembleDebug` 빌드 성공 확인

### 결정
- cairo 의존성이 없는 resvg-js 사용 (Windows에서 cairosvg/svglib 동작 불가)
- foreground 레이어: SVG에서 배경 rect 2개만 제거, 컨텐츠는 그대로 유지

### 미해결
- 없음
