# 重点链路现代化执行规划

> 日期：2026-05-16  
> 目的：把已确认的重构范围拆成可执行步骤，确保每一步都有明确入口、验收点和暂停条件。

## 当前状态

## 进度查询约定

以后用户询问“最新进度”“进度到哪了”或类似问题时，先阅读本文档，尤其是“当前状态”和“最新执行记录”；再结合 `git status -sb`、`git log --oneline --decorate -5`、必要时的终端/构建状态补充说明。进度回答应以文档记录为主，Git 状态只作为辅助校验。

当前已完成：

- 已确认本轮只处理五条重点链路。
- 已确认冻结模块和允许的最小适配边界。
- 已确认目标技术方向和人工冒烟标准。
- 已建立 Kotlin/Compose/现代依赖基线。
- `:app:assembleDevDebug` 构建通过。
- 音乐播放链路已进入阶段 2，完成第一步 Media3 播放核心和旧 Manager 兼容桥接。
- 聊天、动态发布、下载、发现/信息流链路已完成第一轮 Repository/ViewModel/兼容桥接闭环，旧入口继续保留。
- 已在模拟器 `emulator-5554` 完成阶段 7 第一轮入口冒烟，App 可安装启动，播放器、聊天、动态发布、下载页等核心入口可打开。
- 发现页、下载、动态发布/动态列表的新增 Repository/ViewModel 已从 Java 迁到 Kotlin，Java 调用方兼容接口保持不变。
- 下载中/已下载 Fragment 和下载中 Adapter 已从 Java 迁到 Kotlin，下载 Repository 边界继续保持稳定。
- 动态列表 `FeedFragment` 已从 Java 迁到 Kotlin，发布入口、图片预览、位置预览和 EventBus 刷新入口保持兼容。
- 动态发布页 `PublishFeedActivity` 和图片适配器 `ImageAdapter` 已从 Java 迁到 Kotlin，选图、压缩、上传、位置选择和发布通知流程保持兼容。
- 动态主适配器 `FeedAdapter` 已从 Java 迁到 Kotlin，动态卡片、九宫格图片、点赞和评论渲染保持兼容。
- 发现页 `DiscoveryFragment` 和 `DiscoveryAdapter` 已从 Java 迁到 Kotlin，首页聚合刷新、Banner、推荐歌单/单曲和自定义入口保持兼容。
- 发现页剩余小 adapter `SheetAdapter`、`DiscoverySongAdapter` 已从 Java 迁到 Kotlin，歌单/单曲 item 渲染保持兼容。
- 播放器底部控制、黑胶页、播放列表弹窗和相关 adapter 已从 Java 迁到 Kotlin，旧 Activity/Fragment 入口保持兼容。
- 播放器主页面、简单播放器、自定义黑胶 View 和播放器事件已从 Java 迁到 Kotlin，`component/player` 包内业务代码已完成 Kotlin 化。
- 歌词选择/分享页面、歌词列表 adapter 和 LRC/KSC parser 已从 Java 迁到 Kotlin，播放器歌词解析入口保持兼容。
- 歌词模型 `Line`/`Lyric`、`LyricUtil` 和单行歌词 View `LyricLineView` 已从 Java 迁到 Kotlin，并补齐空歌词/逐字歌词边界保护。
- 歌词列表 View `LyricListView` 和桌面歌词 View `GlobalLyricView` 已从 Java 迁到 Kotlin，歌词自定义 View 已完成 Kotlin 化。
- 桌面歌词管理器 `GlobalLyricManagerImpl` 已从 Java 迁到 Kotlin，保留 Java 静态 `getInstance(Context)` 入口和悬浮窗控制接口。
- 音乐 Widget 边界 `WidgetUtil` 和 `MusicWidget` 已从 Java 迁到 Kotlin，保留桌面微件 provider 类名和 Java 静态工具调用入口。
- 通知工具 `NotificationUtil` 已从 Java 迁到 Kotlin，保留消息通知、播放通知、桌面歌词解锁通知和前台服务通知的静态调用入口。
- 播放管理器兼容门面 `MusicPlayerManagerImpl` 已从 Java 迁到 Kotlin，继续通过 Media3 `PlaybackRepository` 承接旧 `MusicPlayerManager` API。
- 播放列表管理器 `MusicListManagerImpl` 已从 Java 迁到 Kotlin，继续保留旧 `MusicListManager` 接口、静态 `getInstance(Context)` 入口、队列持久化和 `PlaybackRepository.setQueue(...)` 同步。
- 播放服务边界 `MusicPlayerService` 已从 Java 迁到 Kotlin，继续保留 Manifest service 类名、MediaSession、通知、Widget 和桌面歌词控制入口。
- 播放链路已完成一轮更完整的模拟器时间线复测：播放/暂停、seek、通知媒体卡片播放/暂停、后台播放在 MediaSession 和 UI 层面通过；上一轮“播放未推进”的判断已修正。
- 聊天详情页 `ChatActivity` 已从 Java 迁到 Kotlin，保留旧 XML/RecyclerView、图片选择压缩、文本/图片发送、历史消息分页、EventBus 新消息入口和 Repository 调用边界。
- 会话列表页 `ConversationActivity` 和 `ConversationAdapter` 已从 Java 迁到 Kotlin，保留会话列表、点击进聊天、长按清消息、未读角标和新消息节流刷新逻辑。
- 聊天链路事件/消息 extra 小模型 `NewMessageEvent`、`MessageUnreadCountChangedEvent`、`MediaMessageExtra` 已从 Java 迁到 Kotlin，Java 调用入口保持兼容。
- 聊天消息列表 `ChatAdapter` 已从 Java 迁到 Kotlin，继续保留文本/图片消息左右布局、头像加载、图片尺寸适配和 RongCloud 消息类型判断，并补齐图片 extra/宽高缺失时的保护。
- 聊天消息工具 `MessageUtil`、离线推送小模型 `Push`/`PushMessage` 和推送入口 `RongPushReceiver`/`PushReceiver`/`PushService` 已从 Java 迁到 Kotlin，继续保留 `MessageUtil.getContent(...)`、`MessageUtil.createPushData(...)`、`Push.PUSH_STYLE_CHAT`、Manifest receiver/service 类名等兼容入口。
- 动态发布页图片选择压缩入口已从 Activity 直连 `ImageCompressionRepository` 收拢到 `CompressFeedImagesUseCase`。
- 下载链路入口 `DownloadActivity`、分页适配器、下载监听器、点击 listener 和下载事件已从 Java 迁到 Kotlin，下载目录当前不再包含 Java 文件。
- 发现/信息流相关的自定义排序页、排序 adapter、发现页 UI 数据模型和排序事件已从 Java 迁到 Kotlin；`DiscoveryAdapter` 已适配 Kotlin 模型列表拷贝，发现目录当前不再包含 Java 文件。
- 动态主模型 `Feed` 和动态刷新事件已从 Java 迁到 Kotlin，动态目录当前不再包含 Java 文件；`FeedAdapter` 已补齐动态缺少 user 时的空值保护。
- 首页主 ViewPager 边界 `MainAdapter` 已从 Java 迁到 Kotlin，继续保留发现/视频/我的/动态/直播五个 tab 的 Fragment 创建顺序。
- 首页 tab 模型 `TabEntity` 已从 Java 迁到 Kotlin，继续实现 `CustomTabEntity` 并保留 Java public 字段访问形态。
- 播放/歌单相关小事件 `MusicPlayListChangedEvent`、`ScanLocalMusicCompleteEvent`、`SheetChangedEvent` 已从 Java 迁到 Kotlin，继续保留 Java 构造和 getter/setter 调用面。
- 阶段 8 已按用户要求启动；发现页首页数据加载已推进到 `DiscoveryViewModel(StateFlow) -> LoadDiscoveryPageUseCase -> DiscoveryRepository`，下载中/已下载页已引入 `DownloadingViewModel`/`DownloadedViewModel` 承接下载操作和列表状态，动态 Feed 列表也已推进到 `FeedViewModel(StateFlow) -> LoadFeedListUseCase -> FeedRepository`，动态发布上传/创建动态已推进到 `FeedPublishViewModel(StateFlow) -> Publish UseCase -> FeedPublishRepository`，聊天会话列表已推进到 `ConversationListViewModel(StateFlow) -> Conversation UseCase -> ConversationRepository`，新消息后的会话列表延迟刷新和会话行 UI 数据也已收敛到 ViewModel，聊天详情历史消息分页、文本/图片发送状态、清未读、标记已读、页面标题用户资料和消息行头像 UI 数据已推进到 `ChatViewModel(StateFlow) -> Chat UseCase -> MessageRepository`，旧 RecyclerView UI 暂时保留。
- 播放器主页面下载按钮的下载状态查询、创建下载和继续下载入口已从 `MusicPlayerActivity` 直连 `DownloadRepository` 收敛到 `DownloadActionsUseCase`。
- 歌曲列表通用适配器 `SongAdapter` 已从 Java 迁到 Kotlin，本地/已下载歌曲删除和下载完成状态查询已从直连 `DownloadRepository` 收敛到 `DownloadActionsUseCase`。
- 歌单详情页 `SheetDetailActivity` 已从 Java 迁到 Kotlin，详情加载、收藏和取消收藏已推进到 `SheetDetailViewModel(StateFlow) -> Sheet UseCase -> SheetRepository`，Activity 不再直接使用 `DefaultRepository`/`HttpObserver`/AutoDispose。
- 评论页 `CommentActivity` 已从 Java 迁到 Kotlin，评论分页加载、创建评论、点赞和取消点赞已推进到 `CommentViewModel(StateFlow) -> Comment UseCase -> CommentRepository`，Activity 不再直接使用 `DefaultRepository`/`HttpObserver`/AutoDispose。
- 评论列表 `CommentAdapter`、评论更多弹窗 `CommentMoreDialogFragment` 和评论模型 `Comment` 已从 Java 迁到 Kotlin，`component/comment` 目录当前不再包含 Java 文件。
- 本地音乐扫描链路 `LocalMusicActivity`、`ScanLocalMusicActivity`、`MusicSortDialogFragment`、`ScanLocalMusicAsyncTask`、`ScanLocalMusicCompleteEvent` 已从 Java 迁到 Kotlin，`component/music` 目录当前不再包含 Java 文件。
- 动态发布相关位置占位入口、登录占位入口、用户占位入口、桌面 `MusicWidget` 和 Rx `ObserverAdapter` 已从 Java 迁到 Kotlin，旧静态启动入口、Manifest 类名和 Widget PendingIntent 行为保持兼容。
- 网络层 `DefaultService`、`HttpObserver`、`NetworkModule`、`NetworkSecurityInterceptor` 已从 Java 迁到 Kotlin，继续保留 Retrofit/Rx/OkHttp/Hilt 调用面。
- 歌词自定义 View `LyricLineView`、`LyricListView`、`GlobalLyricView` 已从 Java 迁到 Kotlin，播放器歌词列表和桌面歌词监听接口继续保持兼容；`component` 目录当前不再包含 Java 文件。
- 公共模型 `Base`/`BaseId`/`Common`/`Resource`、response 包 `BaseResponse`/`DetailResponse`/`ListResponse`/`Meta`、response exception 和 `BaseMultiItemEntity` 已从 Java 迁到 Kotlin，继续保留旧 getter/setter/bean 调用面。
- 播放列表/歌词 manager 接口 `GlobalLyricManager`、`MusicListManager`、`MusicPlayerManager` 和播放列表事件已从 Java 迁到 Kotlin；`MusicPlayerListener` 暂留 Java 以保留默认方法对 Java 实现类的兼容性。
- 公共 adapter/view/config 边界 `TextWatcherAdapter`、`OnPageChangeListenerAdapter`、`BaseFragmentStateAdapter`、`BaseFragmentStatePagerAdapter`、`PlaceholderView`、`Config` 已从 Java 迁到 Kotlin；`BadgeInit` 暂留 Java，因为 `BGABadgeView-Android` 旧注解处理器需要 Java 源生成 `BGABadgeImageView/TextView`。
- 小型工具类 `TextUtil`、`Base64Util`、`SaltUtil`、`SHAUtil`、`ListUtil`、`SizeUtil`、`ScreenUtil`、`SuperTextUtil` 已从 Java 迁到 Kotlin，并通过 `@JvmStatic`/`fun interface` 保持 Java 静态调用和 lambda 调用兼容。

当前尚未完成：

- 五条链路仍未完成深度人工冒烟；目前只完成入口级检查。
- 音乐播放链路仍需继续确认上一首/下一首、多歌曲队列、Widget 控制、桌面歌词开关和歌词进度；真实出声播放以用户观察和 MediaSession/UI 推进为依据，已不再作为当前阻塞项。
- 聊天 Kotlin 迁移后的设备端发送冒烟、动态多图压缩上传、下载任务操作、发现页网络数据和信息流滚动仍需继续验证。
- 播放通知刷新已做去重和低频保护，本轮 logcat 未看到新的通知限流日志，但系统累计 `numRateViolations` 已到 `28`，仍需继续观察是否还有增量。

用户已明确要求直接进入阶段 8；阶段 7 深度人工冒烟仍未补齐，阶段 8 后续编码需要默认带着这个验证风险前进。

深度迁移方向：

- 后续继续在当前项目内渐进迁移，不新建 Android Studio 项目搬代码。
- 新项目只作为最新 Gradle、Compose、Hilt、Navigation 配置参考，不作为主开发战场。
- 先把当前五条链路从 Repository/ViewModel/兼容桥接推进到真正的 `Compose UI -> ViewModel(uiState) -> UseCase/Repository -> DataSource`。
- 再逐步替换选中链路里的 RxJava/EventBus，最后在边界稳定后拆分 `core:*` 和 `feature:*` 模块。

## 最新执行记录

### 2026-05-20 阶段 8 继续：公共边界和工具类 Kotlin 收口

本轮决策：

- 用户要求继续编码，并明确不要每次改完就 push；编码阶段只保留本地代码和文档改动，交接阶段在用户明确要求后再提交并 push。
- 在 `component` 目录清零 Java 后，继续清理外围公共边界，优先选择模型、response、exception、manager 接口、adapter/view/config 和小型 util。
- 不迁移 `BadgeInit` 和 `MusicPlayerListener`：前者受旧 Java annotation processor 约束，后者含默认方法且 Java 实现类较多，先保留以降低风险。

本轮代码变更：

- `component/api` 网络层、歌词自定义 View 和 `component` 目录剩余 Java 已完成 Kotlin 化。
- 公共 model/response/exception、`BaseMultiItemEntity` 和播放列表/歌词 manager 接口已迁到 Kotlin，并适配调用点的可空字段和 `MusicListManager.datum` 非空列表语义。
- `TextWatcherAdapter`、`OnPageChangeListenerAdapter`、`BaseFragmentStateAdapter`、`BaseFragmentStatePagerAdapter`、`PlaceholderView`、`Config` 已迁到 Kotlin。
- `TextUtil`、`Base64Util`、`SaltUtil`、`SHAUtil`、`ListUtil`、`SizeUtil`、`ScreenUtil`、`SuperTextUtil` 已迁到 Kotlin，保留 Java 静态工具调用和 Java lambda 调用兼容。
- `BadgeInit` 曾尝试迁到 Kotlin，但编译证实 `BGABadgeView-Android:compiler:1.2.0` 只从 Java annotation processor 生成绑定类，因此恢复为 Java 并作为已知保留边界。

本轮验证：

- `git diff --check` 通过。
- `./gradlew :app:assembleDevDebug` 通过；剩余提示主要是既有 deprecated API、`FragmentStatePagerAdapter` deprecated、AsyncTask deprecated 和 BGABadge 非增量注解处理器提示。
- `rg --files -g '*.java' app/src/main/java/com/ixuea/courses/mymusic/component` 无输出，`component` 目录当前不再包含 Java。
- 本轮未做设备端冒烟；交接阶段按用户明确要求提交并 push 到 `codex/emulator-smoke-progress`。

下个会话建议：

- 继续清 Java 时，优先选择剩余中等风险的 util 或 adapter：`BaseRecyclerViewAdapter`、`BasePagingDataAdapter`、`PlayListUtil`、`ResourceUtil`、`IntentUtil` 等。
- 暂缓 `Base*Activity/Fragment`、`DefaultRepository`、manager impl、`MusicPlayerService` 和 `MainActivity`，这些会牵动更大运行时边界。

### 2026-05-20 阶段 8 继续：评论模块剩余 Java 清理

本轮决策：

- 继续编码，不做设备端冒烟。
- 在评论页状态链路收口后，继续清理 `component/comment` 里剩余 Java 文件。
- 本轮只迁移评论 model/adapter/dialog，并补齐 Kotlin 空值保护，不扩大到新 UI 或 Compose。

本轮代码变更：

