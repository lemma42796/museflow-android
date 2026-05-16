package com.ixuea.courses.mymusic.component.comment.adapter;

import android.app.Activity;
import android.text.SpannableString;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.comment.model.Comment;
import com.ixuea.courses.mymusic.component.user.activity.UserDetailActivity;
import com.ixuea.courses.mymusic.util.ImageUtil;
import com.ixuea.courses.mymusic.util.RichUtil;
import com.ixuea.courses.mymusic.util.SuperDateUtil;
import com.ixuea.courses.mymusic.util.SuperTextUtil;

import timber.log.Timber;

/**
 * 评论列表适配器
 */
public class CommentAdapter extends BaseQuickAdapter<Comment, BaseViewHolder> {
    public CommentAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, Comment data) {
        ImageView iconView = holder.getView(R.id.icon);
        ImageUtil.showAvatar((Activity) getContext(), iconView, data.getUser().getIcon());

        holder.setText(R.id.nickname, data.getUser().getNickname());
        holder.setText(R.id.time, SuperDateUtil.commonFormat(data.getCreatedAt()));

        holder.setText(R.id.like_count, String.valueOf(data.getLikesCount()));
        //显示点赞状态
        if (data.isLiked()) {
            holder.setImageResource(R.id.like, R.drawable.thumb_selected);
            holder.setTextColorRes(R.id.like_count, R.color.primary);
        } else {
            holder.setImageResource(R.id.like, R.drawable.thumb);
            holder.setTextColorRes(R.id.like_count, R.color.black80);
        }

        TextView contentView = holder.getView(R.id.content);
        SuperTextUtil.setLinkColor(contentView, getContext().getColor(R.color.link));
        holder.setText(R.id.content, processContent(data.getContent()));

        //被回复的评论
        if (data.getParent() == null) {
            //没有被回复的评论
            holder.setGone(R.id.reply_container, true);
        } else {
            holder.setGone(R.id.reply_container, false);

            contentView = holder.getView(R.id.reply_content);
            SuperTextUtil.setLinkColor(contentView, getContext().getColor(R.color.link));

            //内容
            String content = getContext().getString(R.string.reply_comment,
                    data.getParent().getUser().getNickname(),
                    data.getParent().getContent());

            holder.setText(R.id.reply_content, processContent(content));
        }
    }

    /**
     * 处理文本点击事件
     * 这部分可以用监听器回调到Activity中处理
     *
     * @param content
     * @return
     */
    private SpannableString processContent(String content) {
        //设置点击事件
        SpannableString result = RichUtil.processContent(getContext(), content,
                new RichUtil.OnTagClickListener() {
                    @Override
                    public void onTagClick(String data, RichUtil.MatchResult matchResult) {
                        String clickText = RichUtil.removePlaceholderString(data);
                        Timber.d("processContent mention click %s", clickText);
                        UserDetailActivity.startWithNickname(getContext(), clickText);
                    }
                },
                (data, matchResult) -> {
                    String clickText = RichUtil.removePlaceholderString(data);
                    Timber.d("processContent hash tag %s", clickText);
                });

        //返回结果
        return result;
    }

    public void clearData() {
        getData().clear();
        notifyDataSetChanged();
    }
}
