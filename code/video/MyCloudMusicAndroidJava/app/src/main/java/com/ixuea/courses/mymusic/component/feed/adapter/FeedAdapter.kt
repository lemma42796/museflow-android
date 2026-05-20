package com.ixuea.courses.mymusic.component.feed.adapter

import android.app.Activity
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ixuea.courses.mymusic.AppContext
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.comment.model.Comment
import com.ixuea.courses.mymusic.component.feed.model.Feed
import com.ixuea.courses.mymusic.component.user.model.User
import com.ixuea.courses.mymusic.databinding.ItemFeedCommentBinding
import com.ixuea.courses.mymusic.util.Constant
import com.ixuea.courses.mymusic.util.ImageUtil
import com.ixuea.courses.mymusic.util.SpannableStringBuilderUtil
import com.ixuea.courses.mymusic.util.SuperDateUtil
import com.ixuea.courses.mymusic.util.SuperTextUtil
import com.ixuea.superui.decoration.GridDividerItemDecoration
import com.ixuea.superui.util.DensityUtil

/**
 * 动态适配器
 */
class FeedAdapter(layoutResId: Int) : BaseQuickAdapter<Feed, BaseViewHolder>(layoutResId) {
    private var listener: FeedListener? = null

    fun setListener(listener: FeedListener?) {
        this.listener = listener
    }

    override fun convert(holder: BaseViewHolder, item: Feed) {
        val user = item.user

        ImageUtil.showAvatar(
            context as Activity,
            holder.getView<ImageView>(R.id.icon),
            user?.icon
        )

        holder.setText(R.id.nickname, user?.nickname.orEmpty())
        holder.setText(R.id.date, SuperDateUtil.commonFormat(item.createdAt))
        holder.setText(R.id.content, item.content)

        if (!item.province.isNullOrBlank()) {
            holder.setText(R.id.position, "%s . %s".format(item.city.orEmpty(), item.position.orEmpty()))
            holder.setGone(R.id.position, false)
        } else {
            holder.setGone(R.id.position, true)
        }

        bindMedias(holder, item)
        bindDelete(holder, item)
        bindLikes(holder, item)
        bindComments(holder, item)
    }

    private fun bindMedias(holder: BaseViewHolder, item: Feed) {
        val medias = item.medias
        if (medias.isNullOrEmpty()) {
            holder.setGone(R.id.list, true)
            return
        }

        holder.setGone(R.id.list, false)

        val listView = holder.getView<RecyclerView>(R.id.list)
        val spanCount = when {
            medias.size > 4 -> 3
            medias.size > 1 -> 2
            else -> 1
        }
        listView.layoutManager = GridLayoutManager(context, spanCount)

        if (listView.itemDecorationCount > 0) {
            listView.removeItemDecorationAt(0)
        }
        val itemDecoration = GridDividerItemDecoration(
            context,
            DensityUtil.dip2px(context, 5F).toInt()
        )
        listView.addItemDecoration(itemDecoration)

        val adapter = ImageAdapter(R.layout.item_image)
        adapter.setOnItemClickListener { _, _, position ->
            val results = medias.map { it.uri.orEmpty() }
            listener?.onImageClick(listView, results, position)
        }
        listView.adapter = adapter
        listView.tag = adapter

        adapter.setNewInstance(ArrayList<Any>().apply {
            addAll(medias)
        })
    }

    private fun bindDelete(holder: BaseViewHolder, item: Feed) {
        val preference = AppContext.getInstance().preference
        if (preference.isLogin && item.user?.id == preference.userId) {
            holder.setGone(R.id.delete, false)
        } else {
            holder.setGone(R.id.delete, true)
        }
    }

    private fun bindLikes(holder: BaseViewHolder, item: Feed) {
        val likes = item.likes
        val comments = item.comments

        if (likes.isNullOrEmpty() && comments.isNullOrEmpty()) {
            holder.setGone(R.id.like_comment_container, true)
        } else {
            holder.setGone(R.id.like_comment_container, false)
        }

        if (likes.isNullOrEmpty()) {
            holder.setGone(R.id.like_container, true)
            holder.setGone(R.id.like_users, true)
            holder.setImageResource(R.id.like, R.drawable.thumb)
        } else {
            holder.setGone(R.id.like_container, false)
            holder.setGone(R.id.like_users, false)

            SuperTextUtil.setLinkColor(
                holder.getView(R.id.like_users),
                context.getColor(R.color.link)
            )
            holder.setText(R.id.like_users, processLikeUserContent(likes))

            val preference = AppContext.getInstance().preference
            if (preference.isLogin && likes.contains(User(preference.userId))) {
                holder.setImageResource(R.id.like, R.drawable.thumb_selected)
            } else {
                holder.setImageResource(R.id.like, R.drawable.thumb)
            }
        }
    }

    private fun bindComments(holder: BaseViewHolder, item: Feed) {
        val commentContentContainer = holder.getView<LinearLayoutCompat>(R.id.comment_content_container)
        commentContentContainer.removeAllViews()

        val comments = item.comments
        if (comments.isNullOrEmpty()) {
            holder.setGone(R.id.comment_container, true)
        } else {
            holder.setGone(R.id.comment_container, false)

            comments.forEach { comment ->
                val commentBinding = ItemFeedCommentBinding.inflate(
                    LayoutInflater.from(context),
                    commentContentContainer,
                    false
                )

                SuperTextUtil.setLinkColor(commentBinding.root, context.getColor(R.color.link))
                commentBinding.content.text = processContent(comment)
                commentContentContainer.addView(commentBinding.root)
            }
        }
    }

    private fun processContent(data: Comment): SpannableStringBuilder {
        val result = SpannableStringBuilder()

        val user = data.user
        result.append(user?.nickname.orEmpty())
        SpannableStringBuilderUtil.setUserClickSpan(result, 0, result.length, user?.id)

        data.parent?.let { parent ->
            result.append(context.getString(R.string.reply))

            val start = result.length
            result.append(parent.user?.nickname.orEmpty())
            SpannableStringBuilderUtil.setUserClickSpan(result, start, result.length, parent.user?.id)
        }

        result.append(context.getString(R.string.colon_separator))
        result.append(data.content.orEmpty())

        return result
    }

    private fun processLikeUserContent(data: List<User>): SpannableStringBuilder {
        val result = SpannableStringBuilder()
        var start = 0

        data.forEachIndexed { index, user ->
            result.append(user.nickname)
            SpannableStringBuilderUtil.setUserClickSpan(result, start, result.length, user.id)

            if (index != data.size - 1) {
                result.append(Constant.SEPARATOR)
            }

            start = result.length
        }

        return result
    }

    /**
     * 动态监听器
     */
    interface FeedListener {
        /**
         * 点击了动态图片回调
         */
        fun onImageClick(rv: RecyclerView, results: List<String>, index: Int)
    }
}