- `Comment.java` -> `Comment.kt`，保留评论内容、点赞数、歌单/动态 id、父评论、发布人和 `isLiked` 行为。
- `CommentAdapter.java` -> `CommentAdapter.kt`，保留头像、昵称、时间、点赞状态、正文富文本、回复评论富文本和 mention/hash 点击处理。
- `CommentMoreDialogFragment.java` -> `CommentMoreDialogFragment.kt`，保留 `showDialog(...)` 静态入口和评论更多菜单。
- `CommentActivity`、`CommentViewModel` 和 `FeedAdapter` 已适配 Kotlin 化后的 `Comment` 可空字段，后端缺 user/parent/content 时不再依赖 Java platform type。

本轮验证：

- `find app/src/main/java/com/ixuea/courses/mymusic/component/comment -maxdepth 3 -name '*.java' -print` 无输出。
- `git diff --check` 通过。
- `./gradlew :app:assembleDevDebug` 通过，只有既有 Java deprecation/unchecked 和 BGABadge 非增量注解处理器提示。
- 本轮按要求未做设备端冒烟。

下个会话建议：

- 如果继续清 Java 残留，下一刀可处理 `component/sheet` 下的 `Sheet`/`SheetWrapper`/`SheetChangedEvent`，或继续检查其他外围模块。
- 如果转入阶段 8 核心目标，可以挑评论页、歌单详情页或发现页做最小 Compose UI 试点。

### 2026-05-20 阶段 8 继续：评论页状态链路

本轮决策：

- 继续编码，不做设备端冒烟。
- 在歌单详情页收口后，继续处理最后一个明显外围旧页面 `CommentActivity` 的直接 `DefaultRepository` + Rx/`HttpObserver` 边界。
- 本轮只处理评论分页加载、创建评论、点赞和取消点赞，不扩大到评论适配器/model Kotlin 化或评论更多弹窗重构。

本轮代码变更：

- `CommentActivity.java` -> `CommentActivity.kt`，保留歌单评论入口 `startWithSheetId(...)`、下拉刷新、上拉加载更多、回复评论、复制评论、@好友选择、登录状态变化刷新和富文本高亮。
- 新增 `CommentRepository`，封装 `comments(...)`、`createComment(...)`、`commentLike(...)`、`cancelCommentLike(...)`。
- 新增 `LoadCommentsUseCase`、`CreateCommentUseCase`、`LikeCommentUseCase`、`CancelCommentLikeUseCase`，把 Rx 订阅封装到 suspend use case。
- 新增 `CommentViewModel`、`CommentUiState` 和 `CommentLoadOperation`，用 `StateFlow` 驱动分页列表、创建成功事件、点赞更新事件和错误状态。
- `CommentActivity` 不再 import/调用 `DefaultRepository`、`HttpObserver` 或 AutoDispose；刷新仍走 `setNewInstance(...)`，加载更多保留旧的 `addData(...)` 追加行为。

本轮验证：

- `rg "DefaultRepository|HttpObserver|autoDisposable|subscribe\\(" component/comment` 仅剩 Repository/UseCase 内部封装命中，Activity 层无命中。
- `git diff --check` 通过。
- `./gradlew :app:assembleDevDebug` 通过，只有既有 Java deprecation/unchecked 和 BGABadge 非增量注解处理器提示。
- 本轮按要求未做设备端冒烟。

下个会话建议：

- 如继续清 Java 残留，可以迁移 `CommentAdapter`、`CommentMoreDialogFragment`、`Comment` model，或继续清 `component/sheet` 下的 `Sheet`/事件小模型。
- 如转向阶段 8 核心目标，可以从评论页、歌单详情页或发现页挑最小 Compose UI 试点。

### 2026-05-20 阶段 8 继续：歌单详情状态链路

本轮决策：

- 继续编码，不做设备端冒烟。
- 在 `SongAdapter` 下载边界收口后，继续处理 `SheetDetailActivity` 的直接 `DefaultRepository` + Rx/`HttpObserver` 边界。
- 本轮只处理歌单详情页的详情加载、收藏和取消收藏，不扩大到评论页或删除歌单 TODO。

本轮代码变更：

- `SheetDetailActivity.java` -> `SheetDetailActivity.kt`，保留隐式 intent 打开、头图调色、播放全部/单曲、用户入口、评论入口、删除菜单显示和收藏按钮 UI。
- 新增 `SheetRepository`，封装 `sheetDetail(...)`、`collect(...)`、`deleteCollect(...)`。
- 新增 `LoadSheetDetailUseCase`、`CollectSheetUseCase`、`DeleteSheetCollectUseCase`，把 Rx 订阅封装到 suspend use case。
- 新增 `SheetDetailViewModel`、`SheetDetailUiState` 和 `SheetCollectOperation`，用 `StateFlow` 驱动详情数据、loading、错误和收藏成功事件。
- `SheetDetailActivity` 不再 import/调用 `DefaultRepository`、`HttpObserver` 或 AutoDispose；收藏成功后仍本地更新收藏状态/数量并发布 `SheetChangedEvent`。

本轮验证：

- `rg "DefaultRepository|HttpObserver|autoDisposable|subscribe\\(" component/sheet` 仅剩 Repository/UseCase 内部封装命中，Activity 层无命中。
- `git diff --check` 通过。
- `./gradlew :app:assembleDevDebug` 通过，只有既有 `FragmentStatePagerAdapter` deprecated、Java deprecation/unchecked 和 BGABadge 非增量注解处理器提示。
- 本轮按要求未做设备端冒烟。

下个会话建议：

- 继续外围旧页面收口时，下一刀优先处理 `CommentActivity` 直连 `DefaultRepository` + Rx/`HttpObserver`。
- 若转向阶段 8 核心目标，可以从歌单详情或发现页选一个小区域做 Compose UI 试点。

### 2026-05-20 阶段 8 继续：SongAdapter 下载边界收口

本轮决策：

- 继续编码，不做设备端冒烟。
- 在 `feed`/`discovery` Java 残留清理之后，优先处理外围旧页面里最小的下载 Repository 直连点。
- 本轮只迁移 `SongAdapter` 和必要调用点小清理，不扩大到 `SheetDetailActivity`/`CommentActivity` 的 Rx 页面重构。

本轮代码变更：

- `SongAdapter.java` -> `SongAdapter.kt`，保留 Java 调用方需要的 `SongAdapter(int)`、`SongAdapter(int, int, FragmentManager)`、`isEditing()`、`setEditing(...)`、`isSelected(...)`、`setSelected(...)`、`getSelectedIndexes()` 调用面。
- `SongAdapter` 不再 import/持有 `DownloadRepository`；下载状态查询和下载任务删除改为调用 `DownloadActionsUseCase`。
- 已下载页点击歌曲时去掉 Kotlin 化后的多余空值 Elvis 分支。

本轮验证：

- `git diff --check` 通过。
- `./gradlew :app:assembleDevDebug` 通过，只有既有 Java deprecation/unchecked 和 BGABadge 非增量注解处理器提示。
- 本轮按要求未做设备端冒烟。

下个会话建议：

- 继续外围旧页面收口时，优先处理 `SheetDetailActivity` 或 `CommentActivity` 直连 `DefaultRepository` + Rx/`HttpObserver`。
- 若不继续外围页面，下一步可以转向发现页或动态列表的最小 Compose UI 试点。

### 2026-05-20 阶段 8 继续：动态/发现 Java 残留清理

本轮决策：

- 继续编码，不做设备端冒烟。
- 优先修正文档与代码事实不一致的问题：`feed`/`discovery` 目录仍有 git 跟踪的 Java 文件，本轮把这批边界迁到 Kotlin。
- 本轮只处理动态/发现目录 Java 残留和必要的 Kotlin 空值/可变列表适配，不扩大到歌单/评论外围旧页面。

本轮代码变更：

- `Feed.java` -> `Feed.kt`、`FeedChangedEvent.java` -> `FeedChangedEvent.kt`，保留动态发布/列表接口字段和 Java bean getter/setter。
- `FeedAdapter` 对动态缺少 `user` 的场景改为 Kotlin 空值保护，避免模型迁 Kotlin 后继续依赖 Java platform type。
- `SortChangedEvent`、发现页 UI 模型 `BaseSort`/`BannerData`/`ButtonData`/`SheetData`/`SongData`/`FooterData`/`CustomDiscoveryItem`/`IconTitleButtonData` 已迁到 Kotlin。
- `CustomDiscoveryActivity` 和 `CustomDiscoveryAdapter` 已迁到 Kotlin，保留自定义发现页拖拽排序、恢复默认排序、保存排序并发布 `SortChangedEvent`。
- `DiscoveryRepository` 为 Kotlin 化后的 Banner/Sheet/Song 容器显式传入 mutable list，继续兼容现有 adapter `setNewInstance(...)` 数据入口。

本轮验证：

- `find app/src/main/java/com/ixuea/courses/mymusic/component/feed app/src/main/java/com/ixuea/courses/mymusic/component/discovery -name '*.java' -print` 无输出。
- `git diff --check` 通过。
- `./gradlew :app:assembleDevDebug` 通过，只有既有 `FragmentStatePagerAdapter` deprecated、Java deprecation/unchecked 和 BGABadge 非增量注解处理器提示。
- 本轮按要求未做设备端冒烟。

下个会话建议：

- 如果继续清旧边界，下一刀优先处理外围旧页面：`SongAdapter` 直连 `DownloadRepository`，`SheetDetailActivity`/`CommentActivity` 直连 `DefaultRepository` + Rx/`HttpObserver`。
- 如果继续阶段 8 核心目标，可以挑发现页或动态列表的最小区域做 Compose UI 试点。

### 2026-05-20 阶段 8 继续：播放器下载动作边界收口

本轮决策：

- 按用户要求继续编码，不做设备端冒烟。
- 优先执行上轮建议里的“五条链路 UI 直连 Repository/Rx 扫描”，先处理最小且清晰的播放器下载边界。
- 本轮不扩大到歌单/评论等旧外围页面，也不启动模拟器。

本轮代码变更：

- `DownloadActionsUseCase` 增加 `getDownloadById(...)` 和 `download(...)`，继续作为下载 SDK/Repository 的 domain 动作门面。
- `MusicPlayerActivity` 不再 import/持有 `DownloadRepository`。
- 播放器页下载按钮的“已下载/下载中/继续下载/创建下载任务”旧行为保持不变，但 Repository 访问改由 `DownloadActionsUseCase` 承接。

本轮验证：

- 已扫描核心链路 UI 层，播放器页不再直接 import 下载 Repository。
- `git diff --check` 通过。
- `./gradlew :app:assembleDevDebug` 通过，只有既有 Java deprecation/unchecked 和 BGABadge 非增量注解处理器提示。
- 本轮按要求未做设备端冒烟。

下个会话建议：

- 继续核对歌单/评论等外围旧页面是否纳入当前五条链路的收口范围；如果纳入，再把 `SongAdapter`、`SheetDetailActivity`、`CommentActivity` 的直接 Repository/Rx 调用按小刀迁出。
- 若仍限定核心五条链路，可以开始选择一个最低风险页面做 Compose UI 试点。

### 2026-05-20 收尾交接：阶段 8 下载与动态发布小补

本次交接结论：

- 当前分支继续使用 `codex/emulator-smoke-progress`。
- 本轮完成下载模块剩余 Java 边界 Kotlin 化、下载中终态刷新归属收口，以及动态发布图片压缩 Repository 直连清理。
- 下载目录 `component/download` 当前不再包含 Java 文件。
- 动态发布页 `PublishFeedActivity` 当前不再直接 import/调用 `ImageCompressionRepository` 或 `ImageCompressor.CompressionCallback`，图片压缩入口已收敛到 `CompressFeedImagesUseCase`。
- 本轮按用户收尾要求更新本文档、提交并推送；后续恢复默认规则：除非用户明确要求，不要每个小切片都 push。

本轮已验证：

- `find app/src/main/java/com/ixuea/courses/mymusic/component/download -name '*.java' -print` 无输出。
- `rg "ImageCompressionRepository|getInstance\\(\\)\\.compressImages|component\\.feed\\.repository" ...` 确认动态发布 Activity 不再直连压缩 Repository。
- `git diff --check` 通过。
- `./gradlew :app:assembleDevDebug` 通过。

下个会话建议：

- 第一优先：继续扫描五条链路 UI 层是否仍有直接 Repository/Rx 调用，只把剩余 UI 直连边界收到 UseCase/ViewModel，不先扩大到大规模 Compose 重写。
- 第二优先：如果代码事实确认 UI 直连基本清完，再挑一个最小、低风险页面做 Compose UI 试点。
- 验收入口：下载页切换、下载中单项暂停/继续/删除、全部暂停/继续/删除、已下载播放入口，以及动态发布选图压缩/发布流程。
- 仍未完成：本轮没有启动模拟器，也没有做下载页或动态发布页人工冒烟。

### 2026-05-20 阶段 8 小补：动态发布图片压缩用例边界

本轮决策：

- 继续阶段 8 编码，清理五条链路里 UI 层直接触碰 Repository 的小边界。
- 本轮只处理动态发布页图片选择压缩入口，不改变 PictureSelector、压缩工具实现、发布上传流程或用户可见交互。
- 按用户最新要求，本轮只保留本地改动和文档记录，不自动 push。

本轮代码变更：

- 新增 `CompressFeedImagesUseCase`，把 `ImageCompressionRepository.compressImages(...)` 封装到 feed domain 层。
- `PublishFeedActivity` 的 PictureSelector `CompressFileEngine` 改为调用 `CompressFeedImagesUseCase`，Activity 不再直接 import/调用 `ImageCompressionRepository` 或 `ImageCompressor.CompressionCallback`。
- 原有压缩完成后把 `originalFilePath/compressedFilePath` 回传给 PictureSelector 的行为保持不变。

验证：

- `rg "ImageCompressionRepository|getInstance\\(\\)\\.compressImages|component\\.feed\\.repository" ...` 确认动态发布 Activity 不再直连压缩 Repository。
- `./gradlew :app:assembleDevDebug` 通过，只有既有 BGABadge 非增量注解处理器提示。
- 本轮未启动模拟器，未做选图压缩/发布人工冒烟。

下个会话建议：

- 继续阶段 8 时，可再扫描五条链路 UI 层直接 Rx/Repository 调用；如果只剩 domain/repository 层调用，再考虑进入最小 Compose UI 试点。

### 2026-05-20 阶段 8 继续：下载模块剩余 Java 边界 Kotlin 化

本轮决策：

- 继续阶段 8 编码，优先把下载模块内剩余 Java 壳层迁到 Kotlin，降低后续状态链路/Compose 推进时的跨语言边界成本。
- 本轮只迁移下载入口 Activity、分页 adapter、下载监听器、点击 listener 和下载事件小模型，不扩大到下载 SDK、下载服务或设备端人工冒烟。
- 按用户最新要求，本轮只保留本地改动和文档记录，不自动 push。

本轮代码变更：

- `DownloadActivity` 从 Java 迁到 Kotlin，继续保留旧 XML/ViewPager/TabLayout 入口和两个下载分页顺序。
- `DownloadAdapter` 从 Java 迁到 Kotlin，继续创建已下载页和下载中页 Fragment。
- `MyDownloadListener` 从 Java 迁到 Kotlin，保留主线程刷新分发和下载进度 300ms 节流。
- `OnItemClickListener` 从 Java 迁到 Kotlin `fun interface`，继续兼容旧 RecyclerView adapter 点击回调。
- `DownloadChangedEvent` 从 Java 迁到 Kotlin 空事件类，继续作为 EventBus 下载状态刷新信号。
- `component/download` 目录当前不再包含 Java 文件。

验证：

- `find app/src/main/java/com/ixuea/courses/mymusic/component/download -name '*.java' -print` 无输出。
- `git diff --check` 通过。
- `./gradlew :app:assembleDevDebug` 通过，只有既有 Java deprecation/unchecked、`FragmentStatePagerAdapter` deprecated 和 BGABadge 非增量注解处理器提示。
- 本轮未启动模拟器，未做下载页手工操作冒烟。

下个会话建议：

- 如继续编码，可以先核对五条链路 UI 层还剩哪些直接 Repository/Rx 订阅，再挑一个最小入口继续推进阶段 8。
- 如先验收，优先跑下载页切换、下载中单项暂停/继续/删除、全部暂停/继续/删除和已下载列表播放入口。

### 2026-05-20 阶段 8 小补：下载中 Adapter 终态回调

本轮决策：

- 继续阶段 8 编码，收窄下载中列表里最后一个 Adapter 主动改列表状态的小边界。
- 本轮只处理下载任务进入终态后的列表刷新/事件通知归属，不扩大到下载 SDK、下载服务或设备端冒烟。
- 按用户最新要求，本轮只保留本地改动和文档记录，不自动 push。

本轮代码变更：

- `DownloadingAdapter` 不再在下载完成/移除等终态里直接 `removeData(position)` 或发布 `DownloadChangedEvent`。
- `DownloadingAdapterListener` 增加 `onDownloadTerminalState(...)`，下载状态终态统一回调外层。
- `DownloadingFragment` 收到终态回调后调用 `DownloadingViewModel.load()` 重新读取下载中列表；如果回调来自下载管理器通知，再由 Fragment 发布 `DownloadChangedEvent`。
- `LoadDownloadingUseCase` 对下载中列表做可见状态过滤，避免已完成/已移除等终态数据在 RecyclerView bind 阶段反复触发刷新。
- 这样下载中列表的删除/终态移除都通过 Fragment/ViewModel 状态链路刷新，Adapter 更接近只负责行渲染和用户操作回调。

