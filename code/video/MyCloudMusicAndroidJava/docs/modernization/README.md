# 重点链路现代化重构总纲

> 日期：2026-05-16  
> 目标：只重构最有技术含量、最有性能优化潜力的链路；其他模块冻结，保证项目能跑。

## 已确认范围

本轮只重构下面五条链路：

1. 音乐播放链路
2. 聊天 IM 链路
3. 动态图片压缩/上传链路
4. 下载进度刷新链路
5. 首页/发现/信息流列表刷新链路

其他模块全部冻结。

“冻结”的含义：

- 不做 Compose 重写，除非被上述链路直接依赖。
- 不做大行为调整。
- 不替换 SDK，除非为了让选中链路编译或运行。
- 不做纯粹的命名、包结构、样式、代码洁癖式重构。
- 旧 Java/XML 页面继续保留，能打开即可。

## 2026-05-24 当前收口状态

本轮现代化编码主线已进入可交接状态：

- `app/src/main/java` 下 Java 源码数为 `0`。
- `app/src/main/res/layout` 已清零；最后一个 `music_widget.xml` 已迁到 Jetpack Glance，`res/xml/music_widget.xml` 只保留 AppWidget provider 元数据。
- 五条重点链路已完成 Kotlin/Compose/Flow/Media3 方向的主体迁移；播放器、聊天、动态发布、下载、发现/信息流、本地音乐和歌词周边的旧 XML/Adapter/Java 尾巴已收口。
- 已完成一轮模拟器最小可信冒烟：安装启动、发现页、播放器、后台通知、动态列表、下载管理、会话列表、动态发布页和本地音乐扫描入口均可打开；完整深度人工冒烟仍未补齐。
- 公开展示面的第一层文案/课程痕迹清理和 MuseFlow 图片/图标资产替换已完成。
- 2026-05-24 已纠偏 `MainActivity` 的 public-slim 空壳问题：启动入口重新变为 Kotlin/Compose 可操作首页，接回发现、动态、小播放控件、本地音乐、消息、下载和发布入口。

下一会话入口：

- 先从 `docs/modernization/execution-plan.md` 的“最新交接”恢复现场。
- 不要继续用“清 Java/XML 数量”或“public slim”作为删页面依据；现代化目标是保留可用产品链路。
- 优先做设备端可视/人工冒烟：恢复后的首页导航、发现页加载/滚动/点歌、小播放控件、本地音乐/扫描、消息、下载、动态发布、launcher/themed icon、splash 和 Widget preview。
- 本地未跟踪的 `docs/modernization/course-trace-cleanup-task.md` 仍是私有任务说明；该文件不要 `git add`，不要 push。

## 初始项目状态

这是一个 Java/XML 为主的 Android 项目，主要使用：

- Java
- XML + ViewBinding
- RxJava
- EventBus
- MediaPlayer
- 自定义 Manager
- 若干第三方 SDK

已经看到的重点性能/架构问题：

- 播放链路里有同步 `MediaPlayer.prepare()`。
- 播放进度以 16ms 定时器全局分发。
- EventBus 承担跨页面状态分发。
- 基础 RecyclerView Adapter 大量使用 `notifyDataSetChanged()`。
- 图片上传链路里仍有 `AsyncTask` 遗留。
- 图片、音频、下载、聊天、嵌套列表都缺少统一状态模型。

## 目标技术方向

新写或重写的选中链路统一走现代 Android 栈：

- Kotlin 优先。
- Jetpack Compose 重写选中页面/组件。
- ViewModel + 不可变 UI State + 单向数据流。
- Coroutines、Flow、StateFlow、SharedFlow 替代新代码里的 RxJava/EventBus。
- Hilt 做依赖注入。
- Media3 ExoPlayer + MediaSessionService 重做播放。
- WorkManager/CoroutineWorker 用于可靠的后台上传/下载相关任务。
- Paging 3 + Compose LazyList 处理聊天、信息流、发现页等分页/长列表。
- DataStore 处理被选中链路触碰到的小型偏好配置。

## 范围边界

### 本轮要做

- 重构五条选中链路。
- 通过兼容层让老 Java 页面能调用新 Kotlin 服务。
- 保留现有后端 API、模型、登录态和资源地址。
- 保证用户可见行为大体可用，目标是“能跑”。
- 优化主线程卡顿、全量刷新、过度回调、媒体链路阻塞。

### 本轮不做

- 全量 XML 改 Compose。
- 全量 Java 改 Kotlin。
- 全 App 导航重写。
- 支付、地图、分享、视频、订单、地址、资料、设置、登录注册、Web、引导、扫码等冻结模块。
- 自动化测试建设。

## 工作规则

如果新旧实现并存：新实现负责选中链路，旧实现变成适配器或兜底。不要为了“看起来整洁”去改无关模块。

## 交付顺序

1. 建立文档和冻结策略。
2. 建立 Kotlin/Compose/现代依赖基线。
3. 在兼容门面后面重做音乐播放核心。
4. 在现有入口后面重做聊天核心和 Compose 聊天页面。
5. 替换动态图片压缩/上传执行链路。
6. 替换下载进度状态和列表刷新链路。
7. 替换首页/发现/信息流列表状态和渲染链路。
8. 构建项目并做人工冒烟验证。

## 人工冒烟标准

本轮目标是“能跑”，不是完整测试证明。收尾前至少验证：

- App 能启动。
- 登录态/Session 不被破坏。
- 音乐能播放、暂停、拖动、切歌、后台播放。
- 歌词或进度 UI 不明显卡顿。
- 会话列表能打开。
- 聊天历史能加载。
- 文本消息能发送。
- 图片消息链路能进入压缩/发送流程。
- 动态发布能选择、压缩、上传图片。
- 下载列表能打开，进度变化不会明显整表闪动。
- 首页/发现/信息流能打开并滚动。
