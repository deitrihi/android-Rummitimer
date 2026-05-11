# 릴리즈 번들 빌드: versionCode 자동 증가 후 서명된 AAB 생성

$ErrorActionPreference = "Stop"
$ScriptDir  = Split-Path -Parent $MyInvocation.MyCommand.Path
$gradleFile = Join-Path $ScriptDir "app\build.gradle.kts"
$utf8NoBom  = New-Object System.Text.UTF8Encoding $false

# ── 1. versionCode 읽기 및 증가 ────────────────────────────────
$content = [System.IO.File]::ReadAllText($gradleFile)

if ($content -notmatch 'versionCode\s*=\s*(\d+)') {
    Write-Error "versionCode not found in build.gradle.kts"
    exit 1
}

$currentVersion = [int]$Matches[1]
$newVersion     = $currentVersion + 1

$content = $content -replace "versionCode\s*=\s*$currentVersion", "versionCode = $newVersion"
[System.IO.File]::WriteAllText($gradleFile, $content, $utf8NoBom)

Write-Host "[1/3] versionCode: $currentVersion -> $newVersion" -ForegroundColor Cyan

# ── 2. Java 환경 설정 ──────────────────────────────────────────
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
$env:PATH      = "$env:JAVA_HOME\bin;$env:PATH"

# ── 3. 릴리즈 번들 빌드 ───────────────────────────────────────
Write-Host "[2/3] Building release bundle..." -ForegroundColor Cyan
$gradlew = Join-Path $ScriptDir "gradlew.bat"
& $gradlew bundleRelease

if ($LASTEXITCODE -ne 0) {
    # 빌드 실패 시 versionCode 롤백
    $content = [System.IO.File]::ReadAllText($gradleFile)
    $content = $content -replace "versionCode\s*=\s*$newVersion", "versionCode = $currentVersion"
    [System.IO.File]::WriteAllText($gradleFile, $content, $utf8NoBom)
    Write-Host "[FAIL] Build failed. versionCode rolled back: $newVersion -> $currentVersion" -ForegroundColor Red
    exit 1
}

# ── 4. 결과 출력 ──────────────────────────────────────────────
$aabPath = Join-Path $ScriptDir "app\build\outputs\bundle\release\com.deitrihi.rummitimer-release.aab"

Write-Host "[3/3] Done." -ForegroundColor Green
Write-Host "  versionCode : $newVersion"
Write-Host "  Output      : $aabPath"
