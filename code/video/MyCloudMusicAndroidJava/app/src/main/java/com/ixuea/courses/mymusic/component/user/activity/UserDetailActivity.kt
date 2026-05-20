package com.ixuea.courses.mymusic.component.user.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class UserDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finish()
    }

    companion object {
        @JvmStatic
        fun startWithId(context: Context, id: String?) {
            val intent = Intent(context, UserDetailActivity::class.java).apply {
                putExtra("id", id)
            }
            context.startActivity(intent)
        }

        @JvmStatic
        fun startWithNickname(context: Context, nickname: String?) {
            val intent = Intent(context, UserDetailActivity::class.java).apply {
                putExtra("nickname", nickname)
            }
            context.startActivity(intent)
        }
    }
}
