# 性能稳定性治理计划

> 日期：2026-05-24
> 目的：把“Media3 播放系统 + 性能稳定性治理”从简历口径落成可执行工程主线，避免把尚未完成的性能体系包装成已完成成果。

## 当前结论

已完成的真实基础：

- 播放核心已经基于 Media3 ExoPlayer、MediaSessionService、MediaSession 和系统媒体通知运行。
- 播放状态、队列状态和歌词状态已经通过 `PlaybackRepository` 暴露为 `StateFlow`。
- 首页、播放器、下载、聊天、动态、评论、歌单、本地音乐等核心页面已经使用 Compose 或 Compose 壳承载。
- 部分列表已经使用稳定 key；播放器背景已经按 API 31+ `RenderEffect` 和低版本 Glide blur fallback 做过分层处理。
- 2026-05-24 已新增 `:macrobenchmark` 模块、`benchmark` build type、启动/首页滚动/首页进播放器 Macrobenchmark 用例和 Baseline Profile 生成用例。
- 2026-05-24 已在 API 36 模拟器上跑通第一轮性能基线和 Baseline Profile 生成，具体数据见下方“第一轮基线记录”。
- 2026-05-24 已在 Redmi `25060RK16C` 真机上取得有效冷启动基线；此前 no-input “打开播放器”结果只证明触发过 action/包仍可见，未证明播放器页面可见，已更正为无效记录。
- 2026-05-24 已给播放器 Compose 根节点补稳定 UI marker，并新增 benchmark-only 入口显式准备播放队列后打开真实 `MusicPlayerActivity`；marker 版 no-input 播放器 benchmark 和 Baseline Profile 生成已在 Redmi 真机通过。
- 2026-05-24 已将 marker 版 Baseline Profile 固化到 `app/src/main/baseline-prof.txt`，过滤 benchmark-only profile 条目后由 `devBenchmark` 构建打包进 `assets/dexopt/baseline.prof`/`baseline.profm`，并完成同设备 no-input benchmark 前后对比。
- 2026-05-24 已继续扩展 app-internal/no-input benchmark hook：用 benchmark-only receiver、静音本地 WAV 和真实 `PlaybackService` 控制链路覆盖播放/暂停/恢复/seek，并以 1 次迭代覆盖歌词面板显示 marker。
- 2026-05-24 已重新验证真机输入注入：`adb shell input keyevent HOME` 和 `adb shell input swipe ...` 均成功；原始输入型 `StartupAndPlayerBenchmark` 已恢复可跑，冷启动、首页点歌进播放器和修复后的首页滚动均有同设备通过证据。
- 2026-05-24 已新增下载列表刷新 no-input benchmark：benchmark-only 内存下载 manager 预置 18 条下载中任务，通过 broadcast 推进进度并触发行级刷新；`DownloadListNoInputBenchmark.downloadingListRefreshNoInput` 已在 Redmi 真机通过。
- 2026-05-25 已新增歌词拖拽 benchmark：benchmark-only 播放器 fixture 改为 60 秒静音 WAV 和 60 行歌词，歌词列表补稳定 resource id，`PlaybackLyricDragBenchmark.playbackLyricDragSmoke` 与 3 次迭代 `playbackLyricDrag` 均在 Redmi 真机通过。
- 2026-05-25 已对 marker 版播放器首屏做 Perfetto trace 分析，并做一处默认封面路径局部优化：API 31+ 已使用平台 `RenderEffect` 时，空封面路径不再额外走 Glide 加载同一张 `default_cover`；两轮同设备复测显示 P50/P90/P95 改善，P99 与基线同级但仍受首帧 traversal/relayout 尾帧影响。
- 2026-05-25 已给播放器首屏补 `MFP.*` 自定义 trace section，并在同设备 benchmark trace 中验证可见：`RecordPageView` 创建约 1.37 ms，默认封面 fast path 约 0.001 ms，当前可确认业务初始化切片不是首帧尾部主要来源。
- 2026-05-25 已给下载列表刷新补 `DLP.*` 自定义 trace section，并在同设备 benchmark trace 中验证可见：行 listener refresh、整行重组、状态区、文件大小格式化和进度比例计算均可拆分观察；未保留“把 refreshTick 下沉到状态区”的试验，因为正式帧指标变差。

尚未完成的部分：

- 真机已有有效冷启动基线、marker 版 no-input 播放器首屏基线和原始输入型首页滚动/首页点歌证据；修复后完整原始输入型 3 用例套件已全绿，仍需继续扩展更多交互型场景。
- 播放页播放/暂停/恢复/seek 已有 no-input 真机基线；歌词面板已完成 1 次覆盖验证，下载列表刷新和播放器歌词拖拽也已有初始真机基线，歌词 KSC 逐字 + 拖拽 + seek 联动已补入口；持续播放组合和真实歌曲长时间交互仍未覆盖。
- 已有 marker/no-input 路径上的 Baseline Profile 固化前后对比，以及播放器首屏默认封面路径的一处局部优化前后复测；但尚未用更多交互型场景和优化前后多轮数据证明首页滚动、播放控制、歌词拖拽或下载列表刷新有稳定改善。

因此当前可以对外描述为“已建设 Macrobenchmark + Baseline Profile 性能验证链路，完成模拟器基线、真机冷启动基线、可信播放器首屏基线、原始输入型首页滚动/首页点歌证据、播放控制 no-input 基线、下载列表刷新 no-input 基线、歌词拖拽基线，并固化了 marker 版 Baseline Profile 取得同设备前后对比，已开始用 Perfetto 和自定义 trace section 做播放器首屏、下载刷新局部优化复测”，但仍不能描述为“已完成系统化性能治理”。

## 目标定位

这条主线服务两个目标：

- 产品目标：让音乐播放、首页推荐流、播放器页、下载进度列表和歌词交互在真实设备上稳定、流畅、可复测。
- 技术表达目标：形成能被追问的硬技术闭环，包括性能基线、可解释优化点、复测命令和结果记录。

