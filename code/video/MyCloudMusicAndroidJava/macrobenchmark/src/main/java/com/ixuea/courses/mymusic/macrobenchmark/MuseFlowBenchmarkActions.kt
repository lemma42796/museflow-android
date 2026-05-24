package com.ixuea.courses.mymusic.macrobenchmark

import android.content.ComponentName
import android.content.Intent
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.StaleObjectException
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until

internal const val TARGET_PACKAGE = "com.ixuea.courses.mymusic"
private const val MAIN_ACTIVITY = "$TARGET_PACKAGE.MainActivity"
private const val BENCHMARK_PLAYER_ENTRY_ACTIVITY = "$TARGET_PACKAGE.benchmark.BenchmarkPlayerEntryActivity"
private const val BENCHMARK_PLAYBACK_ACTION_RECEIVER = "$TARGET_PACKAGE.benchmark.BenchmarkPlaybackActionReceiver"
private const val BENCHMARK_DOWNLOAD_ENTRY_ACTIVITY = "$TARGET_PACKAGE.benchmark.BenchmarkDownloadEntryActivity"
private const val BENCHMARK_DOWNLOAD_ACTION_RECEIVER = "$TARGET_PACKAGE.benchmark.BenchmarkDownloadActionReceiver"
private const val BENCHMARK_PLAYBACK_ACTION = "$TARGET_PACKAGE.benchmark.PLAYBACK_ACTION"
private const val BENCHMARK_DOWNLOAD_REFRESH_ACTION = "$TARGET_PACKAGE.benchmark.DOWNLOAD_REFRESH"
private const val BENCHMARK_DOWNLOAD_CLEANUP_ACTION = "$TARGET_PACKAGE.benchmark.DOWNLOAD_CLEANUP"
private const val EXTRA_COMMAND = "command"
private const val EXTRA_POSITION_MS = "position_ms"
private const val COMMAND_PLAY = "play"
private const val COMMAND_PAUSE = "pause"
private const val COMMAND_RESUME = "resume"
private const val COMMAND_SEEK = "seek"
private const val COMMAND_SHOW_LYRIC = "show_lyric"
private const val PLAYER_SCREEN_MARKER = "MuseFlowMusicPlayerScreen"
private const val PLAYER_LYRIC_MARKER = "MuseFlowMusicPlayerLyricPanel"
private const val DOWNLOAD_SCREEN_MARKER = "MuseFlowDownloadScreen"
private const val DOWNLOADING_LIST_MARKER = "MuseFlowDownloadingList"

internal fun MacrobenchmarkScope.waitForHome() {
    check(device.wait(Until.hasObject(By.pkg(TARGET_PACKAGE)), 5_000)) {
        "Target package $TARGET_PACKAGE did not appear."
    }
    device.waitForIdle()
}

internal fun MacrobenchmarkScope.scrollHomeFeed() {
    waitForHome()
    repeat(3) {
        flingHomeContent(Direction.DOWN)
    }
    repeat(2) {
        flingHomeContent(Direction.UP)
    }
}

internal fun MacrobenchmarkScope.openPlayerFromHome() {
    waitForHome()
    val candidate = device.wait(
        Until.findObject(By.textContains("Yesterday")),
        2_000,
    ) ?: device.wait(
        Until.findObject(By.textContains("播放")),
        2_000,
    )

    checkNotNull(candidate) {
        "Home song/player entry was not found."
    }.click()
    waitForPlayerScreen()
}

internal fun MacrobenchmarkScope.startHomeWithoutInput() {
    startActivityAndWait(
        Intent().setClassName(TARGET_PACKAGE, MAIN_ACTIVITY),
    )
    waitForHome()
}

internal fun MacrobenchmarkScope.openPlayerFromBenchmarkEntryWithoutInput() {
    startActivityAndWait(
        Intent().setClassName(TARGET_PACKAGE, BENCHMARK_PLAYER_ENTRY_ACTIVITY),
    )
    waitForPlayerScreen()
}

internal fun MacrobenchmarkScope.openDownloadListFromBenchmarkEntryWithoutInput() {
    startActivityAndWait(
        Intent().setClassName(TARGET_PACKAGE, BENCHMARK_DOWNLOAD_ENTRY_ACTIVITY),
    )
    waitForDownloadingList()
}

internal fun MacrobenchmarkScope.startBenchmarkPlaybackWithoutInput() {
    sendPlaybackCommand(COMMAND_PLAY)
    waitForPlayerMarker(800)
}

