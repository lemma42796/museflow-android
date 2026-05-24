package com.ixuea.courses.mymusic.benchmark

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.ixuea.courses.mymusic.component.player.activity.MusicPlayerActivity

class BenchmarkPlayerEntryActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BenchmarkPlayerFixture.seedPlayerQueue(applicationContext)
        startActivity(Intent(this, MusicPlayerActivity::class.java))
        finish()
    }
}