不做纯炫技 UI，不为了堆名词引入和音乐 App 无关的技术。

## 第一阶段：性能基线

范围：

- 冷启动到 `MainActivity` 首屏。
- 首页推荐流加载后滚动。
- 从首页点歌进入 `MusicPlayerActivity`。
- 播放器页播放/暂停、seek、切到歌词页。
- 下载管理页双 tab 切换和下载中列表刷新。

交付物：

- 新增 `macrobenchmark` 或等价 benchmark 模块。
- 新增 Baseline Profile 生成用例。
- 记录首轮基线数据到本文档或独立报告。

验收：

- benchmark 任务能在本地设备或模拟器执行。
- 每个场景至少有一次可复现结果。
- 文档记录设备型号、系统版本、构建 variant、命令和关键指标。

## 第一轮基线记录

日期：2026-05-24

环境：

- 设备：`emulator-5554`
- Gradle 设备名：`Medium_Phone_API_36.1(AVD) - 16`
- 系统：Android 16 / API 36
- 模型：`sdk_gphone64_arm64`
- CPU：1 core，最高频率 2000 MHz
- 内存：约 2 GB
- 构建：`:app` 的 `devBenchmark`，`:macrobenchmark` 的 `devBenchmark`
- 说明：Benchmark 输出明确提示模拟器结果不代表真实用户设备，只能作为第一轮工程基线。

已跑通命令：

```bash
./gradlew :macrobenchmark:compileDevBenchmarkKotlin
ANDROID_SERIAL=emulator-5554 ./gradlew :macrobenchmark:connectedDevBenchmarkAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.ixuea.courses.mymusic.macrobenchmark.StartupAndPlayerBenchmark
ANDROID_SERIAL=emulator-5554 ./gradlew :macrobenchmark:connectedDevBenchmarkAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.ixuea.courses.mymusic.macrobenchmark.BaselineProfileGenerator
```

结果：

- `:macrobenchmark:compileDevBenchmarkKotlin` 通过。
- `StartupAndPlayerBenchmark` 3 个用例全部通过，耗时约 4m57s。
- `BaselineProfileGenerator.generate` 通过，耗时约 4m23s。
- Baseline Profile 临时产物运行时生成在 `macrobenchmark/build/outputs/connected_android_test_additional_output/devBenchmark/connected/Medium_Phone_API_36.1(AVD) - 16/BaselineProfileGenerator_generate-baseline-prof.txt`，本轮产物约 28,484 行；收尾时已清理 `macrobenchmark/build/` 生成物，下一步应在真机确认后再决定是否固化到 `app/src/main/baseline-prof.txt` 或 source set profile 目录。

关键指标：

| 场景 | 指标 | 第一轮结果 |
| --- | --- | --- |
| 冷启动 | `timeToInitialDisplayMs` | min 436.4 ms / median 524.3 ms / max 718.8 ms |
| 首页推荐流滚动 | `frameDurationCpuMs` | P50 2.9 ms / P90 5.8 ms / P95 7.6 ms / P99 16.5 ms |
| 首页推荐流滚动 | `frameOverrunMs` | P50 -11.5 ms / P90 -6.4 ms / P95 1.4 ms / P99 20.4 ms |
| 首页进播放器 | `frameDurationCpuMs` | P50 13.5 ms / P90 29.3 ms / P95 55.2 ms / P99 83.6 ms |
| 首页进播放器 | `frameOverrunMs` | P50 25.3 ms / P90 79.2 ms / P95 88.8 ms / P99 103.1 ms |

初步判断：

- 首页推荐流滚动已有较好的 CPU 帧耗时，但 P99 overrun 仍需在真机上确认。
- 首页进入播放器是当前最明显的性能热点，可能与播放器首屏 Compose 组合、背景图/高斯加载、黑胶 View 初始化、歌词 View 初始化和播放状态同步有关。
- 冷启动已有可复测数字，但只代表 API 36 模拟器的首轮基线；是否需要优化要等真机和 release-like 环境复测后判断。

## 第二轮真机冷启动与 no-input 纠偏记录

日期：2026-05-24

环境：

- 设备：`adb-5TJRHINFJJHMLVMJ-EYNgJg._adb-tls-connect._tcp`
- 型号：Redmi `25060RK16C`
- 系统：Android 16 / API 36
- 设备代号：`dali`
- 构建指纹：`Redmi/dali/dali:16/BP2A.250605.031.A3/OS3.0.301.0.WONCNXM:user/release-keys`
- CPU：8 cores，最高频率 3.73 GHz
- 内存：约 16 GB
- 构建：`:app` 的 `devBenchmark`，`:macrobenchmark` 的 `devBenchmark`

已跑命令：

```bash
./gradlew :macrobenchmark:compileDevBenchmarkKotlin
ANDROID_SERIAL=adb-5TJRHINFJJHMLVMJ-EYNgJg._adb-tls-connect._tcp ./gradlew :macrobenchmark:connectedDevBenchmarkAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.ixuea.courses.mymusic.macrobenchmark.StartupAndPlayerBenchmark
ANDROID_SERIAL=adb-5TJRHINFJJHMLVMJ-EYNgJg._adb-tls-connect._tcp ./gradlew :macrobenchmark:connectedDevBenchmarkAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.ixuea.courses.mymusic.macrobenchmark.StartupAndPlayerNoInputBenchmark
ANDROID_SERIAL=adb-5TJRHINFJJHMLVMJ-EYNgJg._adb-tls-connect._tcp ./gradlew :macrobenchmark:connectedDevBenchmarkAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.ixuea.courses.mymusic.macrobenchmark.BaselineProfileNoInputGenerator
```

结果：

