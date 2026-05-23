package com.ixuea.courses.mymusic.component.feed.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ixuea.courses.mymusic.component.feed.activity.PublishFeedActivity
import com.ixuea.courses.mymusic.component.feed.ui.FeedScreen
import com.ixuea.courses.mymusic.component.feed.ui.FeedUiState
import com.ixuea.courses.mymusic.component.feed.ui.FeedViewModel
import com.ixuea.courses.mymusic.component.user.activity.UserDetailActivity
import com.ixuea.courses.mymusic.component.user.domain.ObserveUserDetailRequestsUseCase
import com.ixuea.courses.mymusic.fragment.BaseLogicFragment
import com.ixuea.courses.mymusic.ui.compose.MuseFlowTheme
import com.ixuea.courses.mymusic.util.Constant
import com.ixuea.courses.mymusic.util.ImageUtil
import com.wanglu.photoviewerlibrary.PhotoViewer
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * 首页-动态界面
 */
class FeedFragment : BaseLogicFragment() {
    private var userId: String? = null
    private lateinit var composeView: ComposeView
    private lateinit var viewModel: FeedViewModel
    private var handledErrorVersion = 0L
    private val observeUserDetailRequests = ObserveUserDetailRequestsUseCase()

    override fun getLayoutView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        composeView = ComposeView(inflater.context).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed,
            )
        }
        return composeView
    }

    override fun initDatum() {
        super.initDatum()
        userId = arguments?.getString(Constant.USER_ID) ?: arguments?.getString(Constant.ID)
        viewModel = ViewModelProvider(this)[FeedViewModel::class.java]
        viewModel.observeChanges(userId)
        renderContent()
        observeFeedNavigation()

        loadData()
    }

    override fun loadData(isPlaceholder: Boolean) {
        viewModel.load(userId)
    }

    private fun renderContent() {
        composeView.setContent {
            val state by viewModel.uiState.collectAsState()

            LaunchedEffect(state.errorVersion) {
                renderError(state)
            }

            MuseFlowTheme {
                FeedScreen(
                    state = state,
                    isLogin = sp.isLogin,
                    currentUserId = sp.userId,
                    onCreateClick = {
                        loginAfter {
                            startActivity(PublishFeedActivity::class.java)
                        }
                    },
                    onImageClick = ::showImages,
                )
            }
        }
    }

    private fun observeFeedNavigation() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                observeUserDetailRequests().collect { userId ->
                    UserDetailActivity.startWithId(hostActivity, userId)
                }
            }
        }
    }

    private fun renderError(state: FeedUiState) {
        if (state.errorVersion == handledErrorVersion) {
            return
        }

        handledErrorVersion = state.errorVersion
        if (state.error != null) {
            Timber.e(state.error, "feed list error %s", state.errorMessage)
        } else {
            Timber.e("feed list error %s", state.errorMessage)
        }
    }

    private fun showImages(results: List<String>, index: Int) {
        val imageUris = ArrayList(results)

        PhotoViewer
            .setData(imageUris)
            .setCurrentPage(index)
            .setShowImageViewInterface(object : PhotoViewer.ShowImageViewInterface {
                override fun show(iv: ImageView, url: String) {
                    ImageUtil.show(hostActivity, iv, url)
                }
            })
            .start(this)
    }

    companion object {
        @JvmStatic
        fun newInstance(): FeedFragment {
            return newInstance(null)
        }

        @JvmStatic
        fun newInstance(userId: String?): FeedFragment {
            return FeedFragment().apply {
                arguments = Bundle().apply {
                    if (!userId.isNullOrBlank()) {
                        putString(Constant.USER_ID, userId)
                    }
                }
            }
        }
    }
}
