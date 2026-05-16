package com.ixuea.courses.mymusic.component.user.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseActivity;
import com.ixuea.courses.mymusic.component.sheet.model.Sheet;
import com.ixuea.courses.mymusic.component.user.model.ui.TitleData;
import com.ixuea.courses.mymusic.model.ui.BaseMultiItemEntity;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.courses.mymusic.util.ImageUtil;

import java.util.ArrayList;

/**
 * 用户详见歌单界面适配器
 */
public class UserDetailSheetAdapter extends BaseMultiItemQuickAdapter<BaseMultiItemEntity, BaseViewHolder> {
    public UserDetailSheetAdapter() {
        super(new ArrayList<>());
        addItemType(Constant.STYLE_TITLE, R.layout.item_title_small);
        addItemType(Constant.STYLE_SHEET, R.layout.item_topic);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, BaseMultiItemEntity d) {
        if (d instanceof TitleData) {
            TitleData data = (TitleData) d;
            holder.setText(R.id.title, data.getTitle());
        } else {
            Sheet data = (Sheet) d;

            //显示封面
            ImageUtil.show((BaseActivity) getContext(), holder.getView(R.id.icon), data.getIcon());

            //标题
            holder.setText(R.id.title, data.getTitle());

            //音乐数量
            holder.setText(R.id.info, getContext().getResources().getString(R.string.song_count, data.getSongsCount()));
        }
    }
}