- `:macrobenchmark:compileDevBenchmarkKotlin` 通过。
- 原始 `StartupAndPlayerBenchmark` 在真机上执行到 3 个用例中的冷启动，冷启动 `timeToInitialDisplayMs` 为 min 296.8 ms / median 320.1 ms / max 359.6 ms；首页滚动和首页点歌两个输入型用例失败，错误为 `SecurityException: Injecting input events requires ... INJECT_EVENTS permission`。
- 进一步验证 `adb shell input keyevent HOME` 也被同一真机系统策略拦截，因此为真机补充了无输入注入用例。
- `StartupAndPlayerNoInputBenchmark.coldStartupNoInput` 通过，得到有效真机冷启动数据。
- `StartupAndPlayerNoInputBenchmark.openPlayerViaMainActionNoInput` 当时通过，耗时约 1m28s，但后续复盘发现成功条件只等待 `TARGET_PACKAGE` 可见，未等待当前页面为 `MusicPlayerActivity`，也未等待播放器控件或稳定 UI marker，因此该用例结果不能作为“播放器页面已打开”或“播放器首屏性能”证据。
- `BaselineProfileNoInputGenerator.generateNoInput` 当时通过并生成约 21,418 行 profile，但它同样依赖上述不充分的播放器 action 判定；该产物只能作为临时误差记录，不能固化到 app source set。

关键指标：

| 场景 | 指标 | 真机 no-input 结果 |
| --- | --- | --- |
| 冷启动 | `timeToInitialDisplayMs` | min 299.6 ms / median 318.3 ms / max 326.4 ms |
| no-input 播放器 action | `frameDurationCpuMs` | 无效记录：P99 24.5 ms，但未证明播放器页面可见 |
| no-input 播放器 action | `frameOverrunMs` | 无效记录：P99 22.3 ms，但未证明播放器页面可见 |

初步判断：

- 真机冷启动明显好于模拟器首轮基线，当前不应把冷启动当作首要优化对象。
- 此轮没有形成可信的真机播放器页面首屏数据；之前的 no-input 播放器 action 指标不能用于判断播放器是否流畅。
- 当前设备安全策略会阻断 UiAutomator/adb input 的点击滚动用例；后续要么在真机开发者选项里允许 USB 输入模拟后复跑原始用例，要么继续扩展 app-internal/no-input benchmark hook，并必须等待 `MusicPlayerActivity` 可见或播放器 UI marker 出现，才能记录播放器相关指标。

## 第三轮播放器 UI marker 修正记录

日期：2026-05-24

已完成：

- `MusicPlayerScreen` 根节点新增稳定 `testTag` marker：`MuseFlowMusicPlayerScreen`，并通过 `testTagsAsResourceId` 暴露给 UiAutomator。
- 原始首页点击进播放器用例点击后也等待播放器 marker，不再接受只等待 `TARGET_PACKAGE` 可见的成功条件。
- 新增 benchmark build type 专用 `BenchmarkPlayerEntryActivity`，用于准备一条 benchmark 播放队列并打开真实 `MusicPlayerActivity`。
- `StartupAndPlayerNoInputBenchmark.openPlayerViaBenchmarkEntryNoInput` 和 `BaselineProfileNoInputGenerator.generateNoInput` 同步走 benchmark-only 入口并等待 marker，避免再次固化未进入播放器页的 profile。

验证：

```bash
./gradlew :app:compileDevBenchmarkKotlin :macrobenchmark:compileDevBenchmarkKotlin
ANDROID_SERIAL=adb-5TJRHINFJJHMLVMJ-EYNgJg._adb-tls-connect._tcp ./gradlew :macrobenchmark:connectedDevBenchmarkAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.ixuea.courses.mymusic.macrobenchmark.StartupAndPlayerNoInputBenchmark
ANDROID_SERIAL=adb-5TJRHINFJJHMLVMJ-EYNgJg._adb-tls-connect._tcp ./gradlew :macrobenchmark:connectedDevBenchmarkAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.ixuea.courses.mymusic.macrobenchmark.BaselineProfileNoInputGenerator
```

结果：

- 编译通过。
- `StartupAndPlayerNoInputBenchmark` 真机通过：2 个用例全部成功。
- `coldStartupNoInput`：`timeToInitialDisplayMs` min 307.7 ms / median 329.2 ms / max 382.5 ms。
- `openPlayerViaBenchmarkEntryNoInput`：`frameCount` median 37；`frameDurationCpuMs` P50 3.1 ms / P90 4.5 ms / P95 5.3 ms / P99 26.9 ms；`frameOverrunMs` P50 -3.5 ms / P90 -1.3 ms / P95 -0.2 ms / P99 27.1 ms。
- 手动 `uiautomator dump` 已确认当前播放器页存在 `resource-id="MuseFlowMusicPlayerScreen"`；手动 `am start -W -n com.ixuea.courses.mymusic/.benchmark.BenchmarkPlayerEntryActivity` 最终打开 `MusicPlayerActivity`，`TotalTime` 314 ms。
- `BaselineProfileNoInputGenerator.generateNoInput` 真机通过，生成 profile 23,799 行；产物位于 `macrobenchmark/build/outputs/connected_android_test_additional_output/devBenchmark/connected/25060RK16C - 16/BaselineProfileNoInputGenerator_generateNoInput-baseline-prof.txt`。

下一步：

- 已完成 marker 版 profile 固化、同设备前后对比和原始输入型首页滚动/首页点歌恢复验证；下一步转向下载列表刷新、歌词拖拽和更多优化前后对比。

## 第八轮 歌词拖拽 benchmark 记录

日期：2026-05-25

已完成：

- 新增 `PlaybackLyricDragBenchmark`，包含单迭代 `playbackLyricDragSmoke` 和 3 次迭代 `playbackLyricDrag` 两个用例。
- `BenchmarkPlayerFixture` 从 5 秒静音 WAV 改为 60 秒静音 WAV，并生成 60 行每秒一行的 KSC 逐字歌词；fixture 预填 `parsedLyric`，避免播放器打开后歌词列表为空，并覆盖 accurate lyric 高亮分支。
- `LyricListView` 补稳定 resource id：`lyric_list`、`lyric_drag_container`、`lyric_drag_play`，benchmark 通过播放器 benchmark-only 入口打开真实 `MusicPlayerActivity` 后切到歌词面板，再用真实坐标 swipe 覆盖歌词列表上下拖拽。
- 长时间卡住的早期尝试已定位为持续播放/进度刷新会干扰该拖拽用例收尾；当前歌词拖拽 benchmark 的 setup 不持续启动播放，只测已加载歌词列表的真实拖拽帧表现。

