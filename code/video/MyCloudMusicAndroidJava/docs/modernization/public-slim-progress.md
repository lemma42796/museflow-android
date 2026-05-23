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

## 2026-05-20 GitHub README handoff

Latest public presentation state:

- GitHub `origin/master` was updated from the main workspace branch `codex/github-root-readme`.
- Latest pushed commit before this handoff-doc update: `03c3355 Present README as new project`.
- Root `README.md` is now Chinese-first and presents MuseFlow Android as a standalone Android music/community app.
- Root `README.en.md` is the English version. The two files link to each other instead of showing both languages on one page.
- README wording intentionally avoids describing the repository as a rewrite, modernization, legacy migration, or public-slim cleanup. Keep that public-facing tone unless the user asks to expose project-history context.

Remote safety state:

- `origin` is GitHub: `https://github.com/lemma42796/museflow-android.git`.
- `upstream` is the original Gitee backup: `git@gitee.com:yyh455/my-cloud-music-android-java.git`.
- `upstream` push URL is intentionally disabled locally as `DISABLED_GITEE_BACKUP_DO_NOT_PUSH`.
- Gitee `upstream/master` was confirmed unchanged at `4d6c6759afcf34f2a038c34b69c4395462d45df0`.
- The accidental Gitee branch `codex/emulator-smoke-progress` was deleted from Gitee and should not be recreated.

Next-session rules:

- Before any push, run `git remote -v`, `git status --short --branch`, and state the exact push target.
- Push public README/docs presentation changes only to GitHub with `git push origin HEAD:master`.
- Do not run any write operation against `upstream`; treat it as read-only backup.
- Ignore the local untracked full-workspace files/modules unless the user explicitly asks to clean or inspect them.

## 2026-05-24 full-workspace progress branch handoff

Current full workspace branch: `codex/emulator-smoke-progress`.

Latest state in the full workspace:

- Java source cleanup is complete: `app/src/main/java` has no `.java` files.
- Normal layout XML cleanup is complete: `app/src/main/res/layout` is empty.
- The last Widget RemoteViews layout was replaced with Jetpack Glance; `res/xml/music_widget.xml` remains only as AppWidget provider metadata.
- Public-facing text/course trace cleanup has one low-risk pass completed on the progress branch; the next visible gap is bitmap/image assets.

Next public-facing asset work:

- Generate new MuseFlow Android launcher, splash/logo, placeholder/default avatar-cover, and Widget preview assets in a new session.
- Use the local untracked `docs/modernization/course-trace-cleanup-task.md` as the task note, but do not submit or push that file.
- Keep Gitee read-only and push only to GitHub `origin`.
