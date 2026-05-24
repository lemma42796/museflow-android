package com.ixuea.courses.mymusic.macrobenchmark

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until

internal const val TARGET_PACKAGE = "com.ixuea.courses.mymusic"

internal fun MacrobenchmarkScope.waitForHome() {
    check(device.wait(Until.hasObject(By.pkg(TARGET_PACKAGE)), 5_000)) {
        "Target package $TARGET_PACKAGE did not appear."
    }
    device.waitForIdle()
}

internal fun MacrobenchmarkScope.scrollHomeFeed() {
    waitForHome()
    val root = checkNotNull(device.findScrollableRoot()) {
        "Scrollable home content was not found."
    }
    repeat(3) {
        root.fling(Direction.DOWN)
        device.waitForIdle()
    }
    repeat(2) {
        root.fling(Direction.UP)
        device.waitForIdle()
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
    device.waitForIdle()
    device.wait(Until.hasObject(By.pkg(TARGET_PACKAGE)), 3_000)
}

private fun UiDevice.findScrollableRoot(): UiObject2? {
    return findObject(By.scrollable(true))
        ?: findObject(By.res(TARGET_PACKAGE, "content"))
        ?: findObject(By.pkg(TARGET_PACKAGE))
}