验证：

```bash
./gradlew :app:compileDevBenchmarkKotlin :macrobenchmark:compileDevBenchmarkKotlin
ANDROID_SERIAL=adb-5TJRHINFJJHMLVMJ-EYNgJg._adb-tls-connect._tcp ./gradlew :macrobenchmark:connectedDevBenchmarkAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.ixuea.courses.mymusic.macrobenchmark.PlaybackLyricDragBenchmark#playbackLyricDragSmoke
ANDROID_SERIAL=adb-5TJRHINFJJHMLVMJ-EYNgJg._adb-tls-connect._tcp ./gradlew :macrobenchmark:connectedDevBenchmarkAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.ixuea.courses.mymusic.macrobenchmark.PlaybackLyricDragBenchmark#playbackLyricDrag
```

结果：

- `:app:compileDevBenchmarkKotlin :macrobenchmark:compileDevBenchmarkKotlin` 通过。
- `playbackLyricDragSmoke` 真机通过：1 个用例成功，耗时约 54s；`frameCount` 388，`frameDurationCpuMs` P50 4.4 ms / P90 5.3 ms / P95 7.7 ms / P99 10.4 ms；`frameOverrunMs` P50 -2.9 ms / P90 -1.5 ms / P95 0.3 ms / P99 4.2 ms。
- `playbackLyricDrag` 真机通过：1 个用例成功，3 次迭代，耗时约 2m18s；`frameCount` median 956；`frameDurationCpuMs` P50 4.6 ms / P90 9.5 ms / P95 10.3 ms / P99 11.5 ms；`frameOverrunMs` P50 -3.6 ms / P90 1.6 ms / P95 2.6 ms / P99 4.3 ms。
- Perfetto trace 已生成在 `macrobenchmark/build/outputs/connected_android_test_additional_output/devBenchmark/connected/25060RK16C - 16/PlaybackLyricDragBenchmark_playbackLyricDrag_iter*.perfetto-trace`。

边界：

- 本轮最初覆盖的是 benchmark-only 静音 60 秒歌曲、已加载歌词列表、非持续播放状态下的播放器歌词列表拖拽帧表现；后续已补 KSC 逐字 + 拖拽 + seek 联动入口，但仍不等同于真实歌曲长时间播放和桌面歌词拖拽。
- 后续若优化歌词链路，可用当前 `playbackLyricDrag` 和 `playbackLyricDragSeekLinkage` 做优化前后对比。

## 第八轮补充 歌词 seek 联动入口

日期：2026-05-25

已完成：

- 歌词 benchmark fixture 切到 KSC 逐字歌词，保留 60 秒静音 WAV 和 60 行歌词，触发 `LyricListView` accurate lyric 高亮逻辑。
- `PlaybackLyricDragBenchmark.playbackLyricDragSeekLinkage` 新增歌词联动覆盖：打开真实播放器、切换歌词面板、执行多段 seek 和歌词拖拽。

验证：

```bash
./gradlew :app:compileDevBenchmarkKotlin :macrobenchmark:compileDevBenchmarkKotlin
ANDROID_SERIAL=adb-5TJRHINFJJHMLVMJ-EYNgJg._adb-tls-connect._tcp ./gradlew :macrobenchmark:connectedDevBenchmarkAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.ixuea.courses.mymusic.macrobenchmark.PlaybackLyricDragBenchmark#playbackLyricDragSeekLinkage
```

结果：

- `:app:compileDevBenchmarkKotlin :macrobenchmark:compileDevBenchmarkKotlin` 通过。
- `playbackLyricDragSmoke` 真机复跑通过，确认 KSC fixture 没有破坏原有歌词拖拽路径。
- `playbackLyricDragSeekLinkage` 真机通过：1 个用例成功，耗时约 1m02s；`frameCount` 293；`frameDurationCpuMs` P50 4.4 ms / P90 5.5 ms / P95 7.8 ms / P99 10.8 ms；`frameOverrunMs` P50 -4.1 ms / P90 -2.5 ms / P95 -0.5 ms / P99 4.5 ms。
- Perfetto trace 已生成在 `macrobenchmark/build/outputs/connected_android_test_additional_output/devBenchmark/connected/25060RK16C - 16/PlaybackLyricDragBenchmark_playbackLyricDragSeekLinkage_iter000_2026-05-25-11-26-32.perfetto-trace`。

边界：

- 新用例是 benchmark-only KSC fixture，目标是形成可复跑的歌词拖拽/seek 联动基线；持续播放组合真机收尾仍会卡住，真实歌曲、桌面歌词和长时间播放仍属于后续人工冒烟范围。

下一步：

- 转入真机基线、trace 分析和优化前后复测；优先看歌词拖拽/seek 联动、下载列表刷新、播放器首屏和首页进播放器的 Perfetto 热点。

## 第九轮 播放器首屏 trace 与默认封面优化记录

日期：2026-05-25

已完成：

- 复跑 `StartupAndPlayerNoInputBenchmark.openPlayerViaBenchmarkEntryNoInput`，取得同设备优化前基线。
- 使用设备侧 `trace_processor_shell` 查看播放器首屏 Perfetto trace；应用主线程最长切片集中在首帧 `Choreographer#doFrame`/`traversal` 和窗口 `relayoutWindow`，未发现某个自定义业务函数独占长耗时。
- `MusicPlayerActivity.loadBackground(...)` 增加空封面快速路径：当歌曲封面为空且 API 31+ 已可使用平台 `RenderEffect` 模糊时，直接复用背景 ImageView 默认的 `default_cover`，避免再通过 Glide 加载同一张默认封面并触发一次 `SwitchDrawableUtil` 切换；API 23-30 仍保留原 Glide blur fallback。

