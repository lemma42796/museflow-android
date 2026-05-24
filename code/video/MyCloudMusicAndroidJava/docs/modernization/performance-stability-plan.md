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

尚未完成的部分：

- 真机已有有效冷启动基线和 marker 版 no-input 播放器首屏基线，但原始滚动/点击版用例被当前真机系统的 `INJECT_EVENTS` 权限限制拦截，尚未形成完整交互型真机基线。
- 播放页播放/暂停/恢复/seek 已有 no-input 真机基线；歌词面板已完成 1 次覆盖验证，但歌词拖拽、下载列表刷新等更细场景尚未覆盖。
- 已有 marker/no-input 路径上的 Baseline Profile 固化前后对比，但尚未用更多交互型场景和优化前后多轮数据证明首页滚动、播放控制、歌词拖拽或下载列表刷新有稳定改善。

因此当前可以对外描述为“已建设 Macrobenchmark + Baseline Profile 性能验证链路，完成模拟器基线、真机冷启动基线、可信播放器首屏基线、播放控制 no-input 基线，并固化了 marker 版 Baseline Profile 取得同设备前后对比”，但仍不能描述为“已完成系统化性能治理”。

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

- 已完成 marker 版 profile 固化和同设备前后对比；下一步转向补齐原始输入型或 app-internal/no-input 扩展场景。
- 真机原始输入型首页滚动/首页点歌仍被 `INJECT_EVENTS` 限制，后续继续作为设备策略问题单独处理。

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
- 仍未覆盖真实输入型首页滚动/点击、歌词拖拽和下载列表刷新。

下一步：

- 继续补下载列表刷新 no-input 场景，或在设备策略允许后复跑原始输入型 benchmark。
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

- 继续处理真机 `INJECT_EVENTS` 限制，争取复跑原始输入型首页滚动/首页点歌 benchmark。
- 若设备策略仍不放行，继续扩展 app-internal/no-input benchmark hook，覆盖播放/暂停、seek、歌词页拖拽和下载列表刷新。

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

1. 处理当前真机 `INJECT_EVENTS` 限制：优先复跑原始输入型 benchmark；如设备仍不放行，就继续扩展 no-input/app-internal 替代场景。
2. 扩展下载列表刷新和歌词拖拽场景，形成更完整的可复测性能证据。
3. 围绕“播放器首屏”查看 Perfetto trace，优先拆解播放器首屏组合、背景图加载、歌词/黑胶初始化和状态同步成本。
4. 基于已通过的播放器首屏和 transport controls 场景做代码级优化，并用同一命令复测前后差异。
