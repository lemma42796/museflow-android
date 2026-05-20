package com.ixuea.courses.mymusic.debug

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.ixuea.courses.mymusic.activity.BaseLogicActivity
import com.ixuea.courses.mymusic.component.discovery.fragment.DiscoveryFragment
import com.ixuea.courses.mymusic.component.feed.fragment.FeedFragment

class SmokeFragmentHostActivity : BaseLogicActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = FrameLayout(this).apply {
            id = View.generateViewId()
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        setContentView(container)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(container.id, createFragment(requireNotNull(intent.getStringExtra(EXTRA_TARGET))))
                .commit()
        }
    }

    private fun createFragment(target: String): Fragment {
        return when (target) {
            TARGET_DISCOVERY -> DiscoveryFragment.newInstance()
            TARGET_FEED -> FeedFragment.newInstance()
            else -> error("Unknown smoke target: $target")
        }
    }

    companion object {
        private const val EXTRA_TARGET = "target"
        private const val TARGET_DISCOVERY = "discovery"
        private const val TARGET_FEED = "feed"

        fun discoveryIntent(context: Context): Intent {
            return Intent(context, SmokeFragmentHostActivity::class.java)
                .putExtra(EXTRA_TARGET, TARGET_DISCOVERY)
        }

        fun feedIntent(context: Context): Intent {
            return Intent(context, SmokeFragmentHostActivity::class.java)
                .putExtra(EXTRA_TARGET, TARGET_FEED)
        }
    }
}
