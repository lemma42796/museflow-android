package com.ixuea.courses.mymusic.component.conversation.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import com.ixuea.courses.mymusic.activity.BaseLogicActivity
import com.ixuea.courses.mymusic.component.chat.activity.ChatActivity
import com.ixuea.courses.mymusic.component.conversation.ui.ConversationScreen
import com.ixuea.courses.mymusic.component.conversation.ui.ConversationListViewModel
import com.ixuea.courses.mymusic.ui.compose.MuseFlowTheme
import timber.log.Timber

/**
 * Conversation list backed by Compose.
 */
class ConversationActivity : BaseLogicActivity() {
    private lateinit var viewModel: ConversationListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ConversationListViewModel::class.java]

        setContent {
            val state by viewModel.uiState.collectAsState()

            LaunchedEffect(state.errorVersion) {
                if (state.errorVersion > 0) {
                    Timber.w("conversation list error %s", state.errorCode)
                }
            }

            MuseFlowTheme {
                ConversationScreen(
                    state = state,
                    onBack = { onBackPressedDispatcher.onBackPressed() },
                    onConversationClick = { item ->
                        startActivityExtraId(ChatActivity::class.java, item.targetId)
                    },
                    onDeleteMessages = { item ->
                        viewModel.deleteMessages(item.conversation)
                    },
                )
            }
        }
    }

    override fun initDatum() {
        super.initDatum()
        viewModel.observeConversationChanges()
    }

    override fun onResume() {
        super.onResume()
        viewModel.load()
    }
}