验证：

```bash
./gradlew :app:compileDevBenchmarkKotlin :macrobenchmark:compileDevBenchmarkKotlin
ANDROID_SERIAL=adb-5TJRHINFJJHMLVMJ-EYNgJg._adb-tls-connect._tcp ./gradlew :macrobenchmark:connectedDevBenchmarkAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.ixuea.courses.mymusic.macrobenchmark.StartupAndPlayerNoInputBenchmark#openPlayerViaBenchmarkEntryNoInput
```

结果：

| 场景 | 指标 | 结果 |
| --- | --- | --- |
| 优化前复跑 | `frameCount` | min 36 / median 37 / max 40 |
| 优化前复跑 | `frameDurationCpuMs` | P50 3.0 ms / P90 4.8 ms / P95 5.3 ms / P99 15.8 ms |
| 优化前复跑 | `frameOverrunMs` | P50 -3.4 ms / P90 -1.3 ms / P95 -0.5 ms / P99 15.4 ms |
| 优化后第 1 轮 | `frameCount` | min 36 / median 36 / max 38 |
| 优化后第 1 轮 | `frameDurationCpuMs` | P50 2.6 ms / P90 4.0 ms / P95 4.8 ms / P99 18.7 ms |
| 优化后第 1 轮 | `frameOverrunMs` | P50 -6.6 ms / P90 -4.7 ms / P95 -3.4 ms / P99 17.1 ms |
| 优化后第 2 轮 | `frameCount` | min 37 / median 38 / max 38 |
| 优化后第 2 轮 | `frameDurationCpuMs` | P50 2.7 ms / P90 4.1 ms / P95 4.9 ms / P99 17.1 ms |
| 优化后第 2 轮 | `frameOverrunMs` | P50 -6.6 ms / P90 -5.0 ms / P95 -3.4 ms / P99 15.4 ms |

Trace 摘要：

- 优化后 median trace 的应用主线程首帧 `Choreographer#doFrame` 约 19.0 ms，其中 `traversal` 约 19.0 ms，`MusicPlayerActivity` 首次 `relayoutWindow` 约 7.6 ms。
- `performCreate:MusicPlayerActivity` 约 1.7 ms；当前 trace 不支持把首帧尾部归因到单个明确业务函数。

自定义 trace section 补充：

- `MusicPlayerActivity` 和 `MusicPlayerScreen` 已补 `MFP.*` trace section，覆盖 `onCreate`/`initViews`/`initDatum`/`onResume`、`showInitData`、背景绑定/加载、`RecordPageView`/`LyricListView` 的 `AndroidView` factory/update、record adapter 初始化和 setData。
- 补 section 后复跑播放器首屏 benchmark 通过：`frameCount` median 38；`frameDurationCpuMs` P50 2.6 ms / P90 4.0 ms / P95 4.7 ms / P99 17.4 ms；`frameOverrunMs` P50 -6.6 ms / P90 -4.7 ms / P95 -3.6 ms / P99 8.5 ms。
- median trace 中 `MFP.*` 已可被 trace processor 查询到；最大自定义切片为 `MFP.record.factory` 约 1.37 ms，其次 `MFP.onCreate.setContent` 约 1.01 ms，`MFP.initViews` 约 0.49 ms，`MFP.download.lookup` 约 0.15 ms，`MFP.background.defaultCoverFastPath` 约 0.001 ms。
- 同一 trace 的主线程最长切片仍是 `Choreographer#doFrame`/`traversal` 约 14.4 ms；自定义切片没有暴露新的业务级长耗时。

判断：

- 这处改动对典型帧有正向信号：两轮复测的 P50/P90/P95 均低于优化前。
- P99 仍不能宣传为稳定改善：第 1 轮优化后 P99 变差，第 2 轮回到与优化前同级；更合理的表述是“减少默认封面路径常规开销，但首屏尾部帧仍主要受 traversal/relayout 影响”。

下一步：

- 播放器首屏若继续优化，应转向减少首帧 layout/relayout 范围，例如拆轻首屏可见树、延后非首屏控件初始化或进一步检查 `RecordPageView`/ViewPager2 首帧布局，而不是继续猜背景加载。
- 也可以转向下载列表刷新或歌词持续播放场景做 Perfetto 拆解和优化前后对比。

## 第十轮 下载列表刷新 DLP trace 记录

日期：2026-05-25

已完成：

- `DownloadActivity`、`DownloadingViewModel` 和 `DownloadScreen` 补 `DLP.*` 自定义 trace section，覆盖 ViewModel 发布下载列表、下载中列表组合、标题查询、行组合、下载状态组合、listener refresh、文件大小格式化和进度比例计算。
- 复跑 `DownloadListNoInputBenchmark.downloadingListRefreshNoInput`，验证 `DLP.*` 切片可被 Perfetto trace processor 查询。
- 基于第一轮 DLP 观察，尝试把 `refreshTick` 从整行 `DownloadingTaskRow` 下沉到 `DownloadStatus`，避免标题/删除按钮跟随进度刷新重组；该试验正式帧指标变差，已回退，不作为保留优化。

验证：

```bash
./gradlew :app:compileDevBenchmarkKotlin :macrobenchmark:compileDevBenchmarkKotlin
ANDROID_SERIAL=adb-5TJRHINFJJHMLVMJ-EYNgJg._adb-tls-connect._tcp ./gradlew :macrobenchmark:connectedDevBenchmarkAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.ixuea.courses.mymusic.macrobenchmark.DownloadListNoInputBenchmark#downloadingListRefreshNoInput
```

结果：

