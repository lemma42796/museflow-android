package com.ixuea.courses.mymusic.component.comment.adapter

import android.app.Activity
import android.text.SpannableString
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.comment.model.Comment
import com.ixuea.courses.mymusic.component.user.activity.UserDetailActivity
import com.ixuea.courses.mymusic.util.ImageUtil
import com.ixuea.courses.mymusic.util.RichUtil
import com.ixuea.courses.mymusic.util.SuperDateUtil
import com.ixuea.courses.mymusic.util.SuperTextUtil
import timber.log.Timber

/**
 * 评论列表适配器
 */
class CommentAdapter(layoutResId: Int) : BaseQuickAdapter<Comment, BaseViewHolder>(layoutResId) {
    override fun convert(holder: BaseViewHolder, item: Comment) {
        val iconView = holder.getView<ImageView>(R.id.icon)
        ImageUtil.showAvatar(context as Activity, iconView, item.user?.icon)

        holder.setText(R.id.nickname, item.user?.nickname.orEmpty())
        holder.setText(R.id.time, SuperDateUtil.commonFormat(item.createdAt))

        holder.setText(R.id.like_count, item.likesCount.toString())
        if (item.isLiked) {
            holder.setImageResource(R.id.like, R.drawable.thumb_selected)
            holder.setTextColorRes(R.id.like_count, R.color.primary)
        } else {
            holder.setImageResource(R.id.like, R.drawable.thumb)
            holder.setTextColorRes(R.id.like_count, R.color.black80)
        }

        var contentView = holder.getView<TextView>(R.id.content)
        SuperTextUtil.setLinkColor(contentView, ContextCompat.getColor(context, R.color.link))
        holder.setText(R.id.content, processContent(item.content.orEmpty()))

        val parent = item.parent
        if (parent == null) {
            holder.setGone(R.id.reply_container, true)
        } else {
            holder.setGone(R.id.reply_container, false)

            contentView = holder.getView(R.id.reply_content)
            SuperTextUtil.setLinkColor(contentView, ContextCompat.getColor(context, R.color.link))

            val content = context.getString(
                R.string.reply_comment,
                parent.user?.nickname.orEmpty(),
                parent.content.orEmpty(),
            )
            holder.setText(R.id.reply_content, processContent(content))
        }
    }

    private fun processContent(content: String): SpannableString {
        return RichUtil.processContent(
            content,
            { data, _ ->
                val clickText = RichUtil.removePlaceholderString(data)
                Timber.d("processContent mention click %s", clickText)
                UserDetailActivity.startWithNickname(context, clickText)
            },
            { data, _ ->
                val clickText = RichUtil.removePlaceholderString(data)
                Timber.d("processContent hash tag %s", clickText)
            },
        )
    }

    fun clearData() {
        data.clear()
        notifyDataSetChanged()
    }
}
