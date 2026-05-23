package com.ixuea.courses.mymusic.component.location.activity

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class PreviewLocationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finish()
    }

    companion object {
        @JvmStatic
        @Suppress("UNUSED_PARAMETER")
        fun start(context: Context, data: Any?) {
            // Location preview is intentionally stubbed in the public slim branch.
        }
    }
}
