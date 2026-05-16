package com.ixuea.courses.mymusic.component.download.fragment;

import android.os.Bundle;

import com.ixuea.android.downloader.domain.DownloadInfo;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.download.adapter.DownloadingAdapter;
import com.ixuea.courses.mymusic.component.download.repository.DownloadRepository;
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
    private DownloadRepository repository;

    @Override
    protected void initViews() {
        super.initViews();
        SuperRecyclerViewUtil.initVerticalLinearRecyclerView(binding.list);
    }


    @Override
    protected void initDatum() {
        super.initDatum();
        repository = DownloadRepository.getInstance();

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
                    repository.resume(data);
                    break;
                default:
                    //暂停下载
                    repository.pause(data);
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
            repository.remove(downloadInfo);
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
        repository.resumeAll();
        adapter.notifyDataSetChanged();
    }

    private void pauseAll() {
        repository.pauseAll();
        adapter.notifyDataSetChanged();
    }

    /**
     * 是否有下载任务
     *
     * @return
     */
    private boolean isDownloading() {
        return repository.isDownloading(adapter.getDatum());
    }

    @Override
    protected void loadData(boolean isPlaceholder) {
        super.loadData(isPlaceholder);
        List<DownloadInfo> downloads = repository.findDownloading();
        adapter.setDatum(downloads);

        //显示按钮状态
        showButtonStatus();
    }

    public static DownloadingFragment newInstance() {

        Bundle args = new Bundle();

        DownloadingFragment fragment = new DownloadingFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
