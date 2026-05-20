# MuseFlow Android

![Android](https://img.shields.io/badge/platform-Android-3DDC84.svg)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-7F52FF.svg)
![Android Gradle Plugin](https://img.shields.io/badge/AGP-8.2.0-3F51B5.svg)
![Status](https://img.shields.io/badge/status-active-blue.svg)

**语言**：中文 | [English](README.en.md)

MuseFlow Android 是一款 Android 音乐与社区应用，提供音乐播放、发现推荐、动态发布、聊天会话、离线下载和歌词展示等功能。项目面向移动端音乐体验，重点关注播放链路、内容浏览、社交互动和本地媒体管理。

## 目录

- [功能亮点](#功能亮点)
- [技术栈](#技术栈)
- [项目结构](#项目结构)
- [快速开始](#快速开始)
- [构建和运行](#构建和运行)
- [验证方式](#验证方式)
- [路线图](#路线图)
- [贡献说明](#贡献说明)
- [许可证](#许可证)

## 功能亮点

- 音乐播放：支持播放控制、播放队列、后台播放通知、歌词展示和桌面组件入口。
- 发现推荐：提供首页推荐、Banner、歌单、歌曲和内容模块浏览。
- 动态社区：支持图文动态、图片选择、图片上传、动态列表和互动入口。
- 即时聊天：支持会话列表、聊天详情、历史消息和文本/图片消息入口。
- 下载管理：支持下载中、已下载、暂停、继续、删除和进度展示。
- 本地体验：保留本地配置、签名构建、多环境构建变体和 Android 设备安装运行流程。

## 技术栈

- 语言：Java、Kotlin
- UI：XML、ViewBinding、Jetpack Compose
- 架构：ViewModel、UseCase、Repository
- 异步：Coroutines、Flow、RxJava
- 媒体：Media3、MediaSession
- 后台任务：WorkManager
- 数据与分页：DataStore、Paging
- 依赖注入：Hilt
- 构建：Android Gradle Plugin 8.2.0、Kotlin 1.9.22、compileSdk 34、targetSdk 33、minSdk 23

## 项目结构

Android 工程位于：

```text
code/video/MyCloudMusicAndroidJava/
```

关键目录：

```text
.
`-- code/video/MyCloudMusicAndroidJava/
    |-- app/                         # Android 主应用
    |-- docs/                        # 项目文档
    |-- LRecyclerview/               # RecyclerView 支撑模块
    |-- glidepalette/                # 调色板辅助模块
    |-- super-j/                     # 通用工具模块
    |-- build.gradle                 # Android 根构建配置
    |-- common.gradle                # 共享 Android 模块配置
    `-- settings.gradle              # 模块和仓库配置
```

## 快速开始

### 环境要求

- Android Studio，使用 JDK 17
- Android SDK 34
- Android 6.0 或更高版本的真机/模拟器
- 能访问 Google Maven、Maven Central、JitPack 和 RongCloud Maven

### 克隆仓库

```bash
git clone https://github.com/lemma42796/museflow-android.git
cd museflow-android/code/video/MyCloudMusicAndroidJava
```

### 本地配置

应用构建需要在 `keystore.properties` 中提供签名配置：

```properties
storeFile=config/your-debug-or-release-key.jks
storePassword=your-store-password
keyAlias=your-key-alias
keyPassword=your-key-password
```

请使用你自己的本地签名材料，并确保私有凭据不进入版本控制。

## 构建和运行

在 `code/video/MyCloudMusicAndroidJava` 目录执行：

```bash
./gradlew :app:assembleDevDebug
```

安装生成的 APK：

```bash
adb install -r app/build/outputs/apk/dev/debug/app-dev-debug.apk
```

其他可用构建变体：

```bash
./gradlew :app:assembleLocalDebug
./gradlew :app:assembleProdDebug
```

## 验证方式

快速本地检查：

```bash
git diff --check
./gradlew :app:assembleDevDebug
./gradlew :app:testDevDebugUnitTest
```

建议的人工验证路径：

- App 启动和会话状态
- 音乐播放、暂停、拖动、上一首/下一首、后台通知、歌词
- 发现页 Banner、推荐模块、歌曲和歌单入口
- 动态列表、图片选择、图片上传和发布流程
- 会话列表、聊天详情、历史消息和消息发送入口
- 下载中/已下载列表、暂停、继续、删除和进度展示

## 路线图

- 完善音乐播放、发现页、动态、聊天和下载的端到端体验。
- 持续优化播放状态、列表刷新、图片处理和后台任务的稳定性。
- 补充更完整的设备端验证记录。
- 增加截图、演示视频和正式发布说明。
- 添加开源许可证和贡献规范。

## 贡献说明

欢迎提交问题反馈、改进建议和 Pull Request。提交代码前建议先运行：

```bash
git diff --check
./gradlew :app:assembleDevDebug
```

如果改动涉及用户可见流程，请补充对应的设备端验证说明。

## 许可证

当前尚未加入开源许可证文件。正式作为开源项目分发前，请先选择许可证并添加 `LICENSE` 文件。
