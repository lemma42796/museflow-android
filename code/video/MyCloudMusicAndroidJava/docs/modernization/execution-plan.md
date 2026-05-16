# 重点链路现代化执行规划

> 日期：2026-05-16  
> 目的：把已确认的重构范围拆成可执行步骤，确保每一步都有明确入口、验收点和暂停条件。

## 当前状态

当前已完成：

- 已确认本轮只处理五条重点链路。
- 已确认冻结模块和允许的最小适配边界。
- 已确认目标技术方向和人工冒烟标准。
- 已建立 Kotlin/Compose/现代依赖基线。
- `:app:assembleDevDebug` 构建通过。
- 音乐播放链路已进入阶段 2，完成第一步 Media3 播放核心和旧 Manager 兼容桥接。
- 聊天、动态发布、下载、发现/信息流链路已完成第一轮 Repository/ViewModel/兼容桥接闭环，旧入口继续保留。

当前尚未完成：

- 音乐播放链路的在线/本地播放、通知控制、歌词进度人工冒烟尚未执行。
- App 安装启动和五条链路完整人工冒烟尚未执行。

因此下一步应进入阶段 7，在模拟器或真机上按五条链路统一做人工冒烟。

## 最新执行记录

### 2026-05-16 阶段 1 完成

构建层改动：

- 接入 Kotlin Android Gradle Plugin `1.9.22`。
- 打开 `app` 模块 Compose 编译能力。
- 锁定 Compose Compiler `1.5.10`，匹配 Kotlin `1.9.22`。
- 引入 Compose BOM `2024.02.02`、Coroutines、Media3、WorkManager、DataStore、Paging Compose。
- 将 Hilt Gradle 插件、runtime、compiler 对齐到 `2.48.1`。
- Paging 从 `3.1.1` 升到 `3.2.1`。

验证结果：

- 命令：`./gradlew :app:assembleDevDebug`
- 结果：通过。
- 产物：`app/build/outputs/apk/dev/debug/app-dev-debug.apk`

注意：

- 当前没有 Kotlin 源码需要 Hilt 注解处理，所以没有启用 KAPT。
- 首次尝试启用 KAPT 时，JDK 17.0.16 的 Gradle daemon 在 KAPT stub 阶段触发 JVM 崩溃；后续等第一批 Kotlin Hilt 类出现时再单独处理 KAPT。
- 构建中存在大量旧第三方 SDK 的 D8/Jetifier warning，暂未作为阶段 1 阻塞项处理。

### 2026-05-16 Gradle 性能整理

已处理：

- 固定 `com.tencent.bugly:crashreport_upgrade` 到 `1.6.1`，移除 `latest.release`。
- 固定 `com.tencent.bugly:nativecrashreport` 到 `3.9.2`，移除 `latest.release`。
- 固定 `com.github.ctiao:DanmakuFlameMaster` 到 `0.3.8`，移除 `+`。
- 开启 `org.gradle.caching=true`，减少可复用任务重复执行。
- 设置 `org.gradle.workers.max=4`，降低 D8、Java 编译、资源处理阶段的峰值并发和内存压力。
- 移除全局 `android.defaults.buildfeatures.buildconfig=true`，改为只在 `app` 模块开启 `buildConfig`。

验证结果：

- 命令：`./gradlew :app:assembleDevDebug`
- 结果：通过。

保留问题：

- `android.enableJetifier=false` 测试失败，`CommentActivity` 通过旧刷新控件仍依赖 support 包桥接，Jetifier 暂时必须保留。
- `BGABadgeView-Android:compiler:1.2.0` 是非增量 annotation processor，仍会拖慢 Java 增量编译。
- MobSDK、JPush、LiteAV 等旧 SDK 仍有大量 D8 stack map warning，当前不阻塞构建，但会影响日志噪音和冷构建耗时。

### 2026-05-16 阶段 2 第一刀：Media3 播放桥接

已处理：

- 新增 `playback` 包：
  - `PlaybackController`：基于 Media3 `ExoPlayer` 的播放核心。
  - `PlaybackState`、`QueueState`、`LyricState`：为后续 Repository/ViewModel 暴露状态流做准备。
  - `PlaybackService`：基于 Media3 `MediaSessionService` 注册新的媒体会话服务。
- 将旧 `MusicPlayerManagerImpl` 改为 Java 兼容门面：
  - 保留原 `MusicPlayerManager` API。
  - 播放、暂停、继续、seek、单曲循环改为转发到 `PlaybackController`。
  - 移除旧 `MediaPlayer.prepare()` 同步准备路径，改为 Media3 异步 prepare。
  - 移除 16ms `TimerTask` 进度广播，改为 `PlaybackController` 内 100ms 协程节流进度。
  - 保留旧 `MusicPlayerListener` 回调，旧 UI 暂不改。
