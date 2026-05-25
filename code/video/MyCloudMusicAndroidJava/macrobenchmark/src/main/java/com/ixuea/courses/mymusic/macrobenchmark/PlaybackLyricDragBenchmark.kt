package com.ixuea.courses.mymusic.macrobenchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class PlaybackLyricDragBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun playbackLyricDragSmoke() = benchmarkRule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(FrameTimingMetric()),
        compilationMode = CompilationMode.Partial(),
        startupMode = StartupMode.WARM,
        iterations = 1,
        setupBlock = {
            startHomeWithoutInput()
            openPlayerFromBenchmarkEntryWithoutInput()
            showPlaybackLyricPanelWithoutInput()
        },
    ) {
        runPlaybackLyricDragSmoke()
    }

    @Test
    fun playbackLyricDrag() = benchmarkRule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(FrameTimingMetric()),
        compilationMode = CompilationMode.Partial(),
        startupMode = StartupMode.WARM,
        iterations = 3,
        setupBlock = {
            startHomeWithoutInput()
            openPlayerFromBenchmarkEntryWithoutInput()
            showPlaybackLyricPanelWithoutInput()
        },
    ) {
        runPlaybackLyricDrag()
    }
}
