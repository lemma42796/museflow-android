package com.ixuea.courses.mymusic

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ixuea.courses.mymusic.component.ad.model.Ad

/**
 * Public slim launcher. Selected feature Activities remain available from
 * source; frozen product areas are removed from this branch.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = TextView(this).apply {
            text = "MuseFlow Android public slim build"
            setPadding(48, 96, 48, 48)
        }
        setContentView(view)
    }

    @Suppress("UNUSED_PARAMETER")
    fun processAdClick(_data: Ad?) {
        // Ads are not part of the public slim feature set.
    }
}
