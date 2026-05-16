# 五条选中链路重构方案

本文档只描述本轮允许主动重构的链路。

## 1. 音乐播放链路

### 当前入口

- `app/src/main/java/com/ixuea/courses/mymusic/manager/impl/MusicPlayerManagerImpl.java`
- `app/src/main/java/com/ixuea/courses/mymusic/manager/impl/MusicListManagerImpl.java`
- `app/src/main/java/com/ixuea/courses/mymusic/service/MusicPlayerService.java`
- `app/src/main/java/com/ixuea/courses/mymusic/component/player/**`
- `app/src/main/java/com/ixuea/courses/mymusic/component/lyric/**`
- `app/src/main/java/com/ixuea/courses/mymusic/component/widget/MusicWidget.java`

### 要移除的问题

- `MediaPlayer.prepare()` 可能阻塞。
- 播放进度通过 16ms 定时器发布。
- 播放、歌词、Service、通知、小播放器、播放列表耦合过重。
- 老 Manager API 依赖全局可变状态。

### 目标结构

- `PlaybackService : MediaSessionService`
- `PlaybackController` 基于 Media3 `ExoPlayer`
- `PlaybackRepository` 暴露：
  - `StateFlow<PlaybackState>`
  - `StateFlow<QueueState>`
  - `StateFlow<LyricState>`
  - `play`、`pause`、`seek`、`next`、`previous`、`setQueue` 等动作
- Compose 播放相关组件：
  - 迷你播放器
  - 全屏播放器
  - 播放列表弹层
  - 歌词视图
- Java 兼容门面，用于旧页面过渡。

### 性能目标

- 不在 UI 敏感路径同步准备网络媒体。
- 进度 UI 以合理刷新频率更新，不再 16ms 全局扇出。
- 封面图加载避免通知栏/小组件/播放器重复拉取。
- 播放状态只有一个可信来源。

### 冒烟验收

- 播放在线歌曲。
- 播放本地/已下载歌曲。
- 暂停/继续。
- 拖动进度。
- 上一首/下一首。
- 后台通知控制可用。
- 歌词/进度更新无明显卡顿。

## 2. 聊天 IM 链路

### 当前入口

- `app/src/main/java/com/ixuea/courses/mymusic/AppContext.java`
- `app/src/main/java/com/ixuea/courses/mymusic/component/conversation/activity/ConversationActivity.java`
- `app/src/main/java/com/ixuea/courses/mymusic/component/chat/activity/ChatActivity.java`
- `app/src/main/java/com/ixuea/courses/mymusic/component/chat/adapter/ChatAdapter.java`
- `app/src/main/java/com/ixuea/courses/mymusic/component/push/receiver/RongPushReceiver.java`

### 要移除的问题

- 融云 SDK callback 通过 Application/global state/EventBus 串起来。
- 历史消息加载、排序、列表修改都在 Activity。
- 基础 Adapter 倾向全量刷新。
- 图片压缩和图片消息发送塞在 Activity。

### 目标结构

- `ChatClient` 包装融云 SDK。
- SDK callback 暴露成 `callbackFlow`。
- `ConversationRepository` 和 `MessageRepository` 管数据。
- Compose 页面：
  - 会话列表
  - 聊天详情
  - 输入栏
  - 图片消息预览/进度行
- 历史消息优先用 Paging 3；如果 SDK 限制明显，则使用 Repository 管理的向上分页和稳定 ID。
- 推送/通知点击统一走新的聊天导航协议。

### 性能目标

- 消息列表使用稳定 key。
- 加载旧消息时只 prepend，不重建整表。
- 图片压缩/发送链路 main-safe。
- 未读数量只有一条状态流，不再到处分发 EventBus。

### 冒烟验收

- 会话列表能打开。
- 从会话列表进入聊天。
- 从用户详情“发送消息”进入聊天。
- 从推送/通知 intent 进入聊天。
- 历史消息能继续加载。
- 文本能发送。
- 图片发送链路能进入并反馈进度/错误。

## 3. 动态图片压缩/上传链路

### 当前入口

- `app/src/main/java/com/ixuea/courses/mymusic/component/feed/activity/PublishFeedActivity.java`
- `app/src/main/java/com/ixuea/courses/mymusic/component/feed/task/UploadFeedImageAsyncTask.java`
- `app/src/main/java/com/ixuea/courses/mymusic/util/ImageCompressor.java`
- `app/src/main/java/com/ixuea/courses/mymusic/component/feed/adapter/FeedAdapter.java`

