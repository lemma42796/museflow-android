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

尚未完成的部分：

- 尚未在真机上形成可代表用户设备的性能基线。
- Baseline Profile 目前只是 generator 跑通并生成临时产物，尚未把真机确认后的 profile 固化到 app source set。
- 尚未覆盖播放页播放/暂停、seek、歌词页拖拽、下载列表刷新等更细场景。
- 尚未用优化前后对比数据证明 jank、启动耗时、帧耗时或核心页面首帧有改善。

因此当前可以对外描述为“已建设 Macrobenchmark + Baseline Profile 性能验证链路并完成第一轮模拟器基线”，但仍不能描述为“已完成系统化性能治理”。

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

1. 在真机上复跑 `StartupAndPlayerBenchmark` 和 `BaselineProfileGenerator`，确认模拟器数据是否有代表性。
2. 围绕“首页进播放器”查看 Perfetto trace，优先拆解播放器首屏组合、背景图加载、歌词/黑胶初始化和状态同步成本。
3. 只在真机确认后固化 Baseline Profile 到 app source set，并复测 profile 前后启动/进播放器数据。
4. 扩展播放页播放/暂停、seek、歌词页拖拽和下载列表刷新场景，再做代码级优化。
