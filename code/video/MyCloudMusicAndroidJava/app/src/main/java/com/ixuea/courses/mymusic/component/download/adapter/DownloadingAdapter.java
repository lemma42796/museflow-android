package com.ixuea.courses.mymusic.component.download.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.ixuea.android.downloader.domain.DownloadInfo;
import com.ixuea.courses.mymusic.AppContext;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.adapter.BaseRecyclerViewAdapter;
import com.ixuea.courses.mymusic.component.download.listener.MyDownloadListener;
import com.ixuea.courses.mymusic.component.download.model.event.DownloadChangedEvent;
import com.ixuea.courses.mymusic.component.song.model.Song;
import com.ixuea.courses.mymusic.databinding.ItemDownloadingBinding;
import com.ixuea.courses.mymusic.util.FileUtil;
import com.ixuea.courses.mymusic.util.LiteORMUtil;
import com.ixuea.superui.dialog.SuperDialog;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.SoftReference;

/**
 * 下载中适配器
 */
public class DownloadingAdapter extends BaseRecyclerViewAdapter<DownloadInfo, DownloadingAdapter.ViewHolder> {
    private final LiteORMUtil orm;
    private final FragmentManager fragmentManager;

    public DownloadingAdapter(Context context, LiteORMUtil orm, FragmentManager fragmentManager) {
        super(context);
        this.orm = orm;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDownloadingBinding binding = ItemDownloadingBinding.inflate(getInflater(), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        //获取当前位置数据
        DownloadInfo data = getData(position);

        //获取业务数据
        Song song = orm.querySong(data.getId());
        holder.bindBase(song);

        //下载信息
        holder.bind(data);
    }

    /**
     * 发送下载数据改变了通知
     *
     * @param isDownloadManagerNotify
     */
    private void publishDownloadStatusChangedEvent(boolean isDownloadManagerNotify) {
        if (isDownloadManagerNotify) {
            EventBus.getDefault().post(new DownloadChangedEvent());
        }
    }

    /**
     * 下载ViewHolder
     */
    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {

        private final ItemDownloadingBinding binding;
        private DownloadInfo data;

        public ViewHolder(ItemDownloadingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        /**
         * 显示基础数据
         *
         * @param data
         */
        public void bindBase(Song data) {
            binding.title.setText(data.getTitle());
        }

        /**
         * 显示下载信息
         *
         * @param
         */
        public void bind(DownloadInfo data) {
            this.data = data;

            //设置下载回调监听器
            data.setDownloadListener(new MyDownloadListener(new SoftReference<>(ViewHolder.this)) {
                @Override
                public void onRefresh() {
                    if (getUserTag() != null && getUserTag().get() != null) {
                        ViewHolder holder = (ViewHolder) getUserTag().get();
                        holder.refresh(true);
                    }
                }
            });

            refresh(false);

            //删除按钮点击事件
            binding.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SuperDialog.newInstance(fragmentManager)
                            .setTitleRes(R.string.confirm_delete)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //从下载框架中删除
                                    AppContext.getInstance().getDownloadManager().remove(data);

                                    //从适配器中删除
                                    removeData(getBindingAdapterPosition());
                                }
                            }).show();
                }
            });
        }

        /**
         * 显示下载信息
         *
         * @param isDownloadManagerNotify
         */
        private void refresh(boolean isDownloadManagerNotify) {
            switch (data.getStatus()) {
                case DownloadInfo.STATUS_PAUSED:
                    //暂停
                    binding.info.setText(R.string.click_download);
                    binding.progress.setVisibility(View.GONE);
                    break;
                case DownloadInfo.STATUS_ERROR:
                    //下载失败了
                    binding.info.setText(R.string.download_failed);
                    binding.progress.setVisibility(View.GONE);
                    break;
                case DownloadInfo.STATUS_DOWNLOADING:
                case DownloadInfo.STATUS_PREPARE_DOWNLOAD:
                    //下载中
                    //准备下载
                    binding.info.setVisibility(View.VISIBLE);
                    binding.progress.setVisibility(View.VISIBLE);

                    //计算进度
                    if (data.getSize() > 0) {
                        //格式化下载进度
                        String start = FileUtil.formatFileSize(data.getProgress());
                        String size = FileUtil.formatFileSize(data.getSize());

                        binding.info.setText(context
                                .getResources()
                                .getString(R.string.download_progress,
                                        start,
                                        size));

                        //设置到进度条
                        binding.progress.setMax((int) data.getSize());
                        binding.progress.setProgress((int) data.getProgress());
                    }
                    break;
                case DownloadInfo.STATUS_WAIT:
                    //等待中
                    binding.info.setText(R.string.wait_download);
                    binding.progress.setVisibility(View.GONE);
                    break;
                default:
                    //下载完成
                    //未下载
                    int position = getBindingAdapterPosition();
                    if (position >= 0) {
                        removeData(position);
                    }

                    //发送通知
                    publishDownloadStatusChangedEvent(isDownloadManagerNotify);
                    break;
            }
        }
    }
}