### 要移除的问题

- 仍有 `AsyncTask` 遗留。
- 压缩/上传编排过度依赖 Activity。
- Multipart 创建和文件处理在页面代码里太重。
- 失败、重试、进度状态弱。

### 目标结构

- `FeedPublishViewModel` 管理已选图片和发布状态。
- `ImageCompressionRepository` 提供 main-safe 的 suspend 压缩。
- `FeedUploadRepository` 提供上传结果/进度状态。
- 如果发布/上传需要离开页面继续执行，再使用 WorkManager。
- Compose 发布媒体选择/预览区域可以先嵌入旧 Activity。

### 性能目标

- 压缩和文件 IO 移到主线程外。
- 限制并发压缩/上传数量。
- 预览和压缩避免加载原图全尺寸 Bitmap。
- 同一文件不重复压缩。

### 冒烟验收

- 能多图选择。
- 能预览已选图片。
- 能压缩图片。
- 能上传图片。
- 能带上传结果发布动态。

## 4. 下载进度刷新链路

### 当前入口

- `app/src/main/java/com/ixuea/courses/mymusic/component/download/fragment/DownloadingFragment.java`
- `app/src/main/java/com/ixuea/courses/mymusic/component/download/fragment/DownloadedFragment.java`
- `app/src/main/java/com/ixuea/courses/mymusic/component/download/adapter/DownloadingAdapter.java`
- `app/src/main/java/com/ixuea/courses/mymusic/component/download/listener/MyDownloadListener.java`
- `app/src/main/java/com/ixuea/courses/mymusic/AppContext.java`

### 要移除的问题

- 下载监听把大量事件折叠成宽泛刷新。
- 进度变化可能导致行级或列表级过度刷新。
- UI 直接访问全局 DownloadManager。

### 目标结构

- `DownloadRepository` 包装现有下载 SDK/Manager。
- `StateFlow<List<DownloadItemUiState>>` 表示下载中列表。
- `StateFlow<List<DownloadedItemUiState>>` 表示已下载列表。
- Compose 下载列表使用稳定 key 和行级进度状态。
- 现有 Activity/Fragment 入口先保留，可直接承载 Compose。

### 性能目标

- 下载进度按 UI 安全频率节流。
- 只更新进度/状态变化的行。
- 暂停、继续、删除命令 main-safe 且幂等。

### 冒烟验收

- 下载中列表能打开。
- 已下载列表能打开。
- 单项暂停/继续可用。
- 全部暂停/继续可用。
- 删除可用。
- 进度变化不明显重刷无关行。

## 5. 首页/发现/信息流列表刷新链路

### 当前入口

- `app/src/main/java/com/ixuea/courses/mymusic/component/discovery/fragment/DiscoveryFragment.java`
- `app/src/main/java/com/ixuea/courses/mymusic/component/discovery/adapter/DiscoveryAdapter.java`
- `app/src/main/java/com/ixuea/courses/mymusic/component/feed/fragment/FeedFragment.java`
- `app/src/main/java/com/ixuea/courses/mymusic/component/feed/adapter/FeedAdapter.java`
- `app/src/main/java/com/ixuea/courses/mymusic/component/comment/activity/CommentActivity.java`

### 要移除的问题

- 发现页通过多次请求和可变 `datum` 拼混合列表。
- 嵌套 RecyclerView 增加滚动复杂度。
- 共享基础 Adapter 倾向全量刷新。
- Feed 图片网格在 bind 阶段临时构建列表。

### 目标结构

- `DiscoveryViewModel` 暴露 typed immutable section list。
- `FeedViewModel` 暴露 feed 分页/列表状态。
- Compose list 使用稳定 section/item key。
- 如果后端分页可用，Feed 使用 Paging 3。
- 旧详情页冻结，但点击路由保持可用。

### 性能目标

- 小数据变化不重建所有 section。
- 稳定 key 保留嵌套横向列表状态。
- 图片按行/卡片尺寸加载。
- loading/error/empty 状态和列表数据分离。

### 冒烟验收

- 发现页能打开。
- Banner/模块/列表项能渲染。
- 信息流能打开。
- 信息流能滚动。
- 图片预览路由可用。
- 歌单、歌曲、用户、详情等旧点击路由仍能进入。

