package com.ixuea.courses.mymusic.component.chat.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.adapter.BaseRecyclerViewAdapter
import com.ixuea.courses.mymusic.component.chat.model.MediaMessageExtra
import com.ixuea.courses.mymusic.component.chat.ui.ChatMessageUiState
import com.ixuea.courses.mymusic.util.Constant
import com.ixuea.courses.mymusic.util.ImageUtil
import com.ixuea.courses.mymusic.util.JSONUtil
import com.ixuea.superui.util.DensityUtil
import com.ixuea.superui.util.SuperViewUtil
import io.rong.imlib.model.Message
import io.rong.imlib.model.MessageContent
import io.rong.message.ImageMessage
import io.rong.message.TextMessage

/**
 * 聊天界面适配器
 */
class ChatAdapter(context: Context) :
    BaseRecyclerViewAdapter<ChatMessageUiState, BaseRecyclerViewAdapter.ViewHolder<ChatMessageUiState>>(context) {

    private val imageMaxWidth: Int = DensityUtil.dip2px(context, 150F).toInt()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseRecyclerViewAdapter.ViewHolder<ChatMessageUiState> {
        return when (viewType) {
            Constant.IMAGE_LEFT -> {
                ImageViewHolder(inflater.inflate(R.layout.item_chat_image_left, parent, false))
            }

            Constant.IMAGE_RIGHT -> {
                ImageViewHolder(inflater.inflate(R.layout.item_chat_image_right, parent, false))
            }

            Constant.TEXT_LEFT -> {
                TextViewHolder(inflater.inflate(R.layout.item_chat_text_left, parent, false))
            }

            else -> {
                TextViewHolder(inflater.inflate(R.layout.item_chat_text_right, parent, false))
            }
        }
    }

    override fun onBindViewHolder(
        holder: BaseRecyclerViewAdapter.ViewHolder<ChatMessageUiState>,
        position: Int
    ) {
        super.onBindViewHolder(holder, position)
        holder.bind(getData(position))
    }

    override fun getItemViewType(position: Int): Int {
        val data = getData(position).message
        val content: MessageContent = data.content
        val isSend = data.messageDirection == Message.MessageDirection.SEND

        return if (content is ImageMessage) {
            if (isSend) Constant.IMAGE_RIGHT else Constant.IMAGE_LEFT
        } else {
            if (isSend) Constant.TEXT_RIGHT else Constant.TEXT_LEFT
        }
    }

    /**
     * 聊天消息公共ViewHolder，比如头像
     */
    private open inner class BaseChatViewHolder(itemView: View) :
        BaseRecyclerViewAdapter.ViewHolder<ChatMessageUiState>(itemView) {

        private val iconView: ImageView = itemView.findViewById(R.id.icon)

        override fun bind(data: ChatMessageUiState) {
            super.bind(data)
            ImageUtil.showAvatar(context as Activity, iconView, data.senderIcon)
        }
    }

    /**
     * 文本消息VH
     */
    private inner class TextViewHolder(itemView: View) : BaseChatViewHolder(itemView) {
        private val contentView: TextView = itemView.findViewById(R.id.content)

        override fun bind(data: ChatMessageUiState) {
            super.bind(data)
            val content = data.message.content as? TextMessage
            contentView.text = content?.content.orEmpty()
        }
    }

    /**
     * 图片消息VH
     */
    private inner class ImageViewHolder(itemView: View) : BaseChatViewHolder(itemView) {
        private val contentView: ImageView = itemView.findViewById(R.id.content)

        override fun bind(data: ChatMessageUiState) {
            super.bind(data)
            val imageMessage = data.message.content as? ImageMessage ?: return
            val extra = parseMediaMessageExtra(imageMessage.extra)
            setImageContentContainerSize(extra?.width ?: 0, extra?.height ?: 0)

            when {
                imageMessage.remoteUri != null -> {
                    ImageUtil.showFull(context, contentView, imageMessage.remoteUri.toString())
                }

                imageMessage.localUri != null -> {
                    ImageUtil.showLocalImage(context, contentView, imageMessage.localUri.toString())
                }

                imageMessage.thumUri != null -> {
                    ImageUtil.showFull(context, contentView, imageMessage.thumUri.toString())
                }

                else -> {
                    contentView.setImageDrawable(null)
                }
            }
        }

        private fun parseMediaMessageExtra(extra: String?): MediaMessageExtra? {
            if (extra.isNullOrBlank()) {
                return null
            }

            return runCatching {
                JSONUtil.fromJSON(extra, MediaMessageExtra::class.java)
            }.getOrNull()
        }

        /**
         * 设置图片容器宽高
         */
        private fun setImageContentContainerSize(width: Int, height: Int) {
            if (width <= 0 || height <= 0) {
                SuperViewUtil.resize(contentView, imageMaxWidth, imageMaxWidth)
                return
            }

            val (newWidth, newHeight) = if (width > height) {
                imageMaxWidth to imageMaxWidth * height / width
            } else {
                imageMaxWidth * width / height to imageMaxWidth
            }

            SuperViewUtil.resize(contentView, newWidth, newHeight)
        }
    }
}