| 场景 | 指标 | 结果 |
| --- | --- | --- |
| DLP 初始埋点 | `frameDurationCpuMs` | P50 14.6 ms / P90 16.5 ms / P95 16.6 ms / P99 16.8 ms |
| DLP 初始埋点 | `frameOverrunMs` | P50 7.4 ms / P90 8.6 ms / P95 8.6 ms / P99 8.7 ms |
| refreshTick 下沉试验 | `frameDurationCpuMs` | P50 15.1 ms / P90 18.4 ms / P95 20.2 ms / P99 21.7 ms |
| refreshTick 下沉试验 | `frameOverrunMs` | P50 7.1 ms / P90 9.2 ms / P95 9.5 ms / P99 9.8 ms |
| 最终保留版本 | `frameDurationCpuMs` | P50 11.9 ms / P90 16.4 ms / P95 17.1 ms / P99 17.7 ms |
| 最终保留版本 | `frameOverrunMs` | P50 7.3 ms / P90 9.5 ms / P95 10.2 ms / P99 10.8 ms |

Trace 摘要：

- DLP 初始埋点 median trace：`DLP.downloading.listenerRefresh` 24 次，max 0.842 ms / total 1.913 ms；`DLP.downloading.row` 24 次，max 0.716 ms / total 6.608 ms；`DLP.downloading.status` 24 次，max 0.446 ms / total 4.329 ms。
- refreshTick 下沉试验 median trace：`DLP.downloading.row` 不再出现在刷新阶段，但 `DLP.downloading.status` max 升到 0.981 ms，正式 benchmark P95/P99 变差，因此不保留。
- 最终保留版本 median trace：`DLP.downloading.listenerRefresh` 24 次，max 0.565 ms / total 1.275 ms；`DLP.downloading.row` 24 次，max 0.533 ms / total 5.178 ms；`DLP.downloading.status` 24 次，max 0.290 ms / total 3.394 ms；`DLP.downloading.formatStart` total 0.706 ms，`DLP.downloading.formatSize` total 0.236 ms，`DLP.downloading.progressFraction` total 0.481 ms。
- 同一最终 trace 的主线程最长切片仍是 `Choreographer#doFrame` 约 8.6 ms，`traversal` 约 4.9 ms，`draw-VRI[DownloadActivity]` 约 4.6 ms，`Recomposer:recompose` 约 3.5 ms，`AndroidOwner:measureAndLayout` 约 2.5 ms。

判断：

- 下载刷新业务回调和单行状态组合都不是明显独占瓶颈；剩余成本主要是 Compose recompose、measure/layout 和 draw。
- `refreshTick` 下沉没有带来正式指标改善，当前不要保留这类“看似更细粒度”的状态拆分作为优化结论。

下一步：

- 若继续优化下载列表，应围绕减少每次刷新触发的可见行数量、降低进度条 draw/animation 成本或批量进度节流做前后对比。
- 也可以先转向持续播放中的歌词拖拽、逐字高亮和 seek 联动场景。

## 第七轮 下载列表刷新 no-input benchmark 记录

日期：2026-05-24

已完成：

- 下载页 Compose 根节点和下载中/已下载列表补稳定 `testTag`，并允许 `DownloadActivity` 通过 intent extra 初始打开指定 tab。
- 新增 benchmark-only `BenchmarkDownloadFixture`，用内存 `DownloadManager` 预置 18 条下载中任务，避免依赖真实网络下载或写入真实下载 DB。
- 新增 benchmark-only `BenchmarkDownloadEntryActivity` 和 `BenchmarkDownloadActionReceiver`：入口负责准备下载任务并打开下载中 tab，receiver 负责推进进度、触发行级刷新，并在 benchmark 结束后清理测试下载行。
- 新增 `DownloadListNoInputBenchmark.downloadingListRefreshNoInput`，以 3 次迭代覆盖下载中列表的进度刷新路径。

验证：

```bash
./gradlew :app:compileDevBenchmarkKotlin :macrobenchmark:compileDevBenchmarkKotlin
ANDROID_SERIAL=adb-5TJRHINFJJHMLVMJ-EYNgJg._adb-tls-connect._tcp ./gradlew :macrobenchmark:connectedDevBenchmarkAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.ixuea.courses.mymusic.macrobenchmark.DownloadListNoInputBenchmark#downloadingListRefreshNoInput
```

结果：

- Kotlin 编译通过。
- `DownloadListNoInputBenchmark#downloadingListRefreshNoInput` 真机通过：1 个用例成功，耗时约 56s。
- `frameCount` median 3；`frameDurationCpuMs` P50 11.2 ms / P90 15.4 ms / P95 15.7 ms / P99 15.9 ms；`frameOverrunMs` P50 2.9 ms / P90 8.2 ms / P95 8.3 ms / P99 8.3 ms。
- Perfetto trace 已生成在 `macrobenchmark/build/outputs/connected_android_test_additional_output/devBenchmark/connected/25060RK16C - 16/DownloadListNoInputBenchmark_downloadingListRefreshNoInput_iter*.perfetto-trace`。

边界：

- 本轮是 no-input/app-internal 初始基线，覆盖的是 benchmark 内存 manager 下的 18 条下载中任务进度刷新；不等同于真实网络下载、暂停/继续/删除全链路人工冒烟。
- 用户观察到 benchmark 结束时页面像“没下载完就闪退”；复查 logcat/crash buffer 未发现 `com.ixuea.courses.mymusic` 的 `AndroidRuntime`/`FATAL EXCEPTION`，更像测试框架收尾杀进程。为避免干扰真实下载状态，fixture 已从真实下载 DB/manager 改成 benchmark-only 内存 `DownloadManager`。
- 后续若优化下载页，应围绕行级刷新、progress 状态持有和 `DownloadInfo` 可变对象读取路径做前后对比。

下一步：

- 继续补歌词拖拽 benchmark；下载列表刷新已经有初始可复测基线，后续转入优化前后对比。

## 第六轮 真机输入型 benchmark 恢复记录

日期：2026-05-24

已完成：

- 在用户确认真机已开启 USB 调试后，重新验证当前设备输入注入状态。
- `adb shell input keyevent HOME` 和 `adb shell input swipe 500 1500 500 500 300` 均成功，说明当前真机输入注入已放行。
- 复跑原始输入型 `StartupAndPlayerBenchmark`，冷启动和首页点歌进播放器通过；首页滚动不再报 `INJECT_EVENTS`，但暴露 `StaleObjectException`。
- 修复 `scrollHomeFeed()`：每次 fling 前重新获取滚动根节点，并对 `StaleObjectException` 做短重试，避免 Compose 滚动后复用失效 `UiObject2`。