验证：

- `git diff --check` 通过。
- `./gradlew :app:assembleDevDebug` 通过，只有既有 Java deprecation/unchecked 和 BGABadge 非增量注解处理器提示。
- 本轮未启动模拟器，未做下载完成自动移出下载中列表或已下载页刷新人工冒烟。

下个会话建议：

- 如继续编码，可以先做五条链路代码事实核对，确认 UI 层是否还存在直接 Repository/Rx 订阅，再决定是否开始 Compose UI 小步推进。
- 如先验收，优先跑下载完成后从“下载中”移除、已下载页出现歌曲、动态发布/列表刷新和发现页首页加载。

### 2026-05-20 阶段 8 继续：下载中/已下载状态链路

本轮决策：

- 继续阶段 8 编码，把下载中/已下载页从 Fragment/Adapter 直接操作 `DownloadRepository` 推进到 `ViewModel(uiState) -> UseCase -> Repository`。
- 本轮只处理下载列表状态、单项暂停/继续、单项删除、全部暂停/继续、全部删除和已下载歌曲列表，不扩大到下载 SDK、下载服务或播放器下载入口。
- `DownloadingAdapter` 继续负责下载进度行渲染和下载完成后的行移除/事件通知，但删除操作改为回调 Fragment/ViewModel。

本轮代码变更：

- 新增 `LoadDownloadingUseCase`、`LoadDownloadedSongsUseCase` 和 `DownloadActionsUseCase`：分别承接下载中列表、已下载歌曲列表和暂停/继续/删除操作。
- 新增 `DownloadingUiState` 和 `DownloadingViewModel`：用 `StateFlow` 暴露下载中列表、是否有任务正在下载和数据版本。
- 新增 `DownloadedUiState` 和 `DownloadedViewModel`：用 `StateFlow` 暴露已下载歌曲列表和数据版本。
- `DownloadingFragment` 不再直接持有 `DownloadRepository`；单项点击、单项删除、全部暂停/继续、全部删除都改为调用 `DownloadingViewModel`，Fragment 只负责按钮状态、空列表 toast 和 adapter 渲染。
- `DownloadedFragment` 不再直接持有 `DownloadRepository`；已下载歌曲列表加载改为调用 `DownloadedViewModel.load(orm)`，点击播放仍保留在 Fragment 作为 UI 路由。
- `DownloadingAdapter` 删除按钮不再直接调用 `DownloadRepository.remove(...)`；确认删除后通过 `DownloadingAdapterListener` 回调外层状态链路。

验证：

- `git diff --check` 通过。
- `./gradlew :app:assembleDevDebug` 通过，只有既有 Java deprecation/unchecked 和 BGABadge 非增量注解处理器提示。
- 本轮未启动模拟器，未做下载中列表、单项暂停/继续、单项删除、全部暂停/继续、全部删除或已下载播放人工冒烟。

下个会话建议：

- 如继续编码，可以先做一轮五条链路代码事实核对，清理执行文档里早先过快/过旧的状态描述，再决定是否继续向 Compose UI 小步推进。
- 如先验收，优先跑发现页首页加载、下载管理页列表和按钮操作、动态发布/列表刷新。

### 2026-05-20 阶段 8 继续：发现页首页状态链路

本轮决策：

- 继续阶段 8 编码，把 `DiscoveryFragment` 的首页聚合加载从 Fragment 直接 Rx 订阅推进到 `ViewModel(uiState) -> UseCase -> Repository`。
- 本轮只处理发现页首页 sections 加载状态，不扩大到自定义排序页、发现页 item 交互、广告点击或启动广告预下载。
- 当前工作区代码没有实际调用启动广告预下载；本轮按代码事实保留现状，只修正执行文档里早先过快的 `RefreshSplashAdUseCase` 说法。

本轮代码变更：

- 新增 `LoadDiscoveryPageUseCase`：桥接 `DiscoveryRepository.homeSections(sp)` Rx 聚合接口，返回 `DiscoveryPage` 或请求错误。
- 新增 `DiscoveryUiState` 和 `DiscoveryViewModel`：使用 `StateFlow` 暴露加载中、首页 sections、数据版本和错误版本。
- `DiscoveryFragment` 不再直接持有 `DiscoveryRepository`、`HttpObserver` 或 AutoDispose 订阅；改为通过 `ViewModelProvider` 获取 `DiscoveryViewModel`，用 `viewLifecycleOwner.lifecycleScope` + `repeatOnLifecycle` 收集状态。
- 旧行为保留：下拉刷新、最小刷新时长、Banner/歌单/单曲/底部 sections 渲染、排序变更刷新、歌单变更刷新、歌单详情跳转、单曲点击播放和自定义发现入口。

验证：

- `git diff --check` 通过。
- `./gradlew :app:assembleDevDebug` 通过，只有既有 BGABadge 非增量注解处理器提示。
- 本轮未启动模拟器，未做发现页网络数据、滚动、排序刷新或歌曲点击播放人工冒烟。

下个会话建议：

- 如继续编码，可以转向下载页代码事实核对，补齐任何仍直接持有 Repository 的状态链路。
- 如先验收，优先跑发现页首页加载、下拉刷新、排序变更后刷新、歌单点击和单曲点击播放。

### 2026-05-20 阶段 8 继续：动态 Feed 列表状态链路

本轮决策：

- 继续阶段 8 编码，把 `FeedFragment` 的动态列表加载从 Fragment 直接 Rx 订阅推进到 `ViewModel(uiState) -> UseCase -> Repository`。
- 本轮只处理动态列表刷新状态，不扩大到点赞、评论、图片预览、用户详情跳转或发布页之外的 EventBus 替换。
- 旧 XML/RecyclerView UI 继续保留，Fragment 仍负责发布入口登录校验、图片预览和用户详情路由。

本轮代码变更：

- 新增 `LoadFeedListUseCase`：桥接 `FeedRepository.feeds(userId)` Rx 接口，返回动态列表或请求错误。
- 新增 `FeedUiState` 和 `FeedViewModel`：使用 `StateFlow` 暴露加载中、feed 列表、数据版本和错误版本。
- `FeedFragment` 不再直接持有 `FeedRepository`、`HttpObserver` 或 AutoDispose 订阅；改为通过 `ViewModelProvider` 获取 `FeedViewModel`，用 `viewLifecycleOwner.lifecycleScope` + `repeatOnLifecycle` 收集状态。
- 发布成功后的 `FeedChangedEvent` 仍触发列表刷新，但刷新入口改为 `viewModel.load(userId)`。
- 旧行为保留：发布入口登录校验、动态图片大图预览、用户详情跳转和发布成功刷新列表。

验证：

- `git diff --check` 通过。
- `./gradlew :app:assembleDevDebug` 通过，只有既有 BGABadge 非增量注解处理器提示。
- 本轮未启动模拟器，未做动态列表网络数据、滚动、图片预览或发布后刷新人工冒烟。

下个会话建议：

- 如继续编码，可以转向发现页或下载页剩余 Fragment 直接 Repository/Rx 边界。
- 如先验收，优先跑动态列表加载、发布页选图压缩上传、发布完成返回后列表刷新。

### 2026-05-20 阶段 8 补齐：动态发布上传/创建状态链路

本轮决策：

- 继续阶段 8 编码，优先补齐当前工作区里仍保留 Activity 直接 Rx 订阅的动态发布页。
- 本轮只处理 `PublishFeedActivity` 的图片上传和创建动态状态，不扩大到动态列表、评论、点赞或全局 EventBus 替换。
- 图片选择器和压缩引擎仍保留在 Activity/PictureSelector 边界；选图结果、上传、发布和完成事件收敛到 `FeedPublishViewModel`。

本轮代码变更：

- 新增 `UploadFeedImagesUseCase`：桥接 `FeedPublishRepository.uploadImages(...)` Rx 接口，返回上传后的 `Resource` 列表或请求错误。
- 新增 `CreateFeedUseCase`：桥接 `FeedPublishRepository.createFeed(...)` Rx 接口，返回创建成功或请求错误。
- 新增 `FeedPublishUiState` 和 `FeedPublishOperation`：用 `StateFlow` 表示九宫格媒体数据、图片上传中、动态创建中、请求错误、上传数量异常和发布完成事件。
- `FeedPublishViewModel` 从只持有选图 LiveData 扩展为发布状态机：保留选择/删除图片状态，发布时先上传图片并校验返回资源数量，再创建动态；无图发布直接创建动态。
- `PublishFeedActivity` 不再直接持有 `FeedPublishRepository`、`HttpObserver` 或 AutoDispose 订阅；改为收集 `FeedPublishViewModel.uiState` 渲染九宫格、loading、上传失败 toast、发布完成后的 `FeedChangedEvent` 和 `finish()`。
- 旧行为保留：内容为空提示、140 字校验、发布内容追加客户端来源、选择/删除图片、压缩回调和发布成功刷新动态列表。

验证：

- `git diff --check` 通过。
- `./gradlew :app:assembleDevDebug` 通过，只有既有播放器 adapter deprecated 警告、Java deprecation/unchecked 提示和 BGABadge 非增量注解处理器提示。
- 本轮未启动模拟器，未做动态发布选图、压缩、上传或发布完成人工冒烟。

下个会话建议：

- 如继续编码，可以转向动态列表或发现页剩余 Fragment 直接 Rx 订阅边界。
- 如先验收，优先跑动态发布入口、选图压缩、多图上传、发布完成刷新动态列表。

### 2026-05-20 阶段 8 继续：聊天详情发送/已读状态链路

本轮决策：

- 继续阶段 8，把 `ChatActivity` 的文本/图片发送 callback 从 Activity 直接 Repository 调用推进到 `ViewModel(uiState) -> UseCase -> Repository`。
- 顺手把会话清未读和收到当前聊天对象消息后的标记已读从 Activity 直接 Repository 调用收敛到 `ChatViewModel`。
- 本轮不替换 RongCloud SDK、不重写图片选择和 Luban 压缩；图片选择/压缩仍留在 Activity 边界，发送和已读异步状态先收敛到 `ChatViewModel`。

本轮代码变更：

- 新增 `SendTextMessageUseCase`：桥接现有 `MessageRepository.sendText(...)` callback，返回成功消息或 RongCloud 错误。
- 新增 `SendImageMessageUseCase`：桥接现有 `MessageRepository.sendImage(...)` callback，并把图片发送进度回传给 ViewModel。
- 新增 `ClearConversationUnreadUseCase` 和 `MarkMessageReadUseCase`：分别承接会话清未读和单条消息标记已读。
- 新增 `LoadConversationUserUseCase` 和 `ConversationItemUiState`：会话列表先发布基础行数据，再异步补齐用户昵称/头像。
- 新增 `LoadChatUserUseCase` 和 `ChatMessageUiState`：聊天消息列表先发布 RongCloud 消息行，再由 ViewModel 异步补齐发送者头像。
- 新增 `ChatSendOperation`，扩展 `ChatUiState`：统一表示文本发送中、图片发送中、图片发送进度、发送错误、清空输入框事件。
- `ChatViewModel` 新增 `sendText(...)`、`sendImage(...)`、`clearUnread(...)` 和 `appendIncomingMessage(...)`，发送成功后统一追加消息、驱动列表平滑滚底；文本发送成功通过 `clearInputVersion` 驱动 Activity 清空输入框；历史消息和新消息都包装为 `ChatMessageUiState` 后再输出给 adapter；聊天目标用户昵称通过 `targetTitleVersion` 驱动页面标题。
- `ChatActivity` 不再直接调用 `UserManager.getUser(...)`、`MessageRepository.sendText(...)` / `sendImage(...)` / `markRead(...)` 或 `ConversationRepository.clearUnread(...)`；Activity 只负责空内容校验、图片选择/压缩入口、收集 `uiState`、设置标题、禁用发送按钮、派发未读数刷新事件和日志记录错误。
- `ChatAdapter` 不再在消息 bind 时调用 `UserManager.getUser(...)`，只按 `ChatMessageUiState.senderIcon` 渲染头像，降低消息列表 holder 复用时异步回调串行的风险。
- `ConversationListViewModel` 新增 `refreshAfterNewMessage()`，把新消息后的 1 秒节流刷新从 `ConversationActivity` 移入 ViewModel；同时把会话时间、最近消息、未读数文本和用户昵称/头像整理为 `ConversationItemUiState` 输出。
- `ConversationAdapter` 不再在 `convert(...)` 中直接调用 `UserManager.getUser(...)`；现在只按 ViewModel 输出的行状态渲染，降低 RecyclerView holder 复用时异步回调串行的风险。
- 旧行为保留：空文本提示、发送成功追加消息并滚底、文本成功后清空输入框、图片选择压缩后发送、收到目标用户新消息后标记已读并追加列表。

验证：

- `git diff --check` 通过。
- `./gradlew :app:assembleDevDebug` 通过，只有既有 MobSDK 仓库提示和 BGABadge 非增量注解处理器提示。
- 本轮未启动模拟器，未做聊天标题用户资料、文本发送、图片选择压缩发送、清未读、新消息追加、聊天消息头像渲染、会话列表新消息刷新或会话行用户资料渲染人工冒烟。

下个会话建议：

- 继续阶段 8 时，可以转入设备端冒烟，重点覆盖聊天发送、聊天/会话列表头像渲染、会话列表刷新和动态发布；如继续编码，可把聊天图片选择/压缩结果事件继续收敛。
- 如果先验收，优先跑会话列表进入聊天、首次历史加载滚底、下拉加载更多、文本发送、图片选择压缩发送、新消息追加。

### 2026-05-18 阶段 8 继续：动态发布上传状态链路

本轮决策：

- 继续阶段 8，把 `PublishFeedActivity` 的图片上传和创建动态从 Activity 内 Rx 订阅推进到 `ViewModel(uiState) -> UseCase -> Repository`。
- 本轮不替换图片选择器和压缩引擎；`PictureSelector` 的压缩 callback 仍留在 Activity/选择器边界，上传和创建动态状态先收敛到 ViewModel。

本轮代码变更：

- 新增 `UploadFeedImagesUseCase`：桥接现有 `FeedPublishRepository.uploadImages(...)` Rx 接口。
- 新增 `CreateFeedUseCase`：桥接现有 `FeedPublishRepository.createFeed(...)` Rx 接口。
- 新增 `FeedPublishUiState` 和 `FeedPublishOperation`：用 `StateFlow` 表示图片列表、上传中、发布中、请求错误、图片上传数量异常和发布完成事件。
- `FeedPublishViewModel` 从只持有选图 LiveData 扩展为发布状态机：保留选图列表，发布时先上传图片，校验返回资源数量，再创建动态。
- `PublishFeedActivity` 不再直接持有 `FeedPublishRepository` 或 `HttpObserver`；改为收集 `FeedPublishViewModel.uiState` 渲染九宫格、loading、错误、上传失败 toast 和发布完成后的 `FeedChangedEvent`/`finish()`。
- 旧行为保留：内容 140 字校验、空内容提示、位置选择字段写入 `Feed`、选择/删除图片、发布成功后刷新动态列表。

验证：

- `git diff --check` 通过。
- `./gradlew :app:assembleDevDebug` 通过，只有既有非增量注解处理器提示。
- 本轮未启动模拟器，未做动态发布选图、压缩、上传或发布完成人工冒烟。

下个会话建议：

- 继续阶段 8 时，可以把聊天发送状态也收敛到 `ChatViewModel`，或开始做一轮设备端冒烟，重点覆盖动态发布和聊天这两条刚改过的链路。

### 2026-05-18 阶段 8 继续：聊天详情历史消息状态链路

本轮决策：

- 继续阶段 8，把 `ChatActivity` 历史消息分页从 Activity 内部 `oldMessageId` + Repository callback 推进到 `ViewModel(uiState) -> UseCase -> Repository`。
- 本轮不重写文本/图片发送协议、不替换 RongCloud SDK callback；只把历史消息列表状态和分页游标收敛到 ViewModel。

本轮代码变更：

- 新增 `LoadChatHistoryUseCase`：桥接现有 `MessageRepository.getHistoryMessages(...)` callback。
- 新增 `ChatUiState` 和 `ChatViewModel`：使用 `StateFlow<ChatUiState>` 持有历史消息列表、加载状态、分页最老消息 id、错误码和滚动事件版本号。
- `ChatActivity` 不再持有 `oldMessageId`，历史消息加载改为调用 `ChatViewModel.loadInitial(...)` / `loadMore(...)`。
- `ChatActivity` 通过 `lifecycleScope` + `repeatOnLifecycle` 收集 `uiState`，用 `adapter.setDatum(state.messages)` 统一渲染消息列表。
- 文本发送成功、图片发送成功、收到目标用户新消息后，仍保留原有发送/接收逻辑，但消息追加改为 `ChatViewModel.appendMessage(...)`，避免后续加载更早历史消息时覆盖新追加消息。
- 首次历史加载后仍滚到底；发送成功和新消息追加后仍平滑滚到底；下拉刷新结束和错误日志也由状态渲染统一处理。

