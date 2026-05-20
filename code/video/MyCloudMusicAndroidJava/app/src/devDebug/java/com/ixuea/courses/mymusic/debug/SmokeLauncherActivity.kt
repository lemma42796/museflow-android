package com.ixuea.courses.mymusic.debug

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.ixuea.courses.mymusic.MainActivity
import com.ixuea.courses.mymusic.activity.BaseLogicActivity
import com.ixuea.courses.mymusic.component.conversation.activity.ConversationActivity
import com.ixuea.courses.mymusic.component.download.activity.DownloadActivity
import com.ixuea.courses.mymusic.component.feed.activity.PublishFeedActivity
import com.ixuea.courses.mymusic.component.music.activity.LocalMusicActivity
import com.ixuea.courses.mymusic.component.player.activity.MusicPlayerActivity

class SmokeLauncherActivity : BaseLogicActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Smoke Launcher"
        setContentView(createContent())
    }

    private fun createContent(): ScrollView {
        val density = resources.displayMetrics.density
        val padding = (20 * density).toInt()
        val buttonHeight = (48 * density).toInt()

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(padding, padding, padding, padding)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        container.addView(TextView(this).apply {
            text = "Dev debug smoke entry"
            textSize = 20f
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = padding
            }
        })

        container.addActivityButton("Main shell", MainActivity::class.java, buttonHeight)
        container.addButton("Discovery fragment", buttonHeight) {
            startActivity(SmokeFragmentHostActivity.discoveryIntent(this))
        }
        container.addButton("Feed fragment", buttonHeight) {
            startActivity(SmokeFragmentHostActivity.feedIntent(this))
        }
        container.addActivityButton("DownloadActivity", DownloadActivity::class.java, buttonHeight)
        container.addActivityButton("ConversationActivity", ConversationActivity::class.java, buttonHeight)
        container.addActivityButton("PublishFeedActivity", PublishFeedActivity::class.java, buttonHeight)
        container.addActivityButton("MusicPlayerActivity", MusicPlayerActivity::class.java, buttonHeight)
        container.addActivityButton("LocalMusicActivity", LocalMusicActivity::class.java, buttonHeight)

        return ScrollView(this).apply {
            addView(container)
        }
    }

    private fun LinearLayout.addActivityButton(
        label: String,
        activityClass: Class<out Activity>,
        height: Int
    ) {
        addButton(label, height) {
            startActivity(Intent(this@SmokeLauncherActivity, activityClass))
        }
    }

    private fun LinearLayout.addButton(label: String, height: Int, onClick: () -> Unit) {
        val margin = (8 * resources.displayMetrics.density).toInt()
        addView(Button(this@SmokeLauncherActivity).apply {
            text = label
            isAllCaps = false
            setOnClickListener { onClick() }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                height
            ).apply {
                bottomMargin = margin
            }
        })
    }
}
