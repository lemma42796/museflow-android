package com.ixuea.courses.mymusic.component.user.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseActivity;
import com.ixuea.courses.mymusic.component.user.model.User;
import com.ixuea.courses.mymusic.component.user.model.ui.TitleData;
import com.ixuea.courses.mymusic.model.ui.BaseMultiItemEntity;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.courses.mymusic.util.ImageUtil;

import java.util.ArrayList;

/**
 * 用户适配器
 */
public class UserAdapter extends BaseMultiItemQuickAdapter<BaseMultiItemEntity, BaseViewHolder> {
    public UserAdapter() {
        super(new ArrayList<>());
        addItemType(Constant.STYLE_TITLE, R.layout.item_title_small);
        addItemType(Constant.STYLE_USER, R.layout.item_topic);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, BaseMultiItemEntity d) {
        if (d instanceof TitleData) {
            TitleData data = (TitleData) d;
            holder.setText(R.id.title, data.getTitleString());
        } else {
            User data = (User) d;

            ImageUtil.showAvatar((BaseActivity) getContext(), holder.getView(R.id.icon), data.getIcon());
            holder.setText(R.id.title, data.getNickname());
            holder.setText(R.id.info, data.getDetail());
        }
    }
}