- 将 `MusicListManagerImpl` 的播放列表、当前歌曲和循环模式同步到 `QueueState`。
- 旧 `MusicPlayerService`、通知、Widget、歌词入口暂时保留，仍通过旧 Manager API 进入新播放核心。

验证结果：

- 命令：`./gradlew :app:assembleDevDebug`
- 结果：通过。

保留问题：

- 尚未做设备端人工冒烟，在线/本地播放、seek、上一首/下一首、后台通知控制需要实机验证。
- 新 `PlaybackService` 已注册，但旧通知和系统控制仍主要走 `MusicPlayerService` + `MediaSessionCompat`。
- `MusicPlayerListener.onPrepared/onCompletion` 的 `MediaPlayer` 参数在 Media3 路径下传 `null`，当前调用方未使用该参数；后续可新增媒体无关回调并逐步替换。

### 2026-05-16 阶段 2 状态入口和 Media3 会话补齐

已处理：

- 新增 `PlaybackRepository`，集中暴露：
  - `StateFlow<PlaybackState>`
  - `StateFlow<QueueState>`
  - `StateFlow<LyricState>`
  - 播放、暂停、继续、seek、循环、队列和歌词状态动作。
- `MusicPlayerManagerImpl` 和 `MusicListManagerImpl` 改为通过 `PlaybackRepository` 接入播放核心和状态。
- 新增 `LegacyMusicSessionPlayer`：
  - 基于 Media3 `ForwardingPlayer`。
  - 将 Media3 session 的 play/pause/seek/previous/next 转发到旧 `MusicListManager`。
  - 空播放列表场景下忽略系统 play 命令，避免旧 `resume()` 走到空数据。
- `PlaybackService` 改为使用 `LegacyMusicSessionPlayer` 创建 Media3 `MediaSession`，不再只是裸露 `ExoPlayer`。
- `MusicPlayerService.onProgress` 只更新 MediaSession 播放位置，并以 2s 间隔刷新 Widget 进度，避免通知/Widget 跟随歌词高频刷新。
- `MusicListManagerImpl` 对空播放列表、空当前歌曲的 play/resume/previous/seek 做防御，降低通知和 Widget 控制入口的崩溃风险。
- `SmallAudioControlPageFragment` 对列表索引和当前歌曲为空做防御，避免播放列表尚未恢复完成时小播放器崩溃。

验证结果：

- 命令：`./gradlew :app:assembleDevDebug :app:testDevDebugUnitTest`
- 结果：通过。

保留问题：

- 按当前策略，先继续补齐代码，模拟器冒烟后置到阶段 2 代码闭环完成后统一执行。
- 旧通知 UI 仍由 `MusicPlayerService` 和 `MediaSessionCompat` 创建；Media3 session 已能代理系统控制，但通知视觉层尚未切到 Media3 默认通知或新的通知管线。

### 2026-05-16 阶段 2 快速验证

已执行：

- 命令：`./gradlew :app:assembleDevDebug`
- 结果：通过。
- 命令：`./gradlew :app:testDevDebugUnitTest`
- 结果：通过。
- 命令：`adb devices`
- 结果：ADB daemon 可启动，但当前无已连接设备或模拟器。

保留问题：

- 尚未安装 APK 启动 App。
- 尚未完成在线/本地播放、seek、上一首/下一首、通知按钮、Widget、歌词进度的设备端冒烟。

### 2026-05-16 阶段 3-6 兼容桥接闭环

用户要求先完成所有重构，再统一跑模拟器测试，因此设备端冒烟后置到代码闭环之后。

已处理：

- 聊天 IM 链路：
  - 新增 `ChatClient`，统一包装融云会话、历史消息、已读、文本发送、图片发送 callback。
  - 新增 `ConversationRepository`、`MessageRepository`。
  - `ConversationActivity`、`ChatActivity` 改为通过 Repository 访问聊天 SDK。
  - `AppContext` 收到融云消息时同步写入新的 `SharedFlow` 入口，同时保留旧 EventBus/通知逻辑。
- 动态图片压缩/上传链路：
  - 新增 `FeedPublishRepository`、`ImageCompressionRepository`、`FeedPublishViewModel`。
  - `PublishFeedActivity` 的选图状态迁移到 ViewModel，Multipart 上传组装迁移到 Repository。
  - 删除旧 `UploadFeedImageAsyncTask` 和配套 `Result`。
  - `ImageCompressor` 补齐解码失败保护、缓存目录兜底、唯一文件名和 Bitmap 回收。
