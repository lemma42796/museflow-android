package com.ixuea.courses.mymusic.component.comment.activity

import android.content.Context
import android.content.Intent
import android.text.Editable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.activity.BaseTitleActivity
import com.ixuea.courses.mymusic.adapter.TextWatcherAdapter
import com.ixuea.courses.mymusic.component.comment.adapter.CommentAdapter
import com.ixuea.courses.mymusic.component.comment.fragment.CommentMoreDialogFragment
import com.ixuea.courses.mymusic.component.comment.model.Comment
import com.ixuea.courses.mymusic.component.comment.ui.CommentLoadOperation
import com.ixuea.courses.mymusic.component.comment.ui.CommentUiState
import com.ixuea.courses.mymusic.component.comment.ui.CommentViewModel
import com.ixuea.courses.mymusic.component.user.activity.UserActivity
import com.ixuea.courses.mymusic.component.user.activity.UserDetailActivity
import com.ixuea.courses.mymusic.databinding.ActivityCommentBinding
import com.ixuea.courses.mymusic.util.Constant
import com.ixuea.courses.mymusic.util.RichUtil
import com.ixuea.superui.toast.SuperToast
import com.ixuea.superui.util.KeyboardUtil
import com.ixuea.superui.util.SuperClipboardUtil
import com.ixuea.superui.util.SuperRecyclerViewUtil
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils
import timber.log.Timber
import kotlin.math.abs

/**
 * 评论界面
 */
class CommentActivity : BaseTitleActivity<ActivityCommentBinding>() {
    private var sheetId: String? = null
    private lateinit var adapter: CommentAdapter
    private lateinit var viewModel: CommentViewModel
    private var parentId: String? = null
    private var handledLoadCompleteVersion = 0L
    private var handledCreateCompleteVersion = 0L
    private var handledLikeUpdateVersion = 0L
    private var handledErrorVersion = 0L

    override fun initViews() {
        super.initViews()
        SuperRecyclerViewUtil.initVerticalLinearRecyclerView(binding.list, true)
    }

    override fun initDatum() {
        super.initDatum()
        sheetId = extraString(Constant.SHEET_ID)
        viewModel = ViewModelProvider(this)[CommentViewModel::class.java]

        adapter = CommentAdapter(R.layout.item_comment)
        binding.list.adapter = adapter
        observeCommentState()

        loadData()
    }

    override fun initListeners() {
        super.initListeners()
        adapter.addChildClickViewIds(
            R.id.icon,
            R.id.user_container,
            R.id.like_container,
            R.id.content,
            R.id.reply_content,
        )
        adapter.setOnItemChildClickListener { itemAdapter, view, position ->
            val data = itemAdapter.getItem(position) as Comment
            when (view.id) {
                R.id.icon,
                R.id.user_container -> {
                    val userId = data.user?.id ?: return@setOnItemChildClickListener
                    startActivityExtraId(UserDetailActivity::class.java, userId)
                }

                R.id.like_container -> likeClick(data)

                R.id.content,
                R.id.reply_content -> showCommentMoreDialog(data)
            }
        }

        adapter.setOnItemClickListener { itemAdapter, _, position ->
            showCommentMoreDialog(itemAdapter.getItem(position) as Comment)
        }

        binding.refresh.setOnRefreshListener {
            loadData()
        }

        binding.refresh.setOnLoadMoreListener {
            loadMore()
        }

        binding.input.send.setOnClickListener {
            sendClick()
        }

        binding.list.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (abs(dy) > 10 && binding.input.content.text.toString().trim().isEmpty()) {
                        clearInputContent()
                    }
                }
            },
        )

        binding.input.content.addTextChangedListener(
            object : TextWatcherAdapter() {
                override fun afterTextChanged(s: Editable) {
                    super.afterTextChanged(s)

                    if (s.toString().endsWith(RichUtil.MENTION)) {
                        UserActivity.start(hostActivity, Constant.STYLE_FRIEND_SELECT)
                    }
                }
            },
        )
    }

    private fun observeCommentState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    render(state)
                }
            }
        }
    }

    private fun render(state: CommentUiState) {
        if (state.loadCompleteVersion != handledLoadCompleteVersion) {
            handledLoadCompleteVersion = state.loadCompleteVersion
            renderComments(state)
        }

        if (state.createCompleteVersion != handledCreateCompleteVersion) {
            handledCreateCompleteVersion = state.createCompleteVersion
            SuperToast.success(R.string.comment_create_success)
            loadData()
            clearInputContent()
            KeyboardUtil.hideKeyboard(hostActivity, binding.input.content)
        }

        if (state.likeUpdateVersion != handledLikeUpdateVersion) {
            handledLikeUpdateVersion = state.likeUpdateVersion
            adapter.notifyDataSetChanged()
        }

        if (state.errorVersion != handledErrorVersion) {
            handledErrorVersion = state.errorVersion
            finishRefreshAndLoadMore(noMoreData = false)
            if (state.error != null) {
                Timber.e(state.error, "comment request error %s", state.errorMessage)
            } else {
                Timber.e("comment request error %s", state.errorMessage)
            }
            state.errorMessage?.takeIf { it.isNotBlank() }?.let(SuperToast::show)
        }
    }

    private fun renderComments(state: CommentUiState) {
        finishRefreshAndLoadMore(state.noMoreData)
        if (state.loadOperation == CommentLoadOperation.REFRESH) {
            adapter.setNewInstance(state.comments.toMutableList())
        } else {
            adapter.addData(state.pageComments)
        }
    }

    private fun finishRefreshAndLoadMore(noMoreData: Boolean) {
        binding.refresh.finishRefresh(2000, true, false)
        binding.refresh.finishLoadMore(2000, true, noMoreData)
    }

    private fun sendClick() {
        val content = binding.input.content.text.toString().trim()

        if (StringUtils.isBlank(content)) {
            SuperToast.show(R.string.hint_comment)
            return
        }

        viewModel.create(sheetId, parentId, content)
    }

    private fun clearInputContent() {
        parentId = null
        binding.input.content.setText("")
        binding.input.content.setHint(R.string.hint_comment)
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
                binding.input.content.hint = resources.getString(
                    R.string.reply_hint,
                    data.user?.nickname.orEmpty(),
                )
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

    private fun likeClick(data: Comment) {
        if (!sp.isLogin) {
            toLogin()
            return
        }

        viewModel.toggleLike(data)
    }

    override fun loadData(isPlaceholder: Boolean) {
        super.loadData(isPlaceholder)
        viewModel.refresh(sheetId)
    }

    private fun loadMore() {
        viewModel.loadMore(sheetId)
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