验证：

- `git diff --check` 通过。
- `./gradlew :app:assembleDevDebug` 通过，只有既有 Java deprecation/unchecked 和非增量注解处理器警告。
- 本轮未启动模拟器，未做聊天详情历史加载、下拉加载更多、文本/图片发送或新消息追加人工冒烟。

下个会话建议：

- 继续阶段 8 时，可以把聊天清未读和发送状态进一步收敛到 ViewModel，也可以转向动态发布图片压缩/上传状态链路。
- 如果先验收，优先跑会话列表进入聊天、首次历史加载滚底、下拉加载更多、文本发送、图片选择压缩发送、新消息追加。

### 2026-05-18 阶段 8 继续：聊天会话列表状态链路

本轮决策：

- 继续阶段 8，把聊天会话列表的数据加载和长按清消息后的刷新从 Activity 回调推进到 `ViewModel(uiState) -> UseCase -> Repository`。
- 本轮只处理会话列表状态；聊天详情页历史消息分页、文本/图片发送、清未读和新消息追加暂时保持旧实现。

本轮代码变更：

- 新增 `LoadConversationListUseCase`：作为会话列表 UseCase，桥接现有 RongCloud callback 风格的 `ConversationRepository.getConversationList(...)`。
- 新增 `DeleteConversationMessagesUseCase`：封装长按会话后删除该会话消息的 RongCloud callback，并由 ViewModel 删除后重新加载列表。
- 新增 `ConversationListUiState` 和 `ConversationListViewModel`：使用 `StateFlow<ConversationListUiState>` 暴露加载中、会话列表、错误码和版本号。
- `ConversationActivity` 不再直接调用 `ConversationRepository.getConversationList(...)` 或 `deleteMessages(...)`；改为 `ViewModelProvider` 获取 `ConversationListViewModel`，通过 `lifecycleScope` + `repeatOnLifecycle` 收集 `uiState`。
- 会话列表旧行为保留：点击进入 `ChatActivity`、onResume 刷新、收到 `NewMessageEvent` 后 1 秒节流刷新、长按清消息后刷新列表。

验证：

- `git diff --check` 通过。
- `./gradlew :app:assembleDevDebug` 通过，只有既有 Java deprecation/unchecked 和非增量注解处理器警告。
- 本轮未启动模拟器，未做会话列表入口、长按清消息或新消息刷新人工冒烟。

下个会话建议：

- 继续阶段 8 时，可以转向 `ChatActivity` 历史消息加载分页，把 `loadMore()` 收敛到 `ChatViewModel(StateFlow)`；文本/图片发送可留到下一刀。
- 如果先验收，优先跑会话列表入口、点击进聊天、长按清消息、新消息触发列表刷新。

### 2026-05-18 阶段 8 继续：动态 Feed 列表状态链路

本轮决策：

- 继续阶段 8，不直接替换 Compose UI；先把动态 Feed 列表的数据加载从 Fragment 网络订阅推进到 `ViewModel(uiState) -> UseCase -> Repository`。
- 只处理动态列表刷新状态，不扩大到发布动态、图片压缩上传或评论/点赞交互链路。

本轮代码变更：

- 新增 `LoadFeedListUseCase`：作为动态列表 UseCase，内部暂时桥接现有 RxJava `FeedRepository.feeds(userId)`。
- 新增 `FeedUiState` 和 `FeedViewModel`：使用 `StateFlow<FeedUiState>` 暴露加载中、feed 列表、业务错误和异常错误。
- `FeedFragment` 不再直接持有/订阅 `FeedRepository`，改为 `ViewModelProvider` 获取 `FeedViewModel`，通过 `viewLifecycleOwner.lifecycleScope` + `repeatOnLifecycle` 收集 `uiState`。
- 动态列表旧行为保留：发布入口登录校验、发布成功后的 `FeedChangedEvent` 刷新、位置预览、图片大图预览和用户详情跳转继续留在 Fragment/UI 路由层。

验证：

- `git diff --check` 通过。
- `./gradlew :app:assembleDevDebug` 通过，只有既有 Kotlin/Java deprecation、未使用参数和非增量注解处理器警告。
- 本轮未启动模拟器，未做设备端动态列表滚动或发布链路人工冒烟。

下个会话建议：

- 继续阶段 8 时，可以把聊天会话列表或聊天详情加载状态收敛到 `StateFlow`，也可以先给 Feed 列表补设备端入口/滚动冒烟。
- 发布动态的图片压缩上传链路仍是后续阶段 8 可处理对象；不要一次性把发布页、评论页和全局 EventBus 都铺开。

### 2026-05-18 阶段 8 启动：发现页状态链路第一刀

本轮决策：

- 用户明确要求直接开始阶段 8，不再等待阶段 7 深度人工冒烟完成。
- 阶段 8 第一刀不直接替换 Compose UI，先把发现页首页数据加载推进到 `ViewModel(uiState) -> UseCase -> Repository`，旧 XML/RecyclerView 渲染继续保留。

本轮代码变更：

- 新增 `LoadDiscoveryPageUseCase`：作为发现页首页 sections 的 UseCase，内部暂时桥接现有 RxJava `DiscoveryRepository.homeSections(...)`。
- 新增 `RefreshSplashAdUseCase`：负责请求启动广告、写入 `PreferenceUtil`、下载广告文件或清理失效广告文件，`DiscoveryFragment` 不再直接操作 `DefaultRepository.splashAd()`、Glide 下载和广告缓存文件。
- 新增 `DiscoveryUiState` 和 `DiscoveryViewModel`：使用 `StateFlow<DiscoveryUiState>` 暴露加载中、sections、业务错误和异常错误。
- `DiscoveryFragment` 不再直接持有/订阅 `DiscoveryRepository`；改为 `ViewModelProvider` 获取 `DiscoveryViewModel`，通过 `viewLifecycleOwner.lifecycleScope` + `repeatOnLifecycle` 收集 `uiState`。
- 发现页旧行为保留：下拉刷新、最小刷新时长、sections 渲染、启动广告预下载、排序/歌单变更后刷新、歌曲点击播放入口都继续保留。
- 启动广告刷新作为主内容加载成功后的附带任务运行；广告刷新失败不会覆盖发现页主内容状态。
- 新增 `DownloadingUiState` 和 `DownloadingViewModel`：下载中页通过 `StateFlow<DownloadingUiState>` 暴露下载列表、是否正在下载和版本号。
- `DownloadingFragment` 不再直接持有 `DownloadRepository`；暂停/继续、全部暂停/继续、删除单项、删除全部都改为调用 `DownloadingViewModel`，Fragment 只负责按钮、toast 和 adapter 渲染。
- `DownloadingAdapter` 删除按钮不再直接调用 `DownloadRepository.remove(...)`；确认删除后回调 Fragment/ViewModel，再由状态刷新驱动列表变化。
- 新增 `DownloadedUiState` 和 `DownloadedViewModel`：已下载页通过 `StateFlow<DownloadedUiState>` 暴露已下载歌曲列表。
- `DownloadedFragment` 不再直接持有 `DownloadRepository`；列表加载改为调用 `DownloadedViewModel.load(orm)`，点击已下载歌曲播放仍保留在 Fragment 作为 UI 路由。
- `app/build.gradle` 补充 `lifecycle-runtime-ktx` 和 `lifecycle-viewmodel-ktx`，支撑阶段 8 的 `lifecycleScope`、`repeatOnLifecycle` 和 `viewModelScope`。

验证：

- 按用户之前“不做测试”的要求，本轮未运行构建、模拟器或人工冒烟。

下个会话建议：

- 继续阶段 8 时可把发现页剩余调试/本地搜索历史初始化移出 Fragment，或转到动态 Feed 列表建立 `FeedViewModel(StateFlow)`。
- 不建议马上全量 Compose 替换；先让五条链路的数据状态收敛到 ViewModel，再按页面逐步 Compose 化。

### 2026-05-18 不做测试后的继续编码

本轮决策：

- 用户明确要求不做测试；本轮不启动模拟器、不做人工冒烟，也不跑构建测试。
- 继续编码时保持原定边界：不扩大到全局基类或非重点业务模块，只处理五条重点链路直接相关的小边界。

本轮代码变更：

- `MainAdapter.java` -> `MainAdapter.kt`：首页主 ViewPager adapter 迁移为 Kotlin，继续通过 `BaseFragmentStatePagerAdapter<Int>` 创建发现、视频、我的、动态、直播五个 tab Fragment。
- `MainActivity` 侧仍以 `new MainAdapter(getHostActivity(), getSupportFragmentManager())` 调用，Java 构造入口保持兼容。
- `TabEntity.java` -> `TabEntity.kt`：继续实现 Flyco `CustomTabEntity`，并用 `@JvmField` 保留 `title`、`selectedIcon`、`unSelectedIcon` 字段。
- `MusicPlayListChangedEvent.java` -> `MusicPlayListChangedEvent.kt`：保留 `position` 的 getter/setter 和 Java `isDeleteAll()`，Kotlin 调用方继续使用 `event.isDeleteAll`。
- `ScanLocalMusicCompleteEvent.java` -> `ScanLocalMusicCompleteEvent.kt`、`SheetChangedEvent.java` -> `SheetChangedEvent.kt`：空事件壳迁移为 Kotlin，Java `new XxxEvent()` 调用保持可用。

验证：

- 按用户要求，本轮未运行构建、模拟器或人工冒烟。

下个会话建议：

- 如继续编码，仍优先挑五条重点链路直接入口的小边界；不要因为跳过测试就扩大到全局 Activity/Fragment 基类迁移。
- 如后续恢复验证，再从阶段 7 深度冒烟清单继续。

### 2026-05-18 收尾保存和新会话交接

本轮收尾状态：

- 本地分支：`master`。
- 远端进度分支：`upstream/codex/emulator-smoke-progress`，继续不要推 `upstream/master`。
- 代码迁移提交 `f779f8e Migrate remaining focused UI boundaries to Kotlin` 已推送到 `upstream/codex/emulator-smoke-progress`。
- `.idea/gradle.xml` 的本地 IDE 元数据变更已恢复，保存本文档前工作区是干净的。
- 本轮补充保存本文档后，会再提交并推送一条文档交接提交到同一个远端进度分支。

本轮已验证：

- `git diff --check` 通过。
- `./gradlew :app:assembleDevDebug` 通过。
- 模拟器 `emulator-5554` 安装 `app/build/outputs/apk/dev/debug/app-dev-debug.apk` 成功。
- 显式启动 `SplashActivity` 后进入 `MainActivity`，App 进程存活。
- 入口级快测通过：发现首页可见、Feed tab 可切换、动态发布页可打开、下载管理页可打开、侧边抽屉“我的消息”可进入会话列表。
- 本轮入口快测期间 `logcat` 未抓到 `AndroidRuntime` 崩溃。

新会话接续建议：

- 先读本文档，再跑 `git status -sb` 和 `git log --oneline --decorate -5` 核对本地/远端状态。
- 如果继续验收，优先做阶段 7 深度冒烟：聊天文本/图片入口、下载暂停/继续/删除、动态选图压缩上传发布、发现页排序保存和列表滚动、播放上一首/下一首/Widget/桌面歌词。
- 如果继续编码，先不要扩大共享基类迁移范围；只处理五条重点链路直接阻塞深度冒烟的小问题。

### 2026-05-18 重点链路剩余 Java 边界 Kotlin 迁移

本轮收尾状态：

- 用户明确要求不要再测，继续编码；本轮不做设备端冒烟。
- 本地分支：`master`。
- 聊天/会话主目录已无 Java 文件，本轮继续收窄聊天发送和离线推送边界；`component/push` 目录也已完成 Kotlin 化。
- 本轮继续迁移后，`component/chat`、`component/conversation`、`component/push`、`component/download`、`component/discovery`、`component/feed` 目录均已扫不到 Java 文件。
- 继续编码后，重点链路直接依赖的通用 ViewPager/RecyclerView adapter 边界也已完成 Kotlin 化。

本轮代码变更：

- `MessageUtil.java` -> `MessageUtil.kt`：保留 `getContent(...)`、`getNickname(...)`、`createPushData(...)` 三个 `@JvmStatic` 静态入口，Java 调用方继续可用。
- `Push.java` -> `Push.kt`：保留 `Push.PUSH_STYLE_CHAT`、`getStyle()`/`setStyle(...)`、`getMessage()`/`setMessage(...)` 等 Java 兼容入口。
- `PushMessage.java` -> `PushMessage.kt`：保留 `getUserId()`/`setUserId(...)`、`getContent()`/`setContent(...)`，继续兼容 Gson 反序列化和 RongCloud 推送点击解析。
- `RongPushReceiver.java` -> `RongPushReceiver.kt`：保留融云小米推送点击入口，解析聊天离线推送后仍跳转 `SplashActivity` 并传递 `Constant.PUSH`/`Constant.ACTION_PUSH`。
- `PushReceiver.java` -> `PushReceiver.kt`、`PushService.java` -> `PushService.kt`：保留极光 SDK receiver/service 类名和继承关系。
- `DownloadActivity.java` -> `DownloadActivity.kt`、`DownloadAdapter.java` -> `DownloadAdapter.kt`：保留下载管理双 tab、ViewPager 和指示器联动。
- `MyDownloadListener.java` -> `MyDownloadListener.kt`、`OnItemClickListener.java` -> `OnItemClickListener.kt`、`DownloadChangedEvent.java` -> `DownloadChangedEvent.kt`：保留下载进度 300ms 刷新节流、主线程回调和 EventBus 刷新事件。
- `SortChangedEvent.java` -> `SortChangedEvent.kt`、发现页 UI 模型 `BaseSort`/`BannerData`/`ButtonData`/`SheetData`/`SongData`/`FooterData`/`CustomDiscoveryItem`/`IconTitleButtonData` 迁移为 Kotlin。
- `CustomDiscoveryActivity.java` -> `CustomDiscoveryActivity.kt`、`CustomDiscoveryAdapter.java` -> `CustomDiscoveryAdapter.kt`：保留自定义发现页拖拽排序、恢复默认排序、保存排序并发布 `SortChangedEvent`。
- `Feed.java` -> `Feed.kt`、`FeedChangedEvent.java` -> `FeedChangedEvent.kt`：保留动态发布/列表接口所需字段和 Java bean getter/setter。
- `DiscoveryAdapter.kt`：对 Kotlin 化后的 `SheetData`/`SongData` 列表做 `toMutableList()` 适配，保持 BaseQuickAdapter 数据入口兼容。
- `FeedAdapter.kt`：动态数据缺少 user 时直接跳过当前 item 绑定，删除按钮判断改为安全访问。
- `OnPageChangeListenerAdapter.java` -> `OnPageChangeListenerAdapter.kt`：保留 Java/Kotlin 匿名子类覆写入口。
- `BaseFragmentStatePagerAdapter.java` -> `BaseFragmentStatePagerAdapter.kt`：保留 `context` 字段、`getData(...)`、`setDatum(...)`，兼容 Java 子类和 Kotlin 下载/播放器适配器。
- `BaseRecyclerViewAdapter.java` -> `BaseRecyclerViewAdapter.kt`：保留 `context` 字段、`getInflater()`、`getDatum()`、`setDatum(...)`、`addData(...)`、`removeData(...)`、`ViewHolder.bind(...)` 等旧调用面。
- `BaseMultiItemEntity.java` -> `BaseMultiItemEntity.kt`：保留多类型列表模型接口。
- `VideoDetailAdapter.java`：仅补齐 `BaseRecyclerViewAdapter.ViewHolder<Object>` 泛型声明，适配 Kotlin 化后的通用 RecyclerView adapter。
- 小幅保护：
  - 文本消息内容为空时 `MessageUtil.getContent(...)` 返回空字符串。
  - 昵称和 id 为空时 `MessageUtil.getNickname(...)` 返回空字符串，避免通知显示链路空指针。
  - 推送点击数据为空、JSON 解析失败或聊天推送缺少 userId 时直接记录日志并返回，避免点击通知时崩溃。
  - 动态列表遇到异常空 user 时不再继续访问头像/昵称字段。

验证：