- 下载进度刷新链路：
  - 新增 `DownloadRepository` 统一包装下载 SDK。
  - `DownloadingFragment`、`DownloadedFragment`、`DownloadingAdapter`、播放器下载入口、歌单下载标记改为通过 Repository 访问下载任务。
  - `MyDownloadListener` 将高频下载进度刷新节流到 300ms，并保证 UI 刷新回到主线程。
  - `BaseRecyclerViewAdapter.setDatum` 修正为空列表时不清空旧数据的问题。
- 首页/发现/信息流列表刷新链路：
  - 新增 `DiscoveryRepository` 和 `DiscoveryPage`，把发现页 Banner、按钮、歌单、单曲聚合为 typed sections。
  - `DiscoveryFragment` 移除串行嵌套请求，改为一次 Repository 聚合输出。
  - `DiscoveryAdapter` 启用稳定 item id。
  - 新增 `FeedRepository`，`FeedFragment` 通过 Repository 刷新动态列表。

验证结果：

- 命令：`./gradlew :app:assembleDevDebug :app:testDevDebugUnitTest`
- 结果：通过。

保留问题：

- 按当前策略尚未启动模拟器或真机，五条链路仍需人工冒烟。
- 聊天、发现、动态、下载仍保留旧 XML/RecyclerView UI；本轮完成的是 Repository/ViewModel/状态入口和旧入口兼容桥接，未继续强行替换为 Compose。

### 2026-05-16 交接上下文

当前代码状态：

- 当前分支：`master`。
- 本轮未拆提交，包含三类相邻工作：
  - 歌词小修：清理歌词绘制日志/测试画笔、复用 `FontMetrics`、回收 `TypedArray`、销毁歌词列表时取消拖拽倒计时。
  - 阶段 2 音乐播放链路：Media3 播放核心、Repository 状态入口、旧 Manager 兼容桥接、Media3 session 到旧队列控制的桥接。
  - 阶段 3-6：聊天、动态发布、下载、发现/信息流的 Repository/ViewModel/兼容桥接闭环。
- 关键新文件：
  - `app/src/main/java/com/ixuea/courses/mymusic/playback/PlaybackController.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/playback/PlaybackRepository.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/playback/LegacyMusicSessionPlayer.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/playback/PlaybackService.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/playback/PlaybackModels.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/chat/repository/ChatClient.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/chat/repository/ConversationRepository.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/chat/repository/MessageRepository.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/discovery/repository/DiscoveryRepository.java`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/download/repository/DownloadRepository.java`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/feed/repository/FeedPublishRepository.java`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/feed/repository/FeedRepository.java`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/feed/ui/FeedPublishViewModel.java`

验证状态：

- `./gradlew :app:assembleDevDebug :app:testDevDebugUnitTest` 通过。
- 尚未连接模拟器或真机安装运行。

后续建议：

- 下一次继续时先读本文档，再看 `git status`。
- 先启动模拟器或连接设备，安装 `app/build/outputs/apk/dev/debug/app-dev-debug.apk`。
- 冒烟顺序建议：
  - App 启动。
  - 音乐播放：在线/本地播放、暂停/继续、seek、上一首/下一首、通知按钮、Widget、歌词和小播放器进度。
  - 聊天：会话列表、进入聊天、历史消息、文本发送、图片发送入口。
  - 动态发布：多图选择、压缩、上传、发布、动态列表刷新。
  - 下载：下载中列表、已下载列表、单项暂停/继续、全部暂停/继续、删除、播放已下载歌曲。
  - 发现/信息流：发现页加载、Banner/歌单/单曲点击、信息流滚动、图片预览。

### 2026-05-16 Git 远端和发布上下文

远端状态：

- `origin` 指向 GitHub：`https://github.com/lemma42796/museflow-android.git`。
- `upstream` 指向 Gitee：`git@gitee.com:yyh455/my-cloud-music-android-java.git`。
- 不要再把本轮现代化提交推到 `upstream`；此前误推已经回滚，`upstream/master` 保持在 `4d6c675`。

GitHub 发布策略：

