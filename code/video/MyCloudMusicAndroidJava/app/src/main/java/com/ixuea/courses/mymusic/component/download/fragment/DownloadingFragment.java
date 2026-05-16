package com.ixuea.courses.mymusic.component.download.fragment;

import android.os.Bundle;

import com.ixuea.android.downloader.callback.DownloadManager;
import com.ixuea.android.downloader.domain.DownloadInfo;
import com.ixuea.courses.mymusic.AppContext;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.download.adapter.DownloadingAdapter;
import com.ixuea.courses.mymusic.databinding.FragmentDownloadingBinding;
import com.ixuea.courses.mymusic.fragment.BaseViewModelFragment;
import com.ixuea.superui.toast.SuperToast;
import com.ixuea.superui.util.SuperRecyclerViewUtil;

import java.util.List;

/**
 * 下载中界面
 */
public class DownloadingFragment extends BaseViewModelFragment<FragmentDownloadingBinding> {
    private DownloadingAdapter adapter;

    @Override
    protected void initViews() {
        super.initViews();
        SuperRecyclerViewUtil.initVerticalLinearRecyclerView(binding.list);
    }


    @Override
    protected void initDatum() {
        super.initDatum();
        //创建适配器
        adapter = new DownloadingAdapter(getHostActivity(), getOrm(), getChildFragmentManager());

        //设置适配器
        binding.list.setAdapter(adapter);

        loadData();
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        adapter.setOnItemClickListener((holder, position) -> {
            //获取点击的数据
            DownloadInfo data = adapter.getData(position);

            switch (data.getStatus()) {
                case DownloadInfo.STATUS_NONE:
                case DownloadInfo.STATUS_PAUSED:
                case DownloadInfo.STATUS_ERROR:
                    //继续下载
                    getDownloadManager().resume(data);
                    break;
                default:
                    //暂停下载
                    getDownloadManager().pause(data);
                    break;
            }

            //显示按钮状态
            showButtonStatus();
        });

        binding.download.setOnClickListener(v -> downloadClick());
        binding.delete.setOnClickListener(v -> deleteClick());
    }

    private void deleteClick() {
        if (adapter.getItemCount() == 0) {
            SuperToast.show(R.string.error_not_download);
            return;
        }

        //删除所有下载任务
        for (DownloadInfo downloadInfo : adapter.getDatum()
        ) {
            getDownloadManager().remove(downloadInfo);
        }

        //清除适配器数据
        adapter.clearData();
    }

    private void downloadClick() {
        if (adapter.getItemCount() == 0) {
            SuperToast.show(R.string.error_not_download);
            return;
        }

        if (isDownloading()) {
            pauseAll();
        } else {
            resumeAll();
        }

        //显示按钮状态
        showButtonStatus();
    }

    /**
     * 显示按钮状态
     */
    private void showButtonStatus() {
        if (isDownloading()) {
            binding.download.setText(R.string.pause_all);
        } else {
            binding.download.setText(R.string.download_all);
        }
    }


    private void resumeAll() {
        getDownloadManager().resumeAll();
        adapter.notifyDataSetChanged();
    }

    private void pauseAll() {
        getDownloadManager().pauseAll();
        adapter.notifyDataSetChanged();
    }

    /**
     * 是否有下载任务
     *
     * @return
     */
    private boolean isDownloading() {
        //获取所有下载任务
        List<DownloadInfo> datum = adapter.getDatum();

        //遍历所有下载任务
        for (DownloadInfo downloadInfo : datum) {
            if (downloadInfo.getStatus() == DownloadInfo.STATUS_DOWNLOADING) {
                //只要有一个是下载
                return true;
            }
        }

        return false;
    }

    @Override
    protected void loadData(boolean isPlaceholder) {
        super.loadData(isPlaceholder);
        List<DownloadInfo> downloads = getDownloadManager().findAllDownloading();
        adapter.setDatum(downloads);

        //显示按钮状态
        showButtonStatus();
    }

    private DownloadManager getDownloadManager() {
        return AppContext.getInstance().getDownloadManager();
    }

    public static DownloadingFragment newInstance() {

        Bundle args = new Bundle();

        DownloadingFragment fragment = new DownloadingFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