- `git diff --check` 通过。
- 首次 `./gradlew :app:assembleDevDebug` 失败，原因是 Kotlin 化后的发现页多类型模型仍按 Java `getItemType()` 实现接口；已改为 `override val itemType`。
- 再次 `./gradlew :app:assembleDevDebug` 通过，生成 `app/build/outputs/apk/dev/debug/app-dev-debug.apk`。
- 尝试做设备快扫时，sandbox 内启动 adb server 失败；提权后 `adb devices` 可运行，但当前无在线设备，因此未执行安装启动冒烟。
- 迁移通用 adapter 边界后，`git diff --check` 通过。
- `BaseRecyclerViewAdapter` Kotlin 化后首次构建失败，原因是 `VideoDetailAdapter` 仍使用 raw `ViewHolder` 类型；补齐泛型后再次 `./gradlew :app:assembleDevDebug` 通过。
- 继续执行快速测试：`git diff --check` 通过，`./gradlew :app:assembleDevDebug` 通过；sandbox 内 `adb devices` 仍因 smartsocket 权限失败，提权后可运行但无在线设备，未执行安装启动冒烟。
- 用户启动模拟器后执行入口快测：
  - `emulator-5554` 在线，安装 `app/build/outputs/apk/dev/debug/app-dev-debug.apk` 成功。
  - 显式启动 `SplashActivity` 后进入 `MainActivity`，App 进程 pid 为 `5449`。
  - 发现页作为首页入口可见；底部播放器显示 `Yesterday`。
  - Feed tab 可切换，点击发布浮动按钮成功进入 `PublishFeedActivity`。
  - Me tab 可切换，点击“下载管理”成功进入 `DownloadActivity`。
  - 侧边抽屉“我的消息”入口成功进入 `ConversationActivity`。
  - 非 exported Activity 不能通过 shell 直接 `am start`，已改走真实 UI 路径。
  - 收尾前台为 `ConversationActivity`，进程仍存活；本轮 `logcat` 未抓到 `AndroidRuntime` 崩溃。

下个会话建议：

- 如果继续编码，建议先不要再扩大共享基类范围；优先检查剩余 Java 是否仍属于五条链路的必要边界，或者转入设备端快速启动/重点入口冒烟。
- 如果后续允许验证，再单独补聊天发送、下载列表、发现页和动态发布入口冒烟。

### 2026-05-17 聊天链路 Kotlin 迁移收尾和新会话接续

本轮收尾状态：

- 本地分支：`master`。
- 本轮推送目标仍是远端进度分支：`upstream/codex/emulator-smoke-progress`，不要推 `upstream/master`。
- 聊天/会话链路主要 UI 目录已完成 Kotlin 化：`component/chat` 和 `component/conversation` 当前不再包含 Java 文件。
- 本轮没有按用户要求继续做设备端冒烟，只做编译级验证。

本轮代码变更：

- `ChatActivity.java` -> `ChatActivity.kt`：保留历史消息分页、文本发送、图片选择/压缩/发送、清未读、EventBus 新消息追加。
- `ConversationActivity.java` -> `ConversationActivity.kt`：保留会话列表加载、点击进入聊天、长按删除会话消息、新消息节流刷新。
- `ConversationAdapter.java` -> `ConversationAdapter.kt`：保留头像、昵称、最后一条消息、时间和未读角标渲染。
- `ChatAdapter.java` -> `ChatAdapter.kt`：保留文本/图片消息左右布局、头像加载、RongCloud 消息类型分发和图片加载顺序。
- `NewMessageEvent.java`、`MessageUnreadCountChangedEvent.java`、`MediaMessageExtra.java` 迁移为 Kotlin。
- 小幅保护：
  - 图片选择结果为空时不再直接访问首项。
  - 图片消息 extra 为空、解析失败或宽高异常时，消息图片容器使用默认方形尺寸。

验证：

- `git diff --check` 通过。
- `./gradlew :app:assembleDevDebug` 通过。

下个会话建议：

- 先读本文档，再跑 `git status -sb` 和 `git log --oneline --decorate -5` 核对本地/远端状态。
- 如果继续编码，建议迁移 `MessageUtil.java` 到 Kotlin，并用 `@JvmStatic` 保持 `MessageUtil.getContent(...)`、`MessageUtil.createPushData(...)` 等 Java 静态调用兼容。
- 如果转入验收，优先快速验证会话列表进入聊天、历史消息加载、空消息提示、文本发送和图片入口。

### 2026-05-17 快速冒烟和聊天详情页 Kotlin 迁移

本轮快速冒烟：

- 复用现有 `app/build/outputs/apk/dev/debug/app-dev-debug.apk` 安装到 `emulator-5554`，未重新构建后再测。
- App 安装启动成功，前台进入 `MainActivity`。
- 底部播放器可见，显示 `Yesterday`；点击可进入 `MusicPlayerActivity`，显示 `Yesterday / Leona Lewis`。
- 点击播放后业务日志出现 `MusicListManagerImpl: play online Yesterday ...`；MediaSession 起初仍为 `PAUSED`，随后进入 `PLAYING(3)`，位置推进到约 `72778ms`。结论：播放可推进，但播放触发到状态上报存在明显延迟。
- Feed tab 可进入，发布浮动按钮可见；点击后成功进入 `PublishFeedActivity`。
- Me tab 可进入，下载管理入口可见；点击后成功进入 `DownloadActivity`。
- 快检期间未抓到 `AndroidRuntime` 崩溃，也未看到新的通知限流日志。
- 收尾时已通过 `KEYCODE_MEDIA_PLAY_PAUSE` 暂停播放，最终 MediaSession 为 `PAUSED(2)`，位置约 `116007ms`。

本轮编码：

- `app/src/main/java/com/ixuea/courses/mymusic/component/chat/activity/ChatActivity.java` 迁移为 `ChatActivity.kt`。
- 保留旧入口、旧布局和旧适配器：`ConversationActivity`、用户详情页、通知入口仍指向同一个 `ChatActivity` 类名。
- 保留聊天发送链路现有行为：历史消息分页、文本发送、图片选择/压缩/发送、进入会话清未读、接收新消息后标记已读并追加到列表。
- 小幅补齐图片选择空结果保护，避免选择器异常返回空列表时直接访问 `result[0]`。
- `ConversationActivity.java` 迁移为 `ConversationActivity.kt`，继续通过 `ConversationRepository` 拉会话、删除会话消息，并保持 EventBus 新消息节流刷新。
- `ConversationAdapter.java` 迁移为 `ConversationAdapter.kt`，继续显示头像、昵称、最后一条消息、时间和未读数。
- `NewMessageEvent.java`、`MessageUnreadCountChangedEvent.java`、`MediaMessageExtra.java` 迁移为 Kotlin；`MediaMessageExtra` 保留无参构造和宽高属性，继续兼容 JSON 反序列化和 `ChatClient` 图片消息 extra 写入。
- `ChatAdapter.java` 迁移为 `ChatAdapter.kt`，保留消息方向和消息类型分发；图片消息 extra 为空、解析失败或宽高异常时改为默认方形尺寸保护。

验证：

- `git diff --check` 通过。
- `./gradlew :app:assembleDevDebug` 通过。

后续建议：

- 继续阶段 7，不进入阶段 8 深度重构。
- 优先用模拟器快速复测 Kotlin 迁移后的聊天详情页：会话列表进入、历史消息加载、空消息提示、文本发送、图片选择入口。
- 若继续编码，下一刀建议迁移 `MessageUtil.java`，仍保持 Java 静态调用兼容边界。

### 2026-05-17 会话收尾和下次接续

本轮收尾状态：

- 仅文档变更需要提交：`docs/modernization/execution-plan.md`。
- 当前本地分支：`master`，HEAD 在 `82a2cfc Migrate playback service boundaries to Kotlin` 之后。
- 远端进度分支：`upstream/codex/emulator-smoke-progress`，不要推 `upstream/master`。
- 模拟器设备：`emulator-5554`；本轮使用的 adb 路径为 `/Users/a123/Library/Android/sdk/platform-tools/adb`。
- 测试收尾时播放器已通过 `KEYCODE_MEDIA_PLAY_PAUSE` 暂停，最终 `MediaSession` 为 `PAUSED(2)`，位置约 `199059ms`。

本轮新结论：

- 播放链路时间线复测通过：播放、暂停、seek、通知媒体卡片播放/暂停、后台播放均在当前模拟器上通过。
- 上一轮“播放未推进”的判断已修正为观察窗口不足；用户也确认退出后自动播放、拖动进度条正常。
- 通知刷新本轮 logcat 未出现新的 `rate limit exceeded` / `Shedding notify`，但 `dumpsys notification` 的累计 `numRateViolations` 已到 `28`，后续继续观察是否增长。
- 发现一个待确认体验点：暂停态拖动进度条后会恢复为播放状态，需要后续按产品预期判断是否需要调整。

下个会话建议：

- 先读本文档，再跑 `git status -sb` 和 `git log --oneline --decorate -5` 核对本地状态。
- 继续阶段 7，不进入阶段 8 深度重构。
- 播放链路优先补测：多歌曲队列下上一首/下一首、Widget 按钮、桌面歌词开关、歌词进度。
- 之后继续补齐：聊天发送、动态多图压缩上传、下载任务操作、发现页网络数据和信息流滚动。

### 2026-05-17 阶段 7 播放链路模拟器复测

环境：

- 设备：Android Emulator `emulator-5554`。
- APK：`app/build/outputs/apk/dev/debug/app-dev-debug.apk`。
- 安装命令：`/Users/a123/Library/Android/sdk/platform-tools/adb install -r -g app/build/outputs/apk/dev/debug/app-dev-debug.apk`。
- 安装结果：成功。
- 启动说明：`monkey -p com.ixuea.courses.mymusic ...` 会进入 LeakCanary launcher；本轮改用显式启动 `com.ixuea.courses.mymusic/.component.splash.activity.SplashActivity`。

已验证：

- App 可从 `SplashActivity` 进入业务 `MainActivity`。
- 首页底部小播放器可见，当前歌曲显示 `Yesterday`。
- 点击底部小播放器可进入 `MusicPlayerActivity`。
- 播放器页面可显示标题、歌手、封面、进度条、播放/上一首/下一首/播放列表等控件。
- 播放列表按钮可打开底部弹窗，列表显示 `Yesterday - Leona Lewis`。
- 播放通知存在，通知 id `100`，包含 previous / Pause / next / lyric 四个 action。
- 本轮 `logcat -s NotificationService NotificationManager` 未复现新的 `rate limit exceeded` / `Shedding notify` 日志。
- App 进程在冒烟后仍存活，pid 为 `23617`。

发现的问题：

- 点击播放按钮和发送 `MEDIA_PLAY` 后，只看到 `MusicListManagerImpl: play online Yesterday https://rs.ixuea.com/music/assets/Yesterday.mp3`，但 MediaSession 仍保持 `PAUSED`，位置停在 `26918ms`。
- 播放器时间文本在等待 3-4 秒后仍停在 `00:26`，未观察到播放进度推进，真实出声播放仍未确认通过。
- 拖动 seekbar 后滑块位置能变化，但起始时间文本仍显示 `00:26`；暂停态 seek 的 UI 文本同步需要继续检查。
- 模拟器内 `ping rs.ixuea.com` 未返回，已手动停止；仍需区分是歌曲资源网络不可达、ExoPlayer 长时间 buffering，还是播放桥接没有正确上报错误/状态。
- `dumpsys notification --noredact` 里 `com.ixuea.courses.mymusic` 聚合统计仍有历史 `numRateViolations=18`，但本轮没有新的 NotificationService 限流日志。

后续建议：

- 优先给 `PlaybackController.onPlaybackStateChanged`、`onIsPlayingChanged`、`onPlayerError` 和 `MusicPlayerManagerImpl.requestAudioFocus` 增加临时/正式日志，复测播放请求后 ExoPlayer 是 buffering、error 还是没有执行。
- 继续验证同一首歌的资源 URL 在模拟器内是否可达；如果网络不可达，补一个本地音频样本或本地已下载歌曲再测真正播放。
- 修复或确认暂停态 seek 后时间文本是否需要立即同步。

### 2026-05-17 播放链路补充观察

用户补充：

- 上一轮记录结束后，模拟器中的歌曲自动开始播放。
- 拖动进度条后进度同步正常。

复查结果：

- 当前播放器页仍显示 `Yesterday / Leona Lewis`。
- UI 起始时间已从本轮早些时候的 `00:26` 更新到 `01:41`。
- `dumpsys media_session` 中同一会话位置已更新到 `101816ms`，说明播放进度确实发生过推进。
- 当前 MediaSession 抓取瞬间仍显示 `PAUSED`，因此还需要复测播放/暂停状态切换的实时上报，但前一节“播放未推进”的判断应修正为“播放启动或状态刷新存在延迟，观察窗口不足”。

后续建议：

- 继续复测播放按钮点击后的完整时间线：点击播放、进入 buffering、进入 playing、进度推进、点击暂停。
- 如果状态上报仍偶发滞后，再给 `PlaybackController` 和 `MusicPlayerService` 增加状态日志定位。

### 2026-05-17 播放链路完整时间线复测

环境：

- 设备：Android Emulator `emulator-5554`。
- APK：沿用已安装的 `app/build/outputs/apk/dev/debug/app-dev-debug.apk`。
- App 进程：`com.ixuea.courses.mymusic`，pid `23617`。
- 起始页面：`MusicPlayerActivity`，当前歌曲 `Yesterday / Leona Lewis`。

已验证：

- 点击播放器播放按钮后，`dumpsys media_session` 在约 1 秒内进入 `PLAYING(3)`，位置为 `115494ms`。
- 后续采样位置继续推进到 `147969ms`、`184739ms`，播放进度连续增长。
- 点击播放器暂停按钮后，`MediaSession` 进入 `PAUSED(2)`，位置 `195237ms`；暂停后的 UI 层级显示 `03:15 / 03:54`。
- 暂停态拖动进度条从约 `03:15` 回退后，`MediaSession` 位置跳到 `92588ms`，随后可继续播放；再次暂停后 UI 显示 `02:07 / 03:54`，`MediaSession` 位置为 `127255ms`。
- 展开通知栏后，系统媒体卡片显示 `Yesterday / Leona Lewis`，进度描述为 `2 minutes, 7 seconds of 3 minutes, 54 seconds`，并显示 Play、Previous track、Next track 控件。
- 点击通知媒体卡片 Play 后，`MediaSession` 进入 `PLAYING(3)`，位置 `144799ms`；再次点击同一按钮后进入 `PAUSED(2)`，位置 `159714ms`。
- 从通知媒体卡片启动播放后按 Home，桌面成为前台，App 播放器 Activity 进入不可见/停止状态；等待约 5 秒后 `MediaSession` 仍为 `PLAYING(3)`，位置推进到 `186343ms`，后台播放通过。
- 收尾时通过 `KEYCODE_MEDIA_PLAY_PAUSE` 暂停，最终状态为 `PAUSED(2)`，位置 `199059ms`。

日志和通知状态：

- 本轮 `logcat -s NotificationService NotificationManager` 未输出新的 `rate limit exceeded` / `Shedding notify` 日志。
- `dumpsys notification --noredact` 中音乐通知仍存在，id `100`，category 为 `transport`，包含 previous / Pause / next / lyric 四个 action。
- `dumpsys notification` 中 `com.ixuea.courses.mymusic` 的累计 `numRateViolations` 当前为 `28`。该计数是系统累计值，不能单独证明全部来自本轮；但较前一节记录的 `18` 有增加，通知刷新限频仍需继续观察。
- 应用日志未见崩溃；播放过程中存在频繁的 `SQLStatement` 进度持久化日志，这是后续可单独评估的噪音/性能点。

结论：

- 上一轮“播放未推进”的问题已通过时间线复测修正：播放、暂停、seek、通知媒体卡片播放/暂停、后台播放在当前模拟器上通过。
- 暂停态拖动进度条后会恢复为播放状态；本轮先记录为现象，后续需要结合产品预期决定是否调整。
- 仍待测：上一首/下一首在多歌曲队列下的行为、Widget 控制、桌面歌词开关和歌词进度。

### 2026-05-17 新会话接续上下文

当前代码状态：

- 当前本地分支：`master`。
- 不要推 `upstream/master`，本轮进度推送目标是已存在的远端进度分支：`upstream/codex/emulator-smoke-progress`。
- 本次新增改动：
  - 清理 Android Studio 本机 IDE 文件，并在 `.gitignore` 忽略 `.idea/deploymentTargetSelector.xml`、`.idea/deviceManager.xml`、`.idea/migrations.xml`。
  - `MusicListManagerImpl` 从 Java 迁到 Kotlin，保留旧 `MusicListManager` 接口和静态入口。
  - `MusicPlayerService` 从 Java 迁到 Kotlin，保留 Manifest service 类名、MediaSession、通知、Widget 和桌面歌词控制入口。
  - 更新本文档的播放列表管理器和播放服务迁移记录。
- 已验证：
  - `git diff --check`
  - `./gradlew :app:assembleDevDebug`
  - `./gradlew :app:testDevDebugUnitTest`
- 下次会话建议：
  - 先看 `git status -sb`、`git log --oneline --decorate -5` 和本文档最新记录。
  - 优先做设备端播放链路冒烟：播放/暂停、上一首/下一首、seek、通知按钮、Widget 按钮、桌面歌词开关、后台播放和 logcat 通知限流。
  - 如果继续编码，播放链路剩余 Java 边界主要是接口类和冻结通用工具；更建议转入设备端冒烟或切到聊天发送链路深一层迁移。

### 2026-05-17 播放服务边界 Kotlin 迁移

已处理：