- 不能直接把本地 `master` 历史推到 GitHub，因为历史里包含旧 `Config.java` 等敏感配置，GitHub push protection 会拒绝。
- GitHub `origin/master` 使用 `codex/github-origin-master` 这条脱敏快照分支推送。
- 脱敏快照保留 `Config.java` 作为空值占位，保证代码引用仍可编译；不要只靠 `.gitignore` 忽略 `Config.java`，因为已跟踪文件和历史提交不会因此消失。
- 脱敏快照清空了 `Config.java` 中的阿里云、IM、百度语音、小米 key，并清空 `app/build.gradle` 中的 `appSecret` 和小米 appkey。
- `keystore.properties` 和 `config/*.jks` 不进入 GitHub 快照，并由根 `.gitignore` 忽略。

后续推送提醒：

- 若继续在本地 `master` 开发，需要同步发布到 GitHub 时，应先把变更带到 `codex/github-origin-master` 的脱敏快照，再推 `origin codex/github-origin-master:master`。
- 不要执行 `git push upstream master`。
- 下次接手时先看本文档、`git status -sb`、`git remote -v`、`git log --oneline --decorate -5`。

## 执行原则

- 每个阶段都要形成最小闭环：能编译、能从旧入口进入、核心动作能跑。
- 新代码优先放在选中链路内部或明确的兼容门面后面。
- 冻结模块只做跳转、编译和极薄桥接，不做顺手重构。
- 先保留旧实现作为兜底，再逐步把入口切到新实现。
- 每阶段完成后记录构建结果、人工冒烟结果和遗留风险。

## 阶段 0：执行规划补齐

状态：已完成。

### 目标

把总纲、技术栈和冻结策略转成后续可执行的阶段计划。

### 产物

- `docs/modernization/execution-plan.md`

### 验收

- 阶段顺序明确。
- 每阶段有入口、主要任务、验收点和暂停条件。

## 阶段 1：建立 Kotlin/Compose/现代依赖基线

状态：已完成。

### 入口

- 根目录 `build.gradle`
- `app/build.gradle`
- `settings.gradle`
- 现有 Android Gradle Plugin、Hilt、Paging 配置

### 主要任务

- 确认 Android Gradle Plugin、Kotlin、Compose、AndroidX、Hilt、Media3、WorkManager、Paging、Coroutines、DataStore 的可用版本。
- 引入 Kotlin Android 插件和必要的 Kotlin 编译配置。
- 引入 Compose 基础配置，但暂不迁移冻结页面。
- 引入五条链路会用到的现代依赖。
- 保持现有 Java/XML 代码继续编译。

### 验收

- Gradle 同步通过。
- `app` 模块能编译。
- 旧 Java 源码不需要批量迁移即可通过编译。
- 没有因为依赖升级破坏冻结 SDK。

### 暂停条件

- 关键第三方 SDK 与 Kotlin/Compose/AGP 基线冲突，且无法通过小范围版本调整解决。
- 构建失败点落在冻结 SDK，修复成本会扩散到冻结模块。

## 阶段 2：音乐播放链路

### 入口

- `MusicPlayerManagerImpl.java`
- `MusicListManagerImpl.java`
- `MusicPlayerService.java`
- `component/player/**`
- `component/lyric/**`
- `component/widget/MusicWidget.java`

### 主要任务

- 新增 Media3 播放核心和 `MediaSessionService`。
- 新增播放状态、队列状态、歌词状态模型。
- 新增 Java 兼容门面，让旧 Manager/API 可以调用新播放核心。
- 把同步 `MediaPlayer.prepare()` 路径替换到异步 Media3 播放路径。
- 降低播放进度刷新频率，避免 16ms 全局分发。
- 先保留旧 UI，必要时再用 Compose 承载播放器局部组件。

### 验收

- 在线歌曲可播放、暂停、继续。
- 本地或已下载歌曲可播放。
- 可拖动进度。
- 上一首、下一首可用。
- 后台通知控制可用。
- 歌词和进度 UI 无明显卡顿。

### 暂停条件

- 系统媒体控制、通知栏或后台播放出现无法小范围修复的回归。
- 兼容门面需要连带重写多个冻结页面。

## 阶段 3：聊天 IM 链路

### 入口

- `AppContext.java`
- `ConversationActivity.java`
- `ChatActivity.java`
- `ChatAdapter.java`
- `RongPushReceiver.java`

### 主要任务

- 新增 `ChatClient` 包装融云 SDK。
- 把 SDK callback 包装成 Flow 或 suspend API。
- 新增会话和消息 Repository。
- 新增会话列表、聊天详情、输入栏的 Compose host 或局部 Compose 承载。
- 将历史消息加载从 Activity 中移到 Repository/ViewModel。
- 图片消息发送只接入新的图片压缩/发送状态模型，不在 Activity 中堆逻辑。

