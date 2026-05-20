package com.ixuea.courses.mymusic.component.conversation.adapter

import android.app.Activity
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.conversation.ui.ConversationItemUiState
import com.ixuea.courses.mymusic.util.ImageUtil

/**
 * 会话适配器
 */
class ConversationAdapter(layoutResId: Int) :
    BaseQuickAdapter<ConversationItemUiState, BaseViewHolder>(layoutResId) {

    override fun convert(holder: BaseViewHolder, item: ConversationItemUiState) {
        val icon = holder.getView<ImageView>(R.id.icon)
        ImageUtil.showAvatar(context as Activity, icon, item.icon)
        holder.setText(R.id.nickname, item.nickname)
        holder.setText(R.id.time, item.timeText)
        holder.setText(R.id.info, item.messageText)
        holder.setVisible(R.id.count, item.hasUnread)
        holder.setText(R.id.count, item.unreadText)
    }
}
