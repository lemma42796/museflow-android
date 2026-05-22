package com.ixuea.courses.mymusic.component.conversation.activity

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemLongClickListener
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.activity.BaseTitleActivity
import com.ixuea.courses.mymusic.component.chat.activity.ChatActivity
import com.ixuea.courses.mymusic.component.conversation.adapter.ConversationAdapter
import com.ixuea.courses.mymusic.component.conversation.ui.ConversationListUiState
import com.ixuea.courses.mymusic.component.conversation.ui.ConversationListViewModel
import com.ixuea.courses.mymusic.databinding.ActivityConversationBinding
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * 我的消息（会话界面）
 */
class ConversationActivity : BaseTitleActivity<ActivityConversationBinding>() {
    private lateinit var adapter: ConversationAdapter
    private lateinit var viewModel: ConversationListViewModel
    private var handledDataVersion = 0L
    private var handledErrorVersion = 0L

    override fun initDatum() {
        super.initDatum()
        viewModel = ViewModelProvider(this)[ConversationListViewModel::class.java]
        viewModel.observeConversationChanges()

        adapter = ConversationAdapter(R.layout.item_conversation)
        binding.list.adapter = adapter
        observeConversationState()
    }

    override fun initListeners() {
        super.initListeners()
        adapter.setOnItemClickListener { _, _, position ->
            val data = adapter.getItem(position)
            startActivityExtraId(ChatActivity::class.java, data.targetId)
        }

        adapter.setOnItemLongClickListener(object : OnItemLongClickListener {
            override fun onItemLongClick(
                adapter: BaseQuickAdapter<*, *>,
                view: View,
                position: Int
            ): Boolean {
                val data = this@ConversationActivity.adapter.getItem(position)
                viewModel.deleteMessages(data.conversation)
                return true
            }
        })
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    override fun loadData(isPlaceholder: Boolean) {
        super.loadData(isPlaceholder)
        viewModel.load()
    }

    private fun observeConversationState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    render(state)
                }
            }
        }
    }

    private fun render(state: ConversationListUiState) {
        if (state.errorVersion != handledErrorVersion) {
            handledErrorVersion = state.errorVersion
            Timber.w("conversation list error %s", state.errorCode)
        }

        if (state.dataVersion != handledDataVersion) {
            handledDataVersion = state.dataVersion
            adapter.setNewInstance(state.conversations.toMutableList())
        }
    }
}