验证：

```bash
ANDROID_SERIAL=adb-5TJRHINFJJHMLVMJ-EYNgJg._adb-tls-connect._tcp ./gradlew :macrobenchmark:connectedDevBenchmarkAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.ixuea.courses.mymusic.macrobenchmark.StartupAndPlayerBenchmark
ANDROID_SERIAL=adb-5TJRHINFJJHMLVMJ-EYNgJg._adb-tls-connect._tcp ./gradlew :macrobenchmark:connectedDevBenchmarkAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.ixuea.courses.mymusic.macrobenchmark.StartupAndPlayerBenchmark#homeFeedScroll
```

结果：

- 完整原始输入型套件复跑到真机：冷启动和 `openPlayerFromHome` 通过，`homeFeedScroll` 因 `StaleObjectException` 失败；失败已确认不是权限限制。
- 修复后单独复跑 `homeFeedScroll` 通过：1 个用例成功，耗时约 4m02s。
- 修复后继续复跑完整 `StartupAndPlayerBenchmark` 3 用例套件通过：3 个用例全部成功，耗时约 8m25s。
- 完整套件冷启动：`timeToInitialDisplayMs` min 299.4 ms / median 348.3 ms / max 415.7 ms。
- 完整套件首页滚动：`frameCount` median 352；`frameDurationCpuMs` P50 4.2 ms / P90 5.0 ms / P95 5.3 ms / P99 6.4 ms；`frameOverrunMs` P50 -4.9 ms / P90 -3.8 ms / P95 -3.3 ms / P99 -1.4 ms。
- 完整套件首页点歌进播放器：`frameCount` median 286；`frameDurationCpuMs` P50 6.9 ms / P90 9.3 ms / P95 10.2 ms / P99 11.7 ms；`frameOverrunMs` P50 -0.6 ms / P90 1.4 ms / P95 3.3 ms / P99 9.2 ms。

边界：

- `StaleObjectException` 修复属于 benchmark 脚本稳定性修复，不代表产品 UI 逻辑改动。

下一步：

- 继续补下载列表刷新和歌词拖拽场景，形成更完整的可复测性能证据。

## 第五轮 播放控制与歌词面板 no-input 扩展记录

日期：2026-05-24

已完成：

- 新增 benchmark-only `BenchmarkPlayerFixture`，准备真实 `PlaybackService` 播放队列、5 秒静音本地 WAV 和短歌词，避免依赖网络、真实音频文件或首页点击。
- 新增 benchmark-only `BenchmarkPlaybackActionReceiver`，支持 `play`、`pause`、`resume`、`seek`、`show_lyric` 命令。
- `BenchmarkPlayerEntryActivity` 改为复用同一 fixture；播放器歌词面板新增 `MuseFlowMusicPlayerLyricPanel` marker。
- 新增 `PlaybackControlsNoInputBenchmark`，拆为 3 次迭代的 `playbackTransportControlsNoInput` 主基线和 1 次迭代的 `playbackLyricPanelNoInput` 覆盖用例。

验证：

```bash
./gradlew :app:compileDevBenchmarkKotlin :macrobenchmark:compileDevBenchmarkKotlin
adb install -r -t app/build/outputs/apk/dev/benchmark/app-dev-benchmark.apk
ANDROID_SERIAL=adb-5TJRHINFJJHMLVMJ-EYNgJg._adb-tls-connect._tcp ./gradlew :macrobenchmark:connectedDevBenchmarkAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.ixuea.courses.mymusic.macrobenchmark.PlaybackControlsNoInputBenchmark#playbackTransportControlsNoInput
adb install -r -t app/build/outputs/apk/dev/benchmark/app-dev-benchmark.apk
ANDROID_SERIAL=adb-5TJRHINFJJHMLVMJ-EYNgJg._adb-tls-connect._tcp ./gradlew :macrobenchmark:connectedDevBenchmarkAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.ixuea.courses.mymusic.macrobenchmark.PlaybackControlsNoInputBenchmark#playbackLyricPanelNoInput
```

结果：

- Kotlin 编译通过。
- `playbackTransportControlsNoInput` 真机通过：1 个用例成功，耗时约 3m25s；`frameCount` median 504；`frameDurationCpuMs` P50 4.9 ms / P90 8.4 ms / P95 9.4 ms / P99 11.7 ms；`frameOverrunMs` P50 -1.1 ms / P90 1.0 ms / P95 3.1 ms / P99 8.0 ms。
- `playbackLyricPanelNoInput` 真机通过：1 个用例成功，耗时约 2m33s；`frameCount` 1092；`frameDurationCpuMs` P50 11.9 ms / P90 19.7 ms / P95 20.7 ms / P99 22.8 ms；`frameOverrunMs` P50 7.9 ms / P90 15.2 ms / P95 16.3 ms / P99 18.5 ms。

边界：

- 当前 MIUI 真机偶发出现 UTP 自动安装后目标包查不到的问题；成功运行前用 `adb install -r -t app/build/outputs/apk/dev/benchmark/app-dev-benchmark.apk` 手动重装目标 APK。
- Transport controls 是 3 次迭代的可复测主基线；歌词面板当前只有 1 次迭代，只证明 marker 与显示路径可被 no-input 覆盖，暂不作为稳定性能改善结论。
- 真实输入型首页滚动/点击已恢复验证；仍未覆盖歌词拖拽和下载列表刷新。

下一步：

- 继续补下载列表刷新和歌词拖拽场景。
- 若继续优化播放器，优先用 transport controls 和播放器首屏两条已通过路径做前后对比。

## 第四轮 Baseline Profile 固化与前后对比记录

日期：2026-05-24

已完成：