### 验收

- 会话列表能打开。
- 能从会话列表、用户详情、推送/通知进入聊天。
- 历史消息能加载和继续加载。
- 文本消息能发送。
- 图片消息链路能进入并反馈进度或错误。

### 暂停条件

- 融云 SDK callback 无法稳定桥接到 Flow，且影响基础收发消息。
- 新聊天入口需要大改冻结的用户详情或推送模块。

## 阶段 4：动态图片压缩/上传链路

### 入口

- `PublishFeedActivity.java`
- `UploadFeedImageAsyncTask.java`
- `ImageCompressor.java`
- `FeedAdapter.java`

### 主要任务

- 新增 `FeedPublishViewModel` 管理已选图片和发布状态。
- 新增 `ImageCompressionRepository`，把压缩和文件 IO 移到主线程外。
- 新增 `FeedUploadRepository`，统一上传结果、进度、失败和重试状态。
- 移除或旁路 `UploadFeedImageAsyncTask`。
- 控制并发压缩/上传数量，避免重复压缩同一文件。

### 验收

- 能多图选择。
- 能预览已选图片。
- 能压缩图片。
- 能上传图片。
- 能带上传结果发布动态。

### 暂停条件

- 上传接口需要后端配合修改。
- 文件权限或图片选择链路需要大范围改动冻结页面。

## 阶段 5：下载进度刷新链路

### 入口

- `DownloadingFragment.java`
- `DownloadedFragment.java`
- `DownloadingAdapter.java`
- `MyDownloadListener.java`
- `AppContext.java`

### 主要任务

- 新增 `DownloadRepository` 包装现有下载 SDK/Manager。
- 用 `StateFlow<List<DownloadItemUiState>>` 表示下载中列表。
- 用 `StateFlow<List<DownloadedItemUiState>>` 表示已下载列表。
- 对下载进度做 UI 安全频率节流。
- 列表使用稳定 key 和行级状态，避免无关行刷新。
- 暂停、继续、删除命令保持幂等。

### 验收

- 下载中列表能打开。
- 已下载列表能打开。
- 单项暂停、继续可用。
- 全部暂停、继续可用。
- 删除可用。
- 进度变化不明显重刷无关行。

### 暂停条件

- 现有下载 SDK 不提供足够的行级进度回调。
- 新 Repository 会迫使全局下载初始化逻辑大改。

## 阶段 6：首页/发现/信息流列表刷新链路

### 入口

- `DiscoveryFragment.java`
- `DiscoveryAdapter.java`
- `FeedFragment.java`
- `FeedAdapter.java`
- `CommentActivity.java`

### 主要任务

- 新增 `DiscoveryViewModel`，输出 typed immutable section list。
- 新增 `FeedViewModel`，输出 feed 分页或列表状态。
- 如果后端分页可用，Feed 优先接 Paging 3。
- Compose list 使用稳定 section/item key。
- 保持歌单、歌曲、用户、详情、图片预览等旧路由可跳转。

### 验收

- 发现页能打开。
- Banner、模块、列表项能渲染。
- 信息流能打开并滚动。
- 图片预览路由可用。
- 歌单、歌曲、用户、详情等旧点击路由仍能进入。

### 暂停条件

- 后端分页能力不足，需要改变接口语义。
- 列表改造连带要求重写多个冻结详情页。

## 阶段 7：构建和人工冒烟

### 主要任务

- 执行项目构建。
- 安装或从 Android Studio/Gradle 启动 App。
- 按总纲中的人工冒烟标准逐项检查。
- 记录未完成项、已知风险和需要回归的冻结入口。

### 验收

- App 能启动。
- 登录态/Session 不被破坏。
- 五条选中链路完成核心冒烟。
- 冻结模块只做启动和跳转级别确认。
- 人工冒烟记录补充到文档。

## 推荐下一步

进入阶段 7：统一做模拟器或真机人工冒烟。

建议按以下顺序：

- 安装 dev debug 包并启动 App。
- 冒烟音乐播放：在线/本地播放、暂停/继续、seek、上一首/下一首、通知、Widget、歌词进度。
- 冒烟聊天：会话列表、进入聊天、历史消息、文本发送、图片发送入口。
- 冒烟动态发布：多图选择、压缩、上传、发布、动态列表刷新。
- 冒烟下载：下载中列表、已下载列表、单项暂停/继续、全部暂停/继续、删除、播放已下载歌曲。
- 冒烟发现/信息流：发现页加载、Banner/歌单/单曲点击、信息流滚动、图片预览。
