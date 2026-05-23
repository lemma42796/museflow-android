package com.ixuea.courses.mymusic.activity

import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.login.activity.LoginHomeActivity
import com.ixuea.courses.mymusic.component.player.activity.MusicPlayerActivity
import com.ixuea.courses.mymusic.manager.MusicListManager
import com.ixuea.courses.mymusic.manager.impl.GlobalLyricManagerImpl
import com.ixuea.courses.mymusic.playback.PlaybackService
import com.ixuea.courses.mymusic.util.LiteORMUtil
import com.ixuea.courses.mymusic.util.PreferenceUtil
import com.ixuea.courses.mymusic.util.ServiceUtil
import com.ixuea.courses.mymusic.util.SuperDarkUtil
import com.ixuea.courses.mymusic.view.PlaceholderView
import com.ixuea.superui.loading.SuperRoundLoadingDialogFragment
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import java.lang.ref.WeakReference

open class BaseLogicActivity : BaseCommonActivity() {
    protected lateinit var sp: PreferenceUtil
    private var loadingWeakReference: WeakReference<SuperRoundLoadingDialogFragment>? = null
    private var cachedPlaceholderView: PlaceholderView? = null
    private lateinit var globalLyricManager: GlobalLyricManagerImpl

    override fun initViews() {
        super.initViews()
        if (SuperDarkUtil.isDark(this)) {
            QMUIStatusBarHelper.setStatusBarDarkMode(this)
        } else {
            QMUIStatusBarHelper.setStatusBarLightMode(this)
        }
    }

    override fun initDatum() {
        super.initDatum()
        globalLyricManager = GlobalLyricManagerImpl.getInstance(applicationContext)

        sp = PreferenceUtil.getInstance(hostActivity)
    }

    val hostActivity: BaseLogicActivity
        get() = this

    fun showLoading() {
        showLoading(getString(R.string.loading))
    }

    fun showLoading(data: Int) {
        showLoading(getString(data))
    }

    fun showLoading(message: String) {
        if (loadingWeakReference?.get() == null) {
            loadingWeakReference = WeakReference(
                SuperRoundLoadingDialogFragment.newInstance(message)
            )
        }

        val dialog = loadingWeakReference?.get() ?: return
        if (dialog.dialog == null || dialog.dialog?.isShowing != true) {
            dialog.show(supportFragmentManager, "SuperRoundLoadingDialogFragment")
        }
    }

    fun hideLoading() {
        loadingWeakReference?.get()?.dismiss()
        loadingWeakReference?.clear()
        loadingWeakReference = null
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

    fun startMusicPlayerActivity() {
        startActivity(MusicPlayerActivity::class.java)
    }

    protected val musicListManager: MusicListManager
        get() = PlaybackService.getListManager(applicationContext)

    override fun onDestroy() {
        super.onDestroy()
    }

    protected val orm: LiteORMUtil
        get() = LiteORMUtil.getInstance(applicationContext)

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

    override fun onResume() {
        super.onResume()
        if (!ServiceUtil.isBackgroundRunning(applicationContext)) {
            globalLyricManager.tryHide()
        }
    }

    override fun onStop() {
        super.onStop()
        if (ServiceUtil.isBackgroundRunning(applicationContext)) {
            globalLyricManager.tryShow()
        }
    }

    override fun onPause() {
        super.onPause()
    }

    protected open fun pageId(): String? {
        return null
    }
}