internal fun MacrobenchmarkScope.runPlaybackTransportControlsWithoutInput() {
    sendPlaybackCommand(COMMAND_PAUSE)
    waitForPlayerWork()
    sendPlaybackCommand(COMMAND_RESUME)
    waitForPlayerWork()
    sendPlaybackCommand(COMMAND_SEEK, positionMs = 1_500)
    waitForPlayerWork()
    sendPlaybackCommand(COMMAND_PAUSE)
    waitForPlayerWork()
}

internal fun MacrobenchmarkScope.runPlaybackLyricPanelWithoutInput() {
    sendPlaybackCommand(COMMAND_SHOW_LYRIC)
    waitForLyricPanel()
    sendPlaybackCommand(COMMAND_SEEK, positionMs = 3_200)
    waitForPlayerWork()
}

internal fun MacrobenchmarkScope.runDownloadListRefreshWithoutInput() {
    repeat(3) {
        sendDownloadRefreshCommand()
        waitForDownloadListWork()
    }
}

internal fun cleanupDownloadBenchmarkData() {
    sendBenchmarkDownloadAction(BENCHMARK_DOWNLOAD_CLEANUP_ACTION)
    Thread.sleep(500)
}

internal fun MacrobenchmarkScope.waitForPlayerScreen() {
    check(device.wait(Until.hasObject(By.res(PLAYER_SCREEN_MARKER)), 5_000)) {
        "Music player screen marker did not appear."
    }
    device.waitForIdle()
}

private fun MacrobenchmarkScope.waitForLyricPanel() {
    check(device.wait(Until.hasObject(By.res(PLAYER_LYRIC_MARKER)), 5_000)) {
        "Music player lyric marker did not appear."
    }
    waitForPlayerMarker()
}

private fun MacrobenchmarkScope.waitForDownloadingList() {
    check(device.wait(Until.hasObject(By.res(DOWNLOAD_SCREEN_MARKER)), 5_000)) {
        "Download screen marker did not appear."
    }
    check(device.wait(Until.hasObject(By.res(DOWNLOADING_LIST_MARKER)), 5_000)) {
        "Downloading list marker did not appear."
    }
    device.waitForIdle()
}

private fun MacrobenchmarkScope.waitForDownloadListWork(delayMs: Long = 350) {
    Thread.sleep(delayMs)
    check(device.wait(Until.hasObject(By.res(DOWNLOADING_LIST_MARKER)), 2_000)) {
        "Downloading list marker did not appear."
    }
}

private fun MacrobenchmarkScope.waitForPlayerWork(delayMs: Long = 300) {
    Thread.sleep(delayMs)
    waitForPlayerMarker()
}

private fun MacrobenchmarkScope.waitForPlayerMarker(delayMs: Long = 300) {
    Thread.sleep(delayMs)
    check(device.wait(Until.hasObject(By.res(PLAYER_SCREEN_MARKER)), 2_000)) {
        "Music player screen marker did not appear."
    }
}

private fun sendPlaybackCommand(command: String, positionMs: Int? = null) {
    val context = InstrumentationRegistry.getInstrumentation().context
    val intent = Intent(BENCHMARK_PLAYBACK_ACTION)
        .setComponent(ComponentName(TARGET_PACKAGE, BENCHMARK_PLAYBACK_ACTION_RECEIVER))
        .putExtra(EXTRA_COMMAND, command)
    if (positionMs != null) {
        intent.putExtra(EXTRA_POSITION_MS, positionMs)
    }
    context.sendBroadcast(intent)
}

private fun sendDownloadRefreshCommand() {
    sendBenchmarkDownloadAction(BENCHMARK_DOWNLOAD_REFRESH_ACTION)
}

private fun sendBenchmarkDownloadAction(action: String) {
    val context = InstrumentationRegistry.getInstrumentation().context
    val intent = Intent(action)
        .setComponent(ComponentName(TARGET_PACKAGE, BENCHMARK_DOWNLOAD_ACTION_RECEIVER))
    context.sendBroadcast(intent)
}

private fun UiDevice.findScrollableRoot(): UiObject2? {
    return findObject(By.scrollable(true))
        ?: findObject(By.res(TARGET_PACKAGE, "content"))
        ?: findObject(By.pkg(TARGET_PACKAGE))
}

private fun MacrobenchmarkScope.flingHomeContent(direction: Direction) {
    var staleNode: StaleObjectException? = null
    repeat(3) {
        val root = checkNotNull(device.findScrollableRoot()) {
            "Scrollable home content was not found."
        }
        try {
            root.fling(direction)
            device.waitForIdle()
            return
        } catch (error: StaleObjectException) {
            staleNode = error
            device.waitForIdle()
        }
    }
    throw staleNode ?: IllegalStateException("Scrollable home content became unavailable.")
}
