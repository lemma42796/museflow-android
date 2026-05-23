package com.ixuea.courses.mymusic.fragment

import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.activity.BaseLogicActivity
import com.ixuea.courses.mymusic.component.login.activity.LoginHomeActivity
import com.ixuea.courses.mymusic.manager.MusicListManager
import com.ixuea.courses.mymusic.playback.PlaybackService
import com.ixuea.courses.mymusic.util.LiteORMUtil
import com.ixuea.courses.mymusic.util.PreferenceUtil
import com.ixuea.courses.mymusic.view.PlaceholderView

abstract class BaseLogicFragment : BaseCommonFragment() {
    private var cachedPlaceholderView: PlaceholderView? = null
    protected lateinit var sp: PreferenceUtil

    override fun initDatum() {
        super.initDatum()

        sp = PreferenceUtil.getInstance(hostActivity)
    }

    protected open fun loadData(isPlaceholder: Boolean) = Unit

    protected fun loadData() {
        loadData(false)
    }

    val placeholderView: PlaceholderView?
        get() {
            if (cachedPlaceholderView == null) {
                cachedPlaceholderView = findViewById(R.id.placeholder)
                cachedPlaceholderView?.setOnClickListener {
                    loadData(true)
                }
            }
            return cachedPlaceholderView
        }

    protected val musicListManager: MusicListManager
        get() = PlaybackService.getListManager(hostActivity.applicationContext)

    protected val orm: LiteORMUtil
        get() = LiteORMUtil.getInstance(hostActivity.applicationContext)

    fun startMusicPlayerActivity() {
        (hostActivity as BaseLogicActivity).startMusicPlayerActivity()
    }

    protected fun toLogin() {
        startActivity(LoginHomeActivity::class.java)
    }

    protected fun loginAfter(data: Runnable) {
        if (sp.isLogin) {
            data.run()
        } else {
            toLogin()
        }
    }
}
