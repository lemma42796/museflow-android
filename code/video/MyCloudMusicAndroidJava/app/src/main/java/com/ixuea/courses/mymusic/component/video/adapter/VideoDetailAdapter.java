package com.ixuea.courses.mymusic.component.video.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.SpannableString;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.adapter.BaseRecyclerViewAdapter;
import com.ixuea.courses.mymusic.component.comment.model.Comment;
import com.ixuea.courses.mymusic.component.user.activity.UserDetailActivity;
import com.ixuea.courses.mymusic.component.video.model.Video;
import com.ixuea.courses.mymusic.databinding.ItemCommentBinding;
import com.ixuea.courses.mymusic.databinding.ItemTitleSmallBinding;
import com.ixuea.courses.mymusic.databinding.ItemVideoListBinding;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.courses.mymusic.util.ImageUtil;
import com.ixuea.courses.mymusic.util.RichUtil;
import com.ixuea.courses.mymusic.util.SuperDateUtil;
import com.ixuea.courses.mymusic.util.SuperTextUtil;
import com.ixuea.superui.util.SuperViewUtil;

import timber.log.Timber;

/**
 * 视频详情适配器
 */
public class VideoDetailAdapter extends BaseRecyclerViewAdapter<Object, BaseRecyclerViewAdapter.ViewHolder> {
    public VideoDetailAdapter(Context context) {
        super(context);
    }

    /**
     * 返回ViewHolder
     *
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (Constant.STYLE_TITLE == viewType) {
            //创建标题ViewHolder
            return new TitleViewHolder(ItemTitleSmallBinding.inflate(getInflater(), parent, false));
        } else if (Constant.STYLE_VIDEO == viewType) {
            //创建视频ViewHolder
            return new VideoViewHolder(ItemVideoListBinding.inflate(getInflater(), parent, false));
        }
        return new CommentViewHolder(ItemCommentBinding.inflate(getInflater(), parent, false));
    }

    /**
     * 绑定数据
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        //获取当前位置数据
        Object data = getData(position);

        holder.bind(data);
    }

    /**
     * 返回view类型
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        //获取当前位置的数据
        Object data = getData(position);

        if (data instanceof String) {
            //标题
            return Constant.STYLE_TITLE;
        } else if (data instanceof Video) {
            //相关推荐视频
            return Constant.STYLE_VIDEO;
        }

        return Constant.STYLE_COMMENT;
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
        SpannableString result = RichUtil.processContent(context, content,
                new RichUtil.OnTagClickListener() {
                    @Override
                    public void onTagClick(String data, RichUtil.MatchResult matchResult) {
                        String clickText = RichUtil.removePlaceholderString(data);
                        Timber.d("processContent mention click %s", clickText);
                        UserDetailActivity.startWithNickname(context, clickText);
                    }
                },
                (data, matchResult) -> {
                    String clickText = RichUtil.removePlaceholderString(data);
                    Timber.d("processContent hash tag %s", clickText);
                });

        //返回结果
        return result;
    }

    /**
     * 标题VH
     */
    class TitleViewHolder extends BaseRecyclerViewAdapter.ViewHolder {

        private final ItemTitleSmallBinding binding;

        public TitleViewHolder(ItemTitleSmallBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        public void bind(Object data) {
            super.bind(data);
            binding.title.setText((String) data);
        }
    }

    /**
     * 视频VH
     */
    class VideoViewHolder extends BaseRecyclerViewAdapter.ViewHolder {

        private final ItemVideoListBinding binding;

        public VideoViewHolder(ItemVideoListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        public void bind(Object d) {
            super.bind(d);

            Video data = (Video) d;

            ImageUtil.show(context, binding.icon, data.getIcon());
            binding.title.setText(data.getTitle());

            //信息
            String timeString = SuperDateUtil.s2ms((int) data.getDuration());
            String info = context.getResources().getString(R.string.video_info,
                    timeString,
                    data.getUser().getNickname());
            binding.info.setText(info);
        }
    }

    /**
     * 评论VH
     */
    class CommentViewHolder extends BaseRecyclerViewAdapter.ViewHolder {

        private final ItemCommentBinding binding;

        public CommentViewHolder(ItemCommentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        public void bind(Object d) {
            super.bind(d);

            Comment data = (Comment) d;

            ImageUtil.showAvatar((Activity) context, binding.icon, data.getUser().getIcon());

            binding.nickname.setText(data.getUser().getNickname());
            binding.time.setText(SuperDateUtil.commonFormat(data.getCreatedAt()));

            binding.likeCount.setText(String.valueOf(data.getLikesCount()));

            //显示点赞状态
            if (data.isLiked()) {
                binding.like.setImageResource(R.drawable.thumb_selected);
                binding.likeCount.setTextColor(context.getColor(R.color.primary));
            } else {
                binding.like.setImageResource(R.drawable.thumb);
                binding.likeCount.setTextColor(context.getColor(R.color.black80));
            }

            SuperTextUtil.setLinkColor(binding.content, context.getColor(R.color.link));
            binding.content.setText(processContent(data.getContent()));

            //被回复的评论
            if (data.getParent() == null) {
                //没有被回复的评论
                SuperViewUtil.gone(binding.replyContainer);
            } else {
                SuperViewUtil.show(binding.replyContainer);

                SuperTextUtil.setLinkColor(binding.replyContent, context.getColor(R.color.link));

                //内容
                String content = context.getString(R.string.reply_comment,
                        data.getParent().getUser().getNickname(),
                        data.getParent().getContent());

                binding.replyContent.setText(processContent(content));
            }
        }
    }
}
