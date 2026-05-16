package com.ixuea.courses.mymusic.component.sheet.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ixuea.android.downloader.domain.DownloadInfo;
import com.ixuea.courses.mymusic.AppContext;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.song.model.Song;
import com.ixuea.superui.dialog.SuperDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * 歌单详情-歌曲适配器
 */
public class SongAdapter extends BaseQuickAdapter<Song, BaseViewHolder> {
    /**
     * 选择状态
     */
    private int[] selectedIndexes;
    private FragmentManager fragmentManager;
    private int offset;

    /**
     * 是否进入编辑模式了
     */
    private boolean editing;

    /**
     * 构造方法
     *
     * @param layoutResId
     */
    public SongAdapter(int layoutResId) {
        super(layoutResId);
    }

    /**
     * 构造方法
     *
     * @param layoutResId
     */
    public SongAdapter(int layoutResId, int offset, FragmentManager fragmentManager) {
        super(layoutResId);
        this.offset = offset;
        this.fragmentManager = fragmentManager;
    }

    /**
     * 显示数据的方法
     *
     * @param holder
     * @param data
     */
    @Override
    protected void convert(@NonNull BaseViewHolder holder, Song data) {
        //显示位置
        holder.setText(R.id.index, String.valueOf(holder.getLayoutPosition() + offset));

        //显示标题
        holder.setText(R.id.title, data.getTitle());

        //显示信息
        holder.setText(R.id.info, data.getSinger().getNickname());

        if (offset != 0) {
            holder.setImageResource(R.id.more, R.drawable.close);

            holder.getView(R.id.more)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SuperDialog.newInstance(fragmentManager)
                                    .setTitleRes(R.string.confirm_delete)
                                    .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //查询下载任务
                                            DownloadInfo downloadInfo = AppContext.getInstance().getDownloadManager().getDownloadById(data.getId());

                                            if (downloadInfo != null) {
                                                //从下载框架删除
                                                AppContext.getInstance().getDownloadManager().remove(downloadInfo);
                                            } else {
                                                AppContext.getInstance().getOrm().deleteSong(data);
                                            }

                                            //从适配器中删除
                                            removeAt(holder.getAdapterPosition());

                                        }
                                    }).show();
                        }
                    });
        } else {
            //是否下载
            DownloadInfo downloadInfo = AppContext.getInstance().getDownloadManager().getDownloadById(data.getId());
            if (downloadInfo != null && downloadInfo.getStatus() == DownloadInfo.STATUS_COMPLETED) {
                //下载完成了

                //显示下载完成了图标
                holder.setGone(R.id.download, false);
            } else {
                holder.setGone(R.id.download, true);
            }
        }

        //处理编辑状态
        if (isEditing()) {
            holder.setVisible(R.id.index, false);
            holder.setVisible(R.id.check, true);
            holder.setVisible(R.id.more, false);

            if (isSelected(holder.getLayoutPosition())) {
                holder.setImageResource(R.id.check, R.drawable.ic_checkbox_selected);
            } else {
                holder.setImageResource(R.id.check, R.drawable.ic_checkbox);
            }
        } else {
            holder.setVisible(R.id.index, true);
            holder.setVisible(R.id.check, false);
            holder.setVisible(R.id.more, true);
        }

    }

    public boolean isEditing() {
        return editing;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;

        if (!editing) {
            //退出编辑模式

            //清除原来的选中状态
            for (int i = 0; i < selectedIndexes.length; i++) {
                selectedIndexes[i] = 0;
            }
        }

        //通知数据改变了
        notifyDataSetChanged();
    }

    @Override
    public void setNewInstance(@Nullable List<Song> list) {
        super.setNewInstance(list);

        //创建一个和数据长度一样的数组
        //这里可以优化
        //因为就目前来说
        //在歌单详情不需要多选
        selectedIndexes = new int[list.size()];
    }

    /**
     * 是否选择
     *
     * @param position
     * @return
     */
    public boolean isSelected(int position) {
        return selectedIndexes[position] == 1;
    }

    /**
     * 设置当前位置是否选中
     *
     * @param position
     * @param isSelected
     */
    public void setSelected(int position, boolean isSelected) {
        //设置选中状态
        selectedIndexes[position] = isSelected ? 1 : 0;

        //通知数据改变了
        notifyItemChanged(position);
    }


    /**
     * 获取选中的索引
     *
     * @return
     */
    public List<Integer> getSelectedIndexes() {
        List<Integer> indexes = new ArrayList<>();

        //遍历数组
        for (int i = 0; i < selectedIndexes.length; i++) {
            if (selectedIndexes[i] == 1) {
                //选中了

                //把索引添加到列表中
                indexes.add(i);
            }
        }

        return indexes;
    }
}
