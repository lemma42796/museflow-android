package com.ixuea.courses.mymusic.macrobenchmark

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class BaselineProfileNoInputGenerator {
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generateNoInput() = baselineProfileRule.collect(
        packageName = TARGET_PACKAGE,
    ) {
        startHomeWithoutInput()
        openPlayerFromBenchmarkEntryWithoutInput()
    }
}
