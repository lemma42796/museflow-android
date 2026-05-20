# MuseFlow Android

![Android](https://img.shields.io/badge/platform-Android-3DDC84.svg)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-7F52FF.svg)
![Android Gradle Plugin](https://img.shields.io/badge/AGP-8.2.0-3F51B5.svg)
![Status](https://img.shields.io/badge/status-active-blue.svg)

**Language**: [中文](README.md) | English

MuseFlow Android is an Android music and community app with music playback, discovery, feed publishing, chat conversations, offline downloads, and lyric display. The project focuses on mobile music listening, content browsing, social interaction, and local media management.

## Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Build And Run](#build-and-run)
- [Verification](#verification)
- [Roadmap](#roadmap)
- [Contributing](#contributing)
- [License](#license)

## Features

- Music playback: playback controls, queue, background notification, lyrics, and widget entry points.
- Discovery: home recommendations, banners, playlists, songs, and content modules.
- Community feed: image/text posts, image selection, image upload, feed list, and interaction entry points.
- Chat: conversation list, chat detail, history messages, and text/image message entry points.
- Downloads: downloading/downloaded lists, pause, resume, delete, and progress display.
- Local app workflow: local configuration, signing setup, build variants, and Android device installation.

## Tech Stack

- Languages: Java, Kotlin
- UI: XML, ViewBinding, Jetpack Compose
- Architecture: ViewModel, UseCase, Repository
- Async: Coroutines, Flow, RxJava
- Media: Media3, MediaSession
- Background work: WorkManager
- Data and paging: DataStore, Paging
- Dependency injection: Hilt
- Build: Android Gradle Plugin 8.2.0, Kotlin 1.9.22, compileSdk 34, targetSdk 33, minSdk 23

## Project Structure

The Android project lives under:

```text
code/video/MyCloudMusicAndroidJava/
```

Key directories:

```text
.
`-- code/video/MyCloudMusicAndroidJava/
    |-- app/                         # Main Android application
    |-- docs/                        # Project documentation
    |-- LRecyclerview/               # RecyclerView support module
    |-- glidepalette/                # Palette helper module
    |-- super-j/                     # Shared utility module
    |-- build.gradle                 # Android root build configuration
    |-- common.gradle                # Shared Android module configuration
    `-- settings.gradle              # Module registry and repositories
```

## Getting Started

### Prerequisites

- Android Studio with JDK 17
- Android SDK 34
- A device or emulator running Android 6.0 or newer
- Network access to Google Maven, Maven Central, JitPack, and RongCloud Maven

### Clone

```bash
git clone https://github.com/lemma42796/museflow-android.git
cd museflow-android/code/video/MyCloudMusicAndroidJava
```

### Local Configuration

The app build expects Android signing values in `keystore.properties`:

```properties
storeFile=config/your-debug-or-release-key.jks
storePassword=your-store-password
keyAlias=your-key-alias
keyPassword=your-key-password
```

Use your own local signing material and keep private credentials out of version control.

## Build And Run

From `code/video/MyCloudMusicAndroidJava`:

```bash
./gradlew :app:assembleDevDebug
```

Install the generated APK:

```bash
adb install -r app/build/outputs/apk/dev/debug/app-dev-debug.apk
```

Other useful variants:

```bash
./gradlew :app:assembleLocalDebug
./gradlew :app:assembleProdDebug
```

## Verification

Fast local checks:

```bash
git diff --check
./gradlew :app:assembleDevDebug
./gradlew :app:testDevDebugUnitTest
```

Recommended manual verification paths:

- App launch and session state
- Playback, pause, seek, previous/next, background notification, and lyrics
- Discovery banners, recommendation modules, songs, and playlist entry points
- Feed list, image selection, image upload, and publishing flow
- Conversation list, chat detail, history messages, and message entry points
- Downloading/downloaded lists, pause, resume, delete, and progress display

## Roadmap

- Improve end-to-end music, discovery, feed, chat, and download experiences.
- Continue improving playback state, list refresh, image processing, and background task stability.
- Add more complete device verification notes.
- Add screenshots, demo videos, and release notes.
- Add an open-source license and contribution guidelines.

## Contributing

Issues, suggestions, and pull requests are welcome. Before submitting code, run:

```bash
git diff --check
./gradlew :app:assembleDevDebug
```

If the change affects a user-visible flow, include the corresponding device verification notes.

## License

No open-source license file has been added yet. Choose and add a `LICENSE` file before distributing this project as an open-source package.
