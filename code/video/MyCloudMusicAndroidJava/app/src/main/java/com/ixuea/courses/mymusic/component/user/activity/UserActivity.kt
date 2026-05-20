package com.ixuea.courses.mymusic.component.user.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ixuea.courses.mymusic.util.Constant

class UserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finish()
    }

    companion object {
        @JvmStatic
        fun start(context: Context, style: Int) {
            val intent = Intent(context, UserActivity::class.java).apply {
                putExtra(Constant.STYLE, style)
            }
            context.startActivity(intent)
        }
    }
}
