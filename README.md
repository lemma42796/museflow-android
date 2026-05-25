# MuseFlow Android

![Android](https://img.shields.io/badge/platform-Android-3DDC84.svg)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-7F52FF.svg)
![Android Gradle Plugin](https://img.shields.io/badge/AGP-8.5.2-3F51B5.svg)
![Gradle](https://img.shields.io/badge/Gradle-8.7-02303A.svg)
![Status](https://img.shields.io/badge/status-active-blue.svg)

**语言**：中文 | [English](README.en.md)

MuseFlow Android 是一款面向移动端的音乐与社区应用，覆盖音乐播放、首页发现、动态发布、即时聊天、离线下载、歌词展示和桌面组件等核心场景。项目重点放在真实 App 常见的复杂链路：媒体播放状态、列表刷新、图片处理、后台服务、设备端性能和可维护的页面状态管理。

## 目录

- [项目亮点](#项目亮点)
- [界面预览](#界面预览)
- [核心功能](#核心功能)
- [工程实现](#工程实现)
- [可验证记录](#可验证记录)
- [项目结构](#项目结构)
- [快速开始](#快速开始)
- [构建和运行](#构建和运行)
- [验证方式](#验证方式)
- [路线图](#路线图)
- [许可证](#许可证)

## 项目亮点

- 完整音乐播放链路：基于 Media3 ExoPlayer、MediaSessionService 和系统媒体控制，支持播放、暂停、seek、上一首/下一首、后台通知、播放队列、歌词和 Widget 联动。
- 多场景业务闭环：首页推荐、歌单/歌曲入口、动态列表、图片选择与上传、聊天会话、下载管理、本地音乐扫描等流程均可在设备端串联验证。
- 清晰的状态分层：主要页面使用 `ViewModel -> UseCase -> Repository` 的状态组织方式，页面层通过状态流渲染 UI，降低网络、缓存、播放和列表刷新逻辑之间的耦合。
- Compose 页面体验：主入口、首页、动态、聊天、下载、歌单详情、评论、本地音乐、歌词选择和播放器相关页面采用 Compose 构建，复杂歌词和黑胶控件通过稳定的 AndroidView 边界复用。
- 性能证据可复跑：工程内包含 Macrobenchmark、Baseline Profile、Perfetto trace section 和 benchmark-only fixture，覆盖冷启动、首页滚动、首页进入播放器、播放控制、歌词拖拽和下载列表刷新。
- 设备兼容治理：构建链路已面向 Android 16 KB page-size 设备做 native packaging 对齐处理，并保留多环境构建变体和本地签名配置。

## 界面预览

<p>
  <img src="docs/assets/screenshots/museflow-home.png" alt="MuseFlow 首页" width="320" />
  <img src="docs/assets/screenshots/museflow-player.png" alt="MuseFlow 播放器" width="320" />
</p>

## 核心功能

- 音乐播放：播放控制、播放队列、进度拖动、循环模式、后台通知、歌词展示、桌面歌词和桌面 Widget。
- 首页发现：推荐 Banner、歌单、单曲、快捷入口、首页滚动和点歌进入播放器。
- 动态社区：动态列表、图片预览、图片选择、图片压缩上传、发布入口和互动入口。
- 即时聊天：会话列表、聊天详情、历史消息、文本消息、图片消息和未读状态。
- 下载管理：下载中/已下载双列表、进度展示、暂停、继续、删除和批量刷新。
- 本地音乐：本地歌曲扫描、列表展示、歌曲播放和歌词相关入口。

## 工程实现

- 语言与 UI：Kotlin、Jetpack Compose、Material 3、AndroidView、Glance App Widget
- 架构组织：ViewModel、UseCase、Repository、StateFlow、SharedFlow
- 媒体播放：Media3 ExoPlayer、MediaSessionService、系统媒体通知、播放队列状态同步
- 后台与数据：WorkManager、DataStore、Paging、Retrofit、OkHttp、Hilt
- 性能工具：Macrobenchmark、Baseline Profile、Perfetto trace、自定义 trace section
- 构建配置：Android Gradle Plugin 8.5.2、Gradle 8.7、Kotlin 1.9.22、compileSdk 34、targetSdk 33、minSdk 23

## 可验证记录

以下数据来自当前仓库记录的本地设备验证，主要用于说明工程链路具备可复跑的度量方式，而不是泛化到所有设备的性能承诺。

- `:app:assembleDevDebug` 可构建调试 APK。
- Macrobenchmark 已覆盖冷启动、首页滚动、首页进播放器、播放控制、歌词面板、歌词拖拽和下载列表刷新。
- Redmi `25060RK16C` 真机上，Baseline Profile 固化后冷启动 median 为 `302.5 ms`。
- 同设备播放器首屏 `frameDurationCpuMs` P99 从 `26.9 ms` 降至 `18.7 ms`，约改善 `30.5%`。
- 首页推荐流滚动真机复测：`frameDurationCpuMs` P99 `6.4 ms`，`frameOverrunMs` P99 `-1.4 ms`。
- 歌词拖拽真机复测：`frameDurationCpuMs` P99 `11.5 ms`，`frameOverrunMs` P99 `4.3 ms`。
- 下载列表刷新 no-input benchmark：`frameDurationCpuMs` P99 `15.9 ms`，`frameOverrunMs` P99 `8.3 ms`。
- Android 16 KB page-size 对齐已完成第一轮构建侧处理，普通模拟器安装启动和 fatal 日志检查通过。

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
    |-- macrobenchmark/              # 启动、播放、歌词和下载场景的性能用例
    |-- docs/                        # 工程记录和验证文档
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
```

性能用例示例：

```bash
./gradlew :app:compileDevBenchmarkKotlin :macrobenchmark:compileDevBenchmarkKotlin
./gradlew :macrobenchmark:connectedDevBenchmarkAndroidTest
```

建议的人工验证路径：

- App 启动和首页状态
- 首页滚动、点歌进入播放器、播放/暂停、seek、上一首/下一首和后台通知
- 歌词面板、歌词拖拽、桌面歌词和 Widget 控制
- 动态列表、图片选择、图片上传和发布流程
- 会话列表、聊天详情、历史消息和消息发送入口
- 下载中/已下载列表、暂停、继续、删除和进度展示

## 路线图

- 补充首页、播放器、动态/聊天和下载管理截图。
- 增加演示视频和更完整的设备端验证记录。
- 继续完善音乐播放、歌词、下载刷新和图片处理的性能复测。
- 补充正式开源许可证和贡献规范。

## 许可证

当前尚未加入开源许可证文件。正式作为开源项目分发前，请先选择许可证并添加 `LICENSE` 文件。
