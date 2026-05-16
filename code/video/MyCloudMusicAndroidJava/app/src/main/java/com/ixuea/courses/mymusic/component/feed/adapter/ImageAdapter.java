package com.ixuea.courses.mymusic.component.feed.adapter;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.model.Resource;
import com.ixuea.courses.mymusic.util.ImageUtil;
import com.luck.picture.lib.entity.LocalMedia;

/**
 * 图片适配器
 */
public class ImageAdapter extends BaseQuickAdapter<Object, BaseViewHolder> {
    public ImageAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, Object d) {
        ImageView iconView = holder.getView(R.id.icon);
        holder.setGone(R.id.close, true);

        if (d instanceof Resource) {
            Resource data = (Resource) d;
            ImageUtil.show(getContext(), iconView, data.getUri());
        } else if (d instanceof LocalMedia) {
            LocalMedia data = (LocalMedia) d;
            //选择的图片
            ImageUtil.showLocalImage(getContext(), iconView, data.getCompressPath());

            //显示删除按钮
            holder.setGone(R.id.close, false);
        } else {
            iconView.setImageResource((int) d);
        }
    }
}