- 将 `BaselineProfileNoInputGenerator.generateNoInput` 生成的 marker 版 profile 固化到 `app/src/main/baseline-prof.txt`。
- 过滤掉 8 行仅存在于 `app/src/benchmark` 的 `BenchmarkPlayerEntryActivity` profile 条目，避免把 benchmark-only 入口写入 main source set；最终 source profile 为 23,791 行。
- `:app:mergeDevBenchmarkArtProfile` 和 `:app:compileDevBenchmarkArtProfile` 已确认消费该文件，合并后的 `devBenchmark` profile 为 27,237 行。
- `app-dev-benchmark.apk` 已确认包含 `assets/dexopt/baseline.prof` 和 `assets/dexopt/baseline.profm`。

验证：

```bash
./gradlew :app:compileDevBenchmarkArtProfile :app:mergeDevBenchmarkArtProfile
ANDROID_SERIAL=adb-5TJRHINFJJHMLVMJ-EYNgJg._adb-tls-connect._tcp ./gradlew :macrobenchmark:connectedDevBenchmarkAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.ixuea.courses.mymusic.macrobenchmark.StartupAndPlayerNoInputBenchmark
```

结果：

- Profile 编译/合并任务通过。
- `StartupAndPlayerNoInputBenchmark` 真机通过：2 个用例全部成功。
- 固化前 marker 版基线：冷启动 `timeToInitialDisplayMs` min 307.7 ms / median 329.2 ms / max 382.5 ms；播放器首屏 `frameDurationCpuMs` P99 26.9 ms，`frameOverrunMs` P99 27.1 ms。
- 固化后复测：冷启动 `timeToInitialDisplayMs` min 289.9 ms / median 302.5 ms / max 319.5 ms；播放器首屏 `frameCount` median 38；`frameDurationCpuMs` P50 3.1 ms / P90 4.6 ms / P95 5.0 ms / P99 18.7 ms；`frameOverrunMs` P50 -4.3 ms / P90 -1.6 ms / P95 -0.8 ms / P99 15.1 ms。

对比判断：

- 冷启动 median 从 329.2 ms 到 302.5 ms，约改善 8.1%。
- 播放器首屏 `frameDurationCpuMs` P99 从 26.9 ms 到 18.7 ms，约改善 30.5%。
- 播放器首屏 `frameOverrunMs` P99 从 27.1 ms 到 15.1 ms，约改善 44.3%。
- 该结论只覆盖 Redmi `25060RK16C`、`devBenchmark`、marker 版 no-input 路径和 5 次迭代；它证明 profile 固化链路有效并给出第一组收益证据，但不能替代完整交互型 benchmark 或人工播放链路冒烟。

下一步：

- 真机 `INJECT_EVENTS` 限制已复测放行；下载列表刷新已补 no-input 初始基线，继续补歌词拖拽和更多优化前后对比。
- 原始输入型 benchmark 已在第六轮恢复；app-internal/no-input hook 继续作为歌词拖拽等难注入场景的补充手段。

## 第二阶段：播放链路稳定性

范围：

- Audio focus 申请、丢失、恢复。
- 耳机拔出/蓝牙媒体键/系统通知控制。
- 前后台切换和进程重启后的播放状态恢复。
- 多歌曲队列、上一首/下一首、Widget 控制和桌面歌词开关。

交付物：

- 播放链路专项 smoke 脚本或手动 checklist。
- 播放错误、音频焦点变化和 MediaSession 控制路径的必要日志。
- 发现问题后优先修复真实状态不一致，而不是只补 UI 显示。

验收：

- MediaSession、播放器页、小播放条、通知栏和 Widget 的播放状态保持一致。
- 后台播放、通知控制、Widget 控制和歌词开关至少完成一轮设备端复测。

## 第三阶段：Compose UI 性能治理

范围：

- 首页推荐流：section key、横向列表 key、图片尺寸约束、滚动状态保持。
- 播放器页：播放进度刷新频率、歌词列表刷新范围、高斯背景加载成本。
- 小播放条：横向滑动、当前歌词行、播放进度和页面跳转。
- 下载页：下载进度刷新频率、行级状态更新、批量操作后的列表稳定性。

优化原则：

- 高频状态不要驱动整页重组。
- 列表必须有稳定 key。
- 图片按实际展示尺寸加载，避免原图级别 bitmap 进入 UI 热路径。
- 能用 `snapshotFlow`、`distinctUntilChanged`、节流或状态拆分解决的，不用全局刷新解决。

验收：

- benchmark 或设备端复测能覆盖优化前后的关键场景。
- 每次优化都记录“问题、改动、验证结果”，不只记录“感觉更流畅”。

## 第四阶段：证据沉淀

最终对外可写的口径必须来自代码和数据：

- 如果只有 Media3 和局部优化，只写“Media3 播放系统 + 局部性能优化”。
- 如果 benchmark 和 profile 已跑通，可以写“建设 Macrobenchmark + Baseline Profile 性能验证链路”。
- 如果优化前后有数据对比，才能写“降低启动耗时/减少 jank/改善帧耗时”。

推荐简历表述在完成第一、三阶段后再升级为：

> 基于 Media3 ExoPlayer + MediaSessionService 构建音乐播放系统，支持后台播放、系统媒体控制、队列状态和 Widget 联动；建设 Macrobenchmark + Baseline Profile 性能验证链路，围绕首页推荐流、播放器页、歌词页和下载列表进行稳定 key、状态拆分、高频状态节流和图片加载约束，降低复杂音乐 UI 场景下的卡顿风险。

## 下一步

优先级：

1. 围绕下载列表刷新、歌词拖拽和播放器首屏查看 Perfetto trace，优先拆解行级刷新、播放器首屏组合、背景图加载、歌词/黑胶初始化和状态同步成本。
2. 继续补持续播放中的歌词拖拽、逐字高亮和 seek 联动场景，避免把当前静音 fixture 的拖拽基线外推到完整歌词交互。
3. 基于已通过的下载列表刷新、歌词拖拽、播放器首屏、首页进播放器和 transport controls 场景做代码级优化，并用同一命令复测前后差异。
