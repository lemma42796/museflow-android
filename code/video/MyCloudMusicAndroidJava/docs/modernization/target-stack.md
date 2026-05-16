# 目标 Android 技术栈

> 快照日期：2026-05-16。真正落依赖版本前，需要再查 AndroidX/Kotlin/Gradle 官方发布信息并锁版本。

## 基线选择

五条选中链路统一向现代 Android 开发方式迁移：

- 语言：Kotlin
- UI：Jetpack Compose + Material 3
- 架构：ViewModel + 不可变 UI State + 单向数据流
- 异步/响应式：Coroutines、Flow、StateFlow、SharedFlow
- 依赖注入：Hilt
- 媒体播放：Jetpack Media3 ExoPlayer、MediaSession、MediaSessionService
- 列表：Paging 3 + Compose LazyList
- 后台任务：WorkManager + CoroutineWorker
- 偏好存储：DataStore
- 性能验证：后续可补 Baseline Profile / Macrobenchmark

## 官方依据

- Android 架构建议：https://developer.android.com/topic/architecture/recommendations
- Jetpack Compose：https://developer.android.com/compose
- Compose LazyList：https://developer.android.com/develop/ui/compose/lists
- Kotlin Coroutines on Android：https://developer.android.com/kotlin/coroutines
- Coroutines 最佳实践：https://developer.android.com/kotlin/coroutines/coroutines-best-practices
- StateFlow / SharedFlow：https://developer.android.com/kotlin/flow/stateflow-and-sharedflow
- Hilt：https://developer.android.com/training/dependency-injection/hilt-android
- Media3：https://developer.android.com/media/media3
- Media3 ExoPlayer：https://developer.android.com/media/media3/exoplayer
- MediaSession：https://developer.android.com/media/media3/session/control-playback
- WorkManager：https://developer.android.com/topic/libraries/architecture/workmanager
- Paging 3：https://developer.android.com/topic/libraries/architecture/paging/v3-overview
- Baseline Profiles：https://developer.android.com/baseline-profiles

## 技术规则

### Kotlin

- 新的选中链路代码用 Kotlin。
- 旧 Java 类只作为历史入口、兼容门面或冻结模块保留。
- 不批量转换无关 Java 文件。

### Compose

- 新的选中链路 UI 用 Compose。
- 可以在旧 Activity/Fragment 里嵌 Compose，降低路由改动范围。
- 冻结页面的 XML 不动。
- 不做无功能的占位页面。

### 状态管理

- ViewModel 暴露不可变状态：
  - `StateFlow<UiState>` 表示屏幕状态。
  - `SharedFlow<UiEvent>` 表示一次性事件。
- UI 只发送用户动作给 ViewModel。
- Repository 对外暴露：
  - `suspend` 函数处理一次性操作。
  - `Flow` 处理连续状态。

### 并发

- 新代码禁止 `AsyncTask`、`TimerTask`、裸 `Thread` 和深层 callback 嵌套。
- 第三方 SDK callback 用 `suspendCancellableCoroutine` 或 `callbackFlow` 包装。
- Dispatcher 通过注入提供，不在 Repository 里硬编码。
- 文件、网络、Bitmap、数据库等重操作必须 main-safe。

### 列表

- Compose `LazyColumn`/`LazyRow` 必须使用稳定 key。
- 聊天历史、信息流、发现页等分页数据优先用 Paging 3。
- 选中链路里避免继续扩散 `notifyDataSetChanged()`。

### 媒体播放

- 选中播放链路用 Media3 ExoPlayer 替代 `MediaPlayer`。
- 后台播放放进 `MediaSessionService`。
- 使用 `Player.Listener` 和 Flow 状态，不再依赖高频全局 Timer 广播。
- 老播放器 Manager API 暂时作为兼容门面保留。

### 后台任务

- 只有需要可靠执行、可能跨进程/跨页面的任务才用 WorkManager。
- 当前屏幕内可取消任务使用 ViewModel coroutine。
- 上传/下载相关状态必须对 UI 暴露可观察进度。

### 偏好存储

- 新增或触碰到的播放、聊天、列表偏好使用 DataStore。
- 无关 SharedPreferences key 不迁移。

## 依赖管理方向

先建立版本目录或集中依赖管理，再批量加库。落地前需要确认：

- Android Gradle Plugin
- Kotlin
- Compose compiler/plugin
- AndroidX
- Hilt
- Media3
- WorkManager
- Paging
- Coroutines

当前项目是 `targetSdk = 33`，Android Gradle Plugin 是 `8.2.0`。要用最新 AndroidX/Compose，需要先有计划地升级构建基线。

