# Public Slim Progress

Date: 2026-05-16

## Goal

Build a public-slim Android branch that removes frozen features from the repository. The user clarified that history cleanup is not required; the priority is removing frozen feature code from the working tree.

## Workspace

- Main full workspace: `/Users/a123/StudioProjects/my-cloud-music-android-java/code/video/MyCloudMusicAndroidJava`
- Public slim worktree: `/private/tmp/museflow-public-slim`
- Current branch in slim worktree: `codex/github-public-slim-ff`

The main full workspace was intentionally not switched, cleaned, or reverted.

## Completed

- Created an independent public-slim worktree.
- Removed frozen feature source packages and modules for mall/order/payment/address/profile/settings/search/scan/video/web/push/splash/guide and related sample modules.
- Removed frozen SDK integrations from Gradle and Manifest:
  - MobSDK/ShareSDK
  - JPush/JAnalytics/Xiaomi push
  - Bugly
  - AMap
  - AliPay/WeChat pay callbacks
  - Tencent super player / GSY video
  - Baidu OCR/speech artifacts
  - LetterIndexView and pinyin utility
- Replaced app entry points with public-slim shells:
  - `AppContext`
  - `MainActivity`
  - login/user/location stub activities
- Slimmed API/Repository to retained paths:
  - discovery/banner/sheets/songs
  - feed publish/list/upload
  - comments
  - user detail/follow/fans/friends basics
  - chat/conversation
  - download/player/music/lyric
- Removed frozen layouts and menu resources that caused ViewBinding to generate classes for deleted SDKs.
- Removed the remaining standalone region selector package.
- Added tiny compatibility behavior for retained paths that can still point at deleted features:
  - banner ad clicks are ignored in the slim build.
  - comment nickname clicks route to the no-op `UserDetailActivity` shell.
- Synced the first retained-chain Kotlin migration from the full workspace:
  - discovery aggregation: `DiscoveryPage.kt`, `DiscoveryRepository.kt`
  - download facade: `DownloadRepository.kt`
  - feed publish/list state entrypoints: `FeedPublishRepository.kt`, `ImageCompressionRepository.kt`, `FeedRepository.kt`, `FeedPublishViewModel.kt`
- Added `local.properties` only in the temporary worktree so Gradle can find `/Users/a123/Library/Android/sdk`; this should stay local and not be pushed.

## Verification

Command used:

```bash
./gradlew :app:assembleDevDebug
./gradlew :app:testDevDebugUnitTest
```

Result: passed.

Output APK:

```text
app/build/outputs/apk/dev/debug/app-dev-debug.apk
```

## Important Context

- Do not push frozen feature code.
- If continuing later, work in `/private/tmp/museflow-public-slim` unless a new strategy is chosen.
- The full local project remains available in the main workspace and should not be damaged by public-slim cleanup.
- Do not push directly from the full workspace to GitHub `master`; sync public-safe changes into this worktree first, then push `origin HEAD:master`.