- 将 `MusicPlayerService` 从 Java 迁到 Kotlin。
- 保留旧入口兼容：
  - Manifest 中 `.service.MusicPlayerService` 类名不变。
  - `MusicPlayerService.getMusicPlayerManager(context)` 和 `MusicPlayerService.getListManager(context)` 继续通过 `@JvmStatic` 暴露给 Java/Kotlin 调用方。
  - 播放通知、Widget、媒体按钮、桌面歌词按钮仍复用原 `Constant.ACTION_*` intent 协议。
- 保留服务核心行为：
  - 启动前台占位通知。
  - 初始化 `MediaSessionCompat` 并处理播放、暂停、上一首、下一首、seek。
  - 播放/暂停/进度回调继续更新 MediaSession、播放通知和桌面 Widget。
  - 播放准备后继续加载封面、更新 metadata，并通知 Widget。
  - 桌面歌词开关和关闭回调继续刷新播放通知。
- 补齐窄范围防御：
  - 上一首/下一首为空时不再继续调用播放。
  - 进度转换到 `Int` 时限制在合法范围内。
  - metadata 歌手名在 `singer` 为空时回退到 `singerNickname`。

验证结果：

- 命令：`./gradlew :app:assembleDevDebug`
- 结果：通过。
- 命令：`./gradlew :app:testDevDebugUnitTest`
- 结果：通过。

保留问题：

- 后续仍需设备端冒烟，重点验证媒体按钮/通知按钮、Widget 按钮、桌面歌词开关、后台播放和通知限流 logcat。
- 如继续编码，播放链路剩余 Java 边界主要是接口类和冻结通用工具；建议先转入设备端播放冒烟或切到聊天发送链路深一层迁移。

### 2026-05-17 播放列表管理器 Kotlin 迁移

已处理：

- 将 `MusicListManagerImpl` 从 Java 迁到 Kotlin。
- 保留旧入口兼容：
  - `MusicListManagerImpl.getInstance(context)` 继续通过 `@JvmStatic` 暴露，并返回 `MusicListManager` 接口。
  - `MusicListManagerImpl.destroy()` 继续保留给退出登录/销毁实例流程调用。
  - `getDatum()`、`setDatum(...)`、`play(...)`、`pause()`、`resume()`、`next()`、`previous()`、`delete(...)`、`deleteAll()`、`seekTo(...)` 接口行为继续兼容旧 Java/XML 调用方。
- 保留播放队列核心行为：
  - 启动时从 LiteORM 恢复播放列表和最后播放歌曲。
  - 播放本地路径、已下载文件或在线资源时继续复用原分支。
  - 播放列表变更继续保存数据库并发送 `MusicPlayListChangedEvent`。
  - 循环模式变更继续同步 Media3 looping 状态和 `PlaybackRepository` 队列状态。
- 补齐窄范围防御：
  - 空播放列表不再默认取第 0 项。
  - 当前歌曲不在列表时，上一首/下一首回退到列表尾/头并记录日志。
  - 删除越界位置会跳过。
  - 随机循环删除当前歌曲时不再选回被删除歌曲。

验证结果：

- 命令：`./gradlew :app:assembleDevDebug`
- 结果：通过。
- 命令：`./gradlew :app:testDevDebugUnitTest`
- 结果：通过。

保留问题：

- 后续仍需设备端冒烟，重点验证播放队列切歌、随机/单曲/列表循环、删除当前歌曲后的续播、通知/Widget 控制和歌词进度联动。
- 如继续编码，可转入 `MusicPlayerService` 边界迁移或先做播放链路完整设备端冒烟。

### 2026-05-17 播放通知刷新收口

已处理：

- `MusicPlayerService` 的播放通知刷新新增状态签名：
  - 歌曲 id。
  - 歌曲时长。
  - 播放/暂停状态。
  - 桌面歌词显示状态。
  - 媒体 metadata 版本。
- 重复签名的通知刷新会直接跳过，避免播放状态重复回调时反复 `startForeground`/`notify`。
- 普通通知刷新增加 1s 最小间隔；播放/暂停、歌词开关、封面/metadata 变化仍可立即刷新。
- 播放进度继续只更新 `MediaSession`，不重建播放通知。

验证结果：

- 命令：`./gradlew :app:assembleDevDebug`
- 结果：通过。
- 命令：`./gradlew :app:testDevDebugUnitTest`
- 结果：通过。

保留问题：

- 需要重新安装到模拟器或真机，播放/暂停/切歌/歌词开关时观察 logcat，确认不再出现 `NotificationService` rate limit 和 `NotificationManager` shedding update。

### 2026-05-17 歌词模型和单行歌词 View Kotlin 迁移

已处理：

- 将歌词模型从 Java 迁到 Kotlin：
  - `Line.kt`
  - `Lyric.kt`
- 将 `LyricUtil` 从 Java 迁到 Kotlin，继续通过 `@JvmStatic` 保持 Java 静态调用兼容。
- 将 `LyricLineView` 从 Java 迁到 Kotlin，XML 全限定类名不变，外部 setter 方法保持兼容。
- 补齐歌词边界保护：
  - 空歌词列表返回安全默认值。
  - 空逐字数组或字数/时长数组长度不一致时不再直接越界。
  - 当前字时长小于等于 0 时跳过除零计算。

验证结果：

- 按用户要求，本批只编码，未执行 Gradle 构建或测试。

保留问题：

- 后续仍需统一构建和设备端验证歌词滚动、逐字高亮、长按选择和分享链路。

### 2026-05-17 歌词列表和桌面歌词 View Kotlin 迁移

已处理：

- 将 `LyricListView` 从 Java 迁到 Kotlin：
  - 保留 XML 全限定类名和 `setData(...)`、`setProgress(...)`、`setLyricListListener(...)` 外部入口。
  - 保留 `LyricListListener` 嵌套接口，`MusicPlayerActivity` 继续实现原接口。
  - 拖拽歌词选中行、3 秒隐藏拖拽 UI、点击 seek 到歌词行逻辑保持兼容。
- 将 `GlobalLyricView` 从 Java 迁到 Kotlin：
  - 保留 `OnGlobalLyricDragListener`、`GlobalLyricListener`、`GlobalLyricOtherListener` 三个嵌套接口。
  - 保留桌面歌词普通/简单样式切换、播放控制、颜色选择、字号调整和拖拽回调。
- 补齐窄范围防御：
  - 歌词列表为空时隐藏列表并跳过滚动/逐字更新。
  - 拖拽落在占位 item 或越界位置时兜底到首尾真实歌词行。
  - 桌面歌词在解析歌词为空、下一行不存在、监听器尚未设置时不再直接崩溃。

验证结果：

- 按用户要求，本批只编码，未执行 Gradle 构建或测试。

保留问题：

- 歌词链路仍需后续统一构建和设备端冒烟，重点验证歌词列表滚动、逐字高亮、桌面歌词拖拽、颜色/字号设置。
- 如继续编码，可转入播放 Manager/通知/Widget 边界。

### 2026-05-17 桌面歌词管理器 Kotlin 迁移

已处理：

- 将 `GlobalLyricManagerImpl` 从 Java 迁到 Kotlin。
- 保留旧入口兼容：
  - `GlobalLyricManagerImpl.getInstance(context)` 继续通过 `@JvmStatic` 暴露给 Java 调用方。
  - 继续实现 `GlobalLyricManager`、`MusicPlayerListener`、`GlobalLyricView.OnGlobalLyricDragListener`、`GlobalLyricView.GlobalLyricListener`。
  - `MusicPlayerService` 继续通过 `setGlobalLyricOtherListener(...)` 注入关闭歌词回调。
- 保留桌面歌词核心行为：
  - 悬浮窗权限不足时跳转 `SplashActivity`。
  - 显示/隐藏桌面歌词并同步 Widget 状态。
  - 锁定/解锁桌面歌词、注册解锁广播、显示/清理解锁通知。
  - 播放/暂停/上一首/下一首控制和拖拽位置保存。
- 补齐窄范围防御：
  - 更新悬浮窗布局前检查 View 是否已添加。
  - `tryHide()`/`tryShow()` 在 View 尚未创建时不再直接崩溃。
  - 后续设置关闭歌词回调时，会同步到已创建的 `GlobalLyricView`。

验证结果：

- 按用户要求，本批只编码，未执行 Gradle 构建或测试。

保留问题：

- 后续仍需统一构建和设备端冒烟，重点验证桌面歌词权限跳转、显示/隐藏、锁定/解锁、拖拽保存和通知解锁。
- 如继续编码，可转入播放 Manager/通知/Widget 边界。

### 2026-05-17 音乐 Widget 边界 Kotlin 迁移

已处理：

- 将 `WidgetUtil` 从 Java 迁到 Kotlin：
  - 保留 `onPlaying(...)`、`onPaused(...)`、`onPrepared(...)`、`onProgress(...)`、`onGlobalLyricShowStatusChanged(...)` 的 `@JvmStatic` 入口。
  - 抽出 `bindSong(...)`、`bindProgress(...)`、`songTitle(...)`，供 Widget provider 复用。
  - 歌曲标题、歌手名、进度和时长增加空值/负数兜底。
- 将 `MusicWidget` 从 Java 迁到 Kotlin：
  - Manifest 中 `.component.widget.MusicWidget` 类名保持不变。
  - 保留 Widget 更新时启动 `MusicPlayerService`、绑定播放/上一首/下一首/歌词 PendingIntent、加载当前歌曲封面和进度。
  - 继续通过 Glide 异步加载封面，失败/清理时兜底 placeholder。

验证结果：

- 按用户要求，本批只编码，未执行 Gradle 构建或测试。

保留问题：

- 后续仍需统一构建和设备端冒烟，重点验证桌面 Widget 刷新、按钮控制、封面显示和歌词按钮状态。
- 如继续编码，可转入播放 Manager 或通知边界。

### 2026-05-17 通知工具 Kotlin 迁移

已处理：

- 将 `NotificationUtil` 从 Java 迁到 Kotlin。
- 保留旧入口兼容：
  - `showAlert(...)`
  - `notify(...)`
  - `getServiceForeground(...)`
  - `createMusicNotification(...)`
  - `showUnlockGlobalLyricNotification(...)`
  - `clearUnlockGlobalLyricNotification(...)`
  - `showMessage(...)`
- 保留通知行为：
  - 默认通知渠道、聊天消息通知渠道、音乐通知渠道继续按原 id 创建。
  - 播放通知继续绑定 `MediaSessionCompat`、上一首/播放暂停/下一首/歌词按钮。
  - 桌面歌词锁定通知继续通过 broadcast 触发解锁。
  - 聊天消息通知继续先查询未读数和用户信息，再跳转聊天入口。
- 补齐窄范围防御：
  - 消息通知的 `targetId` 为空时跳过。
  - 未读数为空时按 0 处理。
  - 用户 id 为空时兜底使用会话 `targetId`。

验证结果：

- 按用户要求，本批只编码，未执行 Gradle 构建或测试。

保留问题：

- 后续仍需统一构建和设备端冒烟，重点验证播放通知按钮、桌面歌词解锁通知、聊天消息通知和通知限流复验。
- 如继续编码，可转入播放 Manager 边界。

### 2026-05-17 播放管理器兼容门面 Kotlin 迁移

已处理：

- 将 `MusicPlayerManagerImpl` 从 Java 迁到 Kotlin。
- 保留旧入口兼容：
  - `MusicPlayerManagerImpl.getInstance(context)` 继续通过 `@JvmStatic` 暴露，并返回 `MusicPlayerManager` 接口。
  - 继续实现 `MusicPlayerManager` 和 `AudioManager.OnAudioFocusChangeListener`。
  - 旧 listener 回调继续保留 `MediaPlayer` 参数位置，Media3 路径仍传 `null`。
- 保留播放核心行为：
  - `play(uri, song)` 请求音频焦点后转发到 `PlaybackRepository.play(...)`。
  - `pause()`、`resume()`、`seekTo(...)`、`setLooping(...)` 继续转发到 Media3 播放仓库。
  - 播放状态、进度、完成、错误继续桥接回旧 `MusicPlayerListener`。
  - 歌词准备继续复用 `LyricParser`，必要时从 `DefaultRepository.songDetail(...)` 拉取歌词。
- 补齐窄范围防御：
  - 焦点恢复时如果没有当前 `uri` 或歌曲，不再直接进入播放。
  - 歌词 ready 回调在当前歌曲为空时跳过。

验证结果：

- 按用户要求，本批只编码，未执行 Gradle 构建或测试。

保留问题：

- 后续仍需统一构建和设备端冒烟，重点验证播放/暂停/恢复、音频焦点抢占恢复、歌词加载和 Media3 listener 桥接。
- 如继续编码，可迁移 `MusicListManagerImpl` 或其他播放服务边界。

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
- 命令：`./gradlew :app:testDevDebugUnitTest`
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

### 2026-05-16 保留链路 Kotlin 迁移第一批

用户决定暂缓阶段 7 设备端冒烟，继续做编码迁移。

已处理：

- 将发现页聚合入口从 Java 迁到 Kotlin：
  - `DiscoveryPage.kt`
  - `DiscoveryRepository.kt`
- 将下载 SDK facade 从 Java 迁到 Kotlin：
  - `DownloadRepository.kt`
- 将动态发布/动态列表状态入口从 Java 迁到 Kotlin：
  - `FeedPublishRepository.kt`
  - `ImageCompressionRepository.kt`
  - `FeedRepository.kt`
  - `FeedPublishViewModel.kt`
- 保留 Java 旧调用方兼容：
  - `Repository.getInstance()` 仍可从 Java 调用。
  - `FeedPublishViewModel.getMediaItems()`、`getSelectedImages()` 仍可从 Java 调用。
  - `DiscoveryPage.getSections()` 仍可从 Java 调用。

验证结果：

- 命令：`./gradlew :app:assembleDevDebug`
- 结果：通过。

下一步编码建议：

- 继续从保留链路中挑小切片迁 Kotlin，优先级建议为：
  - `DiscoveryFragment`、`DiscoveryAdapter`。
  - 最后再碰更大的播放器 Activity/Fragment。

### 2026-05-17 下载链路 Kotlin 迁移第二批

已处理：

- 将下载中列表从 Java 迁到 Kotlin：
  - `DownloadingFragment.kt`
  - `DownloadingAdapter.kt`
- 将已下载列表从 Java 迁到 Kotlin：
  - `DownloadedFragment.kt`
- 保留旧入口兼容：
  - `DownloadAdapter` 仍通过 `DownloadingFragment.newInstance()`、`DownloadedFragment.newInstance()` 创建页面。
  - `DownloadingAdapter` 构造参数保持 `Context`、`LiteORMUtil`、`FragmentManager`。
  - 下载中列表继续通过 `DownloadRepository` 执行暂停、继续、删除、全部暂停、全部继续。
- 顺手补了窄范围防御：
  - 下载删除回调中检查 `bindingAdapterPosition`，避免异步状态刷新后位置失效导致越界。
  - 下载任务关联歌曲缺失时标题兜底为空，避免列表渲染直接崩溃。

验证结果：

- 命令：`./gradlew :app:assembleDevDebug`
- 结果：通过。
- 命令：`./gradlew :app:testDevDebugUnitTest`
- 结果：通过。

保留问题：

- 尚未启动模拟器或真机，下载中/已下载列表的人工冒烟仍待执行。

下一步编码建议：

- 转入设备端冒烟验收，或继续播放器 Activity/Fragment 小切片。

### 2026-05-17 动态列表 Kotlin 迁移第三批

已处理：

- 将动态列表从 Java 迁到 Kotlin：
  - `FeedFragment.kt`
- 保留旧入口兼容：
  - `MainAdapter` 仍通过 `FeedFragment.newInstance()` 创建首页动态页。
  - `UserDetailAdapter` 仍通过 `FeedFragment.newInstance(userId)` 创建用户动态页。
  - `FeedAdapter.FeedListener` 图片点击回调继续打开 `PhotoViewer`。
  - `FeedChangedEvent`、`UserDetailEvent` 的 EventBus 入口继续保留。
- 顺手修正用户动态页参数：
  - `newInstance(userId)` 写入 `Constant.USER_ID`，与 `DefaultRepository.feeds(userId)` 查询字段一致。
  - 读取参数时保留 `Constant.ID` 兜底，避免旧参数入口失效。

验证结果：

- 命令：`./gradlew :app:assembleDevDebug :app:testDevDebugUnitTest`
- 结果：通过。

保留问题：

- 尚未启动模拟器或真机，动态列表加载、图片预览、用户动态过滤和发布后刷新仍待人工冒烟。

下一步编码建议：

- 转入设备端冒烟验收，或继续播放器 Activity/Fragment 小切片。

### 2026-05-17 动态发布页和图片适配器 Kotlin 迁移第四批

已处理：

- 将发布动态页面从 Java 迁到 Kotlin：
  - `PublishFeedActivity.kt`
- 将动态图片适配器从 Java 迁到 Kotlin：
  - `ImageAdapter.kt`
- 保留旧入口兼容：
  - `FeedFragment` 仍通过 `PublishFeedActivity::class.java` 打开发布页。
  - `SelectLocationEvent` 的 EventBus 入口继续保留。
  - `FeedChangedEvent` 发布成功通知继续保留。
  - 选图、压缩、上传、发布仍通过现有 `PictureSelector`、`ImageCompressionRepository`、`FeedPublishRepository` 闭环。
  - `PublishFeedActivity` 和 `FeedAdapter` 仍通过 `ImageAdapter(R.layout.item_image)` 渲染图片。
