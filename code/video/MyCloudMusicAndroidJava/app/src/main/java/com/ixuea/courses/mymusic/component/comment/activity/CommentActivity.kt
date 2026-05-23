package com.ixuea.courses.mymusic.component.comment.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.activity.BaseLogicActivity
import com.ixuea.courses.mymusic.component.comment.fragment.CommentMoreDialogFragment
import com.ixuea.courses.mymusic.component.comment.model.Comment
import com.ixuea.courses.mymusic.component.comment.ui.CommentScreen
import com.ixuea.courses.mymusic.component.comment.ui.CommentUiState
import com.ixuea.courses.mymusic.component.comment.ui.CommentViewModel
import com.ixuea.courses.mymusic.component.user.activity.UserActivity
import com.ixuea.courses.mymusic.component.user.activity.UserDetailActivity
import com.ixuea.courses.mymusic.ui.compose.MuseFlowTheme
import com.ixuea.courses.mymusic.util.Constant
import com.ixuea.courses.mymusic.util.RichUtil
import com.ixuea.superui.toast.SuperToast
import com.ixuea.superui.util.SuperClipboardUtil
import timber.log.Timber

/**
 * Comment screen backed by Compose.
 */
class CommentActivity : BaseLogicActivity() {
    private var sheetId: String? = null
    private lateinit var viewModel: CommentViewModel
    private var parentId: String? = null
    private var inputContent by mutableStateOf("")
    private var replyHintName by mutableStateOf<String?>(null)
    private var handledCreateCompleteVersion = 0L
    private var handledErrorVersion = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sheetId = extraString(Constant.SHEET_ID)
        viewModel = ViewModelProvider(this)[CommentViewModel::class.java]

        setContent {
            val state by viewModel.uiState.collectAsState()

            LaunchedEffect(state.createCompleteVersion) {
                renderCreateComplete(state)
            }
            LaunchedEffect(state.errorVersion) {
                renderError(state)
            }

            MuseFlowTheme {
                CommentScreen(
                    state = state,
                    input = inputContent,
                    replyHintName = replyHintName,
                    onInputChange = ::onInputChange,
                    onBack = { onBackPressedDispatcher.onBackPressed() },
                    onRefresh = { loadData() },
                    onLoadMore = { viewModel.loadMore(sheetId) },
                    onSubmit = ::sendClick,
                    onLike = ::likeClick,
                    onUserClick = ::openCommentUser,
                    onCommentMore = ::showCommentMoreDialog,
                )
            }
        }
    }

    override fun initDatum() {
        super.initDatum()
        loadData()
    }

    private fun onInputChange(content: String) {
        inputContent = content
        if (content.endsWith(RichUtil.MENTION)) {
            UserActivity.start(hostActivity, Constant.STYLE_FRIEND_SELECT)
        }
    }

    private fun sendClick() {
        val content = inputContent.trim()
        if (content.isBlank()) {
            SuperToast.show(R.string.hint_comment)
            return
        }

        viewModel.create(sheetId, parentId, content)
    }

    private fun likeClick(data: Comment) {
        if (!sp.isLogin) {
            toLogin()
            return
        }

        viewModel.toggleLike(data)
    }

    private fun openCommentUser(data: Comment) {
        val userId = data.user?.id ?: return
        startActivityExtraId(UserDetailActivity::class.java, userId)
    }

    private fun showCommentMoreDialog(data: Comment) {
        CommentMoreDialogFragment.showDialog(supportFragmentManager) { dialog, which ->
            dialog.dismiss()
            processClick(which, data)
        }
    }

    private fun processClick(which: Int, data: Comment) {
        when (which) {
            0 -> {
                parentId = data.id
                replyHintName = data.user?.nickname.orEmpty()
            }

            1 -> {
                // TODO 分享评论
            }

            2 -> {
                SuperClipboardUtil.copyText(hostActivity, data.content.orEmpty())
                SuperToast.success(R.string.copy_success)
            }

            3 -> {
                // TODO 举报评论
            }
        }
    }

    private fun renderCreateComplete(state: CommentUiState) {
        if (state.createCompleteVersion == handledCreateCompleteVersion) {
            return
        }

        handledCreateCompleteVersion = state.createCompleteVersion
        SuperToast.success(R.string.comment_create_success)
        clearInputContent()
        loadData()
    }

    private fun renderError(state: CommentUiState) {
        if (state.errorVersion == handledErrorVersion) {
            return
        }

        handledErrorVersion = state.errorVersion
        if (state.error != null) {
            Timber.e(state.error, "comment request error %s", state.errorMessage)
        } else {
            Timber.e("comment request error %s", state.errorMessage)
        }
        state.errorMessage?.takeIf { it.isNotBlank() }?.let(SuperToast::show)
    }

    private fun clearInputContent() {
        parentId = null
        replyHintName = null
        inputContent = ""
    }

    override fun loadData(isPlaceholder: Boolean) {
        super.loadData(isPlaceholder)
        viewModel.refresh(sheetId)
    }

    companion object {
        @JvmStatic
        fun startWithSheetId(context: Context, id: String) {
            val intent = Intent(context, CommentActivity::class.java)
            intent.putExtra(Constant.SHEET_ID, id)
            context.startActivity(intent)
        }
    }
}
