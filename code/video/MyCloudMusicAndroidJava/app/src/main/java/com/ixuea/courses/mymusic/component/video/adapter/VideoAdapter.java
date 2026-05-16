package com.ixuea.courses.mymusic.component.video.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.video.model.Video;
import com.ixuea.courses.mymusic.util.ImageUtil;
import com.ixuea.courses.mymusic.util.SuperDateUtil;

/**
 * 视频adapter
 */
public class VideoAdapter extends BaseQuickAdapter<Video, BaseViewHolder> {
    public VideoAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, Video data) {
        //封面
        ImageView iconView = holder.getView(R.id.icon);

        if (data.getHeight() > data.getWidth()) {
            //竖屏视频

            //封面等比缩放全部显示
            iconView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            //横屏视频

            //封面从中心等比裁剪显示
            iconView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        ImageUtil.show((Activity) getContext(), iconView, data.getIcon());

        //点击数
        holder.setText(R.id.count, String.valueOf(data.getClicksCount()));

        //视频时长
        holder.setText(R.id.time,
                SuperDateUtil.s2ms((int) data.getDuration()));

        //标题
        holder.setText(R.id.title, data.getTitle());

        //头像
        ImageUtil.showAvatar((Activity) getContext(),
                holder.getView(R.id.avatar),
                data.getUser().getIcon());

        //昵称
        holder.setText(R.id.nickname, data.getUser().getNickname());

        //评论数
        holder.setText(R.id.comments_count, String.valueOf(data.getCommentsCount()));

        //最后一个不显示分割线布局
        boolean isLast = holder.getAdapterPosition() == getItemCount() - 1;
        holder.getView(R.id.divider).setVisibility(isLast ? View.GONE : View.VISIBLE);
    }
}