- 顺手清理：
  - 移除原压缩回调中的临时 `Log.d("TAG", ...)` 调试输出。
  - 继续把选图状态保存在 `FeedPublishViewModel`，Activity 只做 UI 事件编排。

验证结果：

- 命令：`./gradlew :app:assembleDevDebug`
- 结果：通过。
- 命令：`./gradlew :app:testDevDebugUnitTest`
- 结果：通过。

保留问题：

- 尚未启动模拟器或真机，发布页多图选择、压缩、上传、位置选择和发布成功刷新仍待人工冒烟。

下一步编码建议：

- 转到发现页：
  - `DiscoveryFragment`
  - `DiscoveryAdapter`

### 2026-05-17 动态主适配器 Kotlin 迁移第五批

已处理：

- 将动态主适配器从 Java 迁到 Kotlin：
  - `FeedAdapter.kt`
- 保留旧入口兼容：
  - `FeedFragment` 仍通过 `FeedAdapter(R.layout.item_feed)` 创建动态列表适配器。
  - `FeedAdapter.FeedListener` 和 `setListener(...)` 继续保留，图片点击仍回调到 `FeedFragment` 打开 `PhotoViewer`。
  - 动态卡片头像、昵称、时间、正文、位置、九宫格图片、删除按钮、点赞用户、评论列表渲染保持原行为。
- 结构整理：
  - 将媒体、删除按钮、点赞、评论渲染拆成 adapter 内部小方法，降低单个 `convert` 方法复杂度。
  - 图片 URL 由 Kotlin `map` 生成，移除原 Java/Guava `Lists.transform` 依赖点。

验证结果：

- 命令：`./gradlew :app:assembleDevDebug`
- 结果：通过。
- 命令：`./gradlew :app:testDevDebugUnitTest`
- 结果：通过。

保留问题：

- 尚未启动模拟器或真机，动态卡片渲染、图片预览、点赞/评论用户点击和发布后刷新仍待人工冒烟。

下一步编码建议：

- 转入设备端冒烟验收，或继续播放器 Activity/Fragment 小切片。

### 2026-05-17 发现页 Fragment/Adapter Kotlin 迁移第六批

已处理：

- 将发现页主 Fragment 从 Java 迁到 Kotlin：
  - `DiscoveryFragment.kt`
- 将发现页多类型主 Adapter 从 Java 迁到 Kotlin：
  - `DiscoveryAdapter.kt`
- 保留旧入口兼容：
  - `MainAdapter` 仍通过 `DiscoveryFragment.newInstance()` 创建首页发现页。
  - `DiscoveryFragment` 仍实现 `OnBannerListener<Ad>` 和 `DiscoveryAdapter.DiscoveryAdapterListener`。
  - Banner 点击、歌单点击、单曲点击、刷新按钮、自定义发现入口、排序变更和歌单变更刷新保持原行为。
  - 启动广告 `splashAd` 拉取、缓存和清理逻辑保持原行为。
- 结构整理：
  - `DiscoveryAdapter` 将 Banner、按钮、歌单、单曲、Footer 渲染拆成内部小方法。
  - Banner 泛型收窄为 `Banner<Ad, BannerImageAdapter<Ad>>` 和 `OnBannerListener<Ad>`。
  - Adapter 保留 stable id 逻辑，继续基于 `BaseSort.sort` 和 item type 生成 id。

验证结果：

- 命令：`./gradlew :app:assembleDevDebug`
- 结果：通过。
- 命令：`./gradlew :app:testDevDebugUnitTest`
- 结果：通过。

保留问题：

- 尚未启动模拟器或真机，发现页加载、Banner 点击、歌单点击、单曲播放、自定义发现入口和启动广告缓存仍待人工冒烟。

下一步编码建议：

- 转入设备端冒烟验收，或继续播放器 Activity/Fragment 小切片。

### 2026-05-17 发现页小 Adapter Kotlin 迁移第七批

已处理：

- 将发现页歌单 item adapter 从 Java 迁到 Kotlin：
  - `SheetAdapter.kt`
- 将发现页单曲 item adapter 从 Java 迁到 Kotlin：
  - `DiscoverySongAdapter.kt`
- 保留旧入口兼容：
  - `DiscoveryAdapter` 仍通过 `SheetAdapter(R.layout.item_sheet)` 和 `DiscoverySongAdapter(R.layout.item_discovery_song)` 创建内部列表。
  - `SheetFragment` 仍通过 `SheetAdapter(R.layout.item_sheet)` 复用 Kotlin 版歌单适配器。
  - 歌单封面、标题、播放量和单曲封面、标题、歌手/专辑占位文本、末尾分割线显示保持原行为。

验证结果：

- 命令：`./gradlew :app:assembleDevDebug`
- 结果：通过。
- 命令：`./gradlew :app:testDevDebugUnitTest`
- 结果：通过。

保留问题：

- 尚未启动模拟器或真机，发现页歌单/单曲渲染和歌单页复用 `SheetAdapter` 仍待人工冒烟。

下一步编码建议：

- 转入设备端冒烟验收。
- 如继续编码，可挑播放器 Activity/Fragment 做 Kotlin 小切片。

### 2026-05-17 播放器底部控制和播放列表 Kotlin 迁移第八批

已处理：

- 将播放器底部控制条容器从 Java 迁到 Kotlin：
  - `SmallAudioControlPageFragment.kt`
- 将播放器底部控制条 item 和黑胶唱片页从 Java 迁到 Kotlin：
  - `SmallAudioControlFragment.kt`
  - `RecordFragment.kt`
- 将播放器相关 adapter 从 Java 迁到 Kotlin：
  - `SmallAudioControlAdapter.kt`
  - `MusicPlayerRecordAdapter.kt`
  - `MusicPlayListAdapter.kt`
  - `SimplePlayerAdapter.kt`
- 将播放列表弹窗从 Java 迁到 Kotlin：
  - `MusicPlayListDialogFragment.kt`
- 保留旧入口兼容：
  - `SmallAudioControlAdapter` 仍通过 `SmallAudioControlFragment.newInstance(song)` 创建底部控制条页面。
  - `MusicPlayerRecordAdapter` 仍通过 `RecordFragment.newInstance(song)` 创建黑胶唱片页面。
  - `SmallAudioControlPageFragment`、`MusicPlayerActivity` 仍可通过 `MusicPlayListDialogFragment.show(...)` 打开播放列表弹窗。
  - `SimplePlayerActivity` 仍通过 `SimplePlayerAdapter(android.R.layout.simple_list_item_1)` 创建简单播放器列表。
- 顺手补了窄范围防御：
  - 底部控制条歌词渲染在空歌词、空行列表时兜底为空，歌词行号限制在合法范围内。
  - 播放列表弹窗 item 点击和侧滑删除会检查当前位置是否仍有效，避免列表变化后越界。
  - 播放列表选中状态允许当前播放歌曲为空，避免 Media3 兼容桥接恢复期间空当前歌曲导致崩溃。

验证结果：

- 命令：`./gradlew :app:assembleDevDebug :app:testDevDebugUnitTest`
- 结果：通过。

保留问题：

- 尚未启动模拟器或真机，底部控制条滑动切歌、播放/暂停、播放列表弹窗、侧滑删除、黑胶旋转和歌词单行进度仍待人工冒烟。

下一步编码建议：

- 转入设备端冒烟验收，或继续迁移 `SimplePlayerActivity`。
- `MusicPlayerActivity` 体量较大，建议拆为播放按钮/进度条/歌词三个小切片处理。

### 2026-05-17 播放器页面 Kotlin 迁移第九批

已处理：

- 将简单播放器页面从 Java 迁到 Kotlin：
  - `SimplePlayerActivity.kt`
- 将主播放器黑胶页面从 Java 迁到 Kotlin：
  - `MusicPlayerActivity.kt`
- 将播放器自定义 View 和事件从 Java 迁到 Kotlin：
  - `RecordView.kt`
  - `RecordPageView.kt`
  - `RecordClickEvent.kt`
- 保留旧入口兼容：
  - `BaseLogicActivity.startMusicPlayerActivity()` 仍打开 `MusicPlayerActivity`。
  - `activity_music_player.xml`、`fragment_record.xml` 仍引用原自定义 View 全限定类名。
  - `RecordPageView` 继续暴露 Java 字段形态的 `binding`、`adapter`，兼容迁移过程中仍直接访问字段的调用方。
  - `MusicPlayerActivity` 继续监听 `RecordClickEvent`、`MusicPlayListChangedEvent`，并保留播放列表、歌词切换、下载入口、循环模式和进度条入口。
- 顺手补了窄范围防御：
  - 主播放器翻页时检查播放列表索引，避免列表变化后 `ViewPager2` 当前位置越界。
  - 上一首/下一首在当前歌曲为空或不在列表时兜底到列表第一首，避免旧 Manager 抛异常。
  - 主播放器初始化、时长、进度、歌词、下载入口在当前歌曲为空时不再直接崩溃。
  - 主播放器销毁时 unregister `ViewPager2.OnPageChangeCallback`。
  - 自定义 View 使用 Kotlin `@JvmField`/公开字段保留旧 Java 访问方式。

验证结果：

- 命令：`./gradlew :app:assembleDevDebug :app:testDevDebugUnitTest`
- 结果：通过。

保留问题：

- 尚未启动模拟器或真机，主播放器黑胶翻页、歌词切换、seek、上一首/下一首、下载入口和背景模糊切换仍待人工冒烟。

下一步编码建议：

- 转入设备端冒烟验收。
- 如继续编码，可挑歌词视图、播放 Manager 或通知/Widget 边界做小切片。

### 2026-05-17 歌词选择/分享和解析 Kotlin 迁移第十批

已处理：

- 将歌词选择和歌词图片分享页面从 Java 迁到 Kotlin：
  - `SelectLyricActivity.kt`
  - `ShareLyricImageActivity.kt`
- 将歌词相关 adapter 从 Java 迁到 Kotlin：
  - `LyricAdapter.kt`
  - `SelectLyricAdapter.kt`
- 将歌词 parser 从 Java 迁到 Kotlin：
  - `LyricParser.kt`
  - `LRCLyricParser.kt`
  - `KSCLyricParser.kt`
- 保留旧入口兼容：
  - `MusicPlayerActivity` 长按歌词仍通过 `SelectLyricActivity` 进入歌词选择页。
  - `ShareLyricImageActivity.start(activity, song, lyric)` 保留 `@JvmStatic` 静态入口。
  - `MusicPlayerManagerImpl` 仍通过 `LyricParser.parse(...)` 解析歌词。
  - `LyricListView` 仍通过 `LyricAdapter(R.layout.item_lyric)` 渲染歌词行。
- 顺手补了窄范围防御：
  - 选择歌词页面在歌曲或解析歌词为空时展示空列表，不再直接崩溃。
  - `SelectLyricAdapter` 支持空列表并校验选中索引边界。
  - LRC/KSC parser 对空歌词内容、格式不完整歌词行直接跳过。
  - `LyricAdapter.setSelectedIndex(...)` 只刷新有效位置，避免空列表或旧索引越界。

验证结果：

- 命令：`./gradlew :app:assembleDevDebug :app:testDevDebugUnitTest`
- 结果：通过。

保留问题：

- 尚未启动模拟器或真机，歌词选择、文本分享、图片分享、LRC/KSC 逐字歌词解析仍待人工冒烟。

下一步编码建议：

- 转入设备端冒烟验收。
- 如继续编码，可迁移 `MusicListManagerImpl` 或其他播放服务边界。

### 2026-05-17 本次收口交接状态

当前代码状态：

- 主工程 `component/player` 包内业务代码已完成 Kotlin 化，原 Java 文件已替换为 Kotlin 文件。
- 歌词链路已迁移：
  - 歌词选择/分享 Activity。
  - 歌词列表和选择歌词 adapter。
  - LRC/KSC parser 和 `LyricParser` 入口。
- 歌词模型、工具类和自定义 View 已迁移：
  - `Line.kt`
  - `Lyric.kt`
  - `LyricUtil.kt`
  - `LyricLineView.kt`
  - `LyricListView.kt`
  - `GlobalLyricView.kt`
- 歌词管理器已迁移：
  - `GlobalLyricManagerImpl.kt`
- 音乐 Widget 边界已迁移：
  - `WidgetUtil.kt`
  - `MusicWidget.kt`
- 通知工具已迁移：
  - `NotificationUtil.kt`
- 播放管理器兼容门面已迁移：
  - `MusicPlayerManagerImpl.kt`
- 最近一次完整验证：
  - 命令：`./gradlew :app:assembleDevDebug`
  - 命令：`./gradlew :app:testDevDebugUnitTest`
  - 结果：通过。
- 后续又完成并验证：
  - `MusicListManagerImpl` Kotlin 迁移。
  - `MusicPlayerService` Kotlin 迁移。
- 已执行模拟器第一轮入口冒烟；完整功能冒烟仍未完成。

下一次会话建议：

- 先看 `git status -sb` 和本文档最新记录。
- 优先做模拟器或真机冒烟，尤其是播放器和歌词链路：
  - 主播放器进入、黑胶翻页、播放/暂停、上一首/下一首、seek。
  - 歌词/黑胶切换、歌词滚动、长按歌词选择。
  - 歌词文本分享、歌词图片分享、LRC/KSC 歌词解析。
  - 下载按钮、通知控制、Widget 控制。
- 如果继续编码，建议转入聊天发送链路，或继续清理播放链路剩余接口/冻结工具边界。
- GitHub 发布仍走 `/private/tmp/museflow-public-slim`，不要直接从主工程 `master` 推 `origin/master`，也不要推 `upstream/master`。

### 2026-05-16 交接上下文

当前代码状态：

- 当前分支：`master`。
- 本轮未拆提交，包含三类相邻工作：
  - 歌词小修：清理歌词绘制日志/测试画笔、复用 `FontMetrics`、回收 `TypedArray`、销毁歌词列表时取消拖拽倒计时。
  - 阶段 2 音乐播放链路：Media3 播放核心、Repository 状态入口、旧 Manager 兼容桥接、Media3 session 到旧队列控制的桥接。
  - 阶段 3-6：聊天、动态发布、下载、发现/信息流的 Repository/ViewModel/兼容桥接闭环。
  - Kotlin 迁移第一批：发现页、下载、动态发布/动态列表的新增 Repository/ViewModel 已从 Java 迁到 Kotlin。
  - Kotlin 迁移第二批：下载中/已下载 Fragment 和下载中 Adapter 已从 Java 迁到 Kotlin。
  - Kotlin 迁移第三批：动态列表 `FeedFragment` 已从 Java 迁到 Kotlin。
  - Kotlin 迁移第四批：动态发布页 `PublishFeedActivity` 和图片适配器 `ImageAdapter` 已从 Java 迁到 Kotlin。
  - Kotlin 迁移第五批：动态主适配器 `FeedAdapter` 已从 Java 迁到 Kotlin。
  - Kotlin 迁移第六批：发现页 `DiscoveryFragment` 和 `DiscoveryAdapter` 已从 Java 迁到 Kotlin。
  - Kotlin 迁移第七批：发现页小 adapter `SheetAdapter` 和 `DiscoverySongAdapter` 已从 Java 迁到 Kotlin。
  - Kotlin 迁移第八批：播放器底部控制、黑胶页、播放列表弹窗和相关 adapter 已从 Java 迁到 Kotlin。
  - Kotlin 迁移第九批：播放器主页面、简单播放器、自定义黑胶 View 和事件已从 Java 迁到 Kotlin。
  - Kotlin 迁移第十批：歌词选择/分享页面、歌词列表 adapter 和 LRC/KSC parser 已从 Java 迁到 Kotlin。
- 关键新文件：
  - `app/src/main/java/com/ixuea/courses/mymusic/playback/PlaybackController.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/playback/PlaybackRepository.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/playback/LegacyMusicSessionPlayer.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/playback/PlaybackService.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/playback/PlaybackModels.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/chat/repository/ChatClient.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/chat/repository/ConversationRepository.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/chat/repository/MessageRepository.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/discovery/model/DiscoveryPage.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/discovery/repository/DiscoveryRepository.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/discovery/fragment/DiscoveryFragment.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/discovery/adapter/DiscoveryAdapter.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/discovery/adapter/SheetAdapter.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/discovery/adapter/DiscoverySongAdapter.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/download/repository/DownloadRepository.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/download/fragment/DownloadingFragment.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/download/fragment/DownloadedFragment.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/download/adapter/DownloadingAdapter.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/feed/repository/FeedPublishRepository.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/feed/repository/ImageCompressionRepository.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/feed/repository/FeedRepository.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/feed/ui/FeedPublishViewModel.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/feed/fragment/FeedFragment.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/feed/activity/PublishFeedActivity.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/feed/adapter/ImageAdapter.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/feed/adapter/FeedAdapter.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/player/fragment/SmallAudioControlPageFragment.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/player/fragment/SmallAudioControlFragment.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/player/fragment/RecordFragment.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/player/fragment/MusicPlayListDialogFragment.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/player/adapter/SmallAudioControlAdapter.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/player/adapter/MusicPlayerRecordAdapter.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/player/adapter/MusicPlayListAdapter.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/player/adapter/SimplePlayerAdapter.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/player/activity/MusicPlayerActivity.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/player/activity/SimplePlayerActivity.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/player/view/RecordPageView.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/player/view/RecordView.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/player/model/event/RecordClickEvent.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/lyric/activity/SelectLyricActivity.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/lyric/activity/ShareLyricImageActivity.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/lyric/adapter/LyricAdapter.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/lyric/adapter/SelectLyricAdapter.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/lyric/parser/LyricParser.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/lyric/parser/LRCLyricParser.kt`
  - `app/src/main/java/com/ixuea/courses/mymusic/component/lyric/parser/KSCLyricParser.kt`

验证状态：

- `./gradlew :app:assembleDevDebug :app:testDevDebugUnitTest` 曾通过。
- Kotlin 迁移第一批后，`./gradlew :app:assembleDevDebug` 和 `./gradlew :app:testDevDebugUnitTest` 通过。
- 下载链路 Kotlin 迁移第二批和动态列表 Kotlin 迁移第三批后，`./gradlew :app:assembleDevDebug :app:testDevDebugUnitTest` 通过。
- 动态发布页和图片适配器 Kotlin 迁移第四批后，`./gradlew :app:assembleDevDebug :app:testDevDebugUnitTest` 通过。
- 动态主适配器 Kotlin 迁移第五批后，`./gradlew :app:assembleDevDebug` 和 `./gradlew :app:testDevDebugUnitTest` 通过。
- 发现页 Fragment/Adapter Kotlin 迁移第六批后，`./gradlew :app:assembleDevDebug` 和 `./gradlew :app:testDevDebugUnitTest` 通过。
- 发现页小 adapter Kotlin 迁移第七批后，`./gradlew :app:assembleDevDebug` 和 `./gradlew :app:testDevDebugUnitTest` 通过。
- 播放器底部控制和播放列表 Kotlin 迁移第八批后，`./gradlew :app:assembleDevDebug :app:testDevDebugUnitTest` 通过。
- 播放器页面 Kotlin 迁移第九批后，`./gradlew :app:assembleDevDebug :app:testDevDebugUnitTest` 通过。
- 歌词选择/分享和解析 Kotlin 迁移第十批后，`./gradlew :app:assembleDevDebug :app:testDevDebugUnitTest` 通过。
- 已连接模拟器 `emulator-5554` 安装运行并完成第一轮入口冒烟。

后续建议：

- 下一次继续时先读本文档，再看 `git status`。
- 如果继续编码，可挑歌词 View、播放 Manager 或通知/Widget 边界小切片；也可以转入设备端冒烟验收。
- 如果回到验收，先启动模拟器或连接设备，安装 `app/build/outputs/apk/dev/debug/app-dev-debug.apk`。
- 冒烟顺序建议：
  - App 启动。
  - 音乐播放：在线/本地播放、暂停/继续、seek、上一首/下一首、通知按钮、Widget、歌词和小播放器进度。
  - 聊天：会话列表、进入聊天、历史消息、文本发送、图片发送入口。
  - 动态发布：多图选择、压缩、上传、发布、动态列表刷新。
  - 下载：下载中列表、已下载列表、单项暂停/继续、全部暂停/继续、删除、播放已下载歌曲。
  - 发现/信息流：发现页加载、Banner/歌单/单曲点击、信息流滚动、图片预览。

### 2026-05-17 阶段 7 模拟器冒烟第一轮

环境：

- 设备：Android Emulator `emulator-5554`。
- APK：`app/build/outputs/apk/dev/debug/app-dev-debug.apk`。
- 安装命令：`adb install -r app/build/outputs/apk/dev/debug/app-dev-debug.apk`。
- 安装结果：成功。
- 启动方式：launcher intent。

已通过的入口级检查：

- App 启动成功，前台进入 `MainActivity`，进程保持运行。
- 首页底部小播放器初始化成功，能显示当前歌曲 `Yesterday`。
- 点击底部小播放器可进入 `MusicPlayerActivity`。
- 播放器页可打开，标题、封面、进度条、播放/上一首/下一首/列表按钮可见。
- 在播放器页点击播放/下一首/拖动进度条后未出现崩溃。
- 系统 `media_session` 中能看到 `com.ixuea.courses.mymusic/MusicListManager` 会话。
- Feed tab 可打开，动态发布按钮可见。
- 点击动态发布按钮可进入 `PublishFeedActivity`，未出现崩溃。
- 侧边栏“我的消息”可进入 `ConversationActivity`，未出现崩溃。
- Me tab 可打开，“下载管理”入口可见。
- 点击“下载管理”可进入 `DownloadActivity`，`下载完成` 和 `正在下载` tab 可见。
- 切换到 `正在下载` tab 后仍停留在 `DownloadActivity`，未出现崩溃。

未完成或需要人工确认的检查：

- 在线/本地歌曲是否真实出声播放仍需人工听感确认。
- 播放器 seek 后 UI 进度是否正确推进仍需人工确认；本轮 UI 层级中进度文本仍显示 `00:10`。
- 后台通知按钮和 Widget 控制尚未完整操作。
- 歌词滚动、歌词选择、歌词文本分享、歌词图片分享、LRC/KSC 逐字解析尚未验证。
- 聊天历史加载、文本发送、图片发送尚未验证。
- 动态多图选择、压缩、上传、发布成功刷新尚未验证。
- 下载任务的暂停、继续、删除、播放已下载歌曲尚未验证；当前模拟器列表为空，只验证了页面和 tab 入口。
- 发现页网络数据加载、Banner/歌单/单曲点击、信息流滚动和图片预览尚未完整验证。

新增风险记录：

- logcat 出现系统通知限流：
  - `NotificationService: Package enqueue rate is 5.4531145 ... package=com.ixuea.courses.mymusic`
  - `NotificationManager: Shedding notify (update) ... rate limit (5.0) exceeded`
- 该风险疑似来自播放通知频繁更新；已在 `MusicPlayerService` 增加通知刷新去重和低频保护，仍需设备端复验。

### 2026-05-16 Git 远端和发布上下文

远端状态：

- `origin` 指向 GitHub：`https://github.com/lemma42796/museflow-android.git`。
- `upstream` 指向 Gitee：`git@gitee.com:yyh455/my-cloud-music-android-java.git`。
- 不要再把本轮现代化提交推到 `upstream`；此前误推已经回滚，`upstream/master` 保持在 `4d6c675`。

GitHub 发布策略：

- GitHub `origin/master` 现在是 public slim 版本，冻结功能主体已从当前文件树移除，但历史不重写。
- 不能直接从主工程 `master` 推 GitHub；主工程仍保留完整代码和冻结模块，直接推会把冻结代码重新带回 GitHub。
- GitHub 发布必须走 public slim worktree：`/private/tmp/museflow-public-slim`。
- public slim 当前工作分支：`codex/github-public-slim-ff`，跟踪 `origin/master`。
- 同步发布时只把 public-safe 的保留链路改动带到 public slim worktree，在那里构建、提交、推 `origin HEAD:master`。
- `local.properties` 只允许留在临时 worktree 本地，不能进入提交。

后续推送提醒：

- 若继续在本地 `master` 开发，需要同步发布到 GitHub 时，应先把变更带到 `/private/tmp/museflow-public-slim`，确认没有冻结功能回流，再推 `origin HEAD:master`。
- GitHub push 若提示私有邮箱保护，使用 GitHub noreply 邮箱 amend 未推送提交后再推。
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

## 阶段 8：深度现代化迁移

状态：已按用户要求启动。阶段 7 深度人工冒烟未完成，当前带验证风险推进；第一刀从发现页状态链路开始。

### 迁移原则

- 在当前项目内继续迁移，不新建项目搬代码。
- 每次只迁一条链路或一个页面，保持 App 随时可编译、可安装、可回退。
- 旧 Java/XML 入口可以暂时保留，作为新实现的 adapter/facade 或兜底。
- 新项目仅作为现代配置参考，不作为主工程。

### 主要任务

1. 工程基线升级：
   - 升级 Gradle、Android Gradle Plugin、Kotlin、compileSdk、Compose BOM 和关键 AndroidX 依赖。
   - 引入 version catalog，集中管理依赖版本。
   - 处理 KAPT/KSP/Hilt 的 Kotlin 注解处理路径，避免继续依赖 Java-only 注解处理。
2. 架构深化：
   - 把五条选中链路统一推进到 `Compose UI -> ViewModel(uiState) -> UseCase/Repository -> DataSource`。
   - ViewModel 只暴露不可变 UI state 和一次性 UI event。
   - Activity/Fragment 逐步退回导航、权限、系统入口承载角色。
3. 状态和异步模型统一：
   - 新代码统一使用 Coroutines、Flow、StateFlow、SharedFlow。
   - 逐步替换选中链路内的 RxJava、EventBus 和全局 mutable state。
   - 对旧 Java 调用方保留薄 adapter，等入口稳定后再删除。
4. Compose 页面迁移：
   - 优先迁移播放器、聊天、动态发布、下载、发现/信息流。
   - 不做全量 XML 一次性替换。
   - 长列表使用稳定 key、不可变 item state 和必要的 Paging 3。
5. 模块化：
   - 等边界稳定后再拆模块。
   - 优先拆 `core:network`、`core:data`、`core:design`、`feature:player`、`feature:chat`、`feature:feed`。
   - 拆模块前先保证包内依赖方向清楚，避免把旧循环依赖搬进新模块。
6. 测试和验收：
   - 先补 ViewModel/Repository 单测。
   - 对五条链路保留设备端冒烟清单。
   - 每次深度迁移完成后跑 `:app:assembleDevDebug` 和关键单测。

### 验收

- 选中链路 UI state 可从 ViewModel 单向驱动。
- 选中链路内不再新增 RxJava/EventBus 依赖。
- Compose 页面覆盖优先级最高的用户路径。
- 主要业务逻辑从 Activity/Fragment 移出。
- 项目仍能构建、安装并通过五条链路冒烟。

### 暂停条件

- 依赖升级导致冻结第三方 SDK 无法运行。
- 单条链路迁移需要连带重写多个冻结模块。
- 新模块拆分暴露出无法短期解决的循环依赖。

## 阶段 8 继续：歌单/登录/用户/歌词模型收口

本轮继续按“先清边界、保持可编译”的节奏推进，不做设备端冒烟。

已完成：

- `component/sheet` 剩余 Java 残留清零：`Sheet`、`SheetWrapper`、`SheetChangedEvent` 已迁到 Kotlin。
- `component/login` 模型/事件迁到 Kotlin：`Session`、`LoginStatusChangedEvent` 保留旧 Java 调用面。
- `component/user` 事件和核心 `User` 模型迁到 Kotlin，保留 `User(id)`、`createLogin(...)`、Parcelable、关注/性别/描述格式化 API。
- `component/ad/model/Ad` 和 `component/lyric/model/Line`、`Lyric` 迁到 Kotlin，保留 Parcelable/CREATOR。
- `component/song/model/Song` 迁到 Kotlin，保留 LiteORM `@Table/@Column/@Ignore`、`SORT_KEYS`、`SOURCE_*`、Parcelable/CREATOR、`isRotate/setRotate`、`isPlayList/setPlayList`、`isLocal`。
- 对迁移后真实可空字段做最小调用点修正，例如歌单图标、选择好友昵称、`SheetWrapper` 泛型类型。
- 对发现页歌曲列表的歌手展示补充空安全，避免 `Song.singer` 为空时崩溃。

验证：

- `find app/src/main/java/com/ixuea/courses/mymusic/component/sheet -maxdepth 4 -name '*.java' -print` 无输出。
- `find app/src/main/java/com/ixuea/courses/mymusic/component/user/model/event -maxdepth 1 -name '*.java' -print` 无输出。
- `find app/src/main/java/com/ixuea/courses/mymusic/component/ad -maxdepth 3 -name '*.java' -print` 无输出。
- `git diff --check` 通过。
- `./gradlew :app:assembleDevDebug` 通过；仅剩既有 Java deprecated/unchecked、Kotlin deprecated API warning 和 BGABadge 非增量注解处理提示。

当前剩余 Java 边界：

- 网络层：`component/api/*`。
- 歌词自定义 View：`GlobalLyricView.java`、`LyricLineView.java`、`LyricListView.java`。

建议下一刀：

- 优先拆歌词自定义 View，或先把 `component/api` 网络层作为稳定边界继续保留，避免影响所有接口调用。

## 阶段 8 继续：本地音乐和外围入口 Kotlin 收口

用户要求加速完成编码；本轮继续按“小模块直接落地、构建兜底”的方式推进，不做设备端冒烟。

已完成：

- `component/music` 本地音乐扫描链路从 Java 迁到 Kotlin：`LocalMusicActivity`、`ScanLocalMusicActivity`、`MusicSortDialogFragment`、`ScanLocalMusicAsyncTask`、`ScanLocalMusicCompleteEvent`。
- 保留本地歌曲列表编辑/删除、排序弹窗、扫描动画、媒体库查询、LiteORM 保存、本地扫描完成 EventBus 通知和跳转播放器行为。
- `component/location/activity`、`component/login/activity/LoginHomeActivity`、`component/user/activity` 和 `component/widget/MusicWidget` 从 Java 迁到 Kotlin，继续保留 public slim 占位页立即 `finish()` 的行为、用户页静态启动入口和桌面 Widget 控制 PendingIntent。
- `component/observer/ObserverAdapter` 从 Java 迁到 Kotlin，保留 Rx `Observer` 空实现。

验证：

- `find app/src/main/java/com/ixuea/courses/mymusic/component/music app/src/main/java/com/ixuea/courses/mymusic/component/location app/src/main/java/com/ixuea/courses/mymusic/component/login/activity app/src/main/java/com/ixuea/courses/mymusic/component/user/activity app/src/main/java/com/ixuea/courses/mymusic/component/widget app/src/main/java/com/ixuea/courses/mymusic/component/observer -type f -name '*.java' -print` 无输出。
- `git diff --check` 通过。
- `./gradlew :app:assembleDevDebug` 通过；剩余提示主要是既有/保留的 deprecated API、AsyncTask 迁移后的 Kotlin deprecated warning、BGABadge 非增量注解处理提示。

当前剩余 Java 边界：

- `component` 目录当前不再包含 Java 文件。
- 工程外围仍有 Java 基类、manager、repository、util、model/response 等公共边界，下一刀可继续按“先小公共类、再高风险核心”的顺序推进。

## 阶段 8 继续：component 剩余 Java 清零

用户要求继续编码；本轮只做本地代码推进，不提交、不 push。

已完成：

- `component/api` 网络层从 Java 迁到 Kotlin：`DefaultService`、`HttpObserver`、`NetworkModule`、`NetworkSecurityInterceptor`。
- 保留 Retrofit 注解接口、Rx `HttpObserver` 构造器、Hilt `@Module/@Provides` 静态 Java 调用面、OkHttp 签名/加密/解密拦截行为。
- `NetworkSecurityInterceptor` 迁移时把请求方式判断收敛为 Kotlin 值比较，并保留 request/response body 读取逻辑。
- 歌词自定义 View 从 Java 迁到 Kotlin：`LyricLineView`、`LyricListView`、`GlobalLyricView`。
- `LyricLineView` 增加逐字歌词数组、时长和索引的边界保护；`LyricListView` 增加拖拽选中行的边界保护；`GlobalLyricView` 增加监听器/歌词空值保护。

验证：

- `find app/src/main/java/com/ixuea/courses/mymusic/component -type f -name '*.java' | sort` 无输出。
- `git diff --check` 通过。
- `./gradlew :app:assembleDevDebug` 通过；剩余提示主要是既有 Java deprecated/unchecked 和 BGABadge 非增量注解处理提示。

当前剩余 Java 边界：

- `component` 目录已经清零 Java。
- 下一刀可以继续清 `model/response`、小 exception、adapter 基类、manager 接口等外围公共 Java；`DefaultRepository`、`Base*Activity/Fragment`、大型 util 先按风险排后处理。

## 最新交接

当前分支：`codex/emulator-smoke-progress`。

当前策略：用户已明确要求继续编码、不做设备端冒烟；下一会话从文档和 git 状态恢复即可。

恢复步骤：

- 先看 `git status --short` 和本节内容，确认当前迁移面。
- 如继续编码，优先处理 `component/music` 本地音乐扫描链路，或 `component/lyric/view` 三个 Java 自定义 View。
- 每个切片保持 `./gradlew :app:assembleDevDebug` 可过；不主动做模拟器/真机冒烟，除非用户重新要求。

剩余 Java 清单以当前仓库为准，可用：

```bash
find app/src/main/java/com/ixuea/courses/mymusic/component -maxdepth 4 -name '*.java' -print | sort
```
